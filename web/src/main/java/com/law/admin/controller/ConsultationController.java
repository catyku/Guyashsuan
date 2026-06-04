package com.law.admin.controller;

import com.law.admin.model.ResultModel;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.*;

@RestController
@RequestMapping("/api/consultation")
public class ConsultationController {

    private final JdbcTemplate jdbc;

    public ConsultationController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Data
    public static class ConsultationRequest {
        @NotBlank(message = "姓名不可空白")
        @Size(max = 100, message = "姓名最多 100 字")
        private String name;

        @Size(max = 20, message = "電話最多 20 字")
        private String phone;

        @Size(max = 255, message = "Email 最多 255 字")
        private String email;

        @Size(max = 255, message = "諮詢主題最多 255 字")
        private String subject;

        private String content;
    }

    @Data
    public static class ReplyRequest {
        @NotBlank(message = "回覆內容不可空白")
        private String reply;

        private String status;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (name LIKE ? OR subject LIKE ? OR content LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (status != null && !status.isBlank()) {
            where.append(" AND status = ?");
            params.add(status);
        }

        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM lw_consultation" + where, Integer.class, params.toArray());

        // CWE-682：使用 long 運算避免大頁碼導致整數溢位
        long offset = Math.max((long)(page - 1) * size, 0L);
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(size);
        queryParams.add(offset);

        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, name, phone, email, subject, content, status, reply, inptime, updid, updtime " +
                        "FROM lw_consultation" + where + " ORDER BY id DESC LIMIT ? OFFSET ?",
                queryParams.toArray());

        ResultModel<Map<String, Object>> result = new ResultModel<>(total == null ? 0 : total, rows);
        result.setPage(page);
        result.setSize(size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, name, phone, email, subject, content, status, reply, inptime, updid, updtime " +
                        "FROM lw_consultation WHERE id = ?", id);
        if (rows.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "諮詢記錄不存在"));
        }
        return ResponseEntity.ok(rows.get(0));
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity<?> reply(@PathVariable Integer id, @Valid @RequestBody ReplyRequest req, Authentication auth) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_consultation WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "諮詢記錄不存在"));
        }

        String newStatus = req.getStatus() != null ? req.getStatus() : "D";
        jdbc.update("UPDATE lw_consultation SET reply = ?, status = ?, updid = ?, updtime = NOW() WHERE id = ?",
                req.getReply(), newStatus, auth.getName(), id);

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "回覆成功"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body, Authentication auth) {
        String status = body.getOrDefault("status", "C");
        jdbc.update("UPDATE lw_consultation SET status = ?, updid = ?, updtime = NOW() WHERE id = ?",
                status, auth.getName(), id);
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "狀態更新成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        int rows = jdbc.update("DELETE FROM lw_consultation WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "諮詢記錄不存在"));
        }
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "刪除成功"));
    }
}
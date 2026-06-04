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
@RequestMapping("/api/service")
public class ServiceController {

    private final JdbcTemplate jdbc;

    public ServiceController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Data
    public static class ServiceRequest {
        @NotBlank(message = "業務名稱不可空白")
        @Size(max = 100, message = "業務名稱最多 100 字")
        private String name;

        @Size(max = 100, message = "英文名稱最多 100 字")
        private String nameEn;

        @Size(max = 100, message = "圖示最多 100 字")
        private String icon;

        private String description;

        private Integer sortOrder;

        private String isShow;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        size = Math.min(size, 100);

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (name LIKE ? OR name_en LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }

        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM lw_service" + where, Integer.class, params.toArray());

        int offset = Math.max((page - 1) * size, 0);
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(size);
        queryParams.add(offset);

        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, name, name_en, icon, description, sort_order, is_show, inptime, updid, updtime " +
                        "FROM lw_service" + where + " ORDER BY sort_order ASC, id ASC LIMIT ? OFFSET ?",
                queryParams.toArray());

        ResultModel<Map<String, Object>> result = new ResultModel<>(total == null ? 0 : total, rows);
        result.setPage(page);
        result.setSize(size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, name, name_en, icon, description, sort_order, is_show, inptime, updid, updtime " +
                        "FROM lw_service WHERE id = ?", id);
        if (rows.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "業務領域不存在"));
        }
        return ResponseEntity.ok(rows.get(0));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ServiceRequest req, Authentication auth) {
        jdbc.update(
                "INSERT INTO lw_service (name, name_en, icon, description, sort_order, is_show, updid, inptime) " +
                        "VALUES (?,?,?,?,?,?,?,NOW())",
                req.getName(), req.getNameEn(), req.getIcon(), req.getDescription(),
                req.getSortOrder() != null ? req.getSortOrder() : 0,
                req.getIsShow() != null ? req.getIsShow() : "Y", auth.getName());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "新增成功"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody ServiceRequest req, Authentication auth) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_service WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "業務領域不存在"));
        }

        StringBuilder sql = new StringBuilder("UPDATE lw_service SET updid=?, updtime=NOW()");
        List<Object> params = new ArrayList<>();
        params.add(auth.getName());

        if (req.getName() != null) { sql.append(", name=?"); params.add(req.getName()); }
        if (req.getNameEn() != null) { sql.append(", name_en=?"); params.add(req.getNameEn()); }
        if (req.getIcon() != null) { sql.append(", icon=?"); params.add(req.getIcon()); }
        if (req.getDescription() != null) { sql.append(", description=?"); params.add(req.getDescription()); }
        if (req.getSortOrder() != null) { sql.append(", sort_order=?"); params.add(req.getSortOrder()); }
        if (req.getIsShow() != null) { sql.append(", is_show=?"); params.add(req.getIsShow()); }

        sql.append(" WHERE id=?");
        params.add(id);
        jdbc.update(sql.toString(), params.toArray());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "修改成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        int rows = jdbc.update("DELETE FROM lw_service WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "業務領域不存在"));
        }
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "刪除成功"));
    }
}
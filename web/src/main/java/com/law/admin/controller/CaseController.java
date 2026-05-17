package com.law.admin.controller;

import com.law.admin.model.ResultModel;
import com.law.admin.service.FileUploadService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.*;

@RestController
@RequestMapping("/api/case")
public class CaseController {

    private final JdbcTemplate jdbc;
    private final FileUploadService fileUploadService;

    @Value("${app.upload.root-path:../uploads-law}")
    private String rootPath;

    public CaseController(JdbcTemplate jdbc, FileUploadService fileUploadService) {
        this.jdbc = jdbc;
        this.fileUploadService = fileUploadService;
    }

    @Data
    public static class CaseRequest {
        @NotBlank(message = "案件類別不可空白")
        @Size(max = 50, message = "案件類別最多 50 字")
        private String category;

        @NotBlank(message = "案件標題不可空白")
        @Size(max = 255, message = "案件標題最多 255 字")
        private String title;

        private String content;

        private String caseDate;

        private String image;

        private String isShow;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (title LIKE ? OR content LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }
        if (category != null && !category.isBlank()) {
            where.append(" AND category = ?");
            params.add(category);
        }

        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM lw_case" + where, Integer.class, params.toArray());

        int offset = Math.max((page - 1) * size, 0);
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(size);
        queryParams.add(offset);

        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, category, title, content, case_date, image, is_show, inptime, updid, updtime " +
                        "FROM lw_case" + where + " ORDER BY id DESC LIMIT ? OFFSET ?",
                queryParams.toArray());

        ResultModel<Map<String, Object>> result = new ResultModel<>(total == null ? 0 : total, rows);
        result.setPage(page);
        result.setSize(size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, category, title, content, case_date, image, is_show, inptime, updid, updtime " +
                        "FROM lw_case WHERE id = ?", id);
        if (rows.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "案件不存在"));
        }
        return ResponseEntity.ok(rows.get(0));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CaseRequest req, Authentication auth) {
        jdbc.update(
                "INSERT INTO lw_case (category, title, content, case_date, image, is_show, updid, inptime) " +
                        "VALUES (?,?,?,?,?,?,?,NOW())",
                req.getCategory(), req.getTitle(), req.getContent(), req.getCaseDate(),
                req.getImage(), req.getIsShow() != null ? req.getIsShow() : "Y", auth.getName());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "新增成功"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody CaseRequest req, Authentication auth) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_case WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "案件不存在"));
        }

        StringBuilder sql = new StringBuilder("UPDATE lw_case SET updid=?, updtime=NOW()");
        List<Object> params = new ArrayList<>();
        params.add(auth.getName());

        if (req.getCategory() != null) { sql.append(", category=?"); params.add(req.getCategory()); }
        if (req.getTitle() != null) { sql.append(", title=?"); params.add(req.getTitle()); }
        if (req.getContent() != null) { sql.append(", content=?"); params.add(req.getContent()); }
        if (req.getCaseDate() != null) { sql.append(", case_date=?"); params.add(req.getCaseDate()); }
        if (req.getImage() != null) { sql.append(", image=?"); params.add(req.getImage()); }
        if (req.getIsShow() != null) { sql.append(", is_show=?"); params.add(req.getIsShow()); }

        sql.append(" WHERE id=?");
        params.add(id);
        jdbc.update(sql.toString(), params.toArray());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "修改成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        int rows = jdbc.update("DELETE FROM lw_case WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "案件不存在"));
        }
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "刪除成功"));
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<?> uploadPhoto(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws Exception {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_case WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "案件不存在"));
        }

        String relativePath = fileUploadService.uploadImage(file, "case", rootPath);
        jdbc.update("UPDATE lw_case SET image = ?, updtime = NOW() WHERE id = ?", relativePath, id);

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "上傳成功", "path", relativePath));
    }
}
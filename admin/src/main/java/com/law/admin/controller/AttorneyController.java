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
@RequestMapping("/api/attorney")
public class AttorneyController {

    private final JdbcTemplate jdbc;
    private final FileUploadService fileUploadService;

    @Value("${app.upload.root-path:../uploads-law}")
    private String rootPath;

    public AttorneyController(JdbcTemplate jdbc, FileUploadService fileUploadService) {
        this.jdbc = jdbc;
        this.fileUploadService = fileUploadService;
    }

    @Data
    public static class AttorneyRequest {
        @NotBlank(message = "姓名不可空白")
        @Size(max = 100, message = "姓名最多 100 字")
        private String name;

        @Size(max = 50, message = "職稱最多 50 字")
        private String title;

        @Size(max = 50, message = "證書字號最多 50 字")
        private String licenseNo;

        private String photo;

        private String specialty;

        private String education;

        private String experience;

        private String description;

        private Integer sortOrder;

        private String isShow;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (name LIKE ? OR title LIKE ? OR specialty LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM lw_attorney" + where, Integer.class, params.toArray());

        int offset = Math.max((page - 1) * size, 0);
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(size);
        queryParams.add(offset);

        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, name, title, license_no, photo, specialty, education, experience, " +
                        "description, sort_order, is_show, inptime, updid, updtime " +
                        "FROM lw_attorney" + where + " ORDER BY sort_order ASC, id ASC LIMIT ? OFFSET ?",
                queryParams.toArray());

        ResultModel<Map<String, Object>> result = new ResultModel<>(total == null ? 0 : total, rows);
        result.setPage(page);
        result.setSize(size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, name, title, license_no, photo, specialty, education, experience, " +
                        "description, sort_order, is_show, inptime, updid, updtime " +
                        "FROM lw_attorney WHERE id = ?", id);
        if (rows.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "律師不存在"));
        }
        return ResponseEntity.ok(rows.get(0));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AttorneyRequest req, Authentication auth) {
        jdbc.update(
                "INSERT INTO lw_attorney (name, title, license_no, photo, specialty, education, " +
                        "experience, description, sort_order, is_show, updid, inptime) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW())",
                req.getName(), req.getTitle(), req.getLicenseNo(), req.getPhoto(),
                req.getSpecialty(), req.getEducation(), req.getExperience(),
                req.getDescription(), req.getSortOrder() != null ? req.getSortOrder() : 0,
                req.getIsShow() != null ? req.getIsShow() : "Y", auth.getName());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "新增成功"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody AttorneyRequest req, Authentication auth) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_attorney WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "律師不存在"));
        }

        StringBuilder sql = new StringBuilder("UPDATE lw_attorney SET updid=?, updtime=NOW()");
        List<Object> params = new ArrayList<>();
        params.add(auth.getName());

        if (req.getName() != null) { sql.append(", name=?"); params.add(req.getName()); }
        if (req.getTitle() != null) { sql.append(", title=?"); params.add(req.getTitle()); }
        if (req.getLicenseNo() != null) { sql.append(", license_no=?"); params.add(req.getLicenseNo()); }
        if (req.getPhoto() != null) { sql.append(", photo=?"); params.add(req.getPhoto()); }
        if (req.getSpecialty() != null) { sql.append(", specialty=?"); params.add(req.getSpecialty()); }
        if (req.getEducation() != null) { sql.append(", education=?"); params.add(req.getEducation()); }
        if (req.getExperience() != null) { sql.append(", experience=?"); params.add(req.getExperience()); }
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
        int rows = jdbc.update("DELETE FROM lw_attorney WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "律師不存在"));
        }
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "刪除成功"));
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<?> uploadPhoto(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws Exception {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_attorney WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "律師不存在"));
        }

        String relativePath = fileUploadService.uploadImage(file, "attorney", rootPath);
        jdbc.update("UPDATE lw_attorney SET photo = ?, updtime = NOW() WHERE id = ?", relativePath, id);

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "上傳成功", "path", relativePath));
    }
}
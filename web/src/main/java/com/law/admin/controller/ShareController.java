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
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    private final JdbcTemplate jdbc;
    private final FileUploadService fileUploadService;

    @Value("${app.upload.root-path:../uploads-law}")
    private String rootPath;

    public ShareController(JdbcTemplate jdbc, FileUploadService fileUploadService) {
        this.jdbc = jdbc;
        this.fileUploadService = fileUploadService;
    }

    @Data
    public static class ShareRequest {
        @NotBlank(message = "文章標題不可空白")
        @Size(max = 255, message = "文章標題最多 255 字")
        private String title;

        private String content;

        private String shareDate;

        private String image;

        private String isShow;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        size = Math.min(size, 100);
        // CWE-682：使用 long 運算避免大頁碼導致整數溢位
        long offset = Math.max((long)(page - 1) * size, 0L);

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (title LIKE ? OR content LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }

        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM lw_share" + where, Integer.class, params.toArray());

        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(size);
        queryParams.add(offset);

        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, title, content, share_date, image, is_show, inptime, updid, updtime " +
                        "FROM lw_share" + where + " ORDER BY id DESC LIMIT ? OFFSET ?",
                queryParams.toArray());

        ResultModel<Map<String, Object>> result = new ResultModel<>(total == null ? 0 : total, rows);
        result.setPage(page);
        result.setSize(size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, title, content, share_date, image, is_show, inptime, updid, updtime " +
                        "FROM lw_share WHERE id = ?", id);
        if (rows.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "文章不存在"));
        }
        return ResponseEntity.ok(rows.get(0));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ShareRequest req, Authentication auth) {
        String normalizedShareDate;
        try {
            normalizedShareDate = normalizeDateForSql(req.getShareDate());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", "INVALID_DATE", "msg", e.getMessage()));
        }
        String normalizedImagePath = normalizeImagePathForPublic(req.getImage());

        jdbc.update(
                "INSERT INTO lw_share (title, content, share_date, image, is_show, updid, inptime) " +
                        "VALUES (?,?,?,?,?,?,NOW())",
                req.getTitle(), req.getContent(), normalizedShareDate,
                normalizedImagePath, req.getIsShow() != null ? req.getIsShow() : "Y", auth.getName());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "新增成功"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody ShareRequest req, Authentication auth) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_share WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "文章不存在"));
        }

        String normalizedShareDate = null;
        if (req.getShareDate() != null) {
            try {
                normalizedShareDate = normalizeDateForSql(req.getShareDate());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("code", "INVALID_DATE", "msg", e.getMessage()));
            }
        }

        StringBuilder sql = new StringBuilder("UPDATE lw_share SET updid=?, updtime=NOW()");
        List<Object> params = new ArrayList<>();
        params.add(auth.getName());

        if (req.getTitle() != null) { sql.append(", title=?"); params.add(req.getTitle()); }
        if (req.getContent() != null) { sql.append(", content=?"); params.add(req.getContent()); }
        if (req.getShareDate() != null) { sql.append(", share_date=?"); params.add(normalizedShareDate); }
        if (req.getImage() != null) { sql.append(", image=?"); params.add(normalizeImagePathForPublic(req.getImage())); }
        if (req.getIsShow() != null) { sql.append(", is_show=?"); params.add(req.getIsShow()); }

        sql.append(" WHERE id=?");
        params.add(id);
        jdbc.update(sql.toString(), params.toArray());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "修改成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        int rows = jdbc.update("DELETE FROM lw_share WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "文章不存在"));
        }
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "刪除成功"));
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<?> uploadPhoto(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws Exception {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_share WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "文章不存在"));
        }

        String relativePath = fileUploadService.uploadImage(file, "share", rootPath);
        String publicPath = normalizeImagePathForPublic(relativePath);
        jdbc.update("UPDATE lw_share SET image = ?, updtime = NOW() WHERE id = ?", publicPath, id);

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "上傳成功", "path", publicPath));
    }

    private String normalizeDateForSql(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }

        String value = rawDate.trim();

        // yyyy-MM-dd
        try {
            return LocalDate.parse(value).toString();
        } catch (Exception ignored) {
            // fall through
        }

        // ISO instant like 2023-02-27T16:00:00.000Z
        try {
            return Instant.parse(value)
                    .atZone(ZoneId.of("Asia/Taipei"))
                    .toLocalDate()
                    .toString();
        } catch (Exception ignored) {
            // fall through
        }

        // ISO offset datetime
        try {
            return OffsetDateTime.parse(value)
                    .atZoneSameInstant(ZoneId.of("Asia/Taipei"))
                    .toLocalDate()
                    .toString();
        } catch (Exception ignored) {
            // fall through
        }

        throw new IllegalArgumentException("日期格式錯誤，請使用 yyyy-MM-dd");
    }

    private String normalizeImagePathForPublic(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return null;
        }

        String path = rawPath.trim();
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("data:")) {
            return path;
        }

        if (path.startsWith("/uploads/")) {
            return path;
        }

        if (path.startsWith("uploads/")) {
            return "/" + path;
        }

        if (path.startsWith("/")) {
            return "/uploads" + path;
        }

        return "/uploads/" + path;
    }
}
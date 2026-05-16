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
@RequestMapping("/api/banner")
public class BannerController {

    private final JdbcTemplate jdbc;
    private final FileUploadService fileUploadService;

    @Value("${app.upload.root-path:../uploads-law}")
    private String rootPath;

    public BannerController(JdbcTemplate jdbc, FileUploadService fileUploadService) {
        this.jdbc = jdbc;
        this.fileUploadService = fileUploadService;
    }

    @Data
    public static class BannerRequest {
        @Size(max = 255, message = "標題最多 255 字")
        private String title;

        private String subtitle;

        @NotBlank(message = "圖片路徑不可空白")
        private String image;

        @Size(max = 500, message = "連結最多 500 字")
        private String linkUrl;

        private Integer sortOrder;

        private String isShow;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, title, subtitle, image, link_url, sort_order, is_show, inptime, updid, updtime " +
                        "FROM lw_banner ORDER BY sort_order ASC, id ASC");
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, title, subtitle, image, link_url, sort_order, is_show, inptime, updid, updtime " +
                        "FROM lw_banner WHERE id = ?", id);
        if (rows.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "輪播不存在"));
        }
        return ResponseEntity.ok(rows.get(0));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody BannerRequest req, Authentication auth) {
        jdbc.update(
                "INSERT INTO lw_banner (title, subtitle, image, link_url, sort_order, is_show, updid, inptime) " +
                        "VALUES (?,?,?,?,?,?,?,NOW())",
                req.getTitle(), req.getSubtitle(), req.getImage(), req.getLinkUrl(),
                req.getSortOrder() != null ? req.getSortOrder() : 0,
                req.getIsShow() != null ? req.getIsShow() : "Y", auth.getName());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "新增成功"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody BannerRequest req, Authentication auth) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_banner WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "輪播不存在"));
        }

        StringBuilder sql = new StringBuilder("UPDATE lw_banner SET updid=?, updtime=NOW()");
        List<Object> params = new ArrayList<>();
        params.add(auth.getName());

        if (req.getTitle() != null) { sql.append(", title=?"); params.add(req.getTitle()); }
        if (req.getSubtitle() != null) { sql.append(", subtitle=?"); params.add(req.getSubtitle()); }
        if (req.getImage() != null) { sql.append(", image=?"); params.add(req.getImage()); }
        if (req.getLinkUrl() != null) { sql.append(", link_url=?"); params.add(req.getLinkUrl()); }
        if (req.getSortOrder() != null) { sql.append(", sort_order=?"); params.add(req.getSortOrder()); }
        if (req.getIsShow() != null) { sql.append(", is_show=?"); params.add(req.getIsShow()); }

        sql.append(" WHERE id=?");
        params.add(id);
        jdbc.update(sql.toString(), params.toArray());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "修改成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        int rows = jdbc.update("DELETE FROM lw_banner WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "輪播不存在"));
        }
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "刪除成功"));
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<?> uploadPhoto(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws Exception {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_banner WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "輪播不存在"));
        }

        String relativePath = fileUploadService.uploadImage(file, "banner", rootPath);
        jdbc.update("UPDATE lw_banner SET image = ?, updtime = NOW() WHERE id = ?", relativePath, id);

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "上傳成功", "path", relativePath));
    }
}
package com.law.admin.controller;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.*;

@RestController
@RequestMapping("/api/site")
public class SiteController {

    private final JdbcTemplate jdbc;

    public SiteController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT site_key, site_value, remark FROM lw_site ORDER BY site_key");
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> get(@PathVariable String key) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT site_key, site_value, remark FROM lw_site WHERE site_key = ?", key);
        if (rows.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "設定不存在"));
        }
        return ResponseEntity.ok(rows.get(0));
    }

    @Data
    public static class SiteRequest {
        @NotBlank(message = "設定值不可空白")
        private String siteValue;

        private String remark;
    }

    @PutMapping("/{key}")
    public ResponseEntity<?> update(@PathVariable String key, @Valid @RequestBody SiteRequest req, Authentication auth) {
        int rows = jdbc.update(
                "UPDATE lw_site SET site_value = ?, remark = ?, updtime = NOW() WHERE site_key = ?",
                req.getSiteValue(), req.getRemark(), key);
        if (rows == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "設定不存在"));
        }
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "修改成功"));
    }
}
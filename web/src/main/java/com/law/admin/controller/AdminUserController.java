package com.law.admin.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.*;

@RestController
@RequestMapping("/api/admin-user")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    @Data
    public static class AdminRequest {
        @NotBlank(message = "帳號不可空白")
        @Size(max = 50, message = "帳號最多 50 字")
        private String username;

        @NotBlank(message = "密碼不可空白")
        @Size(min = 6, max = 50, message = "密碼長度需 6~50")
        private String password;

        @Size(max = 50, message = "顯示名稱最多 50 字")
        private String displayName;

        @Size(max = 20, message = "角色最多 20 字")
        private String role;

        private String isEnabled;
    }

    @Data
    public static class UpdateAdminRequest {
        @Size(max = 50, message = "密碼長度需 6~50")
        private String password;

        @Size(max = 50, message = "顯示名稱最多 50 字")
        private String displayName;

        @Size(max = 20, message = "角色最多 20 字")
        private String role;

        private String isEnabled;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, username, display_name, role, is_enabled, inptime, updtime FROM lw_admin ORDER BY id");
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, username, display_name, role, is_enabled, inptime, updtime FROM lw_admin WHERE id = ?", id);
        if (rows.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "管理員不存在"));
        }
        return ResponseEntity.ok(rows.get(0));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AdminRequest req, Authentication auth) {
        Integer cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM lw_admin WHERE username = ?", Integer.class, req.getUsername());
        if (cnt != null && cnt > 0) {
            return ResponseEntity.badRequest().body(Map.of("code", "DUPLICATE", "msg", "帳號已存在"));
        }

        String encPwd = passwordEncoder.encode(req.getPassword());
        jdbc.update(
                "INSERT INTO lw_admin (username, password, display_name, role, is_enabled, updid, inptime) " +
                        "VALUES (?,?,?,?,?,?,NOW())",
                req.getUsername(), encPwd, req.getDisplayName(),
                req.getRole() != null ? req.getRole() : "ADMIN",
                req.getIsEnabled() != null ? req.getIsEnabled() : "Y", auth.getName());

        // CWE-778：記錄管理員建立操作
        logger.info("[ADMIN] 管理員建立：operator={}, newUser={}, role={}",
                auth.getName(), req.getUsername(), req.getRole());
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "新增成功"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody UpdateAdminRequest req, Authentication auth) {
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM lw_admin WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "管理員不存在"));
        }

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            String encPwd = passwordEncoder.encode(req.getPassword());
            jdbc.update("UPDATE lw_admin SET password = ?, updid = ?, updtime = NOW() WHERE id = ?",
                    encPwd, auth.getName(), id);
        }

        StringBuilder sql = new StringBuilder("UPDATE lw_admin SET updid=?, updtime=NOW()");
        List<Object> params = new ArrayList<>();
        params.add(auth.getName());

        if (req.getDisplayName() != null) { sql.append(", display_name=?"); params.add(req.getDisplayName()); }
        if (req.getRole() != null) { sql.append(", role=?"); params.add(req.getRole()); }
        if (req.getIsEnabled() != null) { sql.append(", is_enabled=?"); params.add(req.getIsEnabled()); }

        sql.append(" WHERE id=?");
        params.add(id);
        jdbc.update(sql.toString(), params.toArray());

        // CWE-778：記錄管理員修改操作
        logger.info("[ADMIN] 管理員資料修改：operator={}, targetId={}", auth.getName(), id);
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "修改成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, Authentication auth) {
        // 不能刪除自己
        String currentUsername = auth.getName();
        Map<String, Object> target = jdbc.queryForMap("SELECT username FROM lw_admin WHERE id = ?", id);
        if (target != null && currentUsername.equals(target.get("username"))) {
            return ResponseEntity.badRequest().body(Map.of("code", "FORBIDDEN", "msg", "不能刪除自己的帳號"));
        }

        int rows = jdbc.update("DELETE FROM lw_admin WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.status(404).body(Map.of("code", "NOT_FOUND", "msg", "管理員不存在"));
        }
        // CWE-778：記錄管理員刪除操作
        logger.info("[ADMIN] 管理員刪除：operator={}, deletedId={}", auth.getName(), id);
        return ResponseEntity.ok(Map.of("code", "OK", "msg", "刪除成功"));
    }
}
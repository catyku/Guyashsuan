package com.law.admin.controller.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 前台公開 API（不需認證）
 */
@RestController
@RequestMapping("/api/consultation/public")
public class PublicConsultationController {

    private final JdbcTemplate jdbc;

    public PublicConsultationController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Data
    public static class PublicConsultationRequest {
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

    @PostMapping
    public ResponseEntity<?> submit(@Valid @RequestBody PublicConsultationRequest req) {
        jdbc.update(
                "INSERT INTO lw_consultation (name, phone, email, subject, content, status, updid, inptime) " +
                        "VALUES (?,?,?,?,?,'P','web',NOW())",
                req.getName(), req.getPhone(), req.getEmail(),
                req.getSubject(), req.getContent());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "諮詢已送出"));
    }
}
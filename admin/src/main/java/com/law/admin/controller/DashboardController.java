package com.law.admin.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final JdbcTemplate jdbc;

    public DashboardController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        Map<String, Object> stats = new HashMap<>();

        Integer attorneyCount = jdbc.queryForObject("SELECT COUNT(*) FROM lw_attorney", Integer.class);
        Integer caseCount = jdbc.queryForObject("SELECT COUNT(*) FROM lw_case", Integer.class);
        Integer shareCount = jdbc.queryForObject("SELECT COUNT(*) FROM lw_share", Integer.class);
        Integer consultationCount = jdbc.queryForObject("SELECT COUNT(*) FROM lw_consultation", Integer.class);
        Integer pendingConsultation = jdbc.queryForObject(
                "SELECT COUNT(*) FROM lw_consultation WHERE status = 'P'", Integer.class);
        Integer bannerCount = jdbc.queryForObject("SELECT COUNT(*) FROM lw_banner", Integer.class);

        stats.put("attorneyCount", attorneyCount != null ? attorneyCount : 0);
        stats.put("caseCount", caseCount != null ? caseCount : 0);
        stats.put("shareCount", shareCount != null ? shareCount : 0);
        stats.put("consultationCount", consultationCount != null ? consultationCount : 0);
        stats.put("pendingConsultation", pendingConsultation != null ? pendingConsultation : 0);
        stats.put("bannerCount", bannerCount != null ? bannerCount : 0);

        return ResponseEntity.ok(stats);
    }
}
package com.law.admin.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AttorneyRepository {

    private final JdbcTemplate jdbc;

    public AttorneyRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 查詢所有可見律師（完整資訊）
     */
    public List<Map<String, Object>> findAllVisible() {
        return jdbc.queryForList(
                "SELECT id, name, title, photo, specialty, education, experience, description FROM lw_attorney WHERE is_show = 'Y' ORDER BY sort_order ASC");
    }

    /**
     * 查詢所有可見律師（簡化版）
     */
    public List<Map<String, Object>> findAllVisibleSimple() {
        return jdbc.queryForList(
                "SELECT id, name, title, photo, specialty FROM lw_attorney WHERE is_show = 'Y' ORDER BY sort_order ASC");
    }

    /**
     * 根據 ID 查詢單個律師
     */
    public Map<String, Object> findVisibleById(Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, name, title, photo, specialty, education, experience, description FROM lw_attorney WHERE id = ? AND is_show = 'Y'", id);
        return rows.isEmpty() ? null : rows.get(0);
    }
}

package com.law.admin.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ServiceRepository {

    private final JdbcTemplate jdbc;

    public ServiceRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 查詢所有可見業務領域
     */
    public List<Map<String, Object>> findAllVisible() {
        return jdbc.queryForList(
                "SELECT id, name, name_en, icon, description FROM lw_service WHERE is_show = 'Y' ORDER BY sort_order ASC");
    }
}

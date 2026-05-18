package com.law.admin.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class BannerRepository {

    private final JdbcTemplate jdbc;

    public BannerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 查詢所有可見輪播（按排序順序）
     */
    public List<Map<String, Object>> findAllVisible() {
        return jdbc.queryForList(
                "SELECT id, title, subtitle, image, link_url, sort_order FROM lw_banner WHERE is_show = 'Y' ORDER BY sort_order ASC");
    }
}

package com.law.admin.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SiteRepository {

    private final JdbcTemplate jdbc;

    public SiteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 查詢所有網站設定（轉為 Map）
     */
    public Map<String, String> findAllSettings() {
        List<Map<String, Object>> settings = jdbc.queryForList(
                "SELECT site_key, site_value FROM lw_site");
        Map<String, String> siteMap = new HashMap<>();
        for (Map<String, Object> row : settings) {
            siteMap.put((String) row.get("site_key"), (String) row.get("site_value"));
        }
        return siteMap;
    }
}

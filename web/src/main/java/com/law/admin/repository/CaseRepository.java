package com.law.admin.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CaseRepository {

    private final JdbcTemplate jdbc;

    public CaseRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 查詢案件實績總數
     */
    public int countVisible() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM lw_case WHERE is_show = 'Y'", Integer.class);
    }

    /**
     * 分頁查詢案件實績
     */
    public List<Map<String, Object>> findVisibleWithPage(int limit, long offset) {
        return jdbc.queryForList(
                "SELECT id, category, title, content, case_date, image FROM lw_case WHERE is_show = 'Y' ORDER BY ISNULL(case_date) ASC, case_date DESC, id DESC LIMIT ? OFFSET ?",
                limit, offset);
    }

    /**
     * 查詢最新案件實績
     */
    public List<Map<String, Object>> findTopVisible(int limit) {
        return jdbc.queryForList(
                "SELECT id, category, title, content, case_date, image FROM lw_case WHERE is_show = 'Y' ORDER BY ISNULL(case_date) ASC, case_date DESC, id DESC LIMIT ?",
                limit);
    }

    /**
     * 根據 ID 查詢單個案件
     */
    public Map<String, Object> findVisibleById(Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, category, title, content, case_date, image FROM lw_case WHERE id = ? AND is_show = 'Y'", id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    /**
     * 查詢所有可見案件（簡化版）
     */
    public List<Map<String, Object>> findAllVisibleSimple() {
        return jdbc.queryForList(
                "SELECT id, category, title, case_date FROM lw_case WHERE is_show = 'Y' ORDER BY ISNULL(case_date) ASC, case_date DESC, id DESC");
    }
}

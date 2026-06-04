package com.law.admin.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ShareRepository {

    private final JdbcTemplate jdbc;

    public ShareRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 查詢情報分享總數
     */
    public int countVisible() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM lw_share WHERE is_show = 'Y'", Integer.class);
    }

    /**
     * 分頁查詢情報分享
     */
    public List<Map<String, Object>> findVisibleWithPage(int limit, long offset) {
        return jdbc.queryForList(
                "SELECT id, title, content, share_date, image FROM lw_share WHERE is_show = 'Y' ORDER BY ISNULL(share_date) ASC, share_date DESC, id DESC LIMIT ? OFFSET ?",
                limit, offset);
    }

    /**
     * 查詢最新情報分享
     */
    public List<Map<String, Object>> findTopVisible(int limit) {
        return jdbc.queryForList(
                "SELECT id, title, content, share_date, image FROM lw_share WHERE is_show = 'Y' ORDER BY ISNULL(share_date) ASC, share_date DESC, id DESC LIMIT ?",
                limit);
    }

    /**
     * 根據 ID 查詢單個情報分享
     */
    public Map<String, Object> findVisibleById(Integer id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, title, content, share_date, image FROM lw_share WHERE id = ? AND is_show = 'Y'", id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    /**
     * 查詢所有可見情報分享（簡化版）
     */
    public List<Map<String, Object>> findAllVisibleSimple() {
        return jdbc.queryForList(
                "SELECT id, title, share_date FROM lw_share WHERE is_show = 'Y' ORDER BY ISNULL(share_date) ASC, share_date DESC, id DESC");
    }
}

package com.law.admin.service;

import com.law.admin.repository.ShareRepository;
import com.law.admin.util.HtmlUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShareService {

    private final ShareRepository shareRepository;

    public ShareService(ShareRepository shareRepository) {
        this.shareRepository = shareRepository;
    }

    /**
     * 獲取分頁情報分享列表
     */
    public Map<String, Object> getShareListWithPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        int total = shareRepository.countVisible();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        List<Map<String, Object>> shares = shareRepository.findVisibleWithPage(pageSize, offset);
        shares.forEach(s -> s.put("summary", HtmlUtils.stripHtml((String) s.get("content"), 120)));

        Map<String, Object> result = new HashMap<>();
        result.put("shares", shares);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("total", total);

        return result;
    }

    /**
     * 獲取最新情報分享（首頁用）
     */
    public List<Map<String, Object>> getTopShares(int limit) {
        List<Map<String, Object>> shares = shareRepository.findTopVisible(limit);
        shares.forEach(s -> s.put("summary", HtmlUtils.stripHtml((String) s.get("content"), 120)));
        return shares;
    }

    /**
     * 獲取情報詳情
     */
    public Map<String, Object> getShareDetail(Integer id) {
        return shareRepository.findVisibleById(id);
    }

    /**
     * 獲取所有情報分享（簡化版）
     */
    public List<Map<String, Object>> getAllSharesSimple() {
        return shareRepository.findAllVisibleSimple();
    }
}

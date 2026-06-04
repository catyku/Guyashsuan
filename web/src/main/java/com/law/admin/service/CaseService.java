package com.law.admin.service;

import com.law.admin.repository.CaseRepository;
import com.law.admin.util.HtmlUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    /**
     * 獲取分頁案件實績列表
     */
    public Map<String, Object> getCaseListWithPage(int page, int pageSize) {
        // CWE-682：使用 long 運算避免大頁碼導致整數溢位
        long offset = Math.max((long)(page - 1) * pageSize, 0L);
        int total = caseRepository.countVisible();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        List<Map<String, Object>> cases = caseRepository.findVisibleWithPage(pageSize, offset);
        cases.forEach(c -> c.put("summary", HtmlUtils.stripHtml((String) c.get("content"), 120)));

        Map<String, Object> result = new HashMap<>();
        result.put("cases", cases);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("total", total);

        return result;
    }

    /**
     * 獲取最新案件實績（首頁用）
     */
    public List<Map<String, Object>> getTopCases(int limit) {
        List<Map<String, Object>> cases = caseRepository.findTopVisible(limit);
        cases.forEach(c -> c.put("summary", HtmlUtils.stripHtml((String) c.get("content"), 120)));
        return cases;
    }

    /**
     * 獲取案件詳情
     */
    public Map<String, Object> getCaseDetail(Integer id) {
        return caseRepository.findVisibleById(id);
    }

    /**
     * 獲取所有案件（簡化版）
     */
    public List<Map<String, Object>> getAllCasesSimple() {
        return caseRepository.findAllVisibleSimple();
    }
}

package com.law.admin.controller.web;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * 前台網頁控制器 — 使用 Thymeleaf 動態渲染
 * 所有前台路由統一使用 .html 後綴（SEO 友善）
 */
@Controller
public class WebController {

    private final JdbcTemplate jdbc;

    public WebController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** 首頁 */
    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        // 輪播
        List<Map<String, Object>> banners = jdbc.queryForList(
                "SELECT id, title, subtitle, image, link_url, sort_order FROM lw_banner WHERE is_show = 'Y' ORDER BY sort_order ASC");
        model.addAttribute("banners", banners);

        // 案件實績 (首頁顯示最新 3 筆)
        List<Map<String, Object>> cases = jdbc.queryForList(
                "SELECT id, category, title, content, case_date, image FROM lw_case WHERE is_show = 'Y' ORDER BY id DESC LIMIT 3");
        model.addAttribute("cases", cases);

        // 情報分享 (首頁顯示最新 3 筆)
        List<Map<String, Object>> shares = jdbc.queryForList(
                "SELECT id, title, content, share_date, image FROM lw_share WHERE is_show = 'Y' ORDER BY id DESC LIMIT 3");
        model.addAttribute("shares", shares);

        // 律師 (首頁顯示)
        List<Map<String, Object>> attorneys = jdbc.queryForList(
                "SELECT id, name, title, photo, specialty FROM lw_attorney WHERE is_show = 'Y' ORDER BY sort_order ASC");
        model.addAttribute("attorneys", attorneys);

        // 網站設定
        loadSiteSettings(model);

        return "index";
    }

    /** 律師列表 */
    @GetMapping("/attorney.html")
    public String attorney(Model model) {
        List<Map<String, Object>> attorneys = jdbc.queryForList(
                "SELECT id, name, title, photo, specialty, education, experience, description FROM lw_attorney WHERE is_show = 'Y' ORDER BY sort_order ASC");
        model.addAttribute("attorneys", attorneys);
        loadSiteSettings(model);
        return "attorney";
    }

    /** 律師詳細 */
    @GetMapping("/attorney/{id}.html")
    public String attorneyDetail(@PathVariable Integer id, Model model) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, name, title, photo, specialty, education, experience, description FROM lw_attorney WHERE id = ? AND is_show = 'Y'", id);
        if (rows.isEmpty()) {
            return "redirect:/attorney.html";
        }
        model.addAttribute("attorney", rows.get(0));
        List<Map<String, Object>> all = jdbc.queryForList(
                "SELECT id, name, title, photo FROM lw_attorney WHERE is_show = 'Y' ORDER BY sort_order ASC");
        model.addAttribute("attorneys", all);
        loadSiteSettings(model);
        return "attorney-detail";
    }

    /** 業務領域 */
    @GetMapping("/service.html")
    public String service(Model model) {
        List<Map<String, Object>> services = jdbc.queryForList(
                "SELECT id, name, name_en, icon, description FROM lw_service WHERE is_show = 'Y' ORDER BY sort_order ASC");
        model.addAttribute("services", services);
        loadSiteSettings(model);
        return "service";
    }

    /** 案件實績列表 */
    @GetMapping("/case.html")
    public String caseList(Model model) {
        List<Map<String, Object>> cases = jdbc.queryForList(
                "SELECT id, category, title, content, case_date, image FROM lw_case WHERE is_show = 'Y' ORDER BY id DESC");
        cases.forEach(c -> c.put("summary", stripHtml((String) c.get("content"), 120)));
        model.addAttribute("cases", cases);
        loadSiteSettings(model);
        return "case";
    }

    /** 案件實績詳細 */
    @GetMapping("/case/{id}.html")
    public String caseDetail(@PathVariable Integer id, Model model) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, category, title, content, case_date, image FROM lw_case WHERE id = ? AND is_show = 'Y'", id);
        if (rows.isEmpty()) {
            return "redirect:/case.html";
        }
        model.addAttribute("item", rows.get(0));
        List<Map<String, Object>> all = jdbc.queryForList(
                "SELECT id, category, title, case_date FROM lw_case WHERE is_show = 'Y' ORDER BY id DESC");
        model.addAttribute("cases", all);
        loadSiteSettings(model);
        return "case-detail";
    }

    /** 情報分享列表 */
    @GetMapping("/share.html")
    public String shareList(Model model) {
        List<Map<String, Object>> shares = jdbc.queryForList(
                "SELECT id, title, content, share_date, image FROM lw_share WHERE is_show = 'Y' ORDER BY id DESC");
        shares.forEach(s -> s.put("summary", stripHtml((String) s.get("content"), 120)));
        model.addAttribute("shares", shares);
        loadSiteSettings(model);
        return "share";
    }

    /** 情報分享詳細 */
    @GetMapping("/share/{id}.html")
    public String shareDetail(@PathVariable Integer id, Model model) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, title, content, share_date, image FROM lw_share WHERE id = ? AND is_show = 'Y'", id);
        if (rows.isEmpty()) {
            return "redirect:/share.html";
        }
        model.addAttribute("item", rows.get(0));
        List<Map<String, Object>> all = jdbc.queryForList(
                "SELECT id, title, share_date FROM lw_share WHERE is_show = 'Y' ORDER BY id DESC");
        model.addAttribute("shares", all);
        loadSiteSettings(model);
        return "share-detail";
    }

    /** 事務所概要 */
    @GetMapping("/about.html")
    public String about(Model model) {
        List<Map<String, Object>> attorneys = jdbc.queryForList(
                "SELECT id, name, title, photo, specialty FROM lw_attorney WHERE is_show = 'Y' ORDER BY sort_order ASC");
        model.addAttribute("attorneys", attorneys);
        loadSiteSettings(model);
        return "about";
    }

    /** 免費法律諮詢 */
    @GetMapping("/consultation.html")
    public String consultation(Model model) {
        loadSiteSettings(model);
        return "consultation";
    }

    /** 404 */
    @GetMapping("/404.html")
    public String notFound() {
        return "404";
    }

    /** 舊路徑重導向（無 .html 後綴 → 有 .html 後綴） */
    @GetMapping("/attorney")
    public String attorneyRedirect() { return "redirect:/attorney.html"; }

    @GetMapping("/service")
    public String serviceRedirect() { return "redirect:/service.html"; }

    @GetMapping("/case")
    public String caseRedirect() { return "redirect:/case.html"; }

    @GetMapping("/share")
    public String shareRedirect() { return "redirect:/share.html"; }

    @GetMapping("/about")
    public String aboutRedirect() { return "redirect:/about.html"; }

    @GetMapping("/consultation")
    public String consultationRedirect() { return "redirect:/consultation.html"; }

    @GetMapping("/attorney/{id}")
    public String attorneyDetailRedirect(@PathVariable Integer id) { return "redirect:/attorney/" + id + ".html"; }

    @GetMapping("/case/{id}")
    public String caseDetailRedirect(@PathVariable Integer id) { return "redirect:/case/" + id + ".html"; }

    @GetMapping("/share/{id}")
    public String shareDetailRedirect(@PathVariable Integer id) { return "redirect:/share/" + id + ".html"; }

    /** 載入網站基本設定 */
    private void loadSiteSettings(Model model) {
        List<Map<String, Object>> settings = jdbc.queryForList(
                "SELECT site_key, site_value FROM lw_site");
        Map<String, String> siteMap = new java.util.HashMap<>();
        for (Map<String, Object> row : settings) {
            siteMap.put((String) row.get("site_key"), (String) row.get("site_value"));
        }
        model.addAttribute("site", siteMap);
    }

    /** 移除 HTML 標籤並截取摘要 */
    private String stripHtml(String html, int maxLen) {
        if (html == null || html.isBlank()) return "";
        String text = html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + "…";
    }
}
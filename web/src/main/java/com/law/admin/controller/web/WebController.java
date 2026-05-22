package com.law.admin.controller.web;

import com.law.admin.service.CaseService;
import com.law.admin.service.ShareService;
import com.law.admin.service.WebCommonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 前台網頁控制器 — 使用 Thymeleaf 動態渲染
 * 所有前台路由統一使用 .html 後綴（SEO 友善）
 */
@Controller
public class WebController {

    private final CaseService caseService;
    private final ShareService shareService;
    private final WebCommonService webCommonService;

    public WebController(CaseService caseService,
                        ShareService shareService,
                        WebCommonService webCommonService) {
        this.caseService = caseService;
        this.shareService = shareService;
        this.webCommonService = webCommonService;
    }

    /** 首頁 */
    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        model.addAttribute("banners", webCommonService.getAllBanners());
        model.addAttribute("cases", caseService.getTopCases(3));
        model.addAttribute("shares", shareService.getTopShares(3));
        model.addAttribute("attorneys", webCommonService.getAllAttorneysSimple());
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "index";
    }

    /** 律師列表 */
    @GetMapping("/attorney.html")
    public String attorney(Model model) {
        model.addAttribute("attorneys", webCommonService.getAllAttorneys());
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "attorney";
    }

    /** 律師詳細 */
    @GetMapping("/attorney/{id}.html")
    public String attorneyDetail(@PathVariable Integer id, Model model) {
        Map<String, Object> attorney = webCommonService.getAttorneyDetail(id);
        if (attorney == null) {
            return "redirect:/attorney.html";
        }
        model.addAttribute("attorney", attorney);
        model.addAttribute("attorneys", webCommonService.getAllAttorneysSimple());
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "attorney-detail";
    }

    /** 業務領域 */
    @GetMapping("/service.html")
    public String service(Model model) {
        model.addAttribute("services", webCommonService.getAllServices());
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "service";
    }

    /** 案件實績列表 */
    @GetMapping("/case.html")
    public String caseList(@RequestParam(defaultValue = "1") int page, Model model) {
        Map<String, Object> result = caseService.getCaseListWithPage(page, 6);
        model.addAllAttributes(result);
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "case";
    }

    /** 案件實績詳細 */
    @GetMapping("/case/{id}.html")
    public String caseDetail(@PathVariable Integer id, Model model) {
        Map<String, Object> caseItem = caseService.getCaseDetail(id);
        if (caseItem == null) {
            return "redirect:/case.html";
        }
        model.addAttribute("item", caseItem);
        model.addAttribute("cases", caseService.getAllCasesSimple());
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "case-detailed";
    }

    /** 情報分享列表 */
    @GetMapping("/share.html")
    public String shareList(@RequestParam(defaultValue = "1") int page, Model model) {
        Map<String, Object> result = shareService.getShareListWithPage(page, 6);
        model.addAllAttributes(result);
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "share";
    }

    /** 情報分享詳細 */
    @GetMapping("/share/{id}.html")
    public String shareDetail(@PathVariable Integer id, Model model) {
        Map<String, Object> shareItem = shareService.getShareDetail(id);
        if (shareItem == null) {
            return "redirect:/share.html";
        }
        model.addAttribute("item", shareItem);
        model.addAttribute("shares", shareService.getAllSharesSimple());
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "share-detailed";
    }

    /** 事務所概要 */
    @GetMapping("/about.html")
    public String about(Model model) {
        model.addAttribute("attorneys", webCommonService.getAllAttorneysSimple());
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "about";
    }

    /** 免費法律諮詢 */
    @GetMapping("/consultation.html")
    public String consultation(Model model) {
        model.addAttribute("site", webCommonService.getSiteSettings());
        return "consultation";
    }

    /** 404 */
    @GetMapping("/404.html")
    public String notFound() {
        return "404-page";
    }

    /** 500 */
    @GetMapping("/500.html")
    public String serverError() {
        return "500-page";
    }
}

package com.law.admin.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA 前端路由支援：所有 /admin/*.html 請求（除了 /admin/index.html）
 * 都 forward 到 /admin/index.html，讓 Vue Router 在客戶端處理路由。
 * 
 * /admin/index.html 不在此 mapping 中，因為它是實際存在的靜態檔案，
 * Spring Boot 的靜態資源處理器會直接提供服務。
 */
@Controller
public class SpaForwardController {

    @RequestMapping(value = {
        "/admin/login.html",
        "/admin/home.html",
        "/admin/attorney.html",
        "/admin/service.html",
        "/admin/case.html",
        "/admin/share.html",
        "/admin/consultation.html",
        "/admin/banner.html",
        "/admin/site.html",
        "/admin/admin-user.html"
    })
    public String forwardAdminRoutes() {
        return "forward:/admin/index.html";
    }
}
package com.law.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HtmlController {

    /**
     * 後台 SPA 路由
     * /admin 首頁與 /admin/*.html 頁面都轉發到 /admin/index.html
     */
    @GetMapping(value = { "/admin", "/admin/", "/admin/{path:(?!index)(?:.*)}.html" })
    public String spa(HttpServletRequest request) {
        log.debug("轉發後台 SPA 路由到 /admin/index.html");
        return "forward:/admin/index.html";
    }
}
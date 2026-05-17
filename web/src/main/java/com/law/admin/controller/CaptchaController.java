package com.law.admin.controller;

import com.wf.captcha.SpecCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);

        HttpSession session = request.getSession(true);
        session.setAttribute("captcha_code", captcha.text().toLowerCase());

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");
        captcha.out(response.getOutputStream());
    }
}
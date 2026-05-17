package com.law.admin.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 覆寫 Spring Boot 預設的錯誤處理：
 * 1. multipart stream 解析失敗 → 400 Bad Request（而非 500）
 * 2. API 404/401/403 → JSON 回應
 * 3. 其他錯誤 → 適當的狀態碼與回應
 */
@RestController
public class CustomErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        // multipart stream 解析異常（客戶端中斷/Cloudflare 截斷等）→ 回傳 400
        if (throwable != null) {
            String msg = throwable.getMessage();
            if (msg != null && (msg.contains("Stream ended unexpectedly")
                    || msg.contains("MultipartStream")
                    || msg.contains("IOFileUploadException"))) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                return "{\"code\":\"BAD_REQUEST\",\"msg\":\"請求資料不完整，請重新嘗試\"}";
            }
        }

        // 設定狀態碼
        if (statusCode != null) {
            response.setStatus(statusCode);
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        // JSON 回應（API 或 AJAX 請求）
        String accept = request.getHeader("Accept");
        boolean isApi = requestUri != null && requestUri.startsWith("/api/");
        boolean wantsJson = isApi || (accept != null && accept.contains("application/json"));

        if (wantsJson) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            if (statusCode != null && statusCode == 401) {
                return "{\"code\":\"UNAUTHORIZED\",\"msg\":\"請先登入\"}";
            } else if (statusCode != null && statusCode == 403) {
                return "{\"code\":\"FORBIDDEN\",\"msg\":\"無存取權限\"}";
            }
            return "{\"code\":\"ERROR\",\"msg\":\"系統錯誤\"}";
        }

        // 非 JSON 請求回傳簡單文字
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        return "Error " + statusCode;
    }
}
package com.law.admin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * 攔截 malformed multipart 請求，避免 Tomcat 拋出 InvalidParameterException
 * 導致 500 Internal Server Error。
 * 
 * 原因：Cloudflare proxy 或惡意爬蟲可能發送 Content-Type: multipart/form-data
 * 但 body 為空或格式錯誤的請求，Tomcat 嘗試解析時會拋出
 * IOFileUploadException / MalformedStreamException，進而包裝成
 * InvalidParameterException。這個異常發生在 CsrfFilter 呼叫
 * request.getParameter() 時，比 @RestControllerAdvice 更早，
 * 因此需要用 Filter 在最上層攔截。
 *
 * 解法：用 SafeMultipartRequestWrapper 包裝 request，
 * 在 getParameter() / getParameterMap() 等方法中 catch
 * InvalidParameterException，回傳 null / empty，讓請求正常流轉。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MultipartExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 只對帶有 multipart Content-Type 的請求做包裝
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            filterChain.doFilter(new SafeMultipartRequestWrapper(request), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 包裝 HttpServletRequest，在 multipart 解析失敗時回傳安全的預設值，
     * 而非讓 InvalidParameterException 一直冒泡到 ErrorPageFilter 變成 500。
     */
    static class SafeMultipartRequestWrapper extends HttpServletRequestWrapper {

        SafeMultipartRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            try {
                return super.getParameter(name);
            } catch (InvalidParameterException e) {
                // multipart 解析失敗，回傳 null（等同於找不到該參數）
                return null;
            }
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            try {
                return super.getParameterMap();
            } catch (InvalidParameterException e) {
                return Collections.emptyMap();
            }
        }

        @Override
        public Enumeration<String> getParameterNames() {
            try {
                return super.getParameterNames();
            } catch (InvalidParameterException e) {
                return Collections.emptyEnumeration();
            }
        }

        @Override
        public String[] getParameterValues(String name) {
            try {
                return super.getParameterValues(name);
            } catch (InvalidParameterException e) {
                return null;
            }
        }
    }
}
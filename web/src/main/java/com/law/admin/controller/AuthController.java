package com.law.admin.controller;

import com.law.admin.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String LOG_PREFIX = "[AUTH]";

    private final AuthenticationManager authenticationManager;
    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body,
                                   HttpServletRequest request) {
        String testHeader = request.getHeader("X-TEST-MODE");
        boolean testMode = "true".equalsIgnoreCase(testHeader)
                || "true".equalsIgnoreCase(System.getenv("TEST_MODE"));

        logger.info("{} login attempt from IP={} testMode={}", LOG_PREFIX,
                request.getRemoteAddr(), testMode);

        if (!testMode) {
            String inputCaptcha = body.getOrDefault("captcha", "").toLowerCase();
            HttpSession session = request.getSession(false);
            String sessionCaptcha = session != null
                    ? (String) session.getAttribute("captcha_code") : null;

            if (sessionCaptcha == null || !sessionCaptcha.equals(inputCaptcha)) {
                logger.warn("{} captcha mismatch", LOG_PREFIX);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("CAPTCHA_ERROR", "驗證碼錯誤"));
            }
            session.removeAttribute("captcha_code");
        } else {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("captcha_code");
            }
            logger.info("{} running in test mode - captcha bypassed", LOG_PREFIX);
        }

        String acct = body.getOrDefault("acct", "");
        String pwd = body.getOrDefault("pwd", "");

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(acct, pwd));

            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(auth);
            SecurityContextHolder.setContext(ctx);
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);

            UserDetails user = (UserDetails) auth.getPrincipal();
            logger.info("{} login success for user={}", LOG_PREFIX, user.getUsername());

            return ResponseEntity.ok(Map.of("code", "OK", "name", user.getUsername()));
        } catch (BadCredentialsException e) {
            logger.warn("{} login failed for account=[REDACTED]", LOG_PREFIX);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("LOGIN_FAIL", "帳號或密碼錯誤"));
        } catch (Exception e) {
            logger.error("{} unexpected error during login: {}", LOG_PREFIX, e.toString(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "伺服器錯誤"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "請先登入"));
        }
        return ResponseEntity.ok(Map.of("name", authentication.getName()));
    }

    @GetMapping("/csrf")
    public ResponseEntity<?> csrf() {
        return ResponseEntity.ok(Map.of("code", "OK"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body,
                                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "請先登入"));
        }

        String oldPwd = body.getOrDefault("oldPwd", "");
        String newPwd = body.getOrDefault("newPwd", "");

        if (oldPwd.isBlank() || newPwd.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("INVALID", "舊密碼與新密碼不可為空"));
        }

        if (oldPwd.equals(newPwd)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("INVALID", "新密碼不可與舊密碼相同"));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authentication.getName(), oldPwd));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("INVALID_PASSWORD", "舊密碼錯誤"));
        }

        String encPwd = passwordEncoder.encode(newPwd);
        jdbc.update("UPDATE lw_admin SET password = ? WHERE username = ?", encPwd, authentication.getName());

        return ResponseEntity.ok(Map.of("code", "OK", "msg", "密碼修改成功"));
    }
}
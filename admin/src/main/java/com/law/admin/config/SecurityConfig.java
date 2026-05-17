package com.law.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.law.admin.service.AdminUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminUserDetailsService userDetailsService;

    public SecurityConfig(AdminUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfRepo.setCookieName("XSRF-TOKEN");
        csrfRepo.setHeaderName("X-CSRF-TOKEN");
        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/csrf", "/api/captcha/**", "/api/consultation/public/**").permitAll()
                // 前台頁面（Thymeleaf 路由）
                .requestMatchers("/", "/attorney", "/attorney/*", "/service", "/case", "/case/*", "/share", "/share/*", "/about", "/consultation", "/404").permitAll()
                // 靜態資源
                .requestMatchers("/admin", "/admin/", "/admin/login.html", "/admin/index.html", "/admin/assets/**").permitAll()
                .requestMatchers("/*.html", "/assets/**", "/uploads/**", "/css/**", "/js/**", "/img/**", "/fonts/**", "/search/**", "/favicon.ico", "/apple-touch-icon*.png").permitAll()
                .requestMatchers("/api/dev/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepo)
                .csrfTokenRequestHandler(csrfHandler)
                .ignoringRequestMatchers("/api/auth/login", "/api/auth/csrf", "/api/captcha/**", "/api/consultation/public/**")
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredSessionStrategy(event -> {
                    var resp = event.getResponse();
                    resp.setStatus(HttpStatus.UNAUTHORIZED.value());
                    resp.setContentType("application/json;charset=UTF-8");
                    resp.getWriter().write("{\"code\":\"SESSION_EXPIRED\",\"msg\":\"已在其他裝置登入，請重新登入\"}");
                })
            )
            .formLogin(form -> form.disable())
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    res.setStatus(HttpStatus.OK.value());
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"code\":\"OK\",\"msg\":\"已登出\"}");
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    String accept = req.getHeader("Accept");
                    String requestUri = req.getRequestURI();
                    boolean isHtmlRequest = (accept != null && accept.contains("text/html"))
                        || requestUri.endsWith(".html")
                        || requestUri.equals(req.getContextPath() + "/")
                        || requestUri.equals(req.getContextPath());
                    if (isHtmlRequest) {
                        res.sendRedirect(req.getContextPath() + "/admin/login.html");
                    } else {
                        res.setStatus(HttpStatus.UNAUTHORIZED.value());
                        res.setContentType("application/json;charset=UTF-8");
                        res.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"msg\":\"請先登入\"}");
                    }
                })
                .accessDeniedHandler((req, res, e) -> {
                    String accept = req.getHeader("Accept");
                    String requestUri = req.getRequestURI();
                    boolean isHtmlRequest = (accept != null && accept.contains("text/html"))
                        || requestUri.endsWith(".html")
                        || requestUri.equals(req.getContextPath() + "/")
                        || requestUri.equals(req.getContextPath());
                    if (isHtmlRequest) {
                        res.sendRedirect(req.getContextPath() + "/admin/login.html");
                    } else {
                        res.setStatus(HttpStatus.FORBIDDEN.value());
                        res.setContentType("application/json;charset=UTF-8");
                        res.getWriter().write("{\"code\":\"FORBIDDEN\",\"msg\":\"無存取權限\"}");
                    }
                })
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' https://static.cloudflareinsights.com; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: blob:; " +
                    "connect-src 'self' https://static.cloudflareinsights.com https://cloudflareinsights.com; " +
                    "font-src 'self' data:; " +
                    "object-src 'none'; " +
                    "frame-ancestors 'none'"
                ))
                .referrerPolicy(rp -> rp.policy(
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .frameOptions(fo -> fo.deny())
            );

        http.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }
}
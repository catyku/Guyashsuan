package com.law.admin.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbc;

    public AdminUserDetailsService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, username, password, is_enabled FROM lw_admin WHERE username = ?", username);

        if (rows.isEmpty()) {
            throw new UsernameNotFoundException("帳號不存在：" + username);
        }

        Map<String, Object> row = rows.get(0);
        String pwd = (String) row.get("password");
        String isEnabled = (String) row.get("is_enabled");

        if (!"Y".equals(isEnabled)) {
            throw new UsernameNotFoundException("帳號已停用：" + username);
        }

        return User.builder()
                .username(username)
                .password(pwd)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();
    }
}
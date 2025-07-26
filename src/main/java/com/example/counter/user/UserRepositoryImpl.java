package com.example.counter.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Objects;

public class UserRepositoryImpl implements UserRepositoryCustom {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int totalUsersCount() {
        Integer visits = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        return Objects.requireNonNullElse(visits, 0);
    }
} 
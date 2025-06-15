package com.example.counter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class VisitRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    Integer numVisits = 0;

    public void logVisit() {
        numVisits++;
        System.out.println("visisting");
    }

    public int getVisitCount() {
        Integer visits = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        return Objects.requireNonNullElse(visits, 0);
    }

}

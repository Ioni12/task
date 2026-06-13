package com.example.task.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountSequenceRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountSequenceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long nextAccountNumber() {
        return jdbcTemplate.queryForObject(
                "SELECT NEXTVAL('account_number_seq')", Long.class
        );
    }
}

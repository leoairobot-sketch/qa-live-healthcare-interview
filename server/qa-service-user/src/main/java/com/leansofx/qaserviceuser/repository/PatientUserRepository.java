package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.model.PatientUser;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PatientUserRepository {

    private final JdbcClient jdbcClient;

    public PatientUserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<PatientUser> findByUsername(String username) {
        return jdbcClient
                .sql("SELECT id, username, password_hash, name, created_at FROM patient_user WHERE username = :username")
                .param("username", username)
                .query((rs, rowNum) -> new PatientUser(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at").toLocalDateTime()))
                .optional();
    }

    public Optional<PatientUser> findById(Long id) {
        return jdbcClient
                .sql("SELECT id, username, password_hash, name, created_at FROM patient_user WHERE id = :id")
                .param("id", id)
                .query((rs, rowNum) -> new PatientUser(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at").toLocalDateTime()))
                .optional();
    }

    /**
     * 插入新用户，返回自增主键
     */
    public long insert(PatientUser user) {
        KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO patient_user (username, password_hash, name) VALUES (:username, :passwordHash, :name)")
                .param("username", user.username())
                .param("passwordHash", user.passwordHash())
                .param("name", user.name())
                .update(keyHolder);
        return keyHolder.getKey().longValue();
    }
}

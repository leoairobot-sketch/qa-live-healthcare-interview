package com.leansofx.qaserviceuser.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansofx.qaserviceuser.model.Doctor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DoctorRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public DoctorRepository(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    public List<Doctor> findAll() {
        return jdbcClient
                .sql("SELECT id, username, password, name, title, department, avatar, experience, specialties, is_active FROM doctor ORDER BY id")
                .query((rs, rowNum) -> new Doctor(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getString("department"),
                        rs.getString("avatar"),
                        rs.getString("experience"),
                        parseSpecialties(rs.getString("specialties")),
                        rs.getBoolean("is_active")))
                .list();
    }

    private List<String> parseSpecialties(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("specialties JSON 解析失败: " + json, e);
        }
    }
}

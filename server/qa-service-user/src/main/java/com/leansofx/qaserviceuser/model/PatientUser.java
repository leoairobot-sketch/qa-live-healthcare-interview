package com.leansofx.qaserviceuser.model;

import java.time.LocalDateTime;

/**
 * 问诊用户实体（数据库完整字段，含密码哈希——仅用于存储与校验，禁止直接对外返回）
 */
public record PatientUser(
        Long id,
        String username,
        String passwordHash,
        String name,
        LocalDateTime createdAt
) {
}

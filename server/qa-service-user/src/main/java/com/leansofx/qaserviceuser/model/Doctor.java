package com.leansofx.qaserviceuser.model;

import java.util.List;

/**
 * 医生实体（数据库完整字段，含密码——仅用于存储与内部处理，禁止直接对外返回）
 */
public record Doctor(
        String id,
        String username,
        String password,
        String name,
        String title,
        String department,
        String avatar,
        String experience,
        List<String> specialties,
        boolean isActive
) {
}

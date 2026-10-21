package com.leansofx.qaserviceuser.dto;

/**
 * 问诊用户认证相关 DTO：注册/登录请求与脱敏后的用户响应（不含密码哈希）
 */
public final class PatientAuthDtos {

    private PatientAuthDtos() {
    }

    public record RegisterRequest(String username, String password, String name) {
    }

    public record LoginRequest(String username, String password) {
    }

    public record PatientUserResponse(Long id, String username, String name) {
    }
}

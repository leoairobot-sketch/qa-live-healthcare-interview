package com.leansofx.qaserviceuser.dto;

import java.util.List;

/**
 * 医生列表接口响应（脱敏：不含 password）
 */
public record DoctorResponse(
        String id,
        String username,
        String name,
        String title,
        String department,
        String avatar,
        String experience,
        List<String> specialties,
        boolean isActive
) {
}

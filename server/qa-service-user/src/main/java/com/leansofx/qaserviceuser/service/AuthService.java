package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.PatientAuthDtos.PatientUserResponse;
import com.leansofx.qaserviceuser.dto.PatientAuthDtos.RegisterRequest;
import com.leansofx.qaserviceuser.dto.PatientAuthDtos.LoginRequest;
import com.leansofx.qaserviceuser.exception.ApiException;
import com.leansofx.qaserviceuser.model.PatientUser;
import com.leansofx.qaserviceuser.repository.PatientUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final String SESSION_KEY = "patientUserId";
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,32}$");

    private final PatientUserRepository patientUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PatientUserRepository patientUserRepository, PasswordEncoder passwordEncoder) {
        this.patientUserRepository = patientUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 注册并自动登录：校验 → 查重 → bcrypt 入库 → 建立会话
     */
    public PatientUserResponse register(RegisterRequest request, HttpSession session) {
        validateUsername(request.username());
        validatePassword(request.password());
        validateName(request.name());

        if (patientUserRepository.findByUsername(request.username()).isPresent()) {
            throw new ApiException(409, "用户名已被占用");
        }

        long id = patientUserRepository.insert(new PatientUser(
                null, request.username(), passwordEncoder.encode(request.password()), request.name(), null));
        session.setAttribute(SESSION_KEY, id);
        return toResponse(id);
    }

    /**
     * 登录：凭据校验通过后建立会话；用户不存在与密码错误使用统一文案
     */
    public PatientUserResponse login(LoginRequest request, HttpSession session) {
        if (isBlank(request.username()) || isBlank(request.password())) {
            throw new ApiException(400, "用户名和密码不能为空");
        }

        PatientUser user = patientUserRepository.findByUsername(request.username())
                .orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new ApiException(401, "用户名或密码错误");
        }

        session.setAttribute(SESSION_KEY, user.id());
        return toResponse(user.id());
    }

    /**
     * 会话身份查询：无会话或会话过期返回 401
     */
    public PatientUserResponse currentUser(HttpSession session) {
        Long id = sessionAttribute(session);
        if (id == null) {
            throw new ApiException(401, "未登录或会话已过期");
        }
        return toResponse(id);
    }

    /**
     * 登出：销毁会话（无会话时幂等）
     */
    public String logout(HttpSession session) {
        session.invalidate();
        return "已退出登录";
    }

    private Long sessionAttribute(HttpSession session) {
        return (Long) session.getAttribute(SESSION_KEY);
    }

    private PatientUserResponse toResponse(long id) {
        PatientUser user = patientUserRepository.findById(id)
                .orElseThrow(() -> new ApiException(401, "未登录或会话已过期"));
        return new PatientUserResponse(user.id(), user.username(), user.name());
    }

    private void validateUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new ApiException(400, "用户名需为3-32位字母、数字或下划线");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 64) {
            throw new ApiException(400, "密码长度需为6-64位");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 32) {
            throw new ApiException(400, "姓名需为2-32位");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

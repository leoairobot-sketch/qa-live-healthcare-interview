-- 问诊用户表：账号体系（需求3），密码仅存 bcrypt 哈希
CREATE TABLE IF NOT EXISTS patient_user (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(32)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    name          VARCHAR(32)  NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_patient_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

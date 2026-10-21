# Spec Delta

## Purpose

定义问诊用户（患者）的账号与认证能力：注册、用户名密码登录、会话身份查询与登出，以及密码与会话的安全约束。对齐 doctor-auth 的按角色组织方式，覆盖 qa-service-user 的 /api/auth/* 行为。

## ADDED Requirements

### Requirement: 问诊用户可注册账号
系统 SHALL 提供问诊用户注册接口 `POST /api/auth/register`：接受用户名（3–32 位字母/数字/下划线，全局唯一）、密码（6–64 位）与姓名（2–32 位）。注册成功 SHALL 返回 201 与 `{id, username, name}`，密码 MUST 以 bcrypt 哈希存储，且注册成功即建立会话（下发 JSESSIONID）。

#### Scenario: 合法输入注册成功
- **WHEN** 以 `{"username":"patient001","password":"123456","name":"张三"}` 调用注册接口
- **THEN** 返回 201 与用户信息并下发 JSESSIONID；数据库新增记录的密码字段为 bcrypt 哈希（`$2a$` 开头）

#### Scenario: 用户名已存在
- **WHEN** 以已注册的用户名再次调用注册接口
- **THEN** 返回 409 与「用户名已被占用」，数据库记录数不变

#### Scenario: 字段不合法
- **WHEN** 提交密码长度 5 位或姓名 1 位的注册请求
- **THEN** 返回 400，message 说明具体校验规则

### Requirement: 问诊用户可登录并建立会话
系统 SHALL 提供登录接口 `POST /api/auth/login`：凭据正确时返回 200、`{id, username, name}` 与 JSESSIONID Cookie（HttpOnly、SameSite=Lax），并建立服务端会话；会话 SHALL 在 30 分钟不活动后过期。凭据错误（用户不存在或密码错误）MUST 返回 401 且使用统一文案「用户名或密码错误」，不得区分两种错误。

#### Scenario: 正确的用户名和密码
- **WHEN** 以正确凭据调用登录接口
- **THEN** 返回 200、用户信息与有效 JSESSIONID Cookie

#### Scenario: 密码错误
- **WHEN** 以错误密码调用登录接口
- **THEN** 返回 401，提示「用户名或密码错误」

#### Scenario: 用户不存在
- **WHEN** 以未注册用户名调用登录接口
- **THEN** 返回 401，提示文案与密码错误场景完全一致

### Requirement: 会话内可查询当前登录用户
系统 SHALL 提供会话身份查询接口 `GET /api/auth/me`：携带有效会话返回 200 与 `{id, username, name}`；无会话或会话过期 MUST 返回 401。前端 SHALL 在应用刷新时调用该接口静默恢复登录态。

#### Scenario: 携带登录会话查询身份
- **WHEN** 登录成功后携带 JSESSIONID 调用该接口
- **THEN** 返回 200 与当前用户信息

#### Scenario: 无会话或会话过期
- **WHEN** 不带 Cookie（或会话已过期）调用该接口
- **THEN** 返回 401

### Requirement: 问诊用户可登出并使会话失效
系统 SHALL 提供登出接口 `POST /api/auth/logout`：销毁服务端会话，返回 200；对无有效会话的调用 SHALL 幂等返回 200。

#### Scenario: 登录后登出
- **WHEN** 登录后携带 Cookie 调用登出接口
- **THEN** 返回 200，原会话失效（以 GET /api/auth/me 返回 401 验证）

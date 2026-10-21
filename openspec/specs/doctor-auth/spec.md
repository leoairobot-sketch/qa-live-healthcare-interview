# Doctor Authentication Specification

## Purpose

定义医生端的身份认证行为：医生以用户名与密码登录，登录态决定能否进入对应诊室，以及医生端的登出行为。

## Requirements

### Requirement: 医生以用户名与密码登录
`/doctor/login` SHALL 提供必填的用户名与密码表单。凭证与内置医生数据匹配时登录成功并导航到 `/doctor/room/<该医生用户名>`；不匹配时 MUST 保持停留在登录页并提示「用户名或密码错误」。

#### Scenario: 登录成功进入诊室
- **WHEN** 医生输入正确凭证（如用户名 dr-zhang-wei 及对应密码）提交登录
- **THEN** 提示「登录成功」，浏览器导航到 `/doctor/room/dr-zhang-wei`

#### Scenario: 凭证错误
- **WHEN** 医生输入不存在的用户名或错误密码提交登录
- **THEN** 提示「用户名或密码错误」，停留在登录页且未建立登录态

#### Scenario: 必填项缺失
- **WHEN** 用户名或密码为空即提交
- **THEN** 表单显示必填校验提示，不发起登录

### Requirement: 登录页提供测试账号提示
登录页 SHALL 显示可关闭的提示信息，给出可用的测试账号（用户名与密码），便于演示与联调。

#### Scenario: 查看测试账号提示
- **WHEN** 用户访问 `/doctor/login`
- **THEN** 页面显示包含测试账号用户名与密码的提示框

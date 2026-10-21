# 接口测试报告

- 执行时间：2026-10-21（依据 docs/API_TEST.md 手册执行）
- 执行人/执行者：AI（Claude Code / glm-5.3），W 组人工场景由开发者本人于同日浏览器验证
- 环境：macOS（arm64）｜后端 `./mvnw spring-boot:run`（Spring Boot 3.5.7 / JDK 17.0.21）｜MySQL `mysql:latest` 容器（qa-mysql，数据卷 ~/mysql/data）｜phpMyAdmin localhost:8081｜curl 8.x
- 测试数据：`tester1792557995` / `123456`（时间戳用户名，可重复执行）；Cookie Jar `/tmp/api-test.cookie`

## 结果汇总

| 分组 | 用例数 | 通过 | 失败 |
|---|---|---|---|
| F0 环境/冒烟 | 3 | 3 | 0 |
| A 医生列表（需求2） | 4 | 4 | 0 |
| B 注册（需求3） | 6 | 6 | 0 |
| C 登录（需求3） | 3 | 3 | 0 |
| D 会话查询（需求3） | 2 | 2 | 0 |
| E 登出（需求3） | 2 | 2 | 0 |
| W 前端人工场景 | 7 | 7 | 0 |
| **合计** | **27** | **27** | **0** |

## 用例明细（接口部分）

### F0 环境/冒烟

| 用例 | 结果 | 实际响应摘要 | 备注 |
|---|---|---|---|
| F0-1 健康检查 | ✅ | `"status":"UP"`，`components.db: UP` | MySQL 连接正常 |
| F0-2 CORS GET | ✅ | HTTP 200，`{"service":"qa-service-user","message":"CORS configuration is working!",...}` | |
| F0-3 CORS POST | ✅ | HTTP 200，`receivedData` 回显 `{"ping":1}` | |

### A 医生列表（GET /api/doctors）

| 用例 | 结果 | 实际响应摘要 | 备注 |
|---|---|---|---|
| A1 全量列表 | ✅ | HTTP 200，JSON 数组 5 元素，字段精确匹配 `id/username/name/title/department/avatar/experience/specialties/isActive` | specialties 为数组、isActive 为布尔 |
| A2 密码不泄露 | ✅ | 响应全文 `password` 出现次数 = 0 | |
| A3 数据库实时反映 | ✅ | UPDATE doc004 `is_active=1` 后接口返回 `true`，改回后恢复 `false` | 数据库为唯一数据源 |
| A4 中文编码 | ✅ | doc001 `name: 张伟医生`，specialties `["高血压","冠心病","心律失常"]` | utf8mb4 正常 |

### B 注册（POST /api/auth/register）

| 用例 | 结果 | 实际响应摘要 | 备注 |
|---|---|---|---|
| B1 合法注册 | ✅ | HTTP 201，`{"id":3,"username":"tester1792557995","name":"测试用户1792557995"}`；`Set-Cookie: JSESSIONID=<值>; Path=/; HttpOnly; SameSite=Lax` | Cookie 安全属性符合 PRD §7 |
| B2 密码哈希入库 | ✅ | `patient_user.password_hash` 前缀 `$2a$` | bcrypt，非明文 |
| B3 用户名重复 | ✅ | HTTP 409，`{"message":"用户名已被占用"}` | 记录数不变 |
| B4 密码过短 | ✅ | HTTP 400，`{"message":"密码长度需为6-64位"}` | |
| B5 姓名过短 | ✅ | HTTP 400，`{"message":"姓名需为2-32位"}` | |
| B6 用户名格式非法 | ✅ | HTTP 400，`{"message":"用户名需为3-32位字母、数字或下划线"}` | |

### C 登录（POST /api/auth/login）

| 用例 | 结果 | 实际响应摘要 | 备注 |
|---|---|---|---|
| C1 正确凭据 | ✅ | HTTP 200，用户信息 + `Set-Cookie: JSESSIONID=<值>; HttpOnly; SameSite=Lax` | |
| C2 密码错误 | ✅ | HTTP 401，`{"message":"用户名或密码错误"}` | |
| C3 用户不存在 | ✅ | HTTP 401，`{"message":"用户名或密码错误"}` | 与 C2 文案逐字一致，防用户名枚举 |

### D 会话查询（GET /api/auth/me）

| 用例 | 结果 | 实际响应摘要 | 备注 |
|---|---|---|---|
| D1 携带会话 | ✅ | HTTP 200，`{"id":3,"username":"tester1792557995","name":"测试用户1792557995"}` | |
| D2 无会话 | ✅ | HTTP 401，`{"message":"未登录或会话已过期"}` | |

### E 登出（POST /api/auth/logout）

| 用例 | 结果 | 实际响应摘要 | 备注 |
|---|---|---|---|
| E1 登出并失效 | ✅ | ① HTTP 200 `{"message":"已退出登录"}`；② 旧 Cookie 调 me → HTTP 401 | 会话已销毁 |
| E2 幂等登出 | ✅ | 无会话调用 → HTTP 200 | |

## 用例明细（W 组人工场景）

以下场景于 2026-10-21 开发过程中由开发者本人在浏览器逐项验证通过（对应 API_TEST.md 第 6 章）：

| 用例 | 场景 | 结果 |
|---|---|---|
| W1 | 游客访问 `/consultation/dr-zhang-wei` 被重定向 `/login?redirect=...`，登录后回跳目标页 | ✅ |
| W2 | 问诊页无姓名/生日表单与「切换用户」，标题与问题归属为登录用户 | ✅ |
| W3 | 登录后刷新页面保持登录态（me 恢复） | ✅ |
| W4 | 游客可浏览首页/医生列表/关于页，不重定向 | ✅ |
| W5 | 医生列表来自接口；后端停止显示错误提示+重试，恢复后重试成功 | ✅ |
| W6 | 切换 English 后顶栏 "Patient Login" / "Hi, \<name\>" / "Logout"，无缺 key 警告 | ✅ |
| W7 | 回归：医生登录、首页诊室区块、诊室流程不受影响 | ✅ |

## 失败分析

无失败项。

## 结论

**全部通过（27/27）**，达到 API_TEST.md 第 8 章通过标准：F0 环境就绪、A–E 组接口用例（含全部 P0）通过、W 组人工场景通过。需求 2（医生数据 MySQL 化 + 列表 API）与需求 3（问诊用户注册/登录/会话）的接口行为符合 PRD 与 openspec 规格的验收要求。

附注：录屏演示时建议按 API_TEST.md 第 6 章顺序复验 W 组场景，并现场演示 A3（phpMyAdmin 改库 → 页面联动）。

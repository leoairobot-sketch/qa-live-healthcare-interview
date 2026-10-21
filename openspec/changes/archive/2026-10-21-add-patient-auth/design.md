# Design

## Context

PRD：`docs/PRD_PATIENT_AUTH.md`（已确认）。后端 qa-service-user 已具备 MySQL/Flyway/JdbcClient 分层（需求 2 交付），前端为 Vue3 + ant-design-vue + vue-router（无路由守卫）+ vue-i18n（外壳已本地化），问诊身份目前由 `store.verifyPatient`（姓名+生日）提供。本设计将 PRD 第 5/6/9 章落为技术方案。

## Goals / Non-Goals

**Goals:**

- `/api/auth/*` 四接口满足 PRD 8.1–8.4 全部 Scenario；bcrypt 密码、内存 Session、统一错误文案
- 前端登录/注册页 + 门禁回跳 + 顶栏登录态，满足 PRD 8.5–8.7
- 顶栏新元素双语，不破坏需求 1 的外壳本地化行为

**Non-Goals:**

- 不做 JWT、Session 持久化、找回密码、医生端改造（PRD 1.4 已锁定）
- 不迁移 `patient-user.json`/`question-list.json` 历史数据

## Decisions

### D1. 密码哈希：仅引入 `spring-security-crypto`

用其 `BCryptPasswordEncoder`（强度 10），不引入 `spring-boot-starter-security`——避免过滤链接管会话与 401 语义，保持手工 Session 管理的讲解链路最短。备选完整 Security starter：过度设计且会改变现有接口的鉴权行为。

### D2. 会话：Servlet 原生 HttpSession（内存）

`HttpSession` + 容器默认 JSESSIONID Cookie，配置 `server.servlet.session.cookie.http-only=true`、`same-site=lax`、`timeout=30m`。会话属性仅存 `patientUserId`（不存可变对象，读库取最新）。前端经 Vite 代理同源访问，`fetch` 默认 same-origin 即携带 Cookie，无需 `credentials` 配置。备选 Spring Session JDBC：PRD 已定为非目标。

### D3. 表与迁移：Flyway `V2__init_patient_user.sql`

沿用 V1 模式：`patient_user(id BIGINT AUTO_INCREMENT PK, username VARCHAR(32) UNIQUE NOT NULL, password_hash VARCHAR(100) NOT NULL, name VARCHAR(32) NOT NULL, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)`，utf8mb4。与 doctor 表的 VARCHAR 主键不同，用户表用自增 BIGINT——无业务编码需求，且注册接口天然回填。

### D4. 参数校验：service 层手写校验

仅 3 个字段、规则固定（PRD 5.2），手写校验直接产出 PRD 规定的中文 message，不引入 `spring-boot-starter-validation` 的注解-消息双层间接。校验失败返回 400 `{message}`。

### D5. 错误响应：沿用裸 JSON `{message}` + 语义状态码

400（校验）/ 401（凭据错误、未登录）/ 409（用户名占用）。与现有接口风格一致（design D5 of add-doctor-mysql-api），前端按状态码分支提示，无需错误码体系。控制器不抛业务异常栈，统一在 service 返回结果或抛出带 message 的响应映射异常，由 `@RestControllerAdvice` 转 JSON——新增一个最小 `GlobalExceptionHandler`。

### D6. 前端登录态：独立 `src/store/auth.ts` 模块

沿用项目自定义 store 模式（非 Pinia）：`patientUser` ref + `login/register/logout/fetchMe` actions（fetch 相对路径经 Vite 代理）。`App.vue` 挂载时调 `fetchMe()` 静默恢复（401 不提示）。`store/index.ts` 的 `verifyPatient`/`currentPatient` 从 Consultation.vue 摘除后保留定义不动（医生端无引用，避免大范围删除引入回归）。

### D7. 路由守卫：`router.beforeEach` 白名单拦截

守卫规则：目标以 `/consultation` 开头且 `auth.patientUser` 为空 → 重定向 `/login?redirect=<目标 fullPath>`。登录/注册成功后优先跳 `redirect` 参数（防开放重定向：仅接受以 `/` 开头且非 `//` 开头的值）。`/login` 已登录时访问直接跳首页。医生路由不拦截。

### D8. i18n：新增 nav 命名空间 key，登录页正文不本地化

`zh-CN.ts`/`en-US.ts` 增 `nav.patientLogin`、`nav.hello`（含 `{name}` 插值）、`nav.logout`——外壳元素必须双语（需求 1 行为约束）。登录/注册页正文与 Doctors.vue 等页面一致用中文（现有本地化范围仅首页+外壳，PRD 未扩展）。

### D9. Consultation.vue 身份区改造

模板：删除 `v-if="!currentPatient"` 验证表单区块与「切换用户」按钮；标题/数据源从 `store.state.currentPatient` 改为 `auth.patientUser`（id/name 映射 patientId/patientName）。登录由门禁保证，组件内不再处理未登录态。预选医生逻辑（doctorUsername 路由参数）不变。

## Risks / Trade-offs

- [开放重定向] → D7 的 redirect 白名单校验（仅站内相对路径）
- [内存 Session 重启丢失] → PRD 已接受；验收演示前重启后端需重新登录
- [BCrypt 每次登录 ~100ms] → 远低于 P95 500ms 指标
- [删除验证表单误伤医生端] → `verifyPatient` 仅 Consultation.vue 引用（已核实），保留 store 定义零风险
- [注册接口 409 可探测用户名] → PRD 11 章已列为可接受取舍

## Migration Plan

部署顺序不变：`docker compose up -d` → 后端（Flyway 自动执行 V2）→ 前端。回滚：还原代码提交；V2 为纯新增表，回滚无需删表（无存量依赖）。

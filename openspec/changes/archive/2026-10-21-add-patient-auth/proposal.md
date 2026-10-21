# Proposal

## Why

当前问诊用户的身份验证是前端本地「姓名 + 生日」比对（`patient-user.json`），无真实账号体系、可绕过、无会话。面试需求 3 要求为问诊用户提供用户名密码登录；已按 `docs/PRD_TEMPLATE.md` 编写并确认 `docs/PRD_PATIENT_AUTH.md` 作为本变更的需求基线（决议：注册+登录、服务端内存 Session、游客可浏览公开页、替换旧验证流程、注册字段为用户名/密码/姓名、bcrypt 存储）。

## What Changes

- 新增问诊用户账号体系：注册（注册即登录）、登录、登出、会话身份查询 4 个接口（`/api/auth/*`），密码 bcrypt 入库，服务端内存 Session + JSESSIONID Cookie
- MySQL 新增 `patient_user` 表（Flyway V2 迁移）
- 前端新增登录/注册页（`/login`，支持 `?redirect=` 回跳）与顶栏「问诊用户登录」入口、已登录用户菜单（你好/退出登录）
- 问诊门禁：未登录访问 `/consultation*` 重定向登录页，登录/注册成功后回跳
- **BREAKING**（前端行为）：移除问诊页「姓名 + 生日」验证表单与「切换用户」按钮，问诊患者身份（patientId/patientName）改由登录会话提供
- 顶栏新增元素接入中英语言文件（外壳本地化行为保持）

## Capabilities

### New Capabilities

- `patient-auth`: 问诊用户账号与认证能力——注册、登录与会话建立、会话身份查询、登出的全部对外行为（对齐既有 `doctor-auth` 的按角色划分认证能力的组织方式）

### Modified Capabilities

- `patient-consultation`: 问诊入口改为登录门禁——移除「姓名与生日验证身份」「切换用户」两条需求，新增「未登录重定向登录页并回跳」「问诊身份来自登录会话」，预选医生需求的验证措辞同步调整为登录
- `site-shell`: 路由表新增 `/login`；顶栏新增「问诊用户登录」入口与已登录态（你好 + 退出登录），医生登录入口保持不变

## Impact

- **后端 `server/qa-service-user`**：新增 `spring-security-crypto`（仅 bcrypt，不引入安全过滤链）；Flyway `V2__init_patient_user.sql`；auth 相关 controller/service/repository/dto；`PatientUser` 实体
- **前端 `web/qa-web`**：新增 `Login.vue` + `/login` 路由 + 全局路由守卫；新增 auth 状态模块（登录态、me 恢复）；`AppHeader.vue` 增加问诊用户入口与用户菜单；`Consultation.vue` 移除验证表单、身份改接 auth 状态；`store` 的 `verifyPatient` 及患者相关逻辑退役；`locales/zh-CN.ts`、`en-US.ts` 新增 nav 命名空间 key
- **数据库**：`qa_live.patient_user` 新表（username 唯一索引）
- **PRD 溯源**：specs 场景与 PRD 第 8 章验收标准一一对应（8.1–8.4 → patient-auth；8.5/8.7 → patient-consultation；导航入口 → site-shell）
- **不受影响**：医生登录/诊室流程、需求 1 本地化既有行为、需求 2 医生列表 API

# Tasks

## 1. 后端数据层（PRD T-1）

- [x] 1.1 创建 `V2__init_patient_user.sql`（design D3：BIGINT 自增主键、username 唯一索引、password_hash、name、created_at，utf8mb4）；启动服务验证 Flyway 执行 V2 成功、phpMyAdmin 中 `patient_user` 表结构与唯一索引可见
- [x] 1.2 新增 `PatientUser` 实体与 `PatientUserRepository`（JdbcClient：按 username 查询、插入）；以插入+查询一次的临时验证（日志或后续接口联测）确认读写正常、bcrypt 字段原样存取

## 2. 后端 auth 接口（PRD T-2）

- [x] 2.1 `pom.xml` 新增 `spring-security-crypto`；`application.properties` 配置会话 Cookie（HttpOnly、SameSite=Lax、30 分钟超时，design D2）；`./mvnw dependency:resolve` 通过
- [x] 2.2 实现 `AuthService` + `AuthController` + DTO：register/login/logout/me 四接口，含 service 层手写校验（design D4）、BCryptPasswordEncoder、HttpSession 会话、统一 `{message}` 错误与 400/401/409 状态码（design D5，GlobalExceptionHandler）；`./mvnw compile` 通过
- [x] 2.3 curl 按 PRD 8.1–8.4 逐条验证：注册 201/409/400、密码哈希 `$2a$` 开头、登录 200+Cookie/401 统一文案×2、me 200/401、logout 后 me 401（Cookie 用 `-c`/`-b` 维护）

## 3. 前端 auth 基础（PRD T-3）

- [x] 3.1 新增 `src/store/auth.ts`（patientUser 状态 + login/register/logout/fetchMe，fetch 相对路径）；`App.vue` 挂载时调用 fetchMe 静默恢复登录态；`npm run build` 通过
- [x] 3.2 新增 `Login.vue`（登录/注册双表单切换，字段校验与 PRD 4.2 提示文案一致，成功后跳 redirect 参数或首页）并注册 `/login` 路由；浏览器完成注册→自动登录→登出→再登录流程
- [x] 3.3 `locales/zh-CN.ts` 与 `en-US.ts` 同步新增 `nav.patientLogin`/`nav.hello`/`nav.logout` key（中英对齐，hello 含 {name} 插值）；切换 English 验证无缺 key 报错

## 4. 顶栏入口与已登录态（PRD T-3）

- [x] 4.1 `AppHeader.vue` 右侧（医生登录按钮旁）加「问诊用户登录」按钮；已登录态显示「你好，<姓名>」下拉菜单含「退出登录」（调 logout 后清态回首页）；文案全部走 t() 双语；浏览器验证游客态/已登录态切换、EN 文案正确（PRD F-2/F-3）

## 5. 门禁与问诊改造（PRD T-4）

- [x] 5.1 `router/index.ts` 添加 `beforeEach` 守卫：`/consultation*` 未登录重定向 `/login?redirect=<fullPath>`，redirect 值校验仅站内相对路径（design D7）；`/login` 已登录访问跳首页；浏览器验证游客直开 `/consultation/dr-zhang-wei` 被重定向、登录后回跳
- [x] 5.2 `Consultation.vue` 移除姓名生日验证表单与「切换用户」按钮，患者身份（标题、问题归属 patientId/patientName）改接 `auth.patientUser`（design D9）；浏览器验证：已登录直入「<姓名> 的问诊」、提交问题归属登录用户、预选医生行为不变
- [x] 5.3 `npm run build` 通过；回归验证医生登录、医生诊室、首页/医生列表游客浏览不受影响

## 6. 端到端验收（PRD T-5）

- [x] 6.1 对照 PRD 第 8 章 8.1–8.7 与本变更三份 spec delta 逐条自测（接口 curl + 前端人工），记录验证结果；冷启动顺序演练（compose → 后端 → 前端）

## Workflow follow-up

- 全部任务完成后，按项目评审要求归档 change（`/opsx:archive`）
- curl 测试用例与结果汇入 /docs/API_TEST.md 与测试报告（面试交付物，统一整理）

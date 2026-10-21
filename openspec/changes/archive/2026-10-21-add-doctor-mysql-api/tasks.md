# Tasks

## 1. 后端依赖与数据源配置

- [x] 1.1 `qa-service-user/pom.xml` 新增 `spring-boot-starter-jdbc`、`flyway-core`、`flyway-mysql`、`mysql-connector-j`（版本由 Boot 3.5.7 托管），`./mvnw dependency:resolve` 验证依赖可解析
- [x] 1.2 `application.properties` 新增数据源配置：JDBC URL `jdbc:mysql://localhost:3306/qa_live?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8`、用户 root、密码 123456；启动服务验证 `qa_live` 库自动创建（phpMyAdmin 可见）

## 2. 库表迁移与数据访问层

- [x] 2.1 创建 `src/main/resources/db/migration/V1__init_doctor.sql`：按 design D7 建表（utf8mb4，specialties 为 JSON 列），INSERT 5 名医生种子数据（与 `doctor-user-list.json` 逐字段一致，含离线 doc004）；启动服务验证 Flyway 执行成功、phpMyAdmin 中 `doctor` 表有 5 行
- [x] 2.2 重启服务验证 `flyway_schema_history` 记录 V1 且 `doctor` 表仍为 5 行（幂等，spec「重复启动不产生重复数据」）
- [x] 2.3 新增 `Doctor` 实体（含 password）与 `DoctorRepository`（JdbcClient 查全表，specialties 以 String 读取后经 Jackson 转 `List<String>`），暂以一次性验证（如临时测试或日志）确认查询返回 5 条且字段完整

## 3. 医生列表 API

- [x] 3.1 新增 `DoctorResponse` DTO（无 password，`isActive` 为 boolean）与 `DoctorController`：`GET /api/doctors` 返回脱敏列表（design D4/D5）；`curl -s http://localhost:8080/api/doctors | jq` 验证返回 5 个元素、字段齐全（id/username/name/title/department/avatar/experience/specialties/isActive）且无任何 password 键
- [x] 3.2 phpMyAdmin 将 doc004 的 `is_active` 改为 1 后再次 curl，验证 `isActive` 变为 true（改回后恢复 false），确认数据库为唯一数据源

## 4. 前端 Doctors.vue 接入接口

- [x] 4.1 `vite.config.ts` 添加 dev 代理：`/api` → `http://localhost:8080`；`npm run dev` 启动无报错
- [x] 4.2 `Doctors.vue` 改为 `onMounted` 中 `fetch('/api/doctors')` 获取医生列表（相对路径），移除对 `store.state.doctors` 的依赖；本地定义展示用 Doctor 类型（不含 password）；浏览器验证 `/doctors` 渲染 5 张卡片、字段与 JSON 时代一致（spec「全量展示」场景）
- [x] 4.3 加载态与失败态：请求期间显示 `a-spin`；失败时显示错误提示与「重试」按钮，重试成功后正常渲染（停掉后端访问 `/doctors` 验证失败态，重启后端点重试验证恢复）
- [x] 4.4 `npm run build`（含 vue-tsc 类型检查）通过；同时验证医生登录、首页诊室区块仍走本地 JSON 正常（范围限定不被破坏）

## 5. 端到端联调验证

- [x] 5.1 按 `docker compose up -d` → 后端 → 前端的启动顺序完整走一遍冷启动：phpMyAdmin（localhost:8081）改库 → 刷新 `/doctors` 页面数据联动（spec「接口数据变化反映到页面」场景）
- [x] 5.2 对照 specs 两份 delta 逐条自测（列表字段、密码不泄露、种子初始化、幂等、加载/失败态、其他页面不受影响），记录验证结果

## Workflow follow-up

- 全部任务完成后，按项目评审要求归档 change（`/opsx:archive`）
- 面试交付物（curl 测试脚本化、/docs/API_TEST.md、测试报告、prompts.md、视频）在本需求之外统一整理

# Proposal

## Why

当前医生数据以 JSON 文件形式内嵌在前端（`web/qa-web/src/data/doctor-user-list.json`），数据不可管理、无后端支撑。面试需求 2 要求将医生相关数据迁移到 MySQL 存储，并由 `qa-service-user` 提供支撑医生团队页（`Doctors.vue`）的 API，完成前后端联调。基础设施（docker-compose：MySQL + phpMyAdmin）已在本变更前就绪并运行。

## What Changes

- 新增 `GET /api/doctors` 医生列表接口：返回全部医生（含离线），**不返回 password 字段**，数据来源为 MySQL 数据库（含 5 名医生的初始种子数据）
- `qa-service-user` 引入 MySQL 访问能力（JDBC 驱动 + 持久层依赖）、库表初始化与种子数据机制
- `Doctors.vue` 数据源从本地 JSON 改为调用后端 API：新增加载态与请求失败态的页面处理
- 基础设施 `docker-compose.yml`（MySQL 3306 + phpMyAdmin 8081，自定义网络 `qa-net`，数据挂载 `~/mysql/data`）随本变更一并纳入版本管理
- **范围限定**：仅 `Doctors.vue` 改为 API 数据源；首页诊室区块、医生登录等其余医生数据消费方继续使用本地 JSON（面试 README 字面范围）。前端 JSON 文件保留

## Capabilities

### New Capabilities

（无——本变更的行为均归属既有 capability）

### Modified Capabilities

- `user-service-api`: 新增需求——服务提供医生列表 API：`GET /api/doctors` 返回 MySQL 中全部医生（不含密码），数据库预置 5 名医生种子数据；服务新增 MySQL 依赖与库表
- `doctor-directory`: 需求变更——医生团队页数据源从内置 JSON 改为调用 `qa-service-user` 的 `GET /api/doctors`；页面需处理数据加载中与请求失败两种状态；卡片字段展示与进入诊室控制行为保持不变

## Impact

- **后端 `server/qa-service-user`**：`pom.xml` 新增 `spring-boot-starter-jdbc`（或等价持久层）与 `mysql-connector-j`；`application.properties` 新增数据源配置（`localhost:3306`，root/123456）；新增 Doctor 实体/仓库/控制器及库表初始化脚本
- **前端 `web/qa-web`**：`src/views/Doctors.vue` 改为 API 拉取数据（原生 fetch），新增加载/失败 UI；`Doctor` 展示类型不再要求 `password` 字段
- **数据库**：MySQL 新建库（如 `qa_live`）与 `doctor` 表，灌入 5 条医生数据（与现有 JSON 一致）
- **运行依赖**：联调需先启动 docker-compose（MySQL），再启动后端（8080）与前端 dev server；CORS 已全量放行，无需调整
- **不受影响**：医生登录（仍走前端 JSON 校验）、首页、问诊/诊室流程、已完成的 i18n 能力

# Design

## Context

qa-service-user 是 Spring Boot 3.5.7 / Java 17 单模块服务，现仅有 web + actuator 依赖、一个 CORS 测试控制器，无任何持久层。前端 vue3 + ant-design-vue，无 HTTP 客户端库，医生数据硬编码于 `src/data/doctor-user-list.json` 并经 `store/index.ts` 全局分发。

基础设施已就绪：docker-compose 提供 MySQL（宿主机 3306，root/123456，数据挂载 `~/mysql/data`）与 phpMyAdmin（宿主机 8081）。后端端口 8080 无冲突。

## Goals / Non-Goals

**Goals:**

- `GET /api/doctors` 返回 MySQL 中全部医生，不含密码，字段与前端 `Doctor` 展示需求一致
- 库表与种子数据自动初始化，可重复执行（幂等）
- `Doctors.vue` 改为接口数据源，具备加载态与失败重试

**Non-Goals:**

- 医生登录接口化、密码加密存储（需求 3 范围，当前明文与原 JSON 一致）
- 首页诊室区块等其他 JSON 消费方迁移（已确认仅 Doctors.vue）
- 医生数据的增删改管理接口、分页与筛选

## Decisions

### D1. 持久层：spring-boot-starter-jdbc + JdbcClient

单表、单查询场景。JdbcClient（Spring 6.1+）以最少代码完成「查全表 → 映射 DTO」，无 XML、无实体注解、零学习成本，面试讲解链路最短。

- 备选 MyBatis-Plus：国内常见，但为 1 个只读接口引入框架不划算
- 备选 Spring Data JPA：仅做全表查询时反向映射负担更重

### D2. 库表初始化：Flyway 版本化迁移

`flyway-core` + `flyway-mysql`，`V1__init_doctor.sql` 一次完成建库外的一切（建表 + 5 条种子 INSERT）。幂等性由 `flyway_schema_history` 天然保证，满足 spec「重复启动不产生重复数据」；版本化迁移是行业标准实践，后续需求 3（用户表）可平滑追加 V2。

- 备选 `spring.sql.init`（schema.sql/data.sql）：零额外依赖，但幂等要手工写 `IF NOT EXISTS`/条件插入，演进性差
- 备选 phpMyAdmin 手工导入：不可复现，排除

注：数据库 `qa_live` 本身由 `application.properties` 的 JDBC URL `createDatabaseIfNotExist=true` 创建，Flyway 只管表。

### D3. specialties 存储：MySQL JSON 列

`specialties JSON NOT NULL`，Java 侧按 String 读写，DTO 组装时经 Jackson 转为 `List<String>`。保序、结构化、无额外表。备选逗号分隔 VARCHAR（丢结构）与关联表（5 条数据过度设计）。

### D4. 脱敏：实体与 DTO 分离

`Doctor` 实体（含 password）仅存在于仓库层；控制器返回 `DoctorResponse` DTO，无 password 字段——密码不出现在任何序列化路径。手写映射，不引 MapStruct。

### D5. 响应格式：裸 JSON 数组

与现有 `TestController` 风格一致（直接返回对象/集合），前端零解包。不引入 `{code,message,data}` 包装——当前无全局异常与错误码体系，包装是空转。

### D6. 前端请求：原生 fetch + Vite dev 代理

`Doctors.vue` 内 `onMounted` 用 `fetch('/api/doctors')`（相对路径），由 `vite.config.ts` 的 dev 代理转发到 `http://localhost:8080`，避免硬编码后端地址。不引入 axios（单接口场景不值得加依赖）。CORS 已全量放行，代理与直连均可工作。

### D7. 表结构（`qa_live.doctor`）

| 列 | 类型 | 说明 |
|---|---|---|
| id | VARCHAR(64) PK | `doc001` 等，沿用 JSON 原 id |
| username | VARCHAR(64) UNIQUE | 登录名/诊室路由参数 |
| password | VARCHAR(128) | 原值迁移，仅存储不外露 |
| name / title / department / experience | VARCHAR(128/64/64/64) | 展示字段 |
| avatar | VARCHAR(512) | 外链 URL |
| specialties | JSON | 专长数组 |
| is_active | TINYINT(1) | 在线状态 |

字符集 `utf8mb4`。数据与 `doctor-user-list.json` 完全一致（含离线的 doc004）。

### D8. 加载/失败 UI

ant-design-vue 现成组件：`a-spin` 加载态、`a-result`/`a-alert` + 「重试」按钮失败态。不新增依赖。

## Risks / Trade-offs

- [mysql:latest 为 9.x，Flyway 兼容性] → Spring Boot 3.5.7 托管 Flyway 11.x，官方支持 MySQL 9；若启动报不支持，固定镜像为 `mysql:8.4`（compose 改一行）
- [MySQL 未启动时后端启动失败] → 这是预期行为（Flyway 需要连库）；启动顺序写入 API_TEST.md：先 compose 后后端
- [明文密码入库] → 已知取舍，需求 3 处理认证时一并加密；接口侧已通过 D4 保证不外露
- [Vite 代理仅 dev 生效] → 本项目交付形态为本地演示（README 联调要求），生产部署不在面试范围

## Migration Plan

1. `docker compose up -d`（MySQL 就绪）
2. 后端 `./mvnw spring-boot:run`（Flyway 自动建表灌数据）
3. 前端 `npm run dev`，访问 `/doctors` 验证

回滚：还原代码提交即可；`docker compose down`（数据卷 `~/mysql/data` 保留，如需彻底清理手动删除该目录）。

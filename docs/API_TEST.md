# API_TEST.md — 接口测试执行手册

> 适用版本：feature/interview-i18n-db-auth 分支（面试需求 1–3 交付后）
> 手册用途：指导人工或 AI 使用 curl 完成全部接口测试，并按第 7 章模板生成测试报告。

---

## 1. 测试范围

覆盖三个已交付需求的全部对外接口：

| 编号 | 接口 | 方法 | 来源需求 | 验收依据 |
|---|---|---|---|---|
| API-1 | `/api/doctors` | GET | 需求2：医生数据 MySQL 化 | PRD——openspec `user-service-api` delta / `doctor-directory` delta |
| API-2 | `/api/auth/register` | POST | 需求3：问诊用户注册 | PRD_PATIENT_AUTH.md §8.1 |
| API-3 | `/api/auth/login` | POST | 需求3：问诊用户登录 | PRD_PATIENT_AUTH.md §8.2 |
| API-4 | `/api/auth/me` | GET | 需求3：会话身份查询 | PRD_PATIENT_AUTH.md §8.3 |
| API-5 | `/api/auth/logout` | POST | 需求3：用户登出 | PRD_PATIENT_AUTH.md §8.4 |
| API-6 | `/api/test/cors` | GET/POST | 既有：CORS 联调（冒烟） | openspec `user-service-api` 主规格 |
| API-7 | `/actuator/health` | GET | 既有：健康检查（环境冒烟） | openspec `user-service-api` 主规格 |

**不在范围**：前端页面交互（路由守卫、表单提示等）——属人工浏览器验证，见第 6 章清单；医生登录为前端本地逻辑，无接口。

## 2. 环境准备

### 2.1 启动顺序（必须按序）

```bash
# ① 基础设施：MySQL + phpMyAdmin（项目根目录执行）
docker compose up -d
# 等待就绪（输出 healthy 即可）
docker inspect -f '{{.State.Health.Status}}' qa-mysql

# ② 后端：qa-service-user（首次启动自动执行 Flyway V1/V2 建表与种子数据）
cd server/qa-service-user && ./mvnw spring-boot:run

# ③ 前端（仅第 6 章人工场景需要）
cd web/qa-web && npm run dev
```

### 2.2 端口与环境清单

| 服务 | 地址 | 凭据/说明 |
|---|---|---|
| 后端 API | http://localhost:8080 | 全部测试的目标服务 |
| MySQL | localhost:3306 | root / 123456，库 `qa_live`，数据落盘 `~/mysql/data` |
| phpMyAdmin | http://localhost:8081 | 同步观察/修改数据库（80 端口被本机其他服务占用） |
| 前端 dev | http://localhost:5173 | `/api` 由 Vite 代理转发到 8080 |

### 2.3 就绪检查（执行用例前必须全部通过）

```bash
curl -s http://localhost:8080/actuator/health | grep '"status":"UP"'   # F0-1
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/doctors  # F0-2 输出 200
```

### 2.4 通用约定

- 基础地址：`http://localhost:8080`；请求体均为 JSON（`Content-Type: application/json`）
- 错误响应统一为 `{"message": "..."}`；状态码语义：400 校验失败 / 401 未登录或凭据错误 / 409 资源冲突
- 会话：登录接口通过 `Set-Cookie: JSESSIONID` 下发，30 分钟不活动过期；**后端重启后会话失效**（内存 Session，预期行为）
- Cookie 处理：用 cookie jar 文件（`-c` 保存 / `-b` 携带），建议统一用 `/tmp/api-test.cookie`
- 可重复执行：注册用例使用带时间戳的用户名（见 B1），避免 409 干扰重跑

## 3. 测试用例 — 冒烟与环境（F 组）

| 用例 | 步骤（curl） | 预期 |
|---|---|---|
| F0-1 健康检查 | `curl -s http://localhost:8080/actuator/health` | 200，`"status":"UP"`，`components.db` 为 UP |
| F0-2 CORS GET | `curl -s http://localhost:8080/api/test/cors` | 200，含 `"service":"qa-service-user"` |
| F0-3 CORS POST | `curl -s -X POST http://localhost:8080/api/test/cors -H "Content-Type: application/json" -d '{"ping":1}'` | 200，`receivedData` 原样回显 `{"ping":1}` |

## 4. 测试用例 — 医生列表（A 组，需求2）

**前置**：F0 组通过；数据库 `doctor` 表为 5 条种子数据（doc001–doc005，doc004 离线）。

| 用例 | 步骤（curl） | 预期 |
|---|---|---|
| A1 全量列表 | `curl -s http://localhost:8080/api/doctors` | 200；JSON 数组 5 个元素；每个元素含 `id,username,name,title,department,avatar,experience,specialties(数组),isActive(布尔)` |
| A2 密码不泄露 | 同 A1，检查响应全文 | 任一元素均无 `password` 字段 |
| A3 数据库实时反映 | ① `docker exec qa-mysql mysql --default-character-set=utf8mb4 -uroot -p123456 qa_live -e "UPDATE doctor SET is_active=1 WHERE id='doc004';"`<br>② 重复 A1 查看 doc004<br>③ 将 is_active 改回 0 | ② 中 doc004 的 `isActive` 为 `true`（phpMyAdmin 中改库同理）；③ 恢复 `false` |
| A4 中文编码 | 检查 A1 响应中 doc001 的 name | 正确显示「张伟医生」而非乱码 |

## 5. 测试用例 — 认证接口（B/C/D/E 组，需求3）

**前置**：F0 组通过；准备可变变量：

```bash
TS=$(date +%s)                       # 时间戳，保证用户名不冲突
TEST_USER="tester${TS}"
TEST_PASS="123456"
TEST_NAME="测试用户"
JAR=/tmp/api-test.cookie
```

### B 组：注册（对应 PRD 8.1）

| 用例 | 步骤 | 预期 |
|---|---|---|
| B1 合法注册 | `curl -s -i -c $JAR -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\",\"name\":\"$TEST_NAME\"}"` | 201；body 为 `{id, username, name}`；响应头含 `Set-Cookie: JSESSIONID` |
| B2 密码哈希入库 | `docker exec qa-mysql mysql -uroot -p123456 qa_live -e "SELECT LEFT(password_hash,4) FROM patient_user WHERE username='$TEST_USER';"` | 输出 `$2a$`（bcrypt 前缀，非明文） |
| B3 用户名重复 | 用 B1 相同用户名再次注册 | 409，`{"message":"用户名已被占用"}`；数据库记录数不变 |
| B4 密码过短 | `... -d '{"username":"badpwd","password":"12345","name":"张三"}'` | 400，`{"message":"密码长度需为6-64位"}` |
| B5 姓名过短 | `... -d '{"username":"badname","password":"123456","name":"张"}'` | 400，`{"message":"姓名需为2-32位"}` |
| B6 用户名格式非法 | `... -d '{"username":"ab","password":"123456","name":"张三"}'` | 400，`{"message":"用户名需为3-32位字母、数字或下划线"}` |

### C 组：登录（对应 PRD 8.2）

| 用例 | 步骤 | 预期 |
|---|---|---|
| C1 正确凭据 | `curl -s -i -c $JAR -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}"` | 200；body 为用户信息；`Set-Cookie: JSESSIONID` |
| C2 密码错误 | `... -d "{\"username\":\"$TEST_USER\",\"password\":\"wrong1\"}"` | 401，`{"message":"用户名或密码错误"}` |
| C3 用户不存在 | `... -d '{"username":"nobody999","password":"123456"}'` | 401，文案与 C2 **完全一致**（防枚举） |

### D 组：会话身份查询（对应 PRD 8.3）

| 用例 | 步骤 | 预期 |
|---|---|---|
| D1 携带会话查询 | `curl -s -b $JAR http://localhost:8080/api/auth/me` | 200，返回当前用户 `{id, username, name}` |
| D2 无会话查询 | `curl -s http://localhost:8080/api/auth/me` | 401，`{"message":"未登录或会话已过期"}` |

### E 组：登出（对应 PRD 8.4）

| 用例 | 步骤 | 预期 |
|---|---|---|
| E1 登出并失效 | ① `curl -s -b $JAR -c $JAR -X POST http://localhost:8080/api/auth/logout`<br>② `curl -s -b $JAR http://localhost:8080/api/auth/me` | ① 200 `{"message":"已退出登录"}`；② 401（会话已销毁） |
| E2 幂等登出 | 无有效会话时 `curl -s -X POST http://localhost:8080/api/auth/logout` | 200（不报错） |

## 6. 前端人工验证清单（非 curl，浏览器执行）

| 编号 | 场景 | 操作 | 预期 | 来源 |
|---|---|---|---|---|
| W1 | 问诊门禁与回跳 | 退出登录后直接访问 `/consultation/dr-zhang-wei` | 重定向 `/login?redirect=...`；登录后自动回到目标页 | PRD 8.5 |
| W2 | 旧流程下线 | 已登录进入问诊页 | 无姓名/生日表单与「切换用户」；标题为「<登录姓名> 的问诊」；提交问题归属登录用户 | PRD 8.7 |
| W3 | 刷新保持登录态 | 登录后刷新页面 | 顶栏仍显示「你好，<姓名>」 | PRD 8.3/US-4 |
| W4 | 游客浏览 | 未登录访问 `/`、`/doctors`、`/about` | 正常渲染，不重定向 | PRD 8.6 |
| W5 | 医生列表数据源 | 访问 `/doctors`；停掉后端刷新，再点重试 | 5 张卡片来自接口；失败显示错误提示+重试；恢复后重试成功 | 需求2 delta |
| W6 | 顶栏入口与双语 | 切换 English | "Patient Login" / "Hi, <name>" / "Logout"，控制台无缺 key 警告 | site-shell delta |
| W7 | 回归 | 医生登录（dr-zhang-wei/123456）、首页诊室区块 | 行为与改造前一致 | 范围限定 |

## 7. 测试报告生成指引（给 AI 的指令模板）

完成第 3–6 章执行后，将结果写入 `docs/TEST_REPORT.md`，格式如下：

```markdown
# 接口测试报告

- 执行时间：<YYYY-MM-DD HH:mm>
- 执行人/执行者：<姓名 或 AI+模型>
- 环境：<后端启动方式、MySQL 容器版本、操作系统>

## 结果汇总
| 分组 | 用例数 | 通过 | 失败 |
|---|---|---|---|

## 用例明细
| 用例 | 结果 | 实际响应摘要（状态码+关键 body） | 备注 |
|---|---|---|---|

## 失败分析（如有）
<用例编号、现象、原因、修复计划>

## 结论
<是否达到通过标准>
```

要求：每条用例必须附**实际**状态码与响应摘要作为证据（禁止只写「通过」）；失败项不得省略。

## 8. 通过标准

1. F0 组全部通过（环境就绪）
2. A/B/C/D/E 组全部通过（P0：A1–A3、B1–B3、C1–C3、D1–D2、E1；其余 P1）
3. W 组人工场景全部通过（演示录屏时同步覆盖）
4. 任一失败：记录于报告「失败分析」，修复后全量重跑受影响分组

## 9. 常见问题

| 现象 | 原因 | 处理 |
|---|---|---|
| 后端启动报 Flyway/连接错误 | MySQL 未启动或未就绪 | 按第 2.1 章顺序先起 compose，确认 healthy 再起后端 |
| `curl: (7) Failed to connect` 8080 | 后端未启动/编译中 | 查看 spring-boot:run 日志，等待启动完成 |
| D1 突然 401 | 会话 30 分钟过期或后端重启过 | 重新执行 C1 登录再继续 |
| B1 报 409 | 用户名已存在（重跑） | 检查 `$TS` 是否更新，或换新用户名 |
| phpMyAdmin 打不开 8080 | 本机 80 被 Dify nginx 占用属正常，改用 **8081** | — |

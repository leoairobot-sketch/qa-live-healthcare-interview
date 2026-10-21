# User Service API Specification

## Purpose

定义用户服务（qa-service-user）当前对外提供的 HTTP 行为：跨域（CORS）联调测试端点与基于 Actuator 的运维观测端点。该服务部署端口为 8080。

## Requirements

### Requirement: 提供 CORS 联调测试端点
服务 SHALL 在 `/api/test/cors` 提供联调测试端点：GET 请求返回包含 `message`（"CORS configuration is working!"）、`timestamp` 与 `service`（"qa-service-user"）的 JSON；POST 请求 SHALL 接收 JSON 请求体并在响应的 `receivedData` 字段中原样回显。

#### Scenario: GET 测试端点
- **WHEN** 客户端发起 `GET /api/test/cors`
- **THEN** 返回 200 与 JSON，其中 `message` 为 "CORS configuration is working!"、`service` 为 "qa-service-user"

#### Scenario: POST 测试端点回显请求体
- **WHEN** 客户端以请求体 `{"foo":"bar"}` 发起 `POST /api/test/cors`
- **THEN** 返回 200 与 JSON，其中 `receivedData` 为 `{"foo":"bar"}`

### Requirement: 服务允许跨域访问
服务 SHALL 对全部路径放行跨域：允许任意来源，允许 GET/POST/PUT/DELETE/OPTIONS 方法与任意请求头，携带凭证，预检结果缓存 3600 秒。

#### Scenario: 跨域预检请求通过
- **WHEN** 浏览器从其他源发起 `OPTIONS /api/test/cors` 预检请求
- **THEN** 返回允许跨域的响应头，预检通过

### Requirement: 提供健康与运维观测端点
服务 SHALL 在 `/actuator` 前缀下暴露健康检查与运维端点，至少包含 `health`（显示详细状态与组件）、`info`（应用信息）、`metrics`、`env`、`beans`、`loggers`。

#### Scenario: 健康检查
- **WHEN** 客户端发起 `GET /actuator/health`
- **THEN** 返回 200 与健康状态 JSON，且包含详细的状态信息与各组件状态

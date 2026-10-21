# Spec Delta

## ADDED Requirements

### Requirement: 提供医生列表查询 API
服务 SHALL 在 `GET /api/doctors` 提供医生列表查询：返回数据库中的全部医生（含离线医生）的 JSON 数组。每个元素 SHALL 包含 `id`、`username`、`name`、`title`、`department`、`avatar`、`experience`、`specialties`（字符串数组）与 `isActive`（布尔）字段，且 MUST NOT 包含 `password` 字段。医生数据 SHALL 持久化于 MySQL 数据库。

#### Scenario: 查询医生列表
- **WHEN** 数据库中存有 5 名医生，客户端请求 `GET /api/doctors`
- **THEN** 服务返回 HTTP 200 与包含 5 个元素的 JSON 数组，每个元素含 `id`、`username`、`name`、`title`、`department`、`avatar`、`experience`、`specialties`、`isActive` 字段

#### Scenario: 响应不包含密码
- **WHEN** 客户端请求 `GET /api/doctors`
- **THEN** 响应数组中的任何元素都不包含 `password` 字段

#### Scenario: 数据库变更反映到接口
- **WHEN** 通过 phpMyAdmin 将某医生的 `isActive` 由 1 改为 0（或新增一名医生）后再次请求 `GET /api/doctors`
- **THEN** 响应中该医生的 `isActive` 为 `false`（或数组包含新增医生），与数据库当前数据一致

### Requirement: 医生数据自动初始化
服务启动时 SHALL 自动完成医生库表的初始化：若医生表不存在则创建；若表中无数据则灌入与原前端 JSON（`doctor-user-list.json`）一致的 5 名医生种子数据（密码按原值存储，仅用于存储，不在接口中返回）。初始化过程 SHALL 可重复执行而不产生重复数据。

#### Scenario: 空数据库首次启动
- **WHEN** MySQL 中尚无医生库表，服务启动后请求 `GET /api/doctors`
- **THEN** 接口返回 5 名种子医生，与原前端 JSON 数据一致

#### Scenario: 重复启动不产生重复数据
- **WHEN** 服务已初始化过种子数据后再次重启
- **THEN** `GET /api/doctors` 仍返回 5 名医生，无重复记录

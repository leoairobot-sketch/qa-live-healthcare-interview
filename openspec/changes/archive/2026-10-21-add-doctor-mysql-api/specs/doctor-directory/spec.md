# Spec Delta

## MODIFIED Requirements

### Requirement: 医生团队页展示全部医生
医生团队页 SHALL 展示医生列表接口（`GET /api/doctors`）返回的全部医生（含离线医生），每张医生卡片 SHALL 包含：头像、姓名、职称、科室、从业经验与专长标签，并以徽标标识当前在线状态（在线/离线）。

#### Scenario: 查看医生列表全量展示
- **WHEN** 后端 `GET /api/doctors` 返回 5 名医生且用户访问 `/doctors`
- **THEN** 页面展示 5 张医生卡片，离线医生的徽标显示「离线」

## ADDED Requirements

### Requirement: 医生数据从后端服务获取
医生团队页 SHALL 在页面加载时调用 qa-service-user 的 `GET /api/doctors` 获取医生数据，不再读取本地 JSON 文件。页面渲染结果 SHALL 与接口返回数据保持一致。

#### Scenario: 接口数据变化反映到页面
- **WHEN** 通过 phpMyAdmin 在数据库中新增一名医生后，用户刷新 `/doctors` 页面
- **THEN** 页面展示 6 张医生卡片，包含新增的医生

#### Scenario: 其他页面的医生数据源不受影响
- **WHEN** 医生团队页改为接口数据源后，用户使用医生登录或访问首页诊室区块
- **THEN** 这些页面的医生数据仍来自本地 JSON，行为与此前一致

### Requirement: 医生列表加载与失败状态处理
医生团队页 SHALL 在接口数据获取期间展示加载状态；获取失败时 SHALL 展示失败提示，并提供重试入口，重试成功后正常渲染医生列表。

#### Scenario: 数据加载中
- **WHEN** 用户访问 `/doctors` 且接口尚未返回数据
- **THEN** 页面展示加载状态，而非空白或报错

#### Scenario: 接口失败与重试
- **WHEN** 后端服务未启动，用户访问 `/doctors`
- **THEN** 页面展示加载失败提示与重试入口；后端恢复并重试成功后，医生列表正常渲染

# Doctor Directory Specification

## Purpose

定义医生团队页（`/doctors`）的医生目录展示行为：全量医生信息展示、在线状态标识，以及从目录进入在线医生诊室的入口控制。

## Requirements

### Requirement: 医生团队页展示全部医生
医生团队页 SHALL 展示目录中的全部医生（含离线医生），每张医生卡片 SHALL 包含：头像、姓名、职称、科室、从业经验与专长标签，并以徽标标识当前在线状态（在线/离线）。

#### Scenario: 查看医生列表全量展示
- **WHEN** 内置数据包含 5 名医生且用户访问 `/doctors`
- **THEN** 页面展示 5 张医生卡片，离线医生的徽标显示「离线」

### Requirement: 仅在线医生可从目录进入诊室
医生卡片 SHALL 提供进入诊室按钮：在线医生的按钮可点击并导航到 `/consultation/<该医生用户名>`；离线医生的按钮 MUST 处于禁用状态并显示「暂未开放」。

#### Scenario: 在线医生进入诊室
- **WHEN** 用户在 `/doctors` 点击在线医生（如张伟医生）卡片的「进入诊室」按钮
- **THEN** 浏览器导航到 `/consultation/dr-zhang-wei`

#### Scenario: 离线医生按钮禁用
- **WHEN** 用户在 `/doctors` 查看离线医生（如刘敏医生）的卡片
- **THEN** 该卡片按钮显示「暂未开放」且不可点击

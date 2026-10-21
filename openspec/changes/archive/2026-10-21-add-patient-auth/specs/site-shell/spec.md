# Spec Delta

## MODIFIED Requirements

### Requirement: 站点支持全部页面路由
站点 SHALL 支持以下路由且互相独立可直达：`/`（首页）、`/consultation`（患者问诊）、`/consultation/:doctorUsername`（指定医生的问诊）、`/doctors`（医生团队）、`/about`（关于）、`/login`（问诊用户登录/注册）、`/doctor/login`（医生登录）、`/doctor/room/:username`（医生诊室）。其中 `/consultation*` 受登录校验保护（见 patient-consultation 能力），其余路由游客可直达。

#### Scenario: 直接访问指定医生问诊页
- **WHEN** 用户在浏览器地址栏直接输入 `/consultation/dr-li-na`
- **THEN** 页面正常加载问诊页，且问诊目标医生为李娜医生

#### Scenario: 访问医生诊室路由
- **WHEN** 用户直接访问 `/doctor/room/dr-zhang-wei`
- **THEN** 路由可达医生诊室页（是否放行内容由诊室访问控制决定）

#### Scenario: 游客直达登录页
- **WHEN** 未登录用户访问 `/login`
- **THEN** 登录页正常加载，提供登录与注册表单切换

## ADDED Requirements

### Requirement: 顶栏提供问诊用户登录入口与已登录态
顶栏右侧 SHALL 在「医生登录」按钮旁提供问诊用户入口：游客态显示「问诊用户登录」按钮，点击导航到 `/login`；已登录态显示「你好，<姓名>」用户菜单，菜单含「退出登录」项——点击后调用登出接口、清除登录态并返回首页。入口文案 SHALL 接入中英语言资源，随站点语言切换。

#### Scenario: 游客从顶栏进入登录页
- **WHEN** 游客点击顶栏「问诊用户登录」按钮
- **THEN** 浏览器导航到 `/login` 并显示登录表单

#### Scenario: 已登录用户登出
- **WHEN** 已登录用户点击顶栏用户菜单中的「退出登录」
- **THEN** 登录态清除、返回首页，顶栏恢复显示「问诊用户登录」按钮

#### Scenario: 入口文案随语言切换
- **WHEN** 站点语言切换到 English
- **THEN** 「问诊用户登录」按钮与用户菜单文案显示为英文

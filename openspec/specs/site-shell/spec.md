# Site Shell Specification

## Purpose

定义 Web 端的站点级布局与导航行为：固定顶栏、主导航菜单、页脚信息区，以及患者/医生两类用户访问全部页面所需的路由结构。

## Requirements

### Requirement: 站点提供固定顶栏与主导航
站点 SHALL 在所有页面顶部显示固定顶栏（fixed header），包含品牌标识与主导航菜单。菜单 SHALL 包含「首页、问诊、医生、关于」四个入口，分别导航到 `/`、`/consultation`、`/doctors`、`/about`。

#### Scenario: 通过导航菜单切换页面
- **WHEN** 用户点击顶栏菜单中的「医生」
- **THEN** 浏览器导航到 `/doctors` 并显示医生团队页

#### Scenario: 当前页面对应菜单项高亮
- **WHEN** 用户位于 `/consultation` 或其子路径（如 `/consultation/dr-zhang-wei`）
- **THEN** 顶栏菜单中「问诊」项处于选中高亮状态

### Requirement: 顶栏提供医生登录入口
顶栏右侧 SHALL 显示「医生登录」按钮，点击后导航到 `/doctor/login`。

#### Scenario: 从任意页面进入医生登录
- **WHEN** 用户在 `/doctors` 页点击顶栏右侧「医生登录」按钮
- **THEN** 浏览器导航到 `/doctor/login` 并显示医生登录表单

### Requirement: 站点支持全部页面路由
站点 SHALL 支持以下路由且互相独立可直达：`/`（首页）、`/consultation`（患者问诊）、`/consultation/:doctorUsername`（指定医生的问诊）、`/doctors`（医生团队）、`/about`（关于）、`/doctor/login`（医生登录）、`/doctor/room/:username`（医生诊室）。

#### Scenario: 直接访问指定医生问诊页
- **WHEN** 用户在浏览器地址栏直接输入 `/consultation/dr-li-na`
- **THEN** 页面正常加载问诊页，且问诊目标医生为李娜医生

#### Scenario: 访问医生诊室路由
- **WHEN** 用户直接访问 `/doctor/room/dr-zhang-wei`
- **THEN** 路由可达医生诊室页（是否放行内容由诊室访问控制决定）

### Requirement: 站点在所有页面显示页脚
站点 SHALL 在所有页面底部显示统一页脚，包含品牌信息、快速链接（首页/问诊/医生团队/关于我们）、联系方式（客服热线、服务时间、邮箱）与版权声明。

#### Scenario: 查看页脚内容
- **WHEN** 用户访问任意页面并滚动到底部
- **THEN** 页脚显示品牌、快速链接、联系信息与版权声明四类内容

### Requirement: 站点提供关于页面
`/about` SHALL 显示静态介绍内容，至少包含：平台简介、平台特色（专业医生团队/实时在线问诊/隐私安全保护/便捷易用）、服务流程（选择医生→提交问题→医生解答→查看回复）与联系方式。

#### Scenario: 浏览关于页面
- **WHEN** 用户访问 `/about`
- **THEN** 页面按顺序展示平台简介、平台特色、服务流程、联系我们四个区块

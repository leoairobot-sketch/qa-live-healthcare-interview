# Proposal

## Why

首页（`/`）的全部界面文案为硬编码中文，无法满足中英文切换需求（面试需求 1）。站点现已具备完整的中文问诊功能（见 `homepage`、`site-shell` 等既有 specs），需要在此基础上为首页补齐语言切换能力。

## What Changes

- 新增语言切换下拉菜单（中文/English），位于页面右上角（`AppHeader` 右侧区域）
- 引入 `vue-i18n` 作为本地化框架，在 `web/qa-web` 中注册
- 新增 `web/qa-web/src/locales/` 目录，保存中英文语言资源文件（`zh-CN.ts` / `en-US.ts`）
- 将 `Home.vue` 中的全部静态界面文案（hero 区、统计区、开放诊室区）改为通过 i18n key 引用
- 将站点共享外壳文案一并接入本地化（**用户确认的范围扩展**）：`AppHeader` 导航菜单与「医生登录」按钮、`AppFooter` 页脚文案；品牌名与语言自称（中文/English）不翻译
- 用户选择语言后无需刷新页面，动态切换显示内容；选择持久化到 `localStorage`，刷新后保持
- **明确范围**：首页正文 + 共享外壳（导航/页脚）。其他页面的正文文案不参与翻译；首页展示的医生等业务数据内容（姓名、科室、专长）来自数据源，不在本地化范围内（见 `consultation-data` spec 对数据现状的约定）

## Capabilities

### New Capabilities

- `web-localization`: Web 端语言切换与本地化文案能力。本次变更覆盖首页界面文案的中英文切换与右上角语言切换控件；后续其他页面的本地化需求可扩展到该能力下。

### Modified Capabilities

（无——已调研既有 specs：`homepage` 的三条 requirement 均与展示语言无关，`site-shell` 关于顶栏的 requirement 只约定菜单/登录入口的存在与导航目标，均不受本变更影响；语言切换是新增的横切行为，落在新的 `web-localization` 能力中）

## Impact

- **前端 `web/qa-web`**：
  - `package.json`：新增依赖 `vue-i18n`（需执行 `npm install`；当前 `node_modules` 尚未安装）
  - `src/main.ts`：注册 vue-i18n 插件
  - 新增 `src/locales/`（`zh-CN.ts`、`en-US.ts` 及入口 `index.ts`）
  - `src/components/AppHeader.vue`：右上角新增语言切换下拉菜单；导航菜单与登录按钮文案接入 `t()`
  - `src/components/AppFooter.vue`：页脚文案接入 `t()`
  - `src/views/Home.vue`：静态文案替换为 `t()` 调用
- **不受影响**：后端服务、路由结构、store 数据逻辑、其他页面组件；既有 8 个主 specs 的 requirement 均无变更
- 无 API 变更、无数据库变更、无 BREAKING 变更

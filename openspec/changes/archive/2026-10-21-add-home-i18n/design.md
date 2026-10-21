# Design

## Context

- 前端 `web/qa-web` 为 Vue 3.5 + TypeScript + Vite 5 + Ant Design Vue 4 + vue-router，页面布局为 `AppHeader`（固定顶栏）+ 路由视图 + `AppFooter`
- `Home.vue` 的全部静态文案为硬编码中文（hero 区、统计卡片区、开放诊室区块）；医生卡片内容来自本地 JSON 数据（store），现状行为见 `openspec/specs/homepage` 与 `consultation-data`
- 项目当前未安装任何 i18n 依赖，`node_modules` 尚未安装（首个任务需先 `npm install`）
- 语言切换只面向首页；其他页面与业务数据内容见 specs delta 的范围限定

## Goals / Non-Goals

**Goals:**

- 建立可扩展的前端本地化基础设施（框架、目录、类型），后续页面可低成本接入
- 首页文案中英文即时切换，无需刷新；偏好持久化
- 切换控件融入现有 Ant Design 风格，位于页头右上角

**Non-Goals:**

- 不翻译其他页面的正文文案（问诊、医生团队、关于等页面内容，属后续扩展）
- 不翻译品牌名「QA Live Healthcare」与语言菜单中的语言自称（中文/English）
- 不翻译业务数据（医生姓名/科室/专长等，来自数据源，后续将入 MySQL）
- 不做 antd 组件内置文案的 ConfigProvider locale 切换（首页未用到日期选择/分页等带内置文案的组件）
- 不做语言包懒加载与按需打包优化（资源量极小）
- 不做浏览器语言自动检测（默认中文即可，见 specs）

## Decisions

### D1：采用 vue-i18n（v11）作为本地化框架

- **理由**：Vue 3 官方生态事实标准，Composition API（`createI18n` + `useI18n()`）与现有 `<script setup lang="ts">` 代码风格一致；提供插值、复数等后续可扩展能力
- **备选**：手写 reactive 字典 + provide/inject——零依赖，但无类型约束和扩展性，且面试场景下使用标准库更体现工程规范。否决

### D2：语言资源组织为 `src/locales/` 下的 TS 模块

- 结构：`zh-CN.ts`、`en-US.ts` 各导出一个按首页区块嵌套的消息对象（`home.hero.title`、`home.stats.doctors` 等），`index.ts` 汇出 `messages`、locale 枚举与持久化 key 常量
- **理由**：面试要求明确指定资源文件保存在 `web/qa-web/src/locales`；TS 模块相比 JSON 有类型提示且支持注释；嵌套 key 与首页模板结构对应，便于维护
- `createI18n({ legacy: false, ... })` 启用 Composition API 模式，全局单例注册于 `main.ts`

### D3：切换控件放在 `AppHeader.vue` 右侧（医生登录按钮左侧）

- **理由**：「页面右上角」即固定页头的右区；`AppHeader` 是站点布局组件，放这里最自然，且复用 antd 的 `a-dropdown` + `GlobalOutlined` 图标，风格统一
- **备选**：仅在 Home 路由渲染切换器（`v-if="route.name === 'Home'"`）——避免了"其他页面切换无效果"的困惑，但引入路由耦合且演示时不够直观。否决，但接受该已知行为（见 Risks）
- 交互：`a-dropdown` 菜单两项「中文 / English」，选中项打勾标识；点击即调用 `locale.value = <locale>`，vue-i18n 响应式驱动首页文案即时更新

### D4：语言状态与持久化

- locale 初始化顺序：`localStorage['qa-web-locale']` → 不存在时默认 `zh-CN`
- 切换时同步写入 `localStorage` 并更新 `document.documentElement.lang`（可访问性）
- **理由**：需求要求选择持久化；默认中文符合当前站点定位

### D5：`Home.vue` 与共享外壳（AppHeader/AppFooter）改造方式

- `Home.vue` 模板中硬编码文案全部替换为 `t('home.*')`；统计数字、医生卡片数据绑定保持不变
- `AppHeader.vue` 导航菜单与登录按钮、`AppFooter.vue` 页脚文案替换为 `t('nav.*')` / `t('footer.*')`（范围经用户确认扩展，spec 已同步）；品牌名与语言自称保持原样
- **理由**：首页正文与全站外壳两类文案分别归入独立 key 命名空间，页面正文 i18n 可后续按页扩展

## Risks / Trade-offs

- [vue-i18n message 类型与 `vue-tsc` 严格模式可能产生类型摩擦] → 消息对象用 `Record<string, any>` 层级类型或官方 `DefineLocaleMessage` 宽松定义，构建走现有 `npm run build`（vue-tsc -b）验证
- [切换器在非首页可见但无效果，用户可能困惑] → 已通过范围扩展缓解：导航与页脚全站跟随切换，非首页页面上仍有即时可见的变化；页面正文保持中文（spec 已限定范围），演示与文档中说明
- [Node 26 + Vite 5 依赖尚未安装，环境未知问题] → 实施首步先执行 `npm install` 并 `npm run build` 建立基线，出问题先解决环境
- [中英文文案长度差异导致布局抖动] → 英文文案尽量精炼；现有布局有弹性（flex/grid），风险低

## Migration Plan

- 纯前端增量变更，无数据迁移、无 API 变更；部署即构建产物替换
- 回滚策略：revert 对应提交即可，无状态残留（localStorage key 无副作用）

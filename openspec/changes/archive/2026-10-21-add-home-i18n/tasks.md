# Tasks

## 1. 环境与依赖准备

- [x] 1.1 在 `web/qa-web` 执行 `npm install` 安装现有依赖并运行 `npm run build`，确认基线构建通过（当前 `node_modules` 缺失）
- [x] 1.2 安装 `vue-i18n`（`npm install vue-i18n`），确认 `package.json` 出现该依赖且 `npm run build` 仍通过

## 2. 本地化基础设施

- [x] 2.1 创建 `src/locales/zh-CN.ts` 与 `src/locales/en-US.ts`：按首页区块（hero / stats / rooms）组织的完整消息对象，两文件 key 结构完全一致；对照 `Home.vue` 现有文案逐条搬运中文，翻译英文，验证无遗漏 key（对照清单写注释）
- [x] 2.2 创建 `src/locales/index.ts`：导出 `messages`、语言枚举（`zh-CN`/`en-US`）、localStorage key 常量；验证 `vue-tsc` 无类型错误（`npm run build`）
- [x] 2.3 在 `src/main.ts` 注册 `createI18n({ legacy: false, locale: 初始语言 })`：初始语言取 `localStorage`，无记录时默认 `zh-CN`；验证 `npm run dev` 启动无报错

## 3. 语言切换控件（AppHeader）

- [x] 3.1 在 `AppHeader.vue` 右侧（医生登录按钮左侧）添加 `a-dropdown` 语言菜单（GlobalOutlined 图标，中文/English 两项，当前语言打勾）；点击选项即切换 `locale.value`、写入 `localStorage` 并同步 `document.documentElement.lang`
- [x] 3.2 浏览器验证：下拉展开显示两项且当前项标识正确；选择 English 后无需刷新、页头无报错

## 4. 首页文案接入（Home.vue）

- [x] 4.1 将 `Home.vue` 模板中 hero 区文案（标题、副标题、3 个特性项、2 个按钮）替换为 `t('home.hero.*')`；`npm run dev` 中确认中文显示与改造前一致
- [x] 4.2 将统计卡片区与开放诊室区块文案（4 张卡片标签、区块标题与副标题、按钮）替换为对应 `t()` 调用；医生数据绑定（姓名/职称/科室/专长标签）保持原样不动
- [x] 4.3 切换到 English 验证首页全部静态文案为英文且无遗漏的硬编码中文（逐区块目视检查），切换回中文恢复正常

## 5. 集成验证

- [x] 5.1 对照 specs 场景逐条手测：控件展示与展开、中英即时切换（无刷新）、刷新后语言保持、无偏好时默认中文、业务数据不随语言变化；记录验证结果（用户已在浏览器确认）
- [x] 5.2 执行 `npm run build`（含 `vue-tsc -b` 类型检查）通过，作为最终门禁

## 6. 范围扩展：共享外壳本地化（用户确认）

- [x] 6.1 在语言文件中新增 `nav.*` 与 `footer.*` 命名空间（导航菜单/登录按钮/页脚全部文案），中英 key 结构对齐；自动比对无差异
- [x] 6.2 `AppHeader.vue` 导航菜单四项与「医生登录」按钮接入 `t()`；品牌名保持原样
- [x] 6.3 `AppFooter.vue` 页脚文案（栏目标题、快速链接、联系与法律信息、服务时间值）接入 `t()`；品牌名与版权行保持原样
- [x] 6.4 验证：`npm run build` 通过、两组件模板零残留中文、t() key 全覆盖

## Workflow follow-up

- 全部任务完成后，按项目评审要求归档 change（`/opsx:archive`）
- 本变更属于面试需求 1；面试交付文档（/docs、视频、prompts.md）另行统一整理

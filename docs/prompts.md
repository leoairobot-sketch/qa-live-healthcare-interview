# prompts.md — 面试过程 AI 提示词记录

> 记录面试期间指导 AI 完成编码/测试/文档的**全部提示词原文**，按时间与阶段分组。
> 记录来源：Claude Code 本地会话转录（会话 A：项目启动 + 需求1；会话 B：需求2/3 + 测试交付）。
> 工作流模式：环境准备 → OpenSpec 方案（propose）→ 实施（apply）→ 人工验证 → 归档（archive）→ 交付文档。
> 除提示词原文外，同时记录 AI 主动发起的**澄清问答**与**人工验证介入点**（面试流程要求的关键交互）。

---

## 0. 项目启动与会话 A（openspec 初始化 + 需求1）

### P-01 项目现状检查
```
仔细阅读当前项目，帮我检查当前项目现有状态
```
AI 产出：仓库结构梳理（web/qa-web 前端、server 三服务）、面试要求解读、待办清单。

### P-02 初始化 openspec
```
帮我初始化openspec
```
AI 产出：`openspec/` 根目录 + spec-driven 工作流配置。

### P-03 创建 .gitignore
```
创建git排除文件，排除无关文件
```
AI 产出：仓库根 `.gitignore`（node_modules、构建产物、IDE 文件等）。

### P-04 存量功能规格化
```
扫描本仓库 web/ 和 server/ 的现有代码，把每个已存在的功能整理为 openspec/specs/ 下的 spec 文档，每条要求写成可测试的条目。
```
AI 产出：8 个 capability 主规格（homepage、doctor-directory、patient-consultation、doctor-auth、doctor-room、consultation-data、site-shell、user-service-api），为后续 delta 变更建立基线。

---

## 1. 需求1：首页中英文切换（i18n，会话 A）

### P-05 启动第一个需求
```
用 /opsx:propose 启动第一个需求
```
AI 产出：变更 `add-home-i18n` 方案（首次尝试；后变更目录意外丢失）。

### P-06 重新发起（附需求原文）
```
add-home-i18n没有了  用 /opsx:propose 帮我重新来一次
```
propose 参数（需求原文）：
```
给 web 端首页添加中英文切换能力：在页面右上角添加语言切换下拉菜单，用户选择"中文/English"时
动态切换页面显示内容到对应语言。中英文语言资源文件保存在 web/qa-web/src/locales 目录中。
只需处理首页本身，无需处理其他页面（面试需求 1）
```
AI 产出：重建 `add-home-i18n`（proposal + `web-localization` delta spec + tasks 6 组任务）。

### P-07 实施
```
/opsx:apply
```
AI 产出：`locales/zh-CN.ts`、`en-US.ts`、`index.ts`；`main.ts` 注册 vue-i18n（legacy:false + localStorage 持久化）；AppHeader 右上角语言下拉；Home.vue 文案接入 `t()`；`npm run build` 通过。

### P-08 人工验证疑问 → 范围扩展
```
我看页面中的导航还是中文，没有中英切换？
```
AI 说明：按需求字面仅首页正文切换，导航/页脚属共享外壳不在范围；用户确认扩展 → tasks 追加第 6 组（`nav.*`/`footer.*` 命名空间，AppHeader/AppFooter 双语化），复验通过。

### P-09 归档
```
/opsx:archive
```
AI 产出：归档至 `archive/2026-10-21-add-home-i18n/`。

### P-10/P-11 需求2 开工与范围收缩（会话 A 末尾，衔接会话 B）
```
开始需求2吧  先帮我搭建 Java 17 运行时，然后完成 MySQL + docker-compose+phpmyadmin ，最后再做 qa-service-user API
```
（用户随后中断并收缩范围）
```
现在你只搭建 Java 17，完了先停止
```
AI 产出：确认执行边界——仅搭建 Java 17 后停止（新会话以「帮我搭建 Java 17」承接，见 P-12）。

---

## 2. 需求2：医生数据 MySQL 化 + 列表 API + 联调（会话 B）

### P-12 搭建 Java 17
```
帮我搭建 Java 17
```
AI 产出：Homebrew 安装 OpenJDK 17.0.21，配置 `JAVA_HOME` 与 PATH（~/.zshrc），`mvnw -version` 验证与 pom `java.version=17` 匹配。

### P-13 进度盘点
```
帮我看看我接下来要干什么
```
AI 产出：对照 README 盘点三需求进度，指出需求1 代码未提交，给出后续顺序建议。

### P-14 编写 docker-compose
```
帮我编写一个docker-compose，包含最新版的MySQL + phpMyAdmin，端口号映射用默认端口即可，
mysql账号用root，密码用123456，数据挂载在 ~/mysql/data下，两个服务用同一个网络
```
AI 产出：`docker-compose.yml`（mysql:latest + phpmyadmin:latest，自定义网络 qa-net，healthcheck 门控）。

### P-15 启动服务
```
跑起来吧
```
AI 产出：发现宿主机 80 被本机 Dify 栈占用，phpMyAdmin 改映射（后又因后端 8080 冲突定为 8081），MySQL healthy。

### P-16 需求边界澄清
```
这部分需求都明确吗
```
AI 产出：核对前端数据流与后端现状后指出 1 个硬冲突（8080 端口）+ 2 个待拍板边界，并结构化提问。

**澄清问答（AI 发起）**：
| 问题 | 决议 |
|---|---|
| 医生列表 API 数据供哪些前端消费？ | 仅 Doctors.vue（严格按 README 字面） |
| 列表接口 password 字段怎么处理？ | API 不返回密码（表照存，接口脱敏） |
| 8080 端口冲突怎么解？ | phpMyAdmin 挪 8081，后端保 Spring 默认 8080 |

### P-17 生成 OpenSpec 方案
```
/opsx:propose
```
AI 产出：变更 `add-doctor-mysql-api`（proposal + 2 份 delta spec + design 8 项决策 + 13 任务）。

### P-18 实施
```
/opsx:apply
```
AI 产出：Flyway V1 建表与种子、JdbcClient 仓储、`GET /api/doctors` 脱敏接口、Vite 代理、Doctors.vue 改造（加载/失败态）、build 通过；curl 全场景验证。

**人工验证介入点**（AI 暂停等待浏览器确认）：
- 「都已验证，开始失败测试」→ AI 停后端，用户确认失败态 UI
- 「看到了」「可以了」→ AI 恢复后端，用户确认重试成功

### P-19 归档
```
/opsx:archive 归档本变更
```
AI 产出：归档至 `archive/2026-10-21-add-doctor-mysql-api/`（澄清问答中选择「不同步直接归档」）。

---

## 3. 需求3：问诊用户注册登录（PRD 工作流，会话 B）

### P-20 阅读 PRD 模版
```
请阅读docs中的prd
```
AI 产出：`docs/PRD_TEMPLATE.md` 结构解读，指出第 8 章 Requirement/Scenario 与 OpenSpec、API_TEST.md 的对齐关系，并提出 3 个范围问题。

### P-21 编写需求3 PRD
```
用此模版编写需求3 的 PRD，并放在docs中，
1、包含注册和登录
2、暂时不用jwt，用session即可
3、游客也可以看到首页也医生列表
如果还有不确定的地方，请先提问，明确之后再写
```
**澄清问答（AI 发起，动笔前）**：
| 问题 | 决议 |
|---|---|
| 现有「姓名+生日」验证怎么处理？ | 替换旧流程（问诊必须登录） |
| 注册表单收集哪些字段？ | 用户名+密码+姓名 |
| Session 存哪里？ | 服务端内存 Session |

AI 产出：`docs/PRD_PATIENT_AUTH.md`（11 章完整 PRD：权限矩阵、4 接口、表结构、8.1–8.7 共 15 个验收 Scenario、T-1~T-5 拆分）。

### P-22 以 PRD 为准生成方案
```
/opsx:propose 以这份prd为准生成 openspec 方案
```
AI 产出：变更 `add-patient-auth`（新 capability `patient-auth` + 修改 `patient-consultation`/`site-shell`，design 9 项决策含防开放重定向、i18n 双语约束，13 任务）。

### P-23 实施
```
/opsx:apply
```
AI 产出：Flyway V2、`/api/auth/*` 四接口（bcrypt + Session + 统一错误）、Login.vue、路由守卫（门禁+回跳）、顶栏双语入口、Consultation.vue 旧流程下线、curl 11 断言 + 浏览器全场景 + 冷启动演练验证。

**人工验证介入点**：用户完成浏览器 7 项核对后回复「已验证  没问题」。

### P-24 归档
```
/opsx:archive
```
AI 产出：归档至 `archive/2026-10-21-add-patient-auth/`（同样选择「不同步直接归档」）。

---

## 4. 测试交付物（会话 B）

### P-25 编写接口测试手册
```
根据刚归档的三个需求，编写 docs/API_TEST.md，作为接口测试的执行手册。必须包含：测试范围、环境准备、测试用例等必要内容
```
AI 产出：`docs/API_TEST.md`（7 接口 27 用例，F/A/B/C/D/E 分组 + W 人工清单 + 报告模板 + 通过标准 + 常见问题；时间戳用户名设计支持重跑）。

### P-26 执行测试并出报告
```
用这份手册执行测试并生成测试报告
```
AI 产出：`docs/TEST_REPORT.md`（27/27 通过，每条附实际状态码与响应摘要证据，含 Cookie 安全属性、防枚举文案逐字比对等深度断言）。

---

## 5. 本文档

### P-27 整理提示词记录
```
帮我整理prompts.md
```
（其后追加：`你搜索一下本地会话   这个会话之前有另一个会话是聊的` —— 指示 AI 从本地会话转录中找回会话 A 的提示词原文并补全本文档。）

AI 产出：本文档（两个会话共 27 条提示词全量记录）。

---

## 统计与方法说明

- 提示词共 **27 条**（P-01～P-27，跨两个会话按时间顺序连续编号）；另有 AI 主动发起的结构化澄清 4 轮（需求2 边界、PRD 范围、归档同步策略×2）与人工浏览器验证介入 6 次、人工中断纠偏 1 次（P-10→P-11 范围收缩）
- 核心方法：**存量规格化先行**（P-04 建立 8 个 capability 基线）→ **PRD 先行**（模版 → 澄清 → PRD → 方案 → 实施 → 测试 → 归档），每个编码动作都可回溯到 PRD 验收标准或 openspec 规格
- 提示词风格：短指令 + 明确验收物；边界分歧交给 AI 结构化提问后人工拍板；人工仅做浏览器验证与关键决策，未直接编码（符合面试流程要求）

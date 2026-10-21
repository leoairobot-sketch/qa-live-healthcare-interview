# Spec Delta

## REMOVED Requirements

### Requirement: 患者以姓名与生日验证身份
**Reason**: 需求 3 引入用户名密码账号体系（patient-auth），前端本地「姓名 + 生日」验证可绕过且无会话，被登录流程取代。
**Migration**: 问诊身份改由 `/login` 注册/登录获得；`patient-user.json` 与 `verifyPatient` 逻辑退役，仅作历史数据保留不删除文件。

### Requirement: 患者可切换用户
**Reason**: 「切换用户」服务于旧的免密验证流程；账号体系下退出登录统一由顶栏用户菜单承担，避免两处退出入口。
**Migration**: 使用顶栏「退出登录」（POST /api/auth/logout）后重新登录其他账号。

## MODIFIED Requirements

### Requirement: 指定医生链接进入问诊时预选医生
用户通过 `/consultation/:doctorUsername` 访问且目标医生处于在线状态时，系统 SHALL 在用户完成登录后预选该医生为问诊对象，并显示可关闭的「当前诊室」提示；目标医生不存在或离线时 MUST NOT 预选。

#### Scenario: 经诊室链接进入并预选医生
- **WHEN** 用户经 `/consultation/dr-zhang-wei` 登录后进入问诊页
- **THEN** 显示「当前诊室: 张伟医生 - 心内科」提示，提交问题表单中医生已锁定为张伟医生

#### Scenario: 关闭诊室提示后可选任意在线医生
- **WHEN** 用户点击「当前诊室」提示的关闭按钮
- **THEN** 预选医生被清除，提交问题表单可从全部在线医生中选择

## ADDED Requirements

### Requirement: 未登录访问问诊页重定向登录页并支持回跳
前端 SHALL 对 `/consultation` 及其子路由实施登录校验：未登录访问时重定向到 `/login?redirect=<原路径>`；登录或注册成功后 SHALL 跳转 redirect 指定的原目标页。医生相关路由（`/doctor/*`）不受此校验影响。

#### Scenario: 游客直接访问问诊页
- **WHEN** 未登录状态浏览器打开 `/consultation/dr-zhang-wei`
- **THEN** 重定向到 `/login?redirect=/consultation/dr-zhang-wei`

#### Scenario: 登录后回到目标页
- **WHEN** 用户在上述登录页完成登录
- **THEN** 自动跳转回 `/consultation/dr-zhang-wei`，问诊页正常可用

### Requirement: 问诊患者身份来自登录会话
已登录用户进入问诊页时，系统 SHALL 直接以会话用户身份进入问诊门户（不再出现任何身份验证表单）；提交的问题 SHALL 归属会话用户——记录中的患者标识与患者姓名取自登录用户的 id 与姓名。

#### Scenario: 登录用户直接进入问诊门户
- **WHEN** 已登录用户访问 `/consultation`
- **THEN** 不显示身份验证表单，直接以「<登录姓名> 的问诊」进入门户

#### Scenario: 提交问题归属登录用户
- **WHEN** 已登录用户张三提交问诊问题
- **THEN** 「我的问题」列表新增记录的提交人为张三（登录姓名），且仅登录用户本人可见自己的问题列表

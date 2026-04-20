# 管理员与 AI 功能开发清单

## 1. 文档目的

本清单用于对“管理员功能”和“AI 撰写日志功能”进行范围确认、实施拆分和审核决策。

当前阶段：

- 仅输出开发清单
- 不修改现有普通用户端业务逻辑
- 待审核后再按选定项实施

## 2. 老师要求映射

老师提出的需求可拆为 5 个功能块：

1. 用户表增加角色字段，区分普通用户和管理员
2. 后端新增管理员接口，具备对用户、旅游记忆、规划、社交等数据的管理权限
3. 前端新增管理员页面和路由，仅允许管理员访问
4. 登录后根据角色进入不同界面
5. 普通用户端增加“AI 撰写日志”功能，并接入指定大模型配置

## 3. 当前项目现状分析

基于现有代码，当前状态如下：

- 后端已有 `users` 表，但没有 `role` 字段
- 后端已有普通用户侧控制器：
  - `AuthController`
  - `UserController`
  - `TravelController`
  - `PlanController`
  - `CommentController`
  - `LikeController`
  - `FootprintController`
  - `ItineraryController`
  - `FileController`
- 后端安全配置已启用 JWT，但尚未看到基于角色的访问控制落地
- 前端已有普通用户页面和基础登录态存储，但没有管理员路由和管理员界面
- 当前登录返回结构里已有 `user` 对象，后续可扩展 `role`
- 当前 AI 功能尚未接入

涉及的现有关键文件：

- 后端用户实体：[User.java](/E:/项目/Android-VUE-SpringBoot/旅行记忆项目/SpringBoot/src/main/java/com/travelMemory/entity/User.java)
- 后端登录返回：[AuthResponse.java](/E:/项目/Android-VUE-SpringBoot/旅行记忆项目/SpringBoot/src/main/java/com/travelMemory/dto/AuthResponse.java)
- 后端安全配置：[SecurityConfig.java](/E:/项目/Android-VUE-SpringBoot/旅行记忆项目/SpringBoot/src/main/java/com/travelMemory/config/SecurityConfig.java)
- 后端数据库脚本：[schema.sql](/E:/项目/Android-VUE-SpringBoot/旅行记忆项目/SpringBoot/src/main/resources/schema.sql)
- 前端路由：[index.js](/E:/项目/Android-VUE-SpringBoot/旅行记忆项目/VUE/src/router/index.js)
- 前端登录态存储：[userStore.js](/E:/项目/Android-VUE-SpringBoot/旅行记忆项目/VUE/src/stores/userStore.js)

## 4. 建议实施原则

- 不破坏现有普通用户端路由、页面、接口行为
- 管理员接口与普通用户接口分开命名和分层
- 角色判断以后端为准，前端仅做显示和路由拦截
- AI 功能优先放在后端代理调用，避免在前端直接暴露第三方密钥

## 5. 功能开发清单

### A. 角色体系改造

目标：

- 为用户增加角色字段
- 支持 `USER` / `ADMIN` 两类角色

开发项：

- 数据库 `users` 表新增 `role` 字段
- 初始化脚本和样例数据补充默认角色
- `User` 实体增加 `role`
- `UserResponse` 增加 `role`
- 登录/注册返回中带出 `role`
- 新增管理员初始化方案

影响范围：

- `schema.sql`
- `database-init.sql`
- `sample-data.sql`
- `sample-data_cn.sql`
- `User.java`
- `UserResponse.java`
- 认证相关 service / dto

待确认项：

- 默认注册用户是否全部为普通用户
- 是否需要预置一个固定管理员账号

### B. 后端管理员接口

目标：

- 新增一套管理员接口
- 管理员拥有增删改查权限

建议接口分组：

- 管理员用户管理
- 管理员旅行记忆管理
- 管理员旅行计划管理
- 管理员评论管理
- 管理员点赞管理
- 管理员足迹管理
- 管理员文件管理

建议接口前缀：

- `/api/admin/users`
- `/api/admin/travels`
- `/api/admin/plans`
- `/api/admin/comments`
- `/api/admin/likes`
- `/api/admin/footprints`
- `/api/admin/files`

建议能力：

- 用户列表、详情、创建、修改、删除、重置角色
- 旅行记录列表、详情、删除、修改公开状态
- 旅行计划列表、详情、删除、修改
- 评论列表、删除
- 点赞列表、删除
- 足迹列表、删除
- 文件列表、删除
- 统计概览接口

建议实现方式：

- 新建独立的 `Admin*Controller`
- 新建独立的 `Admin*Service`
- 可复用现有 repository，不强制重复造轮子

待确认项：

- 是否要求管理员可以直接新建普通用户内容
- 是否需要操作日志

### C. 后端角色鉴权

目标：

- 只允许管理员访问管理员接口

开发项：

- JWT 中增加角色声明，或在鉴权阶段从数据库装载角色
- Spring Security 增加角色校验
- 管理员接口统一增加角色限制
- 非管理员访问管理员接口时返回 403

建议方式：

- `hasRole('ADMIN')` 或等价实现
- 管理员接口统一纳入安全配置

待确认项：

- 是否保留资源所有者逻辑与角色逻辑并存

### D. 前端管理员端

目标：

- 新建管理员页面、布局、路由
- 仅管理员可访问

建议页面最小集：

1. 管理员首页
2. 用户管理页
3. 旅行记录管理页
4. 旅行计划管理页
5. 社交管理页

建议前端结构：

- 新建管理员布局
- 新建管理员侧边栏/顶部导航
- 新建管理员服务层
- 路由 meta 中增加角色标记

建议路由：

- `/admin`
- `/admin/users`
- `/admin/travels`
- `/admin/plans`
- `/admin/social`

开发项：

- 前端 `userStore` 存储并读取 `role`
- 路由守卫根据 `role` 拦截
- 管理员登录后显示管理员入口或直接跳转管理员首页
- 非管理员访问 `/admin/**` 自动拦截

待确认项：

- 管理员是否与普通用户共用顶部导航
- 登录后是“直接跳转管理员首页”还是“保留角色入口切换”

### E. 登录后按角色显示不同界面

目标：

- 普通用户进入普通用户端
- 管理员进入管理员端

开发项：

- 登录成功后根据 `user.role` 决定跳转路径
- 刷新后根据本地存储角色恢复界面
- 顶部导航、侧边栏按角色切换

建议策略：

- `USER` -> `/dashboard`
- `ADMIN` -> `/admin`

待确认项：

- 管理员是否仍可访问普通用户端页面

### F. AI 撰写日志功能

目标：

- 在普通用户登录后页面中增加“AI 撰写日志”能力

老师给定模型配置：

```json
{
  "SiliconFlow": {
    "Provider": "SiliconFlow",
    "BaseUrl": "https://api.siliconflow.cn/v1",
    "ApiKey": "sk-kkqzlhkehosnzpixglqyhxtsjqbikocvtjixvkaggqybrmge",
    "Model": "tencent/Hunyuan-MT-7B"
  }
}
```

重要建议：

- 不建议把第三方 `ApiKey` 直接放到前端
- 建议由后端新增 AI 代理接口，前端只调用本系统后端

建议功能形态：

- 在“新建旅行记录”页增加 `AI 撰写日志`
- 输入旅行主题、目的地、天数、风格、关键经历
- 点击后由后端调用 SiliconFlow
- 返回草稿内容回填到日记编辑器

建议后端开发项：

- 新增 AI 配置类
- 新增 AI 调用 service
- 新增 AI controller
- 增加超时、失败重试、错误提示

建议前端开发项：

- 记录创建页新增 AI 输入区或弹窗
- 增加“生成中”状态
- 生成结果可编辑，不直接覆盖原文

待确认项：

- AI 功能是否只放在旅行记录创建页
- 是否需要“续写”“润色”“扩写”“摘要”四种模式

### G. 配置与安全

目标：

- 保证管理员和 AI 功能上线后可维护、可配置、相对安全

开发项：

- 后端配置文件增加 SiliconFlow 配置项
- 使用环境变量或本地配置文件注入 API Key
- 避免将密钥提交到前端代码中
- 管理员接口增加统一异常返回
- 增加必要日志

待确认项：

- 是否允许把老师提供的密钥直接写入 `application.properties`
- 是否需要 `.example` 配置模板

## 6. 推荐分阶段实施顺序

### 第一阶段：基础角色改造

- 用户表增加 `role`
- 登录返回 `role`
- 前端存储 `role`
- 登录后按角色跳转

### 第二阶段：管理员后端接口

- 管理员用户管理
- 管理员旅行记录管理
- 管理员旅行计划管理
- 管理员社交管理

### 第三阶段：管理员前端页面

- 管理员布局
- 管理员首页
- 各管理列表页
- 管理员路由拦截

### 第四阶段：AI 撰写日志

- 后端接 SiliconFlow
- 前端记录页接入 AI 按钮
- 回填到编辑器

## 7. 建议审核选项

你可以直接按下面方式给我确认实施范围：

### 方案一：先做管理员最小闭环

- A 角色体系改造
- B 管理员用户/记录/计划接口
- C 角色鉴权
- D 管理员基础页面
- E 登录按角色跳转

### 方案二：管理员功能全做，AI 暂缓

- A
- B
- C
- D
- E
- G

### 方案三：管理员最小闭环 + AI 基础版

- A
- B
- C
- D
- E
- F 基础版
- G

## 8. 我建议的默认执行方案

建议优先选择：

- 方案三，但把 AI 缩成“旅行记录创建页单点接入”

原因：

- 能满足老师全部要求
- 改动范围可控
- 风险主要集中在角色鉴权和 AI 调用两处，便于分批验证

## 9. 本清单对应的后续输出

审核通过后，我可以继续输出：

1. 数据库变更清单
2. 后端接口设计清单
3. 前端页面与路由清单
4. AI 接口接入方案
5. 分步骤实施计划

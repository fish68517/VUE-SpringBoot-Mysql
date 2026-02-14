# 设计文档 - 文山壮族刺绣网站系统

## 概述

文山壮族刺绣网站系统是一个前后分离的 Web 应用，采用 SpringBoot 后端 + Vue 前端的架构。系统分为前台用户界面和后台管理界面两部分，通过 RESTful API 进行通信。所有接口无需复杂认证，支持直接访问。

**技术栈**：
- 后端：SpringBoot + MySQL
- 前端：Vue + HTML5 + CSS3
- 数据库：MySQL
- 通信：RESTful API（JSON）

---

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     前端应用 (Vue)                           │
├──────────────────────┬──────────────────────────────────────┤
│   前台用户界面       │        后台管理界面                   │
│  (首页、作品、知识   │   (用户管理、资源管理、              │
│   互动、用户中心)    │    互动管理、系统管理)               │
└──────────────────────┴──────────────────────────────────────┘
                            ↓ HTTP/REST API
┌─────────────────────────────────────────────────────────────┐
│                  后端应用 (SpringBoot)                       │
├──────────────────────────────────────────────────────────────┤
│  控制层 (Controller) → 业务层 (Service) → 数据层 (Repository)│
└──────────────────────────────────────────────────────────────┘
                            ↓ JDBC
┌─────────────────────────────────────────────────────────────┐
│                    MySQL 数据库                              │
└─────────────────────────────────────────────────────────────┘
```

### 分层架构

**后端分层**：
1. **控制层 (Controller)**: 处理 HTTP 请求，调用业务层
2. **业务层 (Service)**: 实现业务逻辑，调用数据层
3. **数据层 (Repository)**: 数据库操作，使用 JPA 或 MyBatis
4. **实体层 (Entity)**: 数据模型定义

**前端分层**：
1. **页面层 (Pages/Views)**: 用户界面组件
2. **组件层 (Components)**: 可复用的 UI 组件
3. **服务层 (Services)**: API 调用和业务逻辑
4. **状态管理 (Vuex/Pinia)**: 全局状态管理

---

## 组件和接口设计

### 后端 API 接口

#### 1. 首页相关接口

```
GET  /api/home/carousel          - 获取轮播内容
GET  /api/home/features          - 获取核心功能导航
GET  /api/home/announcements     - 获取最新活动和公告
GET  /api/home/statistics        - 获取平台访问数据
```

#### 2. 作品相关接口

```
GET  /api/artworks               - 获取作品列表（支持分类、分页）
GET  /api/artworks/:id           - 获取作品详情
GET  /api/artworks/categories    - 获取作品分类列表
POST /api/artworks/:id/view      - 记录作品浏览
POST /api/artworks/:id/collect   - 收藏作品
DELETE /api/artworks/:id/collect - 取消收藏
```

#### 3. 知识相关接口

```
GET  /api/knowledge              - 获取知识文章列表（支持分类、分页）
GET  /api/knowledge/:id          - 获取知识文章详情
GET  /api/knowledge/categories   - 获取知识分类列表
POST /api/knowledge/:id/view     - 记录知识浏览
```

#### 4. 评论相关接口

```
GET  /api/comments               - 获取评论列表（按作品或话题）
POST /api/comments               - 发布评论
DELETE /api/comments/:id         - 删除评论（管理员）
POST /api/comments/:id/reply     - 回复评论（管理员）
```

#### 5. 话题相关接口

```
GET  /api/topics                 - 获取话题列表
GET  /api/topics/:id             - 获取话题详情
POST /api/topics                 - 创建话题（管理员）
PUT  /api/topics/:id             - 编辑话题（管理员）
DELETE /api/topics/:id           - 删除话题（管理员）
POST /api/topics/:id/pin         - 置顶话题（管理员）
```

#### 6. 投票相关接口

```
GET  /api/votes                  - 获取投票活动列表
GET  /api/votes/:id              - 获取投票详情和统计
POST /api/votes/:id/vote         - 提交投票
```

#### 7. 反馈相关接口

```
POST /api/feedback               - 提交用户反馈
GET  /api/feedback               - 获取反馈列表（管理员）
```

#### 8. 用户相关接口

```
POST /api/users/register         - 用户注册
POST /api/users/login            - 用户登录
GET  /api/users/:id              - 获取用户信息
PUT  /api/users/:id              - 更新用户信息
GET  /api/users/:id/collections  - 获取用户收藏列表
GET  /api/users/:id/history      - 获取用户浏览历史
```

#### 9. 用户管理接口（后台）

```
GET  /api/admin/users            - 获取用户列表
POST /api/admin/users            - 创建用户
PUT  /api/admin/users/:id        - 修改用户信息
DELETE /api/admin/users/:id      - 删除用户
GET  /api/admin/users/:id/logs   - 获取用户行为日志
POST /api/admin/messages         - 发送消息推送
```

#### 10. 资源管理接口（后台）

```
POST /api/admin/artworks         - 上传作品
PUT  /api/admin/artworks/:id     - 编辑作品
DELETE /api/admin/artworks/:id   - 删除作品
POST /api/admin/artworks/:id/approve - 审核作品
POST /api/admin/artworks/:id/reject  - 拒绝作品
POST /api/admin/artworks/:id/offline - 下架作品

POST /api/admin/news             - 发布资讯
PUT  /api/admin/news/:id         - 编辑资讯
DELETE /api/admin/news/:id       - 删除资讯

POST /api/admin/activities       - 发布活动
PUT  /api/admin/activities/:id   - 编辑活动
DELETE /api/admin/activities/:id - 删除活动
```

#### 11. 互动管理接口（后台）

```
GET  /api/admin/comments         - 获取评论列表
DELETE /api/admin/comments/:id   - 删除评论
POST /api/admin/comments/:id/reply - 回复评论

GET  /api/admin/topics           - 获取话题列表
POST /api/admin/topics/:id/pin   - 置顶话题
DELETE /api/admin/topics/:id     - 删除话题

GET  /api/admin/votes/:id/stats  - 获取投票统计

GET  /api/admin/feedback         - 获取反馈列表
```

#### 12. 系统管理接口（后台）

```
GET  /api/admin/settings         - 获取系统设置
PUT  /api/admin/settings         - 更新系统设置
POST /api/admin/backup           - 执行数据备份
POST /api/admin/restore          - 执行数据恢复
GET  /api/admin/statistics       - 获取访问统计数据
```

---

## 数据模型设计

### 核心实体

#### 1. 用户 (User)
```
- id: Long (主键)
- username: String (用户名，唯一)
- password: String (密码，加密存储)
- email: String (邮箱)
- avatar: String (头像 URL)
- bio: String (个人简介)
- role: String (角色：user/admin)
- createdAt: DateTime (创建时间)
- updatedAt: DateTime (更新时间)
```

#### 2. 作品 (Artwork)
```
- id: Long (主键)
- title: String (作品名称)
- description: String (作品描述)
- category: String (分类：日常生活类/节日母题类/针法风格类)
- imageUrl: String (作品图片 URL)
- creator: String (创作者名称)
- technique: String (刺绣技法)
- status: String (状态：draft/approved/rejected/offline)
- viewCount: Integer (浏览次数)
- collectCount: Integer (收藏次数)
- createdAt: DateTime (创建时间)
- updatedAt: DateTime (更新时间)
```

#### 3. 知识 (Knowledge)
```
- id: Long (主键)
- title: String (文章标题)
- content: String (文章内容)
- category: String (分类：技法知识/历史文化/政策法规/常见问题)
- author: String (作者)
- viewCount: Integer (浏览次数)
- createdAt: DateTime (创建时间)
- updatedAt: DateTime (更新时间)
```

#### 4. 评论 (Comment)
```
- id: Long (主键)
- content: String (评论内容)
- userId: Long (用户 ID)
- artworkId: Long (作品 ID，可为空)
- topicId: Long (话题 ID，可为空)
- parentId: Long (父评论 ID，用于回复)
- createdAt: DateTime (创建时间)
- updatedAt: DateTime (更新时间)
```

#### 5. 话题 (Topic)
```
- id: Long (主键)
- title: String (话题标题)
- description: String (话题描述)
- isPinned: Boolean (是否置顶)
- createdBy: Long (创建者 ID)
- createdAt: DateTime (创建时间)
- updatedAt: DateTime (更新时间)
```

#### 6. 投票 (Vote)
```
- id: Long (主键)
- title: String (投票标题)
- description: String (投票描述)
- options: JSON (投票选项列表)
- status: String (状态：active/closed)
- createdAt: DateTime (创建时间)
- endAt: DateTime (结束时间)
```

#### 7. 投票记录 (VoteRecord)
```
- id: Long (主键)
- voteId: Long (投票 ID)
- userId: Long (用户 ID)
- selectedOption: String (选择的选项)
- createdAt: DateTime (投票时间)
```

#### 8. 反馈 (Feedback)
```
- id: Long (主键)
- userId: Long (用户 ID)
- content: String (反馈内容)
- email: String (联系邮箱)
- status: String (状态：pending/processed)
- createdAt: DateTime (创建时间)
```

#### 9. 浏览历史 (ViewHistory)
```
- id: Long (主键)
- userId: Long (用户 ID)
- contentType: String (内容类型：artwork/knowledge)
- contentId: Long (内容 ID)
- viewedAt: DateTime (浏览时间)
```

#### 10. 收藏 (Collection)
```
- id: Long (主键)
- userId: Long (用户 ID)
- artworkId: Long (作品 ID)
- collectedAt: DateTime (收藏时间)
```

#### 11. 操作日志 (OperationLog)
```
- id: Long (主键)
- userId: Long (用户 ID)
- action: String (操作类型：login/view/comment/collect等)
- targetType: String (目标类型：artwork/knowledge/topic等)
- targetId: Long (目标 ID)
- createdAt: DateTime (操作时间)
```

#### 12. 系统设置 (SystemSettings)
```
- id: Long (主键)
- siteName: String (网站名称)
- siteDescription: String (网站描述)
- logo: String (logo URL)
- contactEmail: String (联系邮箱)
- contactPhone: String (联系电话)
- updatedAt: DateTime (更新时间)
```

---

## 前端页面结构

### 前台页面

1. **首页** (`/`)
   - 轮播展示区
   - 核心功能导航
   - 最新活动公告
   - 平台数据统计

2. **作品展示** (`/artworks`)
   - 分类筛选
   - 作品列表（网格/列表视图）
   - 作品详情页面
   - 评论区

3. **知识科普** (`/knowledge`)
   - 分类筛选
   - 文章列表
   - 文章详情页面

4. **互动交流** (`/community`)
   - 话题列表
   - 话题详情和讨论
   - 投票活动列表
   - 投票详情和结果

5. **用户中心** (`/user`)
   - 登录/注册页面
   - 个人信息页面
   - 收藏夹管理
   - 浏览历史
   - 反馈表单

### 后台页面

1. **用户管理** (`/admin/users`)
   - 用户列表
   - 用户详情和编辑
   - 权限管理
   - 行为日志查看
   - 消息推送

2. **资源管理** (`/admin/resources`)
   - 作品管理（上传、审核、下架）
   - 资讯管理
   - 活动管理

3. **互动管理** (`/admin/interactions`)
   - 评论管理
   - 话题管理
   - 投票统计
   - 反馈处理

4. **系统管理** (`/admin/system`)
   - 网站设置
   - 数据备份和恢复
   - 访问统计分析

---

## 错误处理

### 统一响应格式

所有 API 响应采用统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 错误码定义

- `200`: 成功
- `400`: 请求参数错误
- `401`: 未授权（用户未登录）
- `403`: 禁止访问（权限不足）
- `404`: 资源不存在
- `500`: 服务器内部错误

### 异常处理

- 所有异常在 Controller 层统一捕获
- 返回对应的错误码和错误信息
- 记录异常日志用于调试

---

## 测试策略

### 单元测试

- 测试 Service 层的业务逻辑
- 测试数据验证和转换
- 测试工具类和辅助方法

### 集成测试

- 测试 API 端点的完整流程
- 测试数据库操作
- 测试前后端交互

### 前端测试

- 测试页面组件的渲染
- 测试用户交互和事件处理
- 测试 API 调用和数据绑定

### 测试覆盖范围

- 核心业务逻辑（作品管理、评论、投票等）
- 用户认证和授权
- 数据持久化操作
- 错误处理和边界情况

---

## 安全考虑

### 数据安全

- 用户密码使用 BCrypt 加密存储
- 敏感数据不在日志中输出
- 数据库连接使用加密

### 访问控制

- 后台接口通过角色检查进行权限控制
- 用户只能访问自己的数据
- 管理员操作记录详细日志

### 输入验证

- 所有用户输入进行参数验证
- 防止 SQL 注入（使用参数化查询）
- 防止 XSS 攻击（输出转义）

---

## 部署架构

### 开发环境

- 本地 MySQL 数据库
- SpringBoot 开发服务器
- Vue 开发服务器

### 生产环境

- 独立的 MySQL 服务器
- SpringBoot 应用服务器
- 前端静态文件服务器
- 可选：Nginx 反向代理

---

## 性能优化

### 数据库优化

- 为常用查询字段添加索引
- 使用分页查询大数据集
- 缓存热点数据

### 前端优化

- 组件懒加载
- 图片压缩和懒加载
- 减少 API 调用次数

### 后端优化

- 使用缓存减少数据库查询
- 异步处理耗时操作
- 连接池管理


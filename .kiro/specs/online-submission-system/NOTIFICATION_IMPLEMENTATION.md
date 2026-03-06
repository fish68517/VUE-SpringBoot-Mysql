# 消息通知系统实现总结

## 概述
实现了完整的消息通知系统，支持用户注册审核通知、稿件状态变更通知和审稿任务分配通知。

## 后端实现

### 1. NotificationService (核心服务)
**文件**: `SpringBoot/src/main/java/com/submission/service/NotificationService.java`

**主要功能**:
- `sendUserApprovalNotification()` - 发送用户注册审核通知（批准/驳回）
- `sendManuscriptStatusNotification()` - 发送稿件状态变更通知
- `sendReviewerAssignmentNotification()` - 发送审稿人分配通知
- `sendInitialReviewNotification()` - 发送初审结果通知
- `sendReviewSubmissionNotification()` - 发送审稿意见提交通知
- `sendNotification()` - 通用通知发送方法

**特点**:
- 支持通知模板系统，可从数据库读取模板
- 提供默认通知内容生成
- 支持模板变量替换（如 {username}, {manuscriptTitle} 等）

### 2. NotificationController (API接口)
**文件**: `SpringBoot/src/main/java/com/submission/controller/NotificationController.java`

**API端点**:
- `GET /api/notifications` - 获取当前用户所有通知
- `GET /api/notifications/unread-count` - 获取未读通知数
- `POST /api/notifications/{notificationId}/read` - 标记通知为已读
- `GET /api/notifications/{notificationId}` - 获取单个通知详情

### 3. 服务集成

#### UserManagementService
- 在 `approveUser()` 中集成用户批准通知
- 在 `rejectUser()` 中集成用户驳回通知

#### EditorService
- 在 `submitInitialReview()` 中集成初审结果通知
- 在 `assignReviewer()` 中集成审稿人分配通知

#### ReviewerService
- 在 `submitReviewOpinion()` 中集成审稿意见提交通知

#### ManuscriptService
- 在 `submitManuscript()` 中集成稿件提交通知

### 4. MessageService 扩展
- 添加 `getMessageById()` 方法用于获取单个消息

### 5. 数据库初始化
**文件**: `SpringBoot/src/main/resources/db/notification_templates.sql`

**预定义模板**:
- USER_APPROVED - 用户批准通知
- USER_REJECTED - 用户驳回通知
- MANUSCRIPT_REJECTED - 稿件驳回通知
- MANUSCRIPT_REVISION_REQUIRED - 稿件需修改通知
- MANUSCRIPT_ACCEPTED - 稿件接受通知
- MANUSCRIPT_UNDER_REVIEW - 稿件审稿中通知
- INITIAL_REVIEW_PASS - 初审通过通知
- INITIAL_REVIEW_REJECT - 初审驳回通知
- INITIAL_REVIEW_REVISION - 初审需修改通知
- REVIEWER_ASSIGNED - 审稿人分配通知

## 前端实现

### 1. notificationService.js (API服务)
**文件**: `VUE/src/services/notificationService.js`

**功能**:
- `getNotifications()` - 获取所有通知
- `getUnreadCount()` - 获取未读数
- `markAsRead()` - 标记为已读
- `getNotificationById()` - 获取单个通知

### 2. notificationStore.js (Pinia状态管理)
**文件**: `VUE/src/stores/notificationStore.js`

**状态**:
- `notifications` - 通知列表
- `unreadCount` - 未读数
- `loading` - 加载状态
- `error` - 错误信息

**方法**:
- `fetchNotifications()` - 获取通知
- `fetchUnreadCount()` - 获取未读数
- `markAsRead()` - 标记已读
- `getNotificationById()` - 获取单个通知
- `clearNotifications()` - 清空通知

**计算属性**:
- `unreadNotifications` - 未读通知列表
- `readNotifications` - 已读通知列表

### 3. NotificationBell.vue (通知组件)
**文件**: `VUE/src/components/NotificationBell.vue`

**功能**:
- 显示未读通知数的铃铛图标
- 侧边栏抽屉式通知面板
- 支持全部消息和未读消息两个标签页
- 点击通知自动标记为已读
- 显示通知类型、时间和内容
- 自动刷新未读数（每30秒）

**特点**:
- 响应式设计
- 支持消息类型显示（系统通知/编辑沟通）
- 相对时间显示（刚刚、X分钟前等）
- 空状态提示

## 需求覆盖

### 需求 10.3 - 编辑发送审核结果通知
✅ 通过 `EditorService.submitInitialReview()` 集成
- 发送初审结果通知给作者
- 支持通过/驳回/退修三种状态

### 需求 10.4 - 编辑与审稿人沟通
✅ 通过 `EditorService.assignReviewer()` 集成
- 发送审稿人分配通知
- 支持编辑与审稿人的消息沟通

### 需求 17.2 - 系统发送通知
✅ 完整实现
- 用户注册审核通知
- 稿件状态变更通知
- 审稿任务分配通知
- 审稿意见提交通知

## 通知流程

### 用户注册审核流程
1. 管理员批准/驳回用户注册
2. UserManagementService 调用 NotificationService
3. 通知保存到 messages 表
4. 用户登录后在消息中心查看

### 稿件状态变更流程
1. 编辑提交初审意见
2. EditorService 调用 NotificationService
3. 根据初审结果发送相应通知
4. 作者在消息中心查看

### 审稿人分配流程
1. 编辑分配审稿人
2. EditorService 调用 NotificationService
3. 审稿人收到分配通知
4. 审稿人在消息中心查看并接受/拒绝任务

## 数据库表结构

### messages 表
- id: 消息ID
- sender_id: 发送者ID（系统为0）
- recipient_id: 接收者ID
- manuscript_id: 稿件ID（可选）
- content: 消息内容
- type: 消息类型（NOTIFICATION/COMMUNICATION）
- is_read: 是否已读
- created_at: 创建时间

### notification_templates 表
- id: 模板ID
- template_name: 模板名称
- template_content: 模板内容
- template_type: 模板类型
- created_at: 创建时间
- updated_at: 更新时间

## 使用说明

### 后端使用
```java
// 发送用户批准通知
notificationService.sendUserApprovalNotification(userId, true);

// 发送稿件状态变更通知
notificationService.sendManuscriptStatusNotification(manuscriptId, "DRAFT", "SUBMITTED");

// 发送审稿人分配通知
notificationService.sendReviewerAssignmentNotification(reviewerId, manuscriptId, editorId);
```

### 前端使用
```javascript
// 在组件中使用
import { useNotificationStore } from '@/stores/notificationStore';

const notificationStore = useNotificationStore();

// 获取通知
await notificationStore.fetchNotifications();

// 标记为已读
await notificationStore.markAsRead(notificationId);

// 获取未读数
const unreadCount = notificationStore.unreadCount;
```

## 扩展性

系统设计支持以下扩展：
1. 添加新的通知模板
2. 支持邮件/短信通知（通过扩展 NotificationService）
3. 支持通知优先级
4. 支持通知分类和过滤
5. 支持批量操作

## 测试建议

1. 测试用户注册审核通知
2. 测试稿件状态变更通知
3. 测试审稿人分配通知
4. 测试通知标记为已读
5. 测试未读数统计
6. 测试通知模板替换

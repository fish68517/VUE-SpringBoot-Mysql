# 旅游规划与记忆系统 - 实现任务列表

## 第一阶段：项目初始化与基础设施

- [x] 1. 初始化项目结构和配置





  - 创建 SpringBoot 项目目录结构（controllers、services、repositories、entities、config、utils）
  - 创建 Vue.js 3 项目目录结构（src/components、src/views、src/stores、src/router、src/services）
  - 配置 pom.xml 依赖（Spring Boot、Spring Security、MySQL、JWT、Lombok 等）
  - 配置 package.json 依赖（Vue 3、Element Plus、Vue Router、Pinia、Axios、Quill 等）
  - 配置 application.properties（MySQL 连接、JWT 密钥、文件上传路径）
  - _需求: 1.1, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1, 8.1, 9.1_

- [x] 2. 创建数据库和表结构




  - 编写 SQL 脚本创建 MySQL 数据库
  - 创建 users 表（用户表）
  - 创建 travel_records 表（旅游记录表）
  - 创建 multimedia_files 表（多媒体文件表）
  - 创建 travel_plans 表（旅游计划表）
  - 创建 itinerary_items 表（行程项表）
  - 创建 map_footprints 表（地图足迹表）
  - 创建 comments 表（评论表）
  - 创建 likes 表（点赞表）
  - 为所有表添加必要的索引和外键约束
  - _需求: 6.1, 6.2, 6.3, 6.4_

- [x] 3. 配置 Spring Security 和 JWT 认证框架





  - 创建 JwtTokenProvider 类，实现 JWT 令牌的生成和验证
  - 创建 JwtAuthenticationFilter 过滤器，拦截请求并验证 JWT 令牌
  - 创建 SecurityConfig 配置类，配置 Spring Security 和 CORS
  - 创建 AuthenticationEntryPoint 处理未授权请求
  - 配置密码加密器（BCryptPasswordEncoder）
  - _需求: 1.2, 1.3, 10.1, 10.2_

- [x] 4. 配置 Vue Router 和 Pinia 状态管理




  - 创建 router/index.js，定义路由配置（公开路由和受保护路由）
  - 创建 stores/userStore.js，管理用户认证状态和用户信息
  - 创建 stores/travelStore.js，管理旅游记录数据
  - 创建 stores/planStore.js，管理旅游计划数据
  - 创建路由守卫，检查用户认证状态
  - _需求: 8.3, 8.4_

- [x] 5. 配置 Axios 和 API 通信





  - 创建 services/api.js，配置 Axios 实例和基础 URL
  - 创建 Axios 请求拦截器，自动添加 JWT 令牌到请求头
  - 创建 Axios 响应拦截器，处理错误和令牌过期
  - 创建 services/authService.js，封装认证相关的 API 调用
  - _需求: 8.5, 9.1_

## 第二阶段：用户认证与管理

- [x] 6. 实现用户注册功能





  - 创建 User 实体类，包含用户信息字段
  - 创建 UserRepository 接口，继承 JpaRepository
  - 创建 AuthService 类，实现用户注册逻辑（验证邮箱、密码加密、保存用户）
  - 创建 AuthController 类，实现 POST /api/auth/register 端点
  - 创建 UserRegister.vue 组件，包含注册表单和验证
  - 创建 registerService.js，调用后端注册 API
  - _需求: 1.1_

- [x] 7. 实现用户登录功能





  - 在 AuthService 中实现登录逻辑（验证凭证、生成 JWT 令牌）
  - 在 AuthController 中实现 POST /api/auth/login 端点
  - 创建 UserLogin.vue 组件，包含登录表单
  - 在 loginService.js 中实现登录 API 调用
  - 在 userStore 中保存 JWT 令牌和用户信息
  - _需求: 1.2_

- [x] 8. 实现 JWT 令牌验证和刷新





  - 在 AuthController 中实现 POST /api/auth/refresh 端点，刷新过期的 JWT 令牌
  - 在 Axios 响应拦截器中处理 401 错误，自动刷新令牌
  - 在 JwtTokenProvider 中实现令牌有效期检查（24 小时）
  - _需求: 1.3_

- [x] 9. 实现用户信息管理





  - 创建 UserService 类，实现用户信息查询和更新逻辑
  - 在 UserController 中实现 GET /api/users/{id} 端点
  - 在 UserController 中实现 PUT /api/users/{id} 端点
  - 创建 ProfileView.vue 页面，显示和编辑用户资料
  - 创建 ProfileEditor.vue 组件，包含资料编辑表单
  - 在 userService.js 中实现用户信息 API 调用
  - _需求: 1.4, 1.5_

- [x] 10. 实现权限检查和访问控制





  - 在 Service 层实现权限检查逻辑（验证用户是否为资源拥有者）
  - 在 Controller 层使用 @PreAuthorize 注解检查权限
  - 创建自定义权限检查方法，用于验证用户对资源的访问权限
  - _需求: 10.3_

## 第三阶段：旅游记录管理

- [x] 11. 实现旅游记录的创建功能





  - 创建 TravelRecord 实体类，包含记录信息字段
  - 创建 TravelRecordRepository 接口
  - 创建 TravelService 类，实现记录创建逻辑
  - 在 TravelController 中实现 POST /api/travels 端点
  - 创建 RecordCreateView.vue 页面，包含记录创建表单
  - 创建 travelService.js，调用后端创建记录 API
  - _需求: 2.1, 2.2_

- [x] 12. 实现旅游记录的查询功能




  - 在 TravelService 中实现获取用户记录列表的逻辑（分页、排序）
  - 在 TravelController 中实现 GET /api/travels 端点（支持分页参数）
  - 在 TravelController 中实现 GET /api/travels/{id} 端点
  - 创建 RecordListView.vue 页面，显示记录列表（分页）
  - 创建 RecordDetailView.vue 页面，显示记录详情
  - 在 travelService.js 中实现查询 API 调用
  - _需求: 2.3, 2.5_

- [x] 13. 实现旅游记录的编辑功能





  - 在 TravelService 中实现记录更新逻辑
  - 在 TravelController 中实现 PUT /api/travels/{id} 端点
  - 创建 TravelRecordEditor.vue 组件，集成 Quill 富文本编辑器
  - 实现日记内容的富文本编辑和保存
  - 在 travelService.js 中实现更新 API 调用
  - _需求: 2.4, 2.6_

- [x] 14. 实现旅游记录的删除功能




  - 在 TravelService 中实现记录删除逻辑（级联删除关联数据）
  - 在 TravelController 中实现 DELETE /api/travels/{id} 端点
  - 在 RecordDetailView 中添加删除按钮和确认对话框
  - 在 travelService.js 中实现删除 API 调用
  - _需求: 2.7_

- [x] 15. 实现权限设置和公开/私密功能





  - 在 TravelRecord 实体中添加 is_public 字段
  - 在记录创建和编辑时支持权限设置
  - 在 TravelService 中实现权限检查逻辑
  - 在 RecordDetailView 中添加权限设置选项
  - _需求: 5.1, 5.2_

## 第四阶段：多媒体文件管理

- [x] 16. 实现多媒体文件上传功能





  - 创建 MultimediaFile 实体类
  - 创建 MultimediaFileRepository 接口
  - 创建 FileService 类，实现文件上传逻辑（分片上传、文件验证、存储）
  - 在 FileController 中实现 POST /api/files/upload 端点
  - 创建 MultimediaUpload.vue 组件，支持分片上传和进度显示
  - 在 fileService.js 中实现分片上传 API 调用
  - _需求: 7.1, 7.2, 7.3_

- [x] 17. 实现多媒体文件的查询和删除





  - 在 FileService 中实现文件查询逻辑
  - 在 FileController 中实现 GET /api/files/{id} 端点
  - 在 FileService 中实现文件删除逻辑（删除服务器文件和数据库记录）
  - 在 FileController 中实现 DELETE /api/files/{id} 端点
  - 在 RecordDetailView 中显示多媒体文件列表和删除功能
  - _需求: 7.4, 7.5_

- [x] 18. 实现多媒体文件的展示











  - 在 RecordDetailView 中创建多媒体展示区域
  - 实现图片和视频的预览功能
  - 实现图片懒加载优化
  - _需求: 7.5_

## 第五阶段：地图足迹管理

- [x] 19. 实现地图足迹的添加功能





  - 创建 MapFootprint 实体类
  - 创建 MapFootprintRepository 接口
  - 创建 FootprintService 类，实现足迹添加逻辑
  - 在 FootprintController 中实现 POST /api/footprints 端点
  - 创建 MapFootprint.vue 组件，集成高德地图 API
  - 实现地理编码功能（地名转坐标）
  - 实现标记点（Marker）的添加
  - 在 footprintService.js 中实现足迹 API 调用
  - _需求: 3.1, 3.2_

- [x] 20. 实现地图足迹的查询和显示





  - 在 FootprintService 中实现足迹查询逻辑
  - 在 FootprintController 中实现 GET /api/travels/{travelId}/footprints 端点
  - 在 MapFootprint 组件中实现足迹的显示和更新
  - 实现路线（Polyline）的绘制
  - _需求: 3.4_

- [x] 21. 实现地图足迹的删除功能





  - 在 FootprintService 中实现足迹删除逻辑
  - 在 FootprintController 中实现 DELETE /api/footprints/{id} 端点
  - 在 MapFootprint 组件中添加删除功能
  - _需求: 3.5_

## 第六阶段：旅游计划管理

- [x] 22. 实现旅游计划的创建功能





  - 创建 TravelPlan 实体类
  - 创建 TravelPlanRepository 接口
  - 创建 PlanService 类，实现计划创建逻辑
  - 在 PlanController 中实现 POST /api/plans 端点
  - 创建 PlanCreateView.vue 页面，包含计划创建表单
  - 在 planService.js 中实现创建计划 API 调用
  - _需求: 4.1, 4.2_

- [x] 23. 实现旅游计划的查询功能





  - 在 PlanService 中实现计划列表查询逻辑
  - 在 PlanController 中实现 GET /api/plans 端点
  - 在 PlanController 中实现 GET /api/plans/{id} 端点
  - 创建 PlanListView.vue 页面，显示计划列表
  - 创建 PlanDetailView.vue 页面，显示计划详情和行程项
  - 在 planService.js 中实现查询 API 调用
  - _需求: 4.1_

- [x] 24. 实现旅游计划的编辑和删除功能





  - 在 PlanService 中实现计划更新和删除逻辑
  - 在 PlanController 中实现 PUT /api/plans/{id} 端点
  - 在 PlanController 中实现 DELETE /api/plans/{id} 端点
  - 在 PlanDetailView 中添加编辑和删除功能
  - _需求: 4.1_

- [x] 25. 实现行程项的管理功能





  - 创建 ItineraryItem 实体类
  - 创建 ItineraryItemRepository 接口
  - 创建 ItineraryService 类，实现行程项的 CRUD 操作
  - 在 ItineraryController 中实现 POST /api/plans/{planId}/items 端点
  - 在 ItineraryController 中实现 PUT /api/items/{id} 端点
  - 在 ItineraryController 中实现 DELETE /api/items/{id} 端点
  - 创建 ItineraryEditor.vue 组件，支持添加、编辑、删除行程项
  - 在 PlanDetailView 中集成 ItineraryEditor 组件
  - 在 itineraryService.js 中实现行程项 API 调用
  - _需求: 4.3, 4.4, 4.5, 4.6_

## 第七阶段：社交互动功能

- [x] 26. 实现社交动态列表功能





  - 在 TravelService 中实现公开记录查询逻辑（分页、排序）
  - 在 TravelController 中实现 GET /api/travels/public 端点
  - 创建 SocialFeedView.vue 页面，显示公开记录列表
  - 创建 FeedCard.vue 组件，显示单条记录卡片（包含点赞数、评论数）
  - 在 travelService.js 中实现查询公开记录 API 调用
  - _需求: 5.7_

- [x] 27. 实现评论功能





  - 创建 Comment 实体类
  - 创建 CommentRepository 接口
  - 创建 CommentService 类，实现评论的创建和查询逻辑
  - 在 CommentController 中实现 POST /api/comments 端点
  - 在 CommentController 中实现 GET /api/travels/{travelId}/comments 端点
  - 在 CommentController 中实现 DELETE /api/comments/{id} 端点
  - 创建 CommentSection.vue 组件，显示评论列表和评论表单
  - 在 commentService.js 中实现评论 API 调用
  - _需求: 5.4_

- [x] 28. 实现点赞功能





  - 创建 Like 实体类
  - 创建 LikeRepository 接口
  - 创建 LikeService 类，实现点赞的创建和删除逻辑
  - 在 LikeController 中实现 POST /api/likes 端点
  - 在 LikeController 中实现 DELETE /api/likes/{id} 端点
  - 在 TravelService 中实现点赞计数查询
  - 在 FeedCard 和 RecordDetailView 中添加点赞按钮
  - 在 likeService.js 中实现点赞 API 调用
  - _需求: 5.5, 5.6_

## 第八阶段：前端页面和组件集成

- [x] 29. 创建主导航和布局组件





  - 创建 App.vue 主应用组件
  - 创建 Header.vue 导航栏组件
  - 创建 Sidebar.vue 侧边栏组件
  - 创建 Footer.vue 页脚组件
  - 实现响应式布局
  - _需求: 8.1, 8.2_

- [x] 30. 创建仪表板页面





  - 创建 DashboardView.vue 页面
  - 创建 StatisticsCard.vue 组件，显示用户统计信息（记录数、计划数、点赞数等）
  - 实现数据统计和展示
  - _需求: 8.1_

- [x] 31. 优化前端用户体验





  - 实现加载状态和骨架屏
  - 实现错误提示和成功提示
  - 实现表单验证和错误提示
  - 实现图片懒加载
  - 实现虚拟滚动显示大列表
  - _需求: 8.1, 8.2_

## 第九阶段：错误处理和日志

- [x] 32. 实现后端全局异常处理





  - 创建自定义异常类（BusinessException、ValidationException 等）
  - 创建 GlobalExceptionHandler 类，使用 @ControllerAdvice 处理异常
  - 实现统一的错误响应格式
  - 添加日志记录
  - _需求: 9.1, 9.2_

- [x] 33. 实现前端错误处理





  - 创建全局错误处理器
  - 实现 Axios 错误拦截和处理
  - 实现用户友好的错误提示
  - _需求: 9.1_

## 第十阶段：数据验证和安全

- [x] 34. 实现后端数据验证





  - 使用 Bean Validation 注解验证请求参数
  - 在 Service 层实现业务逻辑验证
  - 实现自定义验证器
  - _需求: 10.4_

- [x] 35. 实现前端数据验证





  - 在表单组件中实现客户端验证
  - 实现字段级错误提示
  - _需求: 10.4_

- [x] 36. 实现安全防护





  - 配置 HTTPS 和 CORS
  - 实现 CSRF 防护
  - 实现 XSS 防护（输入清理）
  - 实现 SQL 注入防护（使用参数化查询）
  - _需求: 10.1, 10.2, 10.3, 10.4, 10.5_

## 第十一阶段：测试

- [ ] 37. 编写后端单元测试
  - 为 Service 层编写单元测试（使用 JUnit 5 和 Mockito）
  - 测试用户认证逻辑
  - 测试旅游记录的 CRUD 操作
  - 测试权限检查逻辑
  - _需求: 1.1, 1.2, 2.1, 2.6, 5.1_

- [ ] 38. 编写后端集成测试
  - 为 Controller 层编写集成测试（使用 Spring Boot Test）
  - 测试 API 端点的请求和响应
  - 测试数据库操作
  - _需求: 9.1, 9.2_

- [ ] 39. 编写前端单元测试*
  - 为 Vue 组件编写单元测试（使用 Vitest）
  - 测试组件的渲染和交互
  - 测试 Pinia store 的状态管理
  - _需求: 8.1, 8.4_

- [ ] 40. 编写前端集成测试*
  - 测试组件间的交互
  - 测试 API 调用和数据流
  - _需求: 8.1, 8.5_

## 第十二阶段：部署和优化

- [ ] 41. 配置生产环境
  - 配置生产环境的 application.properties
  - 配置数据库连接池
  - 配置日志级别
  - _需求: 6.1_

- [ ] 42. 优化数据库性能
  - 验证所有索引是否正确创建
  - 测试查询性能
  - 优化慢查询
  - _需求: 6.4_

- [ ] 43. 优化前端性能
  - 构建生产版本
  - 配置 CDN 加速
  - 实现代码分割和懒加载
  - _需求: 8.1, 8.2_

- [x] 44. 创建示例数据和文档





  - 编写 SQL 脚本创建示例数据
  - 编写 API 文档（Swagger/OpenAPI）
  - 编写部署指南
  - _需求: 6.1_


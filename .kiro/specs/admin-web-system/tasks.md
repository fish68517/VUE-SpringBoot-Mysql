# 实现计划 - 管理员Web系统

## 后端实现任务

- [x] 1. 设置Spring Boot项目结构和核心配置




  - 创建项目目录结构：controller、service、mapper、entity、dto、vo、config、utils
  - 配置pom.xml依赖（Spring Boot、MyBatis、MySQL驱动等）
  - 创建application.yml配置文件（数据库连接、日志配置）
  - 配置全局CORS允许所有来源访问
  - 配置静态资源映射使/image/**可以访问image文件夹
  - _需求: 7.1, 7.2, 7.3_

- [x] 2. 创建数据库表和Entity类




  - 创建MySQL数据库和所有表（users、posts、comments、fitness_plans、exercise_data）
  - 编写Entity类映射数据库表
  - 添加Entity类的getter/setter方法
  - _需求: 2.1, 3.1, 4.1, 5.1_

- [x] 3. 实现管理员认证功能




  - 创建Admin实体类和AdminDTO
  - 编写AdminMapper接口和XML映射文件
  - 实现AdminService业务逻辑（登录验证）
  - 创建AdminController处理登录请求
  - 实现登录密码明文验证逻辑
  - _需求: 1.1, 1.2, 1.3_

- [x] 4. 实现用户管理功能





  - 创建UserMapper接口和XML映射文件（查询、更新、删除操作）
  - 实现UserService业务逻辑
  - 创建UserController处理用户管理请求
  - 实现用户列表查询接口（支持分页和搜索）
  - 实现用户详情查询接口
  - 实现用户信息编辑接口
  - 实现用户删除接口
  - _需求: 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 5. 实现社区管理功能





  - 创建PostMapper和CommentMapper接口和XML映射文件
  - 实现PostService和CommentService业务逻辑
  - 创建PostController和CommentController
  - 实现帖子列表查询接口（支持分页）
  - 实现帖子详情查询接口（包含评论和点赞信息）
  - 实现删除帖子接口
  - 实现删除评论接口
  - _需求: 3.1, 3.2, 3.3, 3.4_

- [x] 6. 实现健身计划管理功能




  - 创建FitnessPlanMapper接口和XML映射文件
  - 实现FitnessPlanService业务逻辑
  - 创建FitnessPlanController
  - 实现健身计划列表查询接口（支持分页）
  - 实现计划详情查询接口
  - 实现计划编辑接口
  - 实现计划删除接口
  - _需求: 4.1, 4.2, 4.3, 4.4_

- [x] 7. 实现运动数据管理功能





  - 创建ExerciseDataMapper接口和XML映射文件
  - 实现ExerciseDataService业务逻辑
  - 创建ExerciseDataController
  - 实现运动数据列表查询接口（支持分页）
  - 实现运动数据详情查询接口
  - 实现运动数据删除接口
  - _需求: 5.1, 5.2, 5.3_

- [x] 8. 实现图片上传和访问功能




  - 创建FileUploadController处理图片上传
  - 实现图片保存到image文件夹的逻辑
  - 在数据库中保存图片文件名或相对路径
  - 配置/image/**路径的静态资源访问
  - _需求: 6.1, 6.2, 6.3, 6.4_

- [x] 9. 实现全局异常处理和统一响应格式





  - 创建统一的API响应类（ApiResponse）
  - 创建全局异常处理器（GlobalExceptionHandler）
  - 实现所有异常的统一处理和错误码映射
  - 配置日志输出为中文
  - _需求: 7.3, 7.4_

## 前端实现任务

- [x] 10. 设置Vue 3项目结构和基础配置




  - 创建项目目录结构：pages、components、router、store、api、utils
  - 配置vite.config.js
  - 配置package.json依赖（Vue 3、Vue Router、Pinia、Axios、Element Plus）
  - 配置Axios基础URL和拦截器
  - _需求: 8.1, 8.2, 8.3, 8.4_

- [x] 11. 实现登录页面和认证流程





  - 创建Login.vue页面
  - 实现登录表单（用户名、密码输入框）
  - 创建admin API模块（api/admin.js）
  - 实现登录请求逻辑
  - 实现登录成功后跳转到主页面
  - 实现登录失败错误提示
  - _需求: 1.1, 1.2, 1.3, 1.4_

- [x] 12. 实现管理员主页面和布局





  - 创建Dashboard.vue主页面
  - 创建Sidebar.vue左侧边栏导航
  - 创建Header.vue顶部栏
  - 配置Vue Router路由
  - 实现页面布局（左侧边栏+内容区）
  - 实现导航菜单链接
  - _需求: 8.1, 8.2, 8.3, 8.4, 8.5_

- [x] 13. 实现用户管理页面




  - 创建UserManagement.vue页面
  - 创建user API模块（api/user.js）
  - 实现用户列表表格显示
  - 实现用户搜索功能
  - 实现用户编辑对话框
  - 实现用户删除功能
  - 实现分页功能
  - _需求: 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 14. 实现社区管理页面





  - 创建CommunityManagement.vue页面
  - 创建community API模块（api/community.js）
  - 实现帖子列表表格显示
  - 实现帖子详情查看（包含评论和点赞）
  - 实现删除帖子功能
  - 实现删除评论功能
  - 实现分页功能
  - _需求: 3.1, 3.2, 3.3, 3.4_

- [x] 15. 实现健身计划管理页面





  - 创建FitnessPlanManagement.vue页面
  - 创建fitnessPlan API模块（api/fitnessPlan.js）
  - 实现健身计划列表表格显示
  - 实现计划详情查看
  - 实现计划编辑对话框
  - 实现计划删除功能
  - 实现分页功能
  - _需求: 4.1, 4.2, 4.3, 4.4_

- [x] 16. 实现运动数据管理页面




  - 创建ExerciseDataManagement.vue页面
  - 创建exerciseData API模块（api/exerciseData.js）
  - 实现运动数据列表表格显示
  - 实现运动数据详情查看
  - 实现运动数据删除功能
  - 实现分页功能
  - _需求: 5.1, 5.2, 5.3_

- [x] 17. 实现图片上传功能





  - 创建图片上传组件
  - 实现文件选择和预览
  - 实现上传请求逻辑
  - 实现上传成功提示
  - _需求: 6.1, 6.2, 6.3, 6.4_

- [x] 18. 实现Pinia状态管理




  - 创建admin store（存储登录状态、用户信息）
  - 创建user store（存储用户列表数据）
  - 创建community store（存储社区数据）
  - 实现状态持久化（localStorage）
  - _需求: 8.2_

- [x] 19. 实现路由守卫和权限控制




  - 配置路由元信息
  - 实现全局前置守卫检查登录状态
  - 实现未登录用户重定向到登录页
  - _需求: 1.4_

- [x] 20. 优化UI和用户体验





  - 统一使用Element Plus组件
  - 实现加载状态显示
  - 实现错误提示
  - 实现成功提示
  - 调整页面样式和布局
  - _需求: 8.3, 8.4, 8.5_


这是一个非常专业且详尽的毕业设计（全栈开发）提示词。为了让你能够直接使用中文指令生成代码或作为项目需求文档，我将其进行了**深度汉化和专业术语本地化**。

你可以直接使用以下内容发送给 AI：

***

**角色设定：**
你是一位在构建旅游规划与记忆系统方面拥有深厚造诣的全栈开发专家，请生成一个完整、功能可用且通过验证的 Web 应用程序项目。该系统需集成旅游规划、记忆管理、多媒体记录、地图足迹和社交互动功能，旨在解决旅游记忆碎片化以及缺乏集规划、记录、回忆、分享于一体的综合支持问题。

### 一、 背景与意义
*   **背景：** 随着生活水平的提高，旅游已成为放松身心和体验生活的重要方式。现有应用大多专注于行前规划或导航，缺乏全周期支持（规划、记录、回忆、分享）。用户急需个性化、可视化且交互性强的工具。
*   **意义：**
    1.  **用户价值：** 提供一站式服务，实现高效规划、系统记录、足迹可视化及社交分享，提升情感与社交价值。
    2.  **技术实践：** 采用 Vue.js 3、SpringBoot、MySQL 进行全栈开发，提升工程化能力。
    3.  **行业价值：** 创新旅游+记忆+社交的融合模式，为智慧旅游提供参考。

### 二、 核心内容与待解决问题
*   **基础内容：** 构建一个平台，用于创建旅游记录（名称、目的地、日期）、上传照片/视频、撰写日记、标记地图足迹；创建计划（目的地、时间、预算、每日行程：景点、住宿、交通）；社交功能（公开/私密设置、评论、点赞）。
*   **待解决问题：**
    1.  **用户管理：** 注册、登录、个人资料管理、数据安全与隐私保护。
    2.  **旅游记忆：** 记录的创建/编辑/删除，多文件上传，富文本日记编辑器，地图足迹（标记点、路线）。
    3.  **旅游规划：** 计划的创建/修改/删除，添加每日事项（景点等）。
    4.  **分享/社交：** 公开/私密权限设置，对公开记录的评论与点赞。
    5.  **数据库/架构：** E-R 模型设计（表：用户、旅游记录、多媒体、行程项、评论、点赞；外键、事务处理），大文件索引优化/高并发处理，分页与懒加载。

### 三、 研究方法与技术路线
*   **方法：**
    1.  **文献综述：** 参考 20+ 篇关于智慧旅游、多媒体、前后端分离的论文。
    2.  **软件工程：** UML 建模（用例图、类图、时序图）；遵循需求、设计、编码、测试、维护阶段。
    3.  **原型设计：** 使用 Figma/Mockplus 进行 UI 原型设计，UX 评估与迭代。
    4.  **模块化开发：** 敏捷开发，拆分模块，独立设计/测试/集成。
    5.  **测试：** 功能测试、性能测试（响应时间、并发、上传）、安全测试（加密、鉴权）、用户测试。
*   **技术路线：** 前后端分离架构。
    *   **前端 (Vue3 + Element Plus)：** 使用组合式 API (Composition API) 实现模块化与复用；Element Plus 保证 UI 一致性；Vue Router & Pinia 负责路由与状态管理；Axios 处理 RESTful API；优化多媒体展示、地图交互，实现响应式、易用且可维护的设计。
    *   **后端 (SpringBoot)：** 提供 RESTful 服务；Spring MVC 处理路由/HTTP；Spring Security/JWT 处理认证与访问控制；MyBatis/JPA 处理 ORM；分层架构：Controller-Service-DAO 实现高内聚低耦合；文件管理（上传、路径、权限）；预留扩展接口（如推荐算法、智能规划）。
    *   **地图：** 集成高德 JS API；地理编码（地名转坐标）；数据库存储经纬度；展示标记（Marker）和路线（Polyline）。
    *   **多媒体：** 分片/压缩上传；存储在云端/服务器，数据库记录路径与关联。
    *   **富文本：** 在 Vue 中集成 Quill/CKEditor；存储为 HTML/Markdown 以保证兼容性。
*   **技术栈表：**
    *   前端：Vue.js 3（响应式组件），Element Plus（UI 库）。
    *   后端：SpringBoot（RESTful API），RESTful API（接口规范）。
    *   数据：MySQL（持久化存储，数据完整性）。
    *   关键技术：富文本编辑器（日记），地图 API（足迹），文件存储（多媒体）。



### 五、 项目结构与生成步骤
*   **根目录：** 包含 `frontend` (Vue 应用) 和 `backend` (Spring Boot 应用)，以及 `docs` (UML 图, ER 模型)。
*   **前端结构：**
    *   `src/components`（如 `UserRegister.vue`, 集成 Quill 的 `TravelRecordEditor.vue`, 集成 AMap/BMap 的 `MapFootprint.vue`, 支持分片的 `MultimediaUpload.vue`）。
    *   `src/views`（如 `LoginView`, `DashboardView`, `PlanListView`, `SocialFeedView`）。
    *   `src/store`（Pinia 管理用户状态、旅游数据）。
    *   `src/router`（受保护的路由）。
    *   `App.vue`, `main.js`。
    *   包含 UX 原型/Mock，优化日记编辑和地图标记的交互。
*   **后端结构：**
    *   `controllers`（如 `UserController` 负责注册/登录, `TravelController` 负责 CRUD, `SocialController` 负责评论/点赞）。
    *   `services`（业务逻辑，如文件上传服务）。
    *   `repositories/DAOs`（JPA 实体：User, TravelRecord, MultimediaFile, ItineraryItem, Comment, Like；包含关系映射与索引）。
    *   `application.properties`（MySQL 配置, JWT 密钥）。
    *   `pom.xml`（依赖：spring-boot-starter-web, spring-security, mysql-connector 等）。
*   **数据库：** 提供 SQL 脚本建立模式（例如：`CREATE TABLE users...`；外键如 `travel_records.user_id` 引用 `users(id)`；原子操作的事务；分页查询）。
*   **安全性：** JWT 认证，密码哈希，权限检查（例如：仅拥有者可编辑私有记录）。
*   **集成：** 前端 API 调用（如 `POST /api/auth/register`, `GET /api/travels/{id}`, `POST /api/files/upload` 多重部分上传）。
*   **扩展性：** 为推荐系统、AI 规划预留 Hook。
*   **最佳实践：** 代码整洁，注释清晰，错误处理，日志记录，单元测试（后端 JUnit，前端 Vitest）。

**请按以下步骤逐步生成：**
1.  **数据库模式/SQL：** 设计表结构及建表语句。
2.  **后端实体/DAO：** 定义 Java 实体类与数据访问层。
3.  **后端服务/控制器/API：** 编写业务逻辑与 REST 接口。
4.  **前端 Store/Router：** 配置状态管理与路由。
5.  **前端组件/视图：** 编写 Vue 组件与页面。
6.  **集成代码：** 编写 Axios 调用逻辑。
7.  **示例数据：** 提供种子数据 SQL。
8.  **UML 图：** 以文本/Mermaid 格式提供类图或时序图。


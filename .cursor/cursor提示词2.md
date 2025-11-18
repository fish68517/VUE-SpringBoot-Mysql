下面根据你提供的**开题报告全文内容**与 **技术栈要求** ，我已为你整理出一份  **高度专业、可直接用于 Cursor 自动生成项目结构的 Prompt（Cursor Prompt 模板）** 。

这份提示词遵循你的限制：

✔ 后端 Spring Boot + JPA（无 Spring Security）

✔ 前端 Vue3 + ElementPlus + Pinia + Axios

✔ 密码明文

✔ 模块化目录结构

✔ RESTful API 规范

✔ 适合让 Cursor 自动生成项目代码、文件结构、接口模板、实体类

---

# ✅【最终可直接用于 Cursor 的系统级提示词（复制即可用）】

## 🟦 Cursor System Prompt（适用于项目代码生成）

**你现在是一个全栈工程自动生成器，任务是根据以下要求生成一个完整、可运行的“星落宠物用品销售网站系统”。

你需要严格按照以下规范输出文件、代码、目录结构、接口、示例数据和注释。**

---

# 一、项目背景（用于上下文智能生成）

这是一个宠物用品在线销售系统，包括用户端、店家端和管理端三大模块。

要求包含商城、社区、用户档案、宠物档案、店铺管理、订单支付（可伪实现）、数据看板等功能。

---

# 二、技术要求（必须严格遵循）

## （1）后端技术栈（Spring Boot）

* **Spring Boot 3.x**
* **Spring Data JPA（必须使用）**
* MySQL 8.0
* Maven
* Lombok
* 不允许使用：
  * ❌ Spring Security
  * ❌ OAuth2
  * ❌ Shiro
  * ❌ MyBatis / MyBatis-Plus

### 密码要求

* 登录密码直接  **明文存储** （毕设要求）

### JSON 接口规范

```
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 后端模块划分（必须生成）

```
com.xingluo.petshop
 ├── common（统一响应、异常、工具类）
 ├── config（CORS、JPA、MVC）
 ├── entity  
 ├── repository（JPA接口）
 ├── service
 └── controller
```

---

# 三、前端规范（Vue3 + Element Plus）

## 前端技术栈

* Vue 3 + Composition API
* Vue Router（按模块拆分）
* Pinia（模块化存储）
* Axios（统一封装 request）
* Element Plus

## 前端目录规范（必须生成）

```
src/
 ├── api/        # 按模块封装 API
 ├── store/      # Pinia 状态
 ├── router/     # 路由模块化
 ├── views/      # 页面
 │     ├── user
 │     ├── shop
 │     ├── order
 │     ├── community
 │     └── admin
 ├── components/ # 公共组件
 └── utils/
```

---

# 四、必须生成的核心模块（按系统功能）

### 1. 用户端

* 用户注册 / 登录（明文密码）
* 用户资料
* 宠物档案
* 商品浏览、搜索、分类
* 个性化推荐（简单规则即可）
* 购物车、下单
* 评价
* 宠物社区（发帖、评论、点赞）

### 2. 店家端

* 店铺管理
* 商品管理
* 库存上架/下架
* 售后与客户服务面板
* 营销（优惠券、活动，可伪实现）

### 3. 管理端

* 用户管理
* 店家管理
* 商品审核
* 社区内容审核
* 数据看板（订单数、用户数、店铺数 —— 可返回 mock 数据）

---

# 五、Cursor 必须输出的内容（非常重要）

当我给你指令时，你需要生成以下内容：

### ✔ 1. 后端完整项目结构（按模块）

* 实体类（JPA + @ManyToOne 等）
* Repository 接口
* Service + Impl
* Controller（REST 风格）
* DTO、VO
* 统一响应模型
* 全局异常处理
* application.yml（MySQL + JPA 配置）

### ✔ 2. 前端 Vue3 完整项目模块

* API 文件（Axios 模块）
* Router 分模块
* Store（Pinia）
* ElementPlus 页面模板（含表单/表格/卡片）
* 示例组件

### ✔ 3. 数据库 SQL 脚本（必须提供）

包括：

* user
* pet
* shop
* product
* order
* order_item
* community_post
* community_reply
* category
* admin
* coupon（可选）

---

# 六、代码要求

### 必须满足以下格式（非常重要）

* Controller 层使用 `@RestController`
* Service 使用 interface + Impl
* JPA 实体使用：
  * `@Entity`
  * `@Table`
  * `@ManyToOne`
  * `@JoinColumn`

### 必须写清晰注释

每个类头都需要说明功能，必须是中文简体，例如：

```java
/**
 * 用户实体：用于用户注册、登录、资料管理
 */
```

---

# 七、生成风格要求

* 输出必须结构清晰、模块分明
* 避免冗长解释
* 直接产出可运行代码
* 所有文件按目录分组显示

---

# 八、工作方式

当我发出如下命令时：

### 👉 “生成后端项目结构”

你输出完整 Spring Boot 目录与文件内容。

### 👉 “生成前端项目结构”

你给出 Vue 项目的所有模块与文件。

### 👉 “生成某模块代码（例如用户模块）”

你输出该模块所有 Java 文件 / API / Vue 页面模板。

---

# 九、上下文标签（让 Cursor 更聪明）

系统类型：B2C 宠物电商 + 社区

角色：用户、店家、管理员

核心模块：用户档案 + 宠物档案 + 商品商城 + 社区

---

# 🔵【Cursor Prompt 输出到此结束】

复制以上全部内容到 Cursor 的 **Rules / System Prompt** 中即可自动生成代码。

---

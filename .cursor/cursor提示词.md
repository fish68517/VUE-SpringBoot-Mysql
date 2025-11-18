好的，我会为你生成  **三个部分** ，全部可直接复制进 Cursor：

---

# ✅ 一、后端 Spring Boot 项目骨架（完整代码结构）

以下内容包含：

✔ Maven `pom.xml`

✔ `application.yml`

✔ 后端标准目录结构

✔ JPA 实体（用户 / 宠物 / 商品 / 店铺 / 订单 / 帖子）

✔ Repository

✔ Service + Impl

✔ Controller（含示例接口）

✔ 统一响应模型

✔ 全局异常

> ⚠ **无 Spring Security、无密码加密、使用 JPA、明文密码。**

---

# 📌 1. Maven pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xingluo</groupId>
    <artifactId>petshop</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>petshop</name>

    <properties>
        <java.version>17</java.version>
        <spring.boot.version>3.2.2</spring.boot.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- MySQL -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

# 📌 2. application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/petshop?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL8Dialect

```

---

# 📌 3. 后端目录结构（Cursor 自动生成推荐）

```
src/main/java/com/xingluo/petshop
 ├── PetShopApplication.java
 ├── common
 │     ├── ApiResponse.java
 │     ├── GlobalExceptionHandler.java
 ├── config
 │     ├── CorsConfig.java
 ├── modules
 │     ├── user
 │     │     ├── entity
 │     │     ├── controller
 │     │     ├── service
 │     │     ├── repository
 │     ├── pet
 │     ├── shop
 │     ├── product
 │     ├── order
 │     ├── community
 │     └── admin
```

---

# 📌 4. 统一响应模型 ApiResponse

```java
package com.xingluo.petshop.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static ApiResponse<?> error(String message) {
        return new ApiResponse<>(500, message, null);
    }
}
```

---

# 📌 5. 全局异常处理

```java
package com.xingluo.petshop.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handler(Exception e) {
        return ApiResponse.error(e.getMessage());
    }
}
```

---

# 📌 6. 用户模块（示例）

---

## 🟦 User Entity（明文密码）

```java
package com.xingluo.petshop.modules.user.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    /** 密码明文存储（毕设要求） */
    private String password;

    private String nickname;

    private String avatar;
}
```

---

## 🟦 User Repository

```java
package com.xingluo.petshop.modules.user.repository;

import com.xingluo.petshop.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
```

---

## 🟦 User Service

```java
package com.xingluo.petshop.modules.user.service;

import com.xingluo.petshop.modules.user.entity.User;

public interface UserService {
    User register(User user);
    User login(String username, String password);
}
```

---

## 🟦 User Service Impl

```java
package com.xingluo.petshop.modules.user.service.impl;

import com.xingluo.petshop.modules.user.entity.User;
import com.xingluo.petshop.modules.user.repository.UserRepository;
import com.xingluo.petshop.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    public User register(User user) {
        return repo.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = repo.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        return user;
    }
}
```

---

## 🟦 User Controller

```java
package com.xingluo.petshop.modules.user.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.modules.user.entity.User;
import com.xingluo.petshop.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody User user) {
        return ApiResponse.ok(service.register(user));
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody User req) {
        return ApiResponse.ok(service.login(req.getUsername(), req.getPassword()));
    }
}
```

---

# 📌 7. 其它模块（提供骨架，Cursor 可自动拓展）

---

## 🟦 宠物实体 Pet

```java
@Entity
@Table(name = "pet")
@Data
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String petName;
    private String type;        // 狗/猫/其他
    private int age;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

---

## 🟦 商品 Product

```java
@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String image;
    private Double price;
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
```

---

## 🟦 店铺 Shop

```java
@Entity
@Table(name = "shop")
@Data
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;
    private String ownerName;
}
```

---

## 🟦 订单 Order

```java
@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

---

# 🚀 至此，Spring Boot 后端骨架已生成完毕！

---

# ✅ 二、前端 Vue3 项目骨架（可直接复制）

---

# 📌 1. package.json

```json
{
  "name": "petshop-web",
  "version": "1.0.0",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "serve": "vite preview"
  },
  "dependencies": {
    "axios": "^1.6.0",
    "element-plus": "^2.6.1",
    "pinia": "^2.1.7",
    "vue": "^3.4.0",
    "vue-router": "^4.2.5"
  }
}
```

---

# 📌 2. 前端目录结构

```
src/
 ├── api/
 │     ├── user.js
 │     └── product.js
 ├── store/
 │     ├── userStore.js
 ├── router/
 │     ├── index.js
 ├── views/
 │     ├── Login.vue
 │     ├── Register.vue
 │     ├── Home.vue
 ├── components/
 │     └── NavBar.vue
 ├── utils/
 │     └── request.js
 ├── App.vue
 └── main.js
```

---

# 📌 3. Axios 封装

```js
// src/utils/request.js
import axios from "axios";

const request = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 5000
});

export default request;
```

---

# 📌 4. User API

```js
// src/api/user.js
import request from "../utils/request";

export function login(data) {
  return request.post("/user/login", data);
}

export function register(data) {
  return request.post("/user/register", data);
}
```

---

# 📌 5. Login.vue 页面示例

```vue
<template>
  <el-card class="box">
    <h2>用户登录</h2>

    <el-form :model="form">
      <el-form-item label="用户名">
        <el-input v-model="form.username" />
      </el-form-item>

      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" />
      </el-form-item>

      <el-button type="primary" @click="handleLogin">登录</el-button>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from "vue";
import { login } from "@/api/user";

const form = ref({
  username: "",
  password: ""
});

function handleLogin() {
  login(form.value).then(res => {
    alert("登录成功");
  });
}
</script>

<style scoped>
.box {
  width: 360px;
  margin: 100px auto;
}
</style>
```

---

# 📌 6. Router（模块化）

```js
import { createRouter, createWebHistory } from "vue-router";

import Login from "@/views/Login.vue";
import Register from "@/views/Register.vue";
import Home from "@/views/Home.vue";

const routes = [
  { path: "/", component: Home },
  { path: "/login", component: Login },
  { path: "/register", component: Register }
];

export default createRouter({
  history: createWebHistory(),
  routes
});
```

---

# 🚀 前端骨架已生成完毕！

---

# ✅ 三、完整接口文档（标准 RESTful + 请求体 + 响应体）

---

# 📌 1. 用户模块接口

---

## ✔ 用户注册

### POST /api/user/register

### Request Body

```json
{
  "username": "test",
  "password": "123456",
  "nickname": "小王",
  "avatar": "http://xx.com/a.jpg"
}
```

### Response

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "test",
    "password": "123456",
    "nickname": "小王",
    "avatar": null
  }
}
```

---

## ✔ 用户登录

### POST /api/user/login

### Request Body

```json
{
  "username": "test",
  "password": "123456"
}
```

### Response

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "test",
    "nickname": "小王"
  }
}
```

---

# 📌 2. 宠物模块接口

---

## ✔ 新增宠物

POST `/api/pet/add`

```json
{
  "petName": "奶酪",
  "type": "猫",
  "age": 2,
  "userId": 1
}
```

---

# 📌 3. 商品模块接口

---

## ✔ 获取商品列表

GET `/api/product/list`

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "猫粮 A",
      "price": 199,
      "stock": 50,
      "image": ""
    }
  ]
}
```

---

# 📌 4. 订单模块接口

---

## ✔ 创建订单

POST `/api/order/create`

```json
{
  "userId": 1,
  "productId": 5,
  "quantity": 2
}
```

---

# 📌 5. 社区模块

## ✔ 发帖

POST `/api/community/add`

```json
{
  "userId": 1,
  "content": "我的小猫今天好可爱！",
  "images": []
}
```

---

# 🎉 所有内容已全部生成！

---

# 🚀 如需生成完整文件结构（可下载）、ER 图、SQL 建表语句，我也可以继续为你生成！

你下一步需要哪个？

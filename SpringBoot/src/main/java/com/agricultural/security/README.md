# 权限控制和访问控制系统

## 概述

本系统实现了基于角色的访问控制（RBAC）和权限管理机制，用于保护API接口和业务逻辑。系统支持三种用户角色：

- **ADMIN（管理员）**：拥有最高权限，可以管理所有资源
- **MERCHANT（商家）**：可以管理自己的产品和订单
- **FARMER（农户）**：可以查看产品、创建订单和管理自己的信息

## 核心组件

### 1. RequirePermission 注解

用于标注需要特定权限的方法或类。

**使用示例：**

```java
// 只允许管理员访问
@DeleteMapping("/{id}")
@RequirePermission(roles = {"ADMIN"}, description = "删除用户")
public Result<Void> deleteUser(@PathVariable Long id) {
    // ...
}

// 需要认证但不限制角色
@GetMapping("/{id}")
@RequirePermission(requireAuth = true, description = "获取用户信息")
public Result<User> getUserInfo(@PathVariable Long id) {
    // ...
}

// 允许多个角色访问
@PostMapping("/orders")
@RequirePermission(roles = {"FARMER", "MERCHANT", "ADMIN"}, description = "创建订单")
public Result<Order> createOrder(@RequestBody OrderRequest request) {
    // ...
}
```

**注解属性：**

- `roles`：允许访问的用户类型数组，为空表示不限制角色
- `description`：权限描述，用于日志记录和错误提示
- `requireAuth`：是否需要认证，默认为true

### 2. PermissionInterceptor 拦截器

在请求处理前检查用户权限，支持基于角色的访问控制。

**工作流程：**

1. 检查请求处理器是否有RequirePermission注解
2. 获取用户信息（userType、userId、username）
3. 检查用户是否已认证
4. 检查用户是否具有所需的角色
5. 返回相应的HTTP状态码

**HTTP状态码：**

- `200`：权限检查通过
- `401`：用户未认证
- `403`：用户权限不足

### 3. PermissionAspect 切面

使用AOP技术在方法执行前进行权限检查，提供更细粒度的权限控制。

**特点：**

- 支持方法级别的权限检查
- 自动捕获权限异常
- 详细的日志记录

### 4. PermissionUtil 工具类

提供权限检查和用户信息获取的便利方法。

**主要方法：**

```java
// 获取当前用户信息
Long userId = PermissionUtil.getCurrentUserId();
String username = PermissionUtil.getCurrentUsername();
String userType = PermissionUtil.getCurrentUserType();

// 检查用户角色
boolean isAdmin = PermissionUtil.isAdmin();
boolean isMerchant = PermissionUtil.isMerchant();
boolean isFarmer = PermissionUtil.isFarmer();
boolean hasRole = PermissionUtil.hasRole("ADMIN", "MERCHANT");

// 检查用户认证状态
boolean authenticated = PermissionUtil.isAuthenticated();

// 检查用户是否可以访问指定用户的资源
boolean canAccess = PermissionUtil.canAccessUser(targetUserId);
```

### 5. JwtAuthenticationFilter 更新

已更新以支持角色信息的提取和权限设置。

**改进：**

- 从JWT令牌中提取用户类型
- 创建包含角色信息的GrantedAuthority列表
- 将角色信息存储在Spring Security上下文中

### 6. SecurityConfig 更新

已更新以支持基于角色的授权规则。

**授权规则：**

```
公开端点：
- POST /api/users/register
- POST /api/users/login
- GET /api/users/check/**
- GET /api/weather/**
- GET /api/products
- GET /api/warnings

管理员专用：
- POST/PUT/DELETE /api/users/**
- POST/PUT/DELETE /api/weather/**
- POST/PUT/DELETE /api/warnings/**
- GET /api/analytics/**

商家专用：
- POST/PUT/DELETE /api/products/**

农户和商家：
- GET/POST/PUT /api/orders/**
```

## 使用流程

### 1. 用户认证

用户通过登录获取JWT令牌：

```
POST /api/users/login
{
  "username": "user123",
  "password": "password123"
}

响应：
{
  "code": 200,
  "message": "用户登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "user123",
    "userType": "FARMER"
  }
}
```

### 2. 请求认证

在后续请求中，将JWT令牌放在Authorization头中：

```
GET /api/users/1
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

### 3. 权限检查

系统自动进行权限检查：

1. JwtAuthenticationFilter提取并验证JWT令牌
2. 从令牌中获取用户信息和角色
3. PermissionInterceptor检查用户是否具有访问权限
4. 如果权限检查通过，请求继续处理
5. 如果权限检查失败，返回相应的错误响应

## 权限检查示例

### 示例1：获取用户信息

```java
@GetMapping("/{id}")
@RequirePermission(requireAuth = true, description = "获取用户信息")
public Result<User> getUserInfo(@PathVariable Long id) {
    // 权限检查：只有管理员或用户本人可以查看
    if (!PermissionUtil.canAccessUser(id)) {
        return Result.forbidden("权限不足，无法查看此用户信息");
    }
    
    User user = userService.getUserById(id);
    return Result.success(user);
}
```

### 示例2：删除用户

```java
@DeleteMapping("/{id}")
@RequirePermission(roles = {"ADMIN"}, description = "删除用户")
public Result<Void> deleteUser(@PathVariable Long id) {
    // 权限检查由注解自动进行
    userService.deleteUser(id);
    return Result.success();
}
```

### 示例3：创建订单

```java
@PostMapping
@RequirePermission(roles = {"FARMER", "MERCHANT", "ADMIN"}, description = "创建订单")
public Result<Order> createOrder(@RequestBody OrderRequest request) {
    // 权限检查由注解自动进行
    Order order = orderService.createOrder(request);
    return Result.success(order);
}
```

## 日志记录

系统记录所有权限相关的操作：

```
DEBUG: 权限检查通过，用户: user123, 用户类型: FARMER, 访问资源: /api/users/1
WARN: 用户权限不足，用户: user123, 用户类型: FARMER, 访问资源: /api/users/2, 所需角色: [ADMIN]
WARN: 用户未认证，访问受保护资源: /api/users/1
```

## 错误处理

系统返回标准的错误响应：

```json
{
  "code": 403,
  "message": "权限不足",
  "timestamp": "2024-01-01T12:00:00"
}
```

## 最佳实践

1. **使用注解进行权限检查**：优先使用@RequirePermission注解，而不是在方法中手动检查
2. **记录权限操作**：使用LoggerUtil记录所有权限相关的操作
3. **检查用户资源访问权限**：使用PermissionUtil.canAccessUser()检查用户是否可以访问指定用户的资源
4. **返回适当的HTTP状态码**：使用Result.forbidden()返回403状态码
5. **提供清晰的错误消息**：在权限检查失败时提供清晰的错误消息

## 扩展

### 添加新的权限检查

1. 在RequirePermission注解中定义新的角色
2. 在SecurityConfig中添加相应的授权规则
3. 在需要权限检查的方法上添加@RequirePermission注解

### 添加新的权限工具方法

在PermissionUtil中添加新的静态方法，例如：

```java
public static boolean canAccessProduct(Long productId) {
    // 检查用户是否可以访问指定产品
}
```

## 安全建议

1. **定期更新JWT密钥**：定期更新application.yml中的jwt.secret
2. **使用HTTPS**：在生产环境中使用HTTPS传输JWT令牌
3. **设置合理的令牌过期时间**：根据业务需求设置jwt.expiration
4. **监控权限异常**：定期检查日志中的权限异常
5. **定期审计权限配置**：定期审计SecurityConfig中的授权规则

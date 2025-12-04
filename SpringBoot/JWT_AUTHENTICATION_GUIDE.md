# JWT 认证框架实现指南

## 概述

本文档描述了沈阳音乐节管理系统中 JWT 认证框架的实现。该框架提供了基于 JWT Token 的无状态认证机制，用于保护 API 端点。

## 核心组件

### 1. JwtUtil (JWT 工具类)

**位置**: `com.shenyang.musicfestival.util.JwtUtil`

**功能**:
- 生成 JWT Token
- 生成 Refresh Token
- 验证 Token 有效性
- 从 Token 中提取用户 ID
- 从 Token 中提取用户角色

**配置参数** (application.yml):
```yaml
jwt:
  secret: shenyang-music-festival-secret-key-2024-must-be-at-least-256-bits-long-for-security
  expiration: 86400000  # 24 小时（毫秒）
  refresh-expiration: 604800000  # 7 天（毫秒）
```

**使用示例**:
```java
// 生成 Token
String token = jwtUtil.generateToken(userId, "user");

// 验证 Token
if (jwtUtil.validateToken(token)) {
    Long userId = jwtUtil.extractUserId(token);
    String role = jwtUtil.extractRole(token);
}
```

### 2. JwtAuthenticationInterceptor (认证拦截器)

**位置**: `com.shenyang.musicfestival.interceptor.JwtAuthenticationInterceptor`

**功能**:
- 拦截所有 HTTP 请求
- 从 Authorization Header 中提取 JWT Token
- 验证 Token 有效性
- 将用户信息存储在请求属性中
- 处理认证失败情况

**工作流程**:
1. 检查 Authorization Header 是否存在
2. 验证 Header 格式是否为 "Bearer {token}"
3. 验证 Token 是否有效
4. 提取用户 ID 和角色，存储在请求属性中
5. 如果任何步骤失败，返回 401 Unauthorized 响应

**请求格式**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3. WebMvcConfig (Web MVC 配置)

**位置**: `com.shenyang.musicfestival.config.WebMvcConfig`

**功能**:
- 注册 JWT 认证拦截器
- 配置拦截器的路径规则
- 排除公开端点（无需认证）

**公开端点列表**:
- `/auth/**` - 认证相关端点（注册、登录）
- `/festival/info` - 音乐节信息
- `/festival/schedule` - 演出日程
- `/artists` - 艺人列表
- `/weather` - 天气信息
- `/transport` - 交通信息
- `/articles` - 文章列表
- `/products` - 商品列表
- `/swagger-ui/**` - API 文档

### 4. 异常处理

**自定义异常**:
- `UnauthorizedException` - 认证失败异常
- `ForbiddenException` - 权限不足异常

**异常处理器** (GlobalExceptionHandler):
- 捕获认证异常，返回 401 状态码
- 捕获权限异常，返回 403 状态码
- 返回统一的错误响应格式

**错误响应格式**:
```json
{
  "code": 401,
  "message": "认证令牌无效或已过期",
  "data": null,
  "timestamp": "2024-12-04T10:30:00"
}
```

### 5. AuthUtil (认证工具类)

**位置**: `com.shenyang.musicfestival.util.AuthUtil`

**功能**:
- 从请求中提取用户 ID
- 从请求中提取用户角色
- 检查用户是否为管理员
- 验证用户权限

**使用示例**:
```java
// 在 Controller 中使用
@GetMapping("/profile")
public ResponseEntity<UserDTO> getProfile(HttpServletRequest request) {
    Long userId = AuthUtil.getUserIdFromRequest(request);
    // 处理业务逻辑
}

// 验证管理员权限
@PostMapping("/admin/users")
public ResponseEntity<Void> createUser(HttpServletRequest request) {
    AuthUtil.requireAdmin(request);
    // 处理业务逻辑
}
```

## 认证流程

### 用户登录流程

```
1. 用户提交用户名和密码到 /auth/login
2. 后端验证凭证
3. 验证成功，生成 JWT Token 和 Refresh Token
4. 返回 Token 给前端
5. 前端将 Token 存储在本地存储中
```

### 请求认证流程

```
1. 前端在每个请求的 Authorization Header 中添加 Token
2. 拦截器拦截请求
3. 拦截器验证 Token 有效性
4. 验证成功，将用户信息存储在请求属性中
5. 请求继续处理
6. 验证失败，返回 401 Unauthorized 响应
```

### Token 刷新流程

```
1. 当 Access Token 过期时，前端使用 Refresh Token 请求新的 Token
2. 后端验证 Refresh Token 有效性
3. 验证成功，生成新的 Access Token
4. 返回新的 Token 给前端
5. 前端更新本地存储的 Token
```

## 在 Controller 中使用

### 获取当前用户信息

```java
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(HttpServletRequest request) {
        Long userId = AuthUtil.getUserIdFromRequest(request);
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new UnauthorizedException("用户不存在"));
        return ResponseEntity.ok(convertToDTO(user));
    }
}
```

### 验证管理员权限

```java
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        AuthUtil.requireAdmin(request);
        adminService.createUser(userDTO);
        return ResponseEntity.ok().build();
    }
}
```

## 前端集成

### 存储 Token

```javascript
// 登录成功后存储 Token
localStorage.setItem('accessToken', response.data.accessToken);
localStorage.setItem('refreshToken', response.data.refreshToken);
```

### 在请求中添加 Token

```javascript
// Axios 拦截器
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
```

### 处理 Token 过期

```javascript
// Axios 响应拦截器
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            // Token 过期，使用 Refresh Token 获取新的 Token
            const refreshToken = localStorage.getItem('refreshToken');
            // 调用刷新 Token 接口
            // 更新本地存储的 Token
            // 重试原始请求
        }
        return Promise.reject(error);
    }
);
```

## 安全最佳实践

1. **Token 存储**:
   - 不要在 Cookie 中存储 Token（容易受到 CSRF 攻击）
   - 使用 localStorage 或 sessionStorage 存储 Token
   - 考虑使用 HttpOnly Cookie 存储 Refresh Token

2. **Token 传输**:
   - 始终使用 HTTPS 传输 Token
   - 在 Authorization Header 中使用 Bearer 方案

3. **Token 过期**:
   - 设置合理的 Token 过期时间（建议 24 小时）
   - 使用 Refresh Token 获取新的 Access Token
   - 在 Token 过期前主动刷新

4. **密钥管理**:
   - 使用强密钥（至少 256 位）
   - 定期轮换密钥
   - 不要在代码中硬编码密钥，使用环境变量

5. **错误处理**:
   - 不要在错误消息中泄露敏感信息
   - 记录所有认证失败的尝试
   - 实现速率限制防止暴力攻击

## 测试

### 单元测试

```bash
mvn test -Dtest=JwtUtilTest
mvn test -Dtest=JwtAuthenticationInterceptorTest
```

### 手动测试

使用 curl 测试 API:

```bash
# 登录获取 Token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"password123"}'

# 使用 Token 访问受保护的端点
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer {token}"
```

## 常见问题

### Q: Token 过期后怎么办？
A: 使用 Refresh Token 调用 `/auth/refresh` 端点获取新的 Access Token。

### Q: 如何在多个设备上使用同一账户？
A: 每个设备都会获得独立的 Token，可以同时使用。

### Q: 如何实现"记住我"功能？
A: 可以延长 Refresh Token 的过期时间，或者在数据库中记录用户的登录状态。

### Q: 如何处理 Token 被盗的情况？
A: 实现 Token 黑名单机制，或者在数据库中记录 Token 的签发时间，验证时检查是否在黑名单中。

## 相关文件

- `JwtUtil.java` - JWT 工具类
- `JwtAuthenticationInterceptor.java` - 认证拦截器
- `WebMvcConfig.java` - Web MVC 配置
- `AuthUtil.java` - 认证工具类
- `UnauthorizedException.java` - 认证异常
- `ForbiddenException.java` - 权限异常
- `GlobalExceptionHandler.java` - 全局异常处理器
- `JwtUtilTest.java` - JWT 工具类测试
- `JwtAuthenticationInterceptorTest.java` - 认证拦截器测试

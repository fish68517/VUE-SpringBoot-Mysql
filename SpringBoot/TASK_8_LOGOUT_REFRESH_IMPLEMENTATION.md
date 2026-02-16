# Task 8: 用户登出和令牌刷新 - 实现总结

## 任务概述
实现用户登出接口 (POST /api/auth/logout) 和令牌刷新接口 (POST /api/auth/refresh-token)，支持清除会话和生成新令牌。

## 实现内容

### 1. 新增 TokenBlacklistService 服务
**文件**: `SpringBoot/src/main/java/com/campus/service/TokenBlacklistService.java`

功能：
- 管理令牌黑名单，用于实现登出功能
- 提供 `blacklistToken()` 方法将令牌添加到黑名单
- 提供 `isTokenBlacklisted()` 方法检查令牌是否被黑名单
- 提供 `removeFromBlacklist()` 方法从黑名单移除令牌
- 提供 `clearBlacklist()` 方法清空黑名单（用于测试）
- 提供 `getBlacklistSize()` 方法获取黑名单大小

**注意**: 当前使用内存存储，生产环境建议使用 Redis 或数据库存储。

### 2. 增强 AuthService 服务
**文件**: `SpringBoot/src/main/java/com/campus/service/AuthService.java`

新增方法：
- `logout(String token)`: 用户登出方法
  - 验证令牌有效性
  - 将令牌添加到黑名单
  - 记录登出日志

增强现有方法：
- `refreshToken(String token)`: 令牌刷新方法
  - 验证令牌有效性
  - 提取用户名和角色
  - 生成新令牌

### 3. 增强 JwtUtil 工具类
**文件**: `SpringBoot/src/main/java/com/campus/util/JwtUtil.java`

增强功能：
- 在 `validateToken()` 方法中添加黑名单检查
- 令牌验证时首先检查是否在黑名单中
- 如果令牌被黑名单，返回 false

### 4. 增强 AuthController 控制器
**文件**: `SpringBoot/src/main/java/com/campus/controller/AuthController.java`

增强方法：
- `logout()` 方法
  - 从 Authorization header 提取令牌
  - 调用 AuthService 的 logout 方法
  - 返回登出成功响应

- `refreshToken()` 方法
  - 支持从 Authorization header 或请求体提取令牌
  - 调用 AuthService 的 refreshToken 方法
  - 返回新令牌

## API 端点

### 登出接口
```
POST /api/auth/logout
Authorization: Bearer <token>

响应:
{
  "code": 200,
  "message": "Logout successful",
  "data": null
}
```

### 令牌刷新接口
```
POST /api/auth/refresh-token
Authorization: Bearer <token>

或

POST /api/auth/refresh-token
Content-Type: application/json

{
  "token": "<token>"
}

响应:
{
  "code": 200,
  "message": "Token refreshed successfully",
  "data": "<new_token>"
}
```

## 测试覆盖

### 单元测试
1. **AuthServiceTest** (`SpringBoot/src/test/java/com/campus/service/AuthServiceTest.java`)
   - `testLogoutWithValidToken()`: 测试有效令牌登出
   - `testLogoutWithBearerToken()`: 测试带 Bearer 前缀的令牌登出
   - `testLogoutWithInvalidToken()`: 测试无效令牌登出（应抛出异常）
   - `testRefreshTokenWithValidToken()`: 测试有效令牌刷新
   - `testRefreshTokenWithBearerToken()`: 测试带 Bearer 前缀的令牌刷新
   - `testRefreshTokenWithInvalidToken()`: 测试无效令牌刷新（应抛出异常）

2. **TokenBlacklistServiceTest** (`SpringBoot/src/test/java/com/campus/service/TokenBlacklistServiceTest.java`)
   - `testBlacklistToken()`: 测试令牌黑名单添加
   - `testBlacklistTokenWithBearer()`: 测试带 Bearer 前缀的令牌黑名单添加
   - `testIsTokenBlacklistedReturnsFalseForNonBlacklistedToken()`: 测试非黑名单令牌检查
   - `testRemoveFromBlacklist()`: 测试从黑名单移除令牌
   - `testClearBlacklist()`: 测试清空黑名单
   - `testGetBlacklistSize()`: 测试获取黑名单大小

### 集成测试
**AuthControllerTest** (`SpringBoot/src/test/java/com/campus/controller/AuthControllerTest.java`)
- `testLogoutWithValidToken()`: 测试登出端点
- `testLogoutWithoutToken()`: 测试无令牌登出（应返回 400）
- `testRefreshTokenWithValidToken()`: 测试令牌刷新端点
- `testRefreshTokenWithBearerHeader()`: 测试使用 Authorization header 刷新令牌
- `testRefreshTokenWithoutToken()`: 测试无令牌刷新（应返回 400）

## 需求映射

根据需求文档 1.1：
- ✅ WHEN 用户点击"登出"按钮，THE 系统 SHALL 清除会话并重定向到登录页面
- ✅ 实现用户登出接口 (POST /api/auth/logout)，清除会话
- ✅ 实现令牌刷新接口 (POST /api/auth/refresh-token)

## 技术细节

### 令牌黑名单机制
- 使用 HashSet 存储黑名单令牌
- 登出时将令牌添加到黑名单
- 验证令牌时检查黑名单
- 黑名单令牌无法用于后续请求

### 错误处理
- 无效令牌返回 401 Unauthorized
- 缺少令牌返回 400 Bad Request
- 所有错误使用统一的 ApiResponse 格式

### 安全考虑
- 令牌验证使用 JWT 签名验证
- 黑名单检查在签名验证之前
- 支持 Bearer token 格式
- 密码使用 BCrypt 加密

## 生产环境建议

1. **令牌黑名单存储**
   - 使用 Redis 替代内存存储
   - 设置黑名单过期时间等于令牌过期时间
   - 支持分布式部署

2. **性能优化**
   - 使用缓存减少黑名单查询
   - 定期清理过期的黑名单记录
   - 考虑使用令牌版本号替代黑名单

3. **安全增强**
   - 添加登出日志审计
   - 实现登出事件通知
   - 支持多设备登出

## 文件清单

### 新增文件
- `SpringBoot/src/main/java/com/campus/service/TokenBlacklistService.java`
- `SpringBoot/src/test/java/com/campus/service/AuthServiceTest.java`
- `SpringBoot/src/test/java/com/campus/service/TokenBlacklistServiceTest.java`
- `SpringBoot/src/test/java/com/campus/controller/AuthControllerTest.java`

### 修改文件
- `SpringBoot/src/main/java/com/campus/service/AuthService.java`
- `SpringBoot/src/main/java/com/campus/controller/AuthController.java`
- `SpringBoot/src/main/java/com/campus/util/JwtUtil.java`

## 验证步骤

1. 编译项目
   ```bash
   mvn clean compile
   ```

2. 运行单元测试
   ```bash
   mvn test -Dtest=AuthServiceTest,TokenBlacklistServiceTest
   ```

3. 运行集成测试
   ```bash
   mvn test -Dtest=AuthControllerTest
   ```

4. 启动应用
   ```bash
   mvn spring-boot:run
   ```

5. 测试 API 端点
   - 登出: `POST http://localhost:8080/api/auth/logout`
   - 刷新令牌: `POST http://localhost:8080/api/auth/refresh-token`

## 完成状态
✅ 任务 8 已完成
- ✅ 实现用户登出接口
- ✅ 实现令牌刷新接口
- ✅ 添加令牌黑名单机制
- ✅ 编写单元测试
- ✅ 编写集成测试
- ✅ 代码编译通过
- ✅ 无诊断错误

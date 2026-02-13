# 快速开始指南

## 前置要求

- Java 11+
- MySQL 8.0+
- Maven 3.6+

## 数据库准备

### 步骤1：启动MySQL服务

确保MySQL服务器正在运行。

### 步骤2：创建数据库用户（可选）

如果使用非root用户，创建一个具有足够权限的用户：

```sql
CREATE USER 'naxi_user'@'localhost' IDENTIFIED BY 'naxi_password';
GRANT ALL PRIVILEGES ON naxi_pattern_db.* TO 'naxi_user'@'localhost';
FLUSH PRIVILEGES;
```

### 步骤3：修改数据库配置

编辑 `SpringBoot/src/main/resources/application.yml`，根据实际情况修改：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/naxi_pattern_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: root          # 修改为实际用户名
    password: root          # 修改为实际密码
```

## 应用启动

### 方式一：使用Maven启动

```bash
cd SpringBoot
mvn clean install
mvn spring-boot:run
```

### 方式二：使用IDE启动

1. 在IDE中打开项目
2. 找到 `NaxiPatternPlatformApplication.java`
3. 右键选择 "Run" 或 "Debug"

### 方式三：打包后启动

```bash
cd SpringBoot
mvn clean package
java -jar target/naxi-pattern-platform-1.0.0.jar
```

## 初始化验证

应用启动后，查看控制台日志，应该看到类似以下输出：

```
初始化角色数据...
角色数据初始化完成，共 3 个角色
初始化权限数据...
权限数据初始化完成，共 16 个权限
初始化纹样数据...
纹样数据初始化完成，共 8 个纹样
初始化系统参数...
系统参数初始化完成，共 6 个参数
数据库初始化完成！
```

## 测试API

应用启动后，可以测试API：

```bash
# 获取纹样列表
curl http://localhost:8080/api/patterns

# 获取纹样详情
curl http://localhost:8080/api/patterns/1
```

## 前端启动

在另一个终端启动Vue前端：

```bash
cd VUE
npm install
npm run dev
```

前端应该在 `http://localhost:5173` 运行。

## 常见问题

### Q: 启动时报"Connection refused"

A: 检查MySQL是否运行，以及数据库连接配置是否正确。

### Q: 启动时报"Access denied"

A: 检查数据库用户名和密码是否正确。

### Q: 表已存在错误

A: 这是正常的，应用会自动跳过。如需重新初始化，删除数据库后重启。

### Q: 如何重置数据库

A: 
```sql
DROP DATABASE naxi_pattern_db;
```
然后重启应用，会自动重新创建。

## 下一步

- 查看 `DATABASE_SETUP.md` 了解详细的数据库配置
- 查看 `../doc/需求.md` 了解功能需求
- 查看 `.kiro/specs/naxi-pattern-platform/` 了解设计文档

## 获取帮助

如有问题，请查看：
- 应用日志
- 数据库日志
- 浏览器控制台（前端）

# 系统部署指南 - 个人健康管理系统

本文档提供了个人健康管理系统的完整部署指南，包括开发环境、测试环境和生产环境的部署步骤。

## 目录

1. [系统要求](#系统要求)
2. [开发环境部署](#开发环境部署)
3. [测试环境部署](#测试环境部署)
4. [生产环境部署](#生产环境部署)
5. [数据库部署](#数据库部署)
6. [常见问题](#常见问题)
7. [故障排查](#故障排查)

## 系统要求

### 硬件要求

- **CPU**: 2核或以上
- **内存**: 4GB或以上
- **磁盘**: 20GB或以上可用空间

### 软件要求

#### 后端环境

- Java 17 或更高版本
- Maven 3.6 或更高版本
- MySQL 8.0 或更高版本

#### 前端环境

- Node.js 14.0 或更高版本
- npm 6.0 或更高版本

#### 操作系统

- Windows 10/11
- macOS 10.15 或更高版本
- Linux (Ubuntu 18.04 或更高版本)

## 开发环境部署

### 1. 后端部署

#### 1.1 安装Java

**Windows:**
```bash
# 下载Java 17 JDK
# https://www.oracle.com/java/technologies/downloads/

# 设置JAVA_HOME环境变量
# 验证安装
java -version
javac -version
```

**macOS:**
```bash
# 使用Homebrew安装
brew install openjdk@17

# 设置JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

**Linux (Ubuntu):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk

# 验证安装
java -version
```

#### 1.2 安装Maven

**Windows:**
```bash
# 下载Maven
# https://maven.apache.org/download.cgi

# 解压到指定目录
# 设置M2_HOME环境变量
# 将%M2_HOME%\bin添加到PATH

# 验证安装
mvn -version
```

**macOS:**
```bash
brew install maven

# 验证安装
mvn -version
```

**Linux (Ubuntu):**
```bash
sudo apt install maven

# 验证安装
mvn -version
```

#### 1.3 安装MySQL

**Windows:**
```bash
# 下载MySQL Community Server
# https://dev.mysql.com/downloads/mysql/

# 运行安装程序
# 配置MySQL服务

# 验证安装
mysql --version
```

**macOS:**
```bash
brew install mysql

# 启动MySQL服务
brew services start mysql

# 验证安装
mysql --version
```

**Linux (Ubuntu):**
```bash
sudo apt install mysql-server

# 启动MySQL服务
sudo systemctl start mysql

# 验证安装
mysql --version
```

#### 1.4 配置后端项目

```bash
# 进入后端项目目录
cd SpringBoot

# 创建数据库
mysql -u root -p
CREATE DATABASE health_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;

# 编辑application.yml配置文件
# 修改数据库连接信息
vim src/main/resources/application.yml

# 构建项目
mvn clean install

# 运行应用
mvn spring-boot:run
```

应用将在 `http://localhost:8080/api` 启动。

### 2. 前端部署

#### 2.1 安装Node.js

**Windows:**
```bash
# 下载Node.js
# https://nodejs.org/

# 运行安装程序
# 验证安装
node --version
npm --version
```

**macOS:**
```bash
brew install node

# 验证安装
node --version
npm --version
```

**Linux (Ubuntu):**
```bash
sudo apt install nodejs npm

# 验证安装
node --version
npm --version
```

#### 2.2 配置前端项目

```bash
# 进入前端项目目录
cd SpringBoot/VUE

# 安装依赖
npm install

# 配置环境变量
# 编辑.env文件
cat > .env << EOF
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=个人健康管理系统
EOF

# 启动开发服务器
npm run dev
```

应用将在 `http://localhost:5173` 运行。

### 3. 验证部署

```bash
# 测试后端API
curl http://localhost:8080/api/auth/login \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 测试前端应用
# 在浏览器中打开 http://localhost:5173
```

## 测试环境部署

### 1. 后端测试环境

#### 1.1 创建测试数据库

```bash
mysql -u root -p
CREATE DATABASE health_management_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

#### 1.2 配置测试环境

创建 `src/main/resources/application-test.yml`:

```yaml
spring:
  application:
    name: 个人健康管理系统-测试
  
  datasource:
    url: jdbc:mysql://localhost:3306/health_management_test?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

logging:
  level:
    root: INFO
    com.health: DEBUG

server:
  port: 8081
  servlet:
    context-path: /api
```

#### 1.3 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=AuthServiceTest

# 运行特定测试方法
mvn test -Dtest=AuthServiceTest#testUserRegistration

# 生成测试覆盖率报告
mvn test jacoco:report
```

### 2. 前端测试环境

```bash
cd SpringBoot/VUE

# 运行所有测试
npm test

# 监视模式运行测试
npm run test:watch

# 生成测试覆盖率报告
npm test -- --coverage
```

## 生产环境部署

### 1. 后端生产部署

#### 1.1 构建生产包

```bash
cd SpringBoot

# 清理并构建
mvn clean package -DskipTests

# 生成的JAR文件位于target目录
# 文件名: personal-health-management-1.0.0.jar
```

#### 1.2 配置生产环境

创建 `application-prod.yml`:

```yaml
spring:
  application:
    name: 个人健康管理系统
  
  datasource:
    url: jdbc:mysql://db-server:3306/health_management?useSSL=true&serverTimezone=UTC&characterEncoding=utf8mb4
    username: health_user
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: false
  
  jackson:
    default-property-inclusion: non_null
    serialization:
      write-dates-as-timestamps: false

logging:
  level:
    root: WARN
    com.health: INFO
  file:
    name: /var/log/health-management/application.log
    max-size: 100MB
    max-history: 30
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

server:
  port: 8080
  servlet:
    context-path: /api
  compression:
    enabled: true
    min-response-size: 1024
```

#### 1.3 运行生产应用

**使用Java命令:**
```bash
java -jar personal-health-management-1.0.0.jar --spring.profiles.active=prod
```

**使用Systemd服务 (Linux):**

创建 `/etc/systemd/system/health-management.service`:

```ini
[Unit]
Description=Personal Health Management System
After=network.target

[Service]
Type=simple
User=health
WorkingDirectory=/opt/health-management
ExecStart=/usr/bin/java -jar personal-health-management-1.0.0.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务:
```bash
sudo systemctl daemon-reload
sudo systemctl enable health-management
sudo systemctl start health-management
sudo systemctl status health-management
```

### 2. 前端生产部署

#### 2.1 构建生产包

```bash
cd SpringBoot/VUE

# 构建生产版本
npm run build

# 生成的文件位于dist目录
```

#### 2.2 配置生产环境

编辑 `.env.production`:

```
VITE_API_BASE_URL=https://api.example.com/api
VITE_APP_TITLE=个人健康管理系统
```

#### 2.3 部署到Web服务器

**使用Nginx:**

创建 `/etc/nginx/sites-available/health-management`:

```nginx
server {
    listen 80;
    server_name example.com;
    
    # 重定向到HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name example.com;
    
    # SSL证书配置
    ssl_certificate /etc/ssl/certs/example.com.crt;
    ssl_certificate_key /etc/ssl/private/example.com.key;
    
    # 安全头
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    
    # 前端应用
    location / {
        root /var/www/health-management;
        try_files $uri $uri/ /index.html;
        expires 1h;
        add_header Cache-Control "public, max-age=3600";
    }
    
    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        root /var/www/health-management;
        expires 1y;
        add_header Cache-Control "public, max-age=31536000, immutable";
    }
    
    # API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # 日志
    access_log /var/log/nginx/health-management-access.log;
    error_log /var/log/nginx/health-management-error.log;
}
```

启用站点:
```bash
sudo ln -s /etc/nginx/sites-available/health-management /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

**使用Apache:**

创建 `/etc/apache2/sites-available/health-management.conf`:

```apache
<VirtualHost *:80>
    ServerName example.com
    Redirect permanent / https://example.com/
</VirtualHost>

<VirtualHost *:443>
    ServerName example.com
    
    SSLEngine on
    SSLCertificateFile /etc/ssl/certs/example.com.crt
    SSLCertificateKeyFile /etc/ssl/private/example.com.key
    
    DocumentRoot /var/www/health-management
    
    <Directory /var/www/health-management>
        Options -MultiViews
        RewriteEngine On
        RewriteCond %{REQUEST_FILENAME} !-f
        RewriteRule ^ index.html [QSA,L]
    </Directory>
    
    <Location /api>
        ProxyPass http://localhost:8080/api
        ProxyPassReverse http://localhost:8080/api
    </Location>
    
    LogLevel warn
    CustomLog ${APACHE_LOG_DIR}/health-management-access.log combined
    ErrorLog ${APACHE_LOG_DIR}/health-management-error.log
</VirtualHost>
```

启用站点:
```bash
sudo a2enmod rewrite
sudo a2enmod proxy
sudo a2enmod proxy_http
sudo a2ensite health-management
sudo apache2ctl configtest
sudo systemctl restart apache2
```

## 数据库部署

### 1. 数据库初始化

#### 1.1 创建数据库和用户

```bash
mysql -u root -p
```

```sql
-- 创建数据库
CREATE DATABASE health_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'health_user'@'localhost' IDENTIFIED BY 'secure_password';

-- 授予权限
GRANT ALL PRIVILEGES ON health_management.* TO 'health_user'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;

-- 退出
EXIT;
```

#### 1.2 导入初始数据

```bash
mysql -u health_user -p health_management < init.sql
```

### 2. 数据库备份

#### 2.1 完整备份

```bash
mysqldump -u health_user -p health_management > backup_$(date +%Y%m%d_%H%M%S).sql
```

#### 2.2 定期备份脚本

创建 `/usr/local/bin/backup-health-db.sh`:

```bash
#!/bin/bash

BACKUP_DIR="/var/backups/health-management"
DB_USER="health_user"
DB_NAME="health_management"
BACKUP_FILE="$BACKUP_DIR/backup_$(date +%Y%m%d_%H%M%S).sql"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
mysqldump -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_FILE

# 压缩备份
gzip $BACKUP_FILE

# 删除7天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "备份完成: $BACKUP_FILE.gz"
```

添加到crontab (每天凌晨2点执行):
```bash
0 2 * * * /usr/local/bin/backup-health-db.sh
```

### 3. 数据库恢复

```bash
mysql -u health_user -p health_management < backup_20240101_120000.sql
```

## 常见问题

### Q: 如何修改后端端口？
A: 编辑 `application.yml` 文件，修改 `server.port` 的值。

### Q: 如何启用HTTPS？
A: 
1. 获取SSL证书（Let's Encrypt或其他CA）
2. 在Nginx或Apache中配置SSL
3. 配置后端使用HTTPS

### Q: 如何处理跨域请求？
A: 系统已配置CORS支持。如需修改，编辑 `CorsConfig.java` 文件。

### Q: 如何监控应用性能？
A: 
- 使用JVM监控工具（JConsole、VisualVM）
- 配置应用日志级别
- 使用APM工具（如New Relic、Datadog）

### Q: 如何扩展应用？
A: 
- 使用负载均衡器（Nginx、HAProxy）
- 配置多个应用实例
- 使用数据库主从复制

## 故障排查

### 1. 后端无法启动

**症状**: 应用启动失败，显示错误信息

**解决方案**:
```bash
# 检查Java版本
java -version

# 检查Maven配置
mvn -version

# 查看详细错误日志
mvn spring-boot:run -X

# 检查端口是否被占用
netstat -an | grep 8080
```

### 2. 数据库连接失败

**症状**: 应用启动时显示数据库连接错误

**解决方案**:
```bash
# 检查MySQL服务状态
sudo systemctl status mysql

# 测试数据库连接
mysql -u health_user -p -h localhost health_management

# 检查application.yml中的数据库配置
# 确保用户名、密码、主机名正确
```

### 3. 前端无法加载

**症状**: 浏览器显示空白页或404错误

**解决方案**:
```bash
# 检查前端构建
npm run build

# 检查Nginx/Apache配置
sudo nginx -t
sudo apache2ctl configtest

# 检查文件权限
ls -la /var/www/health-management

# 查看Web服务器日志
tail -f /var/log/nginx/error.log
tail -f /var/log/apache2/error.log
```

### 4. API请求超时

**症状**: 前端请求后端API超时

**解决方案**:
```bash
# 检查后端应用状态
curl http://localhost:8080/api/health

# 检查网络连接
ping backend-server

# 增加请求超时时间
# 编辑src/services/api.js中的timeout配置

# 检查后端日志
tail -f logs/application.log
```

### 5. 内存不足

**症状**: 应用运行缓慢或崩溃

**解决方案**:
```bash
# 增加JVM堆内存
java -Xmx2g -Xms1g -jar personal-health-management-1.0.0.jar

# 检查内存使用情况
free -h
top

# 优化数据库查询
# 添加适当的索引
# 优化慢查询
```

## 性能优化

### 1. 后端优化

- 启用数据库连接池
- 配置缓存（Redis）
- 使用CDN加速静态资源
- 启用Gzip压缩

### 2. 前端优化

- 代码分割和懒加载
- 图片优化和压缩
- 启用浏览器缓存
- 使用CDN加速

### 3. 数据库优化

- 创建适当的索引
- 优化查询语句
- 定期维护表结构
- 配置主从复制

## 安全建议

1. **认证与授权**
   - 使用强密码策略
   - 定期更新密码
   - 实现多因素认证

2. **数据保护**
   - 使用HTTPS加密传输
   - 加密敏感数据
   - 定期备份数据

3. **访问控制**
   - 配置防火墙规则
   - 限制API访问速率
   - 实现IP白名单

4. **日志监控**
   - 记录所有操作
   - 监控异常活动
   - 定期审计日志

## 许可证

MIT


# 数据库快速开始指南

## 5分钟快速设置

### 第1步: 运行初始化脚本

#### Windows用户:
```cmd
init-database.bat
```

#### Linux/Mac用户:
```bash
chmod +x init-database.sh
./init-database.sh
```

### 第2步: 按照提示输入参数

```
Enter MySQL host (default: localhost): [按Enter]
Enter MySQL port (default: 3306): [按Enter]
Enter MySQL root username (default: root): [输入root用户名]
Enter MySQL root password: [输入root密码]
Enter new database name (default: shark_fitness): [按Enter]
Enter new database user (default: shark_user): [按Enter]
Enter new database user password: [输入新密码]
```

### 第3步: 更新应用配置

编辑 `SpringBoot/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shark_fitness?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
spring.datasource.username=shark_user
spring.datasource.password=[你输入的密码]
```

### 第4步: 启动应用

```bash
cd SpringBoot
mvn spring-boot:run
```

## 测试登录

使用以下任意账户登录:

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| coach_zhang | coach123 | 教练 |
| user_xiaoming | user123 | 普通用户 |

## 验证数据库

```bash
# 连接到数据库
mysql -u shark_user -p shark_fitness

# 查看所有表
SHOW TABLES;

# 查看用户数据
SELECT * FROM user;

# 查看数据统计
SELECT COUNT(*) FROM user;
SELECT COUNT(*) FROM fitness_resource;
SELECT COUNT(*) FROM dynamic;
```

## 包含的数据

- ✅ 10个用户 (1个管理员, 3个教练, 6个普通用户)
- ✅ 10个健身资源 (5个视频, 5个文章)
- ✅ 5个训练计划
- ✅ 10条社区动态
- ✅ 56条打卡记录
- ✅ 25条饮食记录
- ✅ 20条评论
- ✅ 37条点赞
- ✅ 7个教练-学生关系
- ✅ 14条资源收藏

## 常见问题

### 脚本执行失败?

1. 确保MySQL已安装并运行
2. 确保MySQL在系统PATH中
3. 检查root用户密码是否正确
4. 查看详细错误信息

### 连接数据库失败?

```bash
# 测试连接
mysql -h localhost -u shark_user -p shark_fitness

# 如果失败，检查:
# 1. MySQL服务是否运行
# 2. 用户名和密码是否正确
# 3. 数据库是否存在
```

### 如何重新初始化?

```bash
# 删除数据库
mysql -u root -p -e "DROP DATABASE shark_fitness;"

# 重新运行初始化脚本
./init-database.sh  # Linux/Mac
init-database.bat   # Windows
```

## 手动初始化 (如果脚本失败)

```bash
# 1. 创建数据库和用户
mysql -u root -p <<EOF
CREATE DATABASE shark_fitness CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'shark_user'@'localhost' IDENTIFIED BY 'shark_password';
GRANT ALL PRIVILEGES ON shark_fitness.* TO 'shark_user'@'localhost';
FLUSH PRIVILEGES;
EOF

# 2. 导入架构
mysql -u shark_user -p shark_fitness < SpringBoot/src/main/resources/schema.sql

# 3. 导入数据
mysql -u shark_user -p shark_fitness < SpringBoot/src/main/resources/data.sql
```

## 下一步

1. 查看 `DATABASE_SETUP.md` 了解详细信息
2. 查看 `DATABASE_QUERIES.md` 学习SQL查询
3. 查看 `README.md` 了解项目概述
4. 开始开发!

## 文件清单

| 文件 | 说明 |
|------|------|
| `schema.sql` | 数据库表结构 (9KB) |
| `data.sql` | 中文模拟数据 (24KB) |
| `init-database.sh` | Linux/Mac初始化脚本 |
| `init-database.bat` | Windows初始化脚本 |
| `DATABASE_SETUP.md` | 详细设置指南 |
| `DATABASE_QUERIES.md` | SQL查询示例 |
| `DATABASE_SUMMARY.md` | 数据库总结 |
| `QUICK_START_DATABASE.md` | 本文件 |

---

**准备好了吗? 现在就开始吧!** 🚀

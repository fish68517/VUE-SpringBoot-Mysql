# 校园健康宠物领养帮养App - Android客户端

## 项目简介

这是校园健康宠物领养帮养App的Android客户端，使用Java开发，基于Retrofit2进行网络请求，支持宠物领养、帮养、社区交流、购物等功能。

## 技术栈

- **开发语言**: Java
- **最低SDK**: 24 (Android 7.0)
- **目标SDK**: 34 (Android 14)
- **网络请求**: Retrofit2 + OkHttp3
- **图片加载**: Glide
- **WebSocket**: Java-WebSocket
- **UI组件**: Material Design Components

## 项目结构

```
app/src/main/java/com/hakimi/
├── HakimiApplication.java          # 应用程序主类
├── activity/                        # Activity
│   ├── SplashActivity.java         # 启动页
│   ├── LoginActivity.java          # 登录页
│   ├── RegisterActivity.java       # 注册页
│   ├── MainActivity.java           # 主界面
│   ├── PetDetailActivity.java      # 宠物详情
│   ├── PublishPetActivity.java     # 发布宠物
│   ├── PublishPostActivity.java    # 发布动态
│   └── ...
├── fragment/                        # Fragment
│   ├── HomeFragment.java           # 首页
│   ├── CommunityFragment.java      # 社区
│   ├── ShoppingFragment.java       # 购物
│   └── MineFragment.java           # 我的
├── adapter/                         # 列表适配器
│   ├── PetAdapter.java             # 宠物列表适配器
│   ├── CommunityPostAdapter.java   # 动态列表适配器
│   └── GoodsAdapter.java           # 商品列表适配器
├── model/                           # 数据模型
│   ├── User.java                   # 用户模型
│   ├── Pet.java                    # 宠物模型
│   ├── CommunityPost.java          # 动态模型
│   └── ...
├── network/                         # 网络请求
│   ├── ApiService.java             # API接口定义
│   └── RetrofitClient.java         # Retrofit客户端
├── utils/                           # 工具类
│   ├── SharedPrefManager.java      # SharedPreferences管理
│   ├── ImageLoader.java            # 图片加载工具
│   └── TextUtils.java              # 文本工具
└── websocket/                       # WebSocket
    └── ChatWebSocketManager.java   # WebSocket管理类
```

## 配置说明

### 1. 服务器地址配置

在 `network/ApiService.java` 中配置服务器地址：

```java
String BASE_URL = "http://10.0.2.2:8081/api/"; // Android模拟器使用10.0.2.2
// 如果使用真机，请改为实际服务器IP，例如：http://192.168.1.100:8081/api/
```

**注意**：
- Android模拟器访问本地服务器使用：`10.0.2.2`
- 真机访问同一网络下的服务器使用：服务器实际IP地址（如：`192.168.1.100`）

### 2. Gradle配置

确保 `build.gradle` 文件中包含必要的依赖：

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    // ... 其他依赖
}
```

### 3. 权限配置

在 `AndroidManifest.xml` 中已配置以下权限：
- INTERNET：网络访问
- ACCESS_NETWORK_STATE：网络状态
- READ_EXTERNAL_STORAGE：读取存储
- WRITE_EXTERNAL_STORAGE：写入存储
- CAMERA：相机权限（用于拍照上传）

## 运行说明

### 1. 环境要求

- Android Studio Arctic Fox 或更高版本
- JDK 1.8 或更高版本
- Android SDK API 24+

### 2. 导入项目

1. 使用Android Studio打开项目
2. 等待Gradle同步完成
3. 配置Android SDK路径（如需要）

### 3. 运行项目

1. 连接Android设备或启动模拟器
2. 确保后端服务器已启动（参考SpringBoot后端README）
3. 点击运行按钮，选择目标设备
4. 等待应用安装和启动

### 4. 测试账号

可以使用后端模拟数据中的测试账号：

- 邮箱：`13800138000`
- 用户名：`admin`
- 密码：`123456`

## 功能模块

### 1. 用户模块
- 用户注册
- 用户登录
- 用户信息管理

### 2. 宠物模块
- 浏览宠物列表
- 查看宠物详情
- 发布宠物信息（领养/帮养）
- 申请领养

### 3. 社区模块
- 浏览动态列表
- 发布动态
- 点赞、评论（待完善）

### 4. 购物模块
- 浏览商品列表
- 查看商品详情
- 购物车（待完善）
- 订单管理（待完善）

### 5. 聊天模块
- WebSocket实时聊天（待完善）

## 注意事项

### 1. 网络配置

- 确保手机和服务器在同一网络下
- 模拟器使用 `10.0.2.2` 访问本地服务器
- 真机需要配置服务器IP地址

### 2. 权限申请

Android 6.0+需要动态申请运行时权限，当前代码中部分权限可能需要在运行时申请。

### 3. 图片资源

项目中的图片资源需要自行添加，建议将应用图标放在 `res/mipmap/` 目录下。

### 4. ProGuard配置

如果需要启用代码混淆，需要在 `proguard-rules.pro` 中添加Retrofit、Gson等库的混淆规则。

## 待完善功能

1. **网络请求完善**
   - 完善所有API接口的调用
   - 添加请求失败重试机制
   - 添加网络状态监听

2. **UI完善**
   - 完善各个Activity的布局和交互
   - 添加加载动画
   - 添加空状态页面

3. **功能完善**
   - 完善评论功能
   - 完善购物车和订单功能
   - 完善WebSocket聊天功能
   - 添加图片选择器

4. **优化**
   - 添加数据缓存
   - 优化列表加载性能
   - 添加下拉刷新和上拉加载更多

## 常见问题

### 1. 网络连接失败

- 检查服务器是否启动
- 检查服务器地址配置是否正确
- 检查网络权限是否已申请

### 2. 编译错误

- 确保所有依赖都已正确下载
- 检查SDK版本是否匹配
- 清理项目后重新构建

### 3. 图片加载失败

- 检查Glide依赖是否正确
- 检查网络权限
- 检查图片URL是否有效

## 联系支持

如有问题，请查看代码注释或联系开发团队。


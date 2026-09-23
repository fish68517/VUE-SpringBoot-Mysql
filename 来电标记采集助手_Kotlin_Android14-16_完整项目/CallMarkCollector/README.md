# 来电标记采集助手（Kotlin / Android 14～16）

这是一个独立 Android 项目，包名为 `com.example.callmarkcollector`，不会与“校园导览助手”混合。应用通过电话状态监听确认响铃，再使用无障碍服务读取来电界面的可见文本，提取主叫号码、是否被标记、标记人数和标记类型。每生成一条记录都会调用上传入口。

## 环境

- Android 14（API 34）及以上手机
- `compileSdk = 36`、`targetSdk = 36`、`minSdk = 34`
- Android Gradle Plugin 8.13.2
- Gradle 8.13
- Kotlin 2.2.21
- JDK 17

## 首次使用

1. 安装并打开应用。
2. 点击“授权读取响铃状态”，允许电话状态权限。
3. 点击“打开无障碍设置”，手动开启“来电标记识别服务”。
4. 服务端接口暂时没有时可以留空；采集后会显示记录并打印待上传 JSON。
5. 后续获得接口后，在首页填写完整 POST 地址并保存。

系统禁止普通应用静默开启无障碍服务，因此第 3 步必须由设备使用者亲自确认。

## 识别与上传流程

```text
手机响铃/来电窗口变化
        ↓
等待界面文字稳定（650 ms，并最多重试 2 次）
        ↓
读取 AccessibilityNodeInfo 的 text/contentDescription
        ↓
解析号码、标记人数、标记类型
        ↓
同一次来电会话去重并生成一条记录
        ↓
立即调用 CallRecordUploader.upload()
        ↓
接口已配置：POST JSON；接口未配置：记录 JSON 与日志
```

## 日志

主要 Logcat 标签：

- `CallMarkCapture`：服务生命周期、电话状态、窗口节点、采集会话
- `CallMarkParser`：号码与标记解析结果
- `CallMarkUpload`：上传入口、JSON、HTTP 状态或异常
- `CallMarkUI`：权限和接口配置变化

查看日志：

```powershell
.\运行来电标记采集.ps1 -跳过清理 -查看日志
```

也可以直接执行：

```powershell
adb logcat -s CallMarkCapture:D CallMarkParser:D CallMarkUpload:D CallMarkUI:D *:S
```

## Windows 一键运行

在项目根目录打开 PowerShell：

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\运行来电标记采集.ps1
```

仅测试并构建 APK：

```powershell
.\运行来电标记采集.ps1 -仅构建
```

## 兼容边界

- 不同品牌的电话应用可能使用不同文案、视图结构，甚至不向无障碍公开敏感文字。解析器已避免依赖固定控件 ID，但不能保证覆盖所有 OEM。
- 第一次真机测试时，请在 Logcat 中检查“窗口文本节点”日志；若厂商使用新的标记词，可在 `CallScreenParser.kt` 的 `knownMarkLabels` 中补充。
- 未授予电话状态权限时，应用会通过包名、Activity 名和“来电/接听/拒绝”等文字进行降级判断，误触发风险更高。
- 项目不读取通讯录和通话记录，也不尝试绕过系统权限。仅应部署在已取得设备使用者授权的场景。
- 当前版本允许 HTTP 便于内网调试；生产环境应使用 HTTPS，并在服务端增加鉴权、限流、去重及隐私合规措施。

服务端字段说明见 [服务端接口约定.md](服务端接口约定.md)。

# Android 拨号页面复刻

这是一个纯本地 Jetpack Compose 页面复刻工程，包含拨号键盘、模拟呼叫页和电话记录页。工程不会调用系统拨号、通讯录或通话记录服务。

## PowerShell 编译

在 `code` 目录执行：

```powershell
Set-Location -LiteralPath "E:\bishe27\springboot-vue-mysql-android\Android拨号界面复刻\code"
.\build.bat assembleDebug
```

`build.bat` 不受 PowerShell ExecutionPolicy 限制，推荐优先使用。如果需要继续使用 `build.ps1`，可仅对当前这一次进程绕过执行策略：

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\build.ps1" -Task assembleDebug
```

脚本优先使用项目内的 `gradlew.bat`，不存在时回退到 PATH 中的 `gradle`。项目已固定使用 Gradle 8.9；不要用 Gradle 9.0 milestone 运行，因为 Android Gradle Plugin 8.7 会触发 `debugRuntimeClasspathCopy` 配置兼容错误。也可以通过 `-GradlePath` 指定 `gradle.bat` 的绝对路径：

```powershell
.\build.ps1 -Task assembleDebug -GradlePath "C:\tools\gradle-8.7\bin\gradle.bat"
```

本工程未在交付前执行 APK 编译；运行前请确保 JDK 17、Android SDK 35 和 Gradle 可用。

param(
    [switch]$仅构建,
    [switch]$跳过清理,
    [switch]$查看日志
)

$ErrorActionPreference = "Stop"
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::InputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)

function Write-阶段([string]$内容) {
    Write-Host "`n[来电标记采集] $内容" -ForegroundColor Cyan
}

function Get-AdbPath {
    $命令 = Get-Command adb -ErrorAction SilentlyContinue
    if ($命令) { return $命令.Source }

    foreach ($SDK变量 in @($env:ANDROID_HOME, $env:ANDROID_SDK_ROOT)) {
        if ($SDK变量) {
            $候选 = Join-Path $SDK变量 "platform-tools\adb.exe"
            if (Test-Path $候选) { return $候选 }
        }
    }
    return $null
}

Push-Location $PSScriptRoot
try {
    Write-阶段 "检查 Java 与 Android 构建环境"
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        throw "未找到 Java。请安装 JDK 17，并正确设置 JAVA_HOME。"
    }
    if (-not (Test-Path ".\gradlew.bat")) {
        throw "项目根目录缺少 gradlew.bat。"
    }

    $任务 = @()
    if (-not $跳过清理) { $任务 += "clean" }
    $任务 += "testDebugUnitTest"
    $任务 += "assembleDebug"

    Write-阶段 "运行解析器单元测试并构建 Debug APK"
    & ".\gradlew.bat" @任务 "--console=plain"
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle 构建失败，退出代码：$LASTEXITCODE"
    }

    $APK = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"
    if (-not (Test-Path $APK)) {
        throw "构建结束但未找到 APK：$APK"
    }
    Write-Host "`n构建成功：$APK" -ForegroundColor Green

    if ($仅构建) {
        Write-Host "已按参数要求仅完成测试和构建。" -ForegroundColor Yellow
        exit 0
    }

    $ADB路径 = Get-AdbPath
    if (-not $ADB路径) {
        Write-Host "未检测到 adb，APK 已生成。可在 Android Studio 中连接手机后手动安装。" -ForegroundColor Yellow
        exit 0
    }

    $设备 = & $ADB路径 devices | Select-String "`tdevice$"
    if (-not $设备) {
        Write-Host "未发现已连接设备，APK 已生成，暂不自动安装。" -ForegroundColor Yellow
        exit 0
    }

    Write-阶段 "安装并启动来电标记采集助手"
    & $ADB路径 install -r $APK
    if ($LASTEXITCODE -ne 0) { throw "APK 安装失败。" }
    & $ADB路径 shell am start -n "com.example.callmarkcollector/.ui.MainActivity"
    if ($LASTEXITCODE -ne 0) { throw "应用已安装，但自动启动失败。" }

    Write-Host "应用已启动。请在页面内授权电话状态权限，并手动开启无障碍服务。" -ForegroundColor Green

    if ($查看日志) {
        Write-阶段 "开始显示采集日志，按 Ctrl+C 结束"
        & $ADB路径 logcat -s "CallMarkCapture:D" "CallMarkParser:D" "CallMarkUpload:D" "CallMarkUI:D" "*:S"
    }
}
catch {
    Write-Host "`n运行失败：$($_.Exception.Message)" -ForegroundColor Red
    Write-Host "请确认 JDK 17、Android SDK 36 与网络环境可用。" -ForegroundColor Yellow
    exit 1
}
finally {
    Pop-Location
}

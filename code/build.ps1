[CmdletBinding()]
param(
    [ValidateSet("assembleDebug", "assembleRelease", "lint", "test")]
    [string]$Task = "assembleDebug",
    [string]$GradlePath = ""
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location -LiteralPath $projectRoot

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "Java was not found. Install JDK 17 and add java to PATH."
}

if ($env:ANDROID_HOME -and -not (Test-Path -LiteralPath $env:ANDROID_HOME)) {
    throw "ANDROID_HOME points to a missing directory: $env:ANDROID_HOME"
}

$gradleCommand = $null
$gradleArguments = @($Task, "--stacktrace")
$wrapper = Join-Path $projectRoot "gradlew.bat"

if ($GradlePath) {
    if (-not (Test-Path -LiteralPath $GradlePath)) {
        throw "The Gradle path does not exist: $GradlePath"
    }
    $gradleCommand = (Resolve-Path -LiteralPath $GradlePath).Path
} elseif (Test-Path -LiteralPath $wrapper) {
    $gradleCommand = $wrapper
} elseif (Get-Command gradle -ErrorAction SilentlyContinue) {
    $gradleCommand = (Get-Command gradle).Source
    Write-Warning "Gradle Wrapper is missing; using Gradle from PATH: $gradleCommand"
} else {
    throw "Neither gradlew.bat nor gradle was found. Install Gradle 8.9+ or pass -GradlePath to gradle.bat."
}

Write-Host "Project: $projectRoot"
Write-Host "Task: $Task"
& $gradleCommand @gradleArguments
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

if ($Task -eq "assembleDebug") {
    $apkPath = Join-Path $projectRoot "app\build\outputs\apk\debug\app-debug.apk"
    if (Test-Path -LiteralPath $apkPath) {
        Write-Host "Debug APK: $apkPath"
    } else {
        Write-Warning "Gradle completed, but the expected APK was not found: $apkPath"
    }
}

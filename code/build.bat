@echo off
setlocal

cd /d "%~dp0"

set "BUILD_TASK=%~1"
if "%BUILD_TASK%"=="" set "BUILD_TASK=assembleDebug"

if not exist "gradlew.bat" (
    echo ERROR: gradlew.bat was not found in %CD%
    exit /b 1
)

echo Project: %CD%
echo Task: %BUILD_TASK%
call "gradlew.bat" %BUILD_TASK% --stacktrace
if errorlevel 1 exit /b %ERRORLEVEL%

if /I "%BUILD_TASK%"=="assembleDebug" (
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo Debug APK: %CD%\app\build\outputs\apk\debug\app-debug.apk
    ) else (
        echo WARNING: Gradle completed, but app-debug.apk was not found.
    )
)

exit /b 0

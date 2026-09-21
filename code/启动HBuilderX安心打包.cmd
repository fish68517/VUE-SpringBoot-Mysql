@echo off
setlocal
if /I "%~1"=="--check" goto check
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\start-hbuilder-safe.ps1"
set "LAUNCH_RESULT=%ERRORLEVEL%"
if not "%LAUNCH_RESULT%"=="0" pause
exit /b %LAUNCH_RESULT%
:check
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\start-hbuilder-safe.ps1" -CheckOnly
exit /b %ERRORLEVEL%

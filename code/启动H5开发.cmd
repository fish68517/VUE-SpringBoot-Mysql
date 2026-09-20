@echo off
cd /d "%~dp0"
if not exist node_modules (
  call npm ci
  if errorlevel 1 goto failed
)
call npm run prepare:demo
if errorlevel 1 goto failed
call npm run dev:h5
goto end
:failed
echo Please check the error above.
pause
:end

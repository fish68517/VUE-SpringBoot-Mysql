@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set "MYSQL_HOST=localhost"
set "MYSQL_PORT=3306"
set "MYSQL_USER=root"
set "MYSQL_PASSWORD=root"
set "MYSQL_CMD=mysql"
set "SCRIPT_DIR=%~dp0"
set "SCHEMA_SQL=%SCRIPT_DIR%schema.sql"
set "DATA_SQL=%SCRIPT_DIR%data.sql"

echo ==============================================
echo   Power Inspection MySQL Init Script
echo ==============================================
echo.
echo Current config:
echo   Host     = %MYSQL_HOST%
echo   Port     = %MYSQL_PORT%
echo   User     = %MYSQL_USER%
echo   Password = %MYSQL_PASSWORD%
echo.

where %MYSQL_CMD% >nul 2>nul
if errorlevel 1 (
    echo mysql command was not found in PATH.
    set /p MYSQL_CMD=Please enter full path of mysql.exe: 
)

if not exist "%SCHEMA_SQL%" (
    echo [ERROR] schema.sql not found: %SCHEMA_SQL%
    pause
    exit /b 1
)

if not exist "%DATA_SQL%" (
    echo [ERROR] data.sql not found: %DATA_SQL%
    pause
    exit /b 1
)

echo [1/2] Running schema.sql to create database and tables...
"%MYSQL_CMD%" --default-character-set=utf8mb4 -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < "%SCHEMA_SQL%"
if errorlevel 1 (
    echo.
    echo [FAILED] schema.sql execution failed.
    echo Check MySQL service, username, and password.
    pause
    exit /b 1
)

echo [2/2] Running data.sql to insert initial data...
"%MYSQL_CMD%" --default-character-set=utf8mb4 -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < "%DATA_SQL%"
if errorlevel 1 (
    echo.
    echo [FAILED] data.sql execution failed.
    echo Check whether tables were created successfully.
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Database initialization completed.
echo Database : power_inspection
echo Default accounts:
echo   admin      / 123456
echo   inspector1 / 123456
echo   maintainer1 / 123456
echo.
pause

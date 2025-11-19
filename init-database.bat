@echo off
REM Database Initialization Script for Little Shark Fitness Management System
REM 小鲨鱼健身管理系统数据库初始化脚本 (Windows)

setlocal enabledelayedexpansion

echo.
echo ==========================================
echo Little Shark Fitness Database Initialization
echo ==========================================
echo.

REM Check if MySQL is installed
where mysql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] MySQL client is not installed or not in PATH.
    echo Please install MySQL and add it to your system PATH.
    pause
    exit /b 1
)

echo [SUCCESS] MySQL client found
echo.

REM Get database connection parameters
set DB_HOST=localhost
set DB_PORT=3306
set DB_ROOT_USER=root
set DB_NAME=shark_fitness
set DB_USER=shark_user

echo Enter database connection parameters:
set /p DB_HOST="Enter MySQL host (default: localhost): "
set /p DB_PORT="Enter MySQL port (default: 3306): "
set /p DB_ROOT_USER="Enter MySQL root username (default: root): "
set /p DB_ROOT_PASSWORD="Enter MySQL root password: "
set /p DB_NAME="Enter new database name (default: shark_fitness): "
set /p DB_USER="Enter new database user (default: shark_user): "
set /p DB_PASSWORD="Enter new database user password: "

echo.
echo Database Configuration:
echo   Host: !DB_HOST!
echo   Port: !DB_PORT!
echo   Database: !DB_NAME!
echo   User: !DB_USER!
echo.

set /p CONFIRM="Continue with database initialization? (y/n): "
if /i not "!CONFIRM!"=="y" (
    echo [WARNING] Database initialization cancelled
    pause
    exit /b 1
)

echo.
echo ==========================================
echo Creating Database and User
echo ==========================================

REM Create database and user
(
    echo CREATE DATABASE IF NOT EXISTS !DB_NAME! CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    echo CREATE USER IF NOT EXISTS '!DB_USER!'@'localhost' IDENTIFIED BY '!DB_PASSWORD!';
    echo GRANT ALL PRIVILEGES ON !DB_NAME!.* TO '!DB_USER!'@'localhost';
    echo FLUSH PRIVILEGES;
) | mysql -h !DB_HOST! -P !DB_PORT! -u !DB_ROOT_USER! -p!DB_ROOT_PASSWORD!

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Database and user created successfully
) else (
    echo [ERROR] Failed to create database and user
    pause
    exit /b 1
)

echo.
echo ==========================================
echo Importing Database Schema
echo ==========================================

REM Import schema
if exist "SpringBoot\src\main\resources\schema.sql" (
    mysql -h !DB_HOST! -P !DB_PORT! -u !DB_USER! -p!DB_PASSWORD! !DB_NAME! < SpringBoot\src\main\resources\schema.sql
    if %ERRORLEVEL% EQU 0 (
        echo [SUCCESS] Database schema imported successfully
    ) else (
        echo [ERROR] Failed to import database schema
        pause
        exit /b 1
    )
) else (
    echo [ERROR] schema.sql not found at SpringBoot\src\main\resources\schema.sql
    pause
    exit /b 1
)

echo.
echo ==========================================
echo Importing Sample Data
echo ==========================================

REM Import sample data
if exist "SpringBoot\src\main\resources\data.sql" (
    mysql -h !DB_HOST! -P !DB_PORT! -u !DB_USER! -p!DB_PASSWORD! !DB_NAME! < SpringBoot\src\main\resources\data.sql
    if %ERRORLEVEL% EQU 0 (
        echo [SUCCESS] Sample data imported successfully
    ) else (
        echo [ERROR] Failed to import sample data
        pause
        exit /b 1
    )
) else (
    echo [ERROR] data.sql not found at SpringBoot\src\main\resources\data.sql
    pause
    exit /b 1
)

echo.
echo ==========================================
echo Database Initialization Completed!
echo ==========================================
echo.

echo Database Connection Information:
echo   Host: !DB_HOST!
echo   Port: !DB_PORT!
echo   Database: !DB_NAME!
echo   Username: !DB_USER!
echo   Password: (as entered)
echo.

echo Update your application configuration:
echo   File: SpringBoot\src\main\resources\application.properties
echo   spring.datasource.url=jdbc:mysql://!DB_HOST!:!DB_PORT!/!DB_NAME!?useUnicode=true^&characterEncoding=utf8mb4^&serverTimezone=Asia/Shanghai
echo   spring.datasource.username=!DB_USER!
echo   spring.datasource.password=!DB_PASSWORD!
echo.

echo Test database connection:
echo   mysql -h !DB_HOST! -P !DB_PORT! -u !DB_USER! -p !DB_NAME!
echo.

echo [SUCCESS] Database initialization script completed successfully!
pause

#!/bin/bash

# Database Initialization Script for Little Shark Fitness Management System
# 小鲨鱼健身管理系统数据库初始化脚本

set -e

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Get script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

print_info "=========================================="
print_info "Little Shark Fitness Database Initialization"
print_info "=========================================="
echo ""

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    print_error "MySQL client is not installed. Please install MySQL first."
    exit 1
fi

print_success "MySQL client found"
echo ""

# Get database connection parameters
read -p "Enter MySQL host (default: localhost): " DB_HOST
DB_HOST=${DB_HOST:-localhost}

read -p "Enter MySQL port (default: 3306): " DB_PORT
DB_PORT=${DB_PORT:-3306}

read -p "Enter MySQL root username (default: root): " DB_ROOT_USER
DB_ROOT_USER=${DB_ROOT_USER:-root}

read -sp "Enter MySQL root password: " DB_ROOT_PASSWORD
echo ""

read -p "Enter new database name (default: shark_fitness): " DB_NAME
DB_NAME=${DB_NAME:-shark_fitness}

read -p "Enter new database user (default: shark_user): " DB_USER
DB_USER=${DB_USER:-shark_user}

read -sp "Enter new database user password: " DB_PASSWORD
echo ""

echo ""
print_info "Database Configuration:"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo ""

read -p "Continue with database initialization? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    print_warning "Database initialization cancelled"
    exit 1
fi

echo ""
print_info "=========================================="
print_info "Creating Database and User"
print_info "=========================================="

# Create database and user
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_ROOT_USER" -p"$DB_ROOT_PASSWORD" <<EOF
CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASSWORD';
GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';
FLUSH PRIVILEGES;
EOF

if [ $? -eq 0 ]; then
    print_success "Database and user created successfully"
else
    print_error "Failed to create database and user"
    exit 1
fi

echo ""
print_info "=========================================="
print_info "Importing Database Schema"
print_info "=========================================="

# Import schema
if [ -f "$SCRIPT_DIR/SpringBoot/src/main/resources/schema.sql" ]; then
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < "$SCRIPT_DIR/SpringBoot/src/main/resources/schema.sql"
    if [ $? -eq 0 ]; then
        print_success "Database schema imported successfully"
    else
        print_error "Failed to import database schema"
        exit 1
    fi
else
    print_error "schema.sql not found at $SCRIPT_DIR/SpringBoot/src/main/resources/schema.sql"
    exit 1
fi

echo ""
print_info "=========================================="
print_info "Importing Sample Data"
print_info "=========================================="

# Import sample data
if [ -f "$SCRIPT_DIR/SpringBoot/src/main/resources/data.sql" ]; then
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < "$SCRIPT_DIR/SpringBoot/src/main/resources/data.sql"
    if [ $? -eq 0 ]; then
        print_success "Sample data imported successfully"
    else
        print_error "Failed to import sample data"
        exit 1
    fi
else
    print_error "data.sql not found at $SCRIPT_DIR/SpringBoot/src/main/resources/data.sql"
    exit 1
fi

echo ""
print_info "=========================================="
print_info "Verifying Database"
print_info "=========================================="

# Verify database
TABLE_COUNT=$(mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SHOW TABLES;" | wc -l)
TABLE_COUNT=$((TABLE_COUNT - 1))

print_success "Database verification completed"
echo "  Total tables: $TABLE_COUNT"

echo ""
print_success "=========================================="
print_success "Database Initialization Completed!"
print_success "=========================================="
echo ""

print_info "Database Connection Information:"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo "  Username: $DB_USER"
echo "  Password: (as entered)"
echo ""

print_info "Update your application configuration:"
echo "  File: SpringBoot/src/main/resources/application.properties"
echo "  spring.datasource.url=jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai"
echo "  spring.datasource.username=$DB_USER"
echo "  spring.datasource.password=$DB_PASSWORD"
echo ""

print_info "Test database connection:"
echo "  mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p $DB_NAME"
echo ""

print_success "Database initialization script completed successfully!"

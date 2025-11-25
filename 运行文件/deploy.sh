#!/bin/bash

# Deployment script for Little Shark Fitness Management System
# This script helps deploy the application to a remote server

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

# Configuration
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BACKEND_JAR="$SCRIPT_DIR/SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar"
FRONTEND_DIST="$SCRIPT_DIR/VUE/dist"

# Check if builds exist
if [ ! -f "$BACKEND_JAR" ]; then
    print_error "Backend JAR not found at $BACKEND_JAR"
    print_info "Please run ./build-all.sh first"
    exit 1
fi

if [ ! -d "$FRONTEND_DIST" ]; then
    print_error "Frontend dist not found at $FRONTEND_DIST"
    print_info "Please run ./build-all.sh first"
    exit 1
fi

print_success "Build artifacts found"
echo ""

# Get deployment parameters
read -p "Enter remote server address (user@host): " REMOTE_SERVER
read -p "Enter backend deployment path (default: /opt/shark-fitness): " BACKEND_PATH
BACKEND_PATH=${BACKEND_PATH:-/opt/shark-fitness}

read -p "Enter frontend deployment path (default: /var/www/shark-fitness/frontend): " FRONTEND_PATH
FRONTEND_PATH=${FRONTEND_PATH:-/var/www/shark-fitness/frontend}

echo ""
print_info "Deployment Configuration:"
echo "  Remote Server: $REMOTE_SERVER"
echo "  Backend Path: $BACKEND_PATH"
echo "  Frontend Path: $FRONTEND_PATH"
echo ""

read -p "Continue with deployment? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    print_warning "Deployment cancelled"
    exit 1
fi

echo ""
print_info "=========================================="
print_info "Deploying Backend"
print_info "=========================================="

# Backup current backend
print_info "Creating backup of current backend..."
ssh "$REMOTE_SERVER" "mkdir -p $BACKEND_PATH/backup && cp $BACKEND_PATH/shark-fitness-system-0.0.1-SNAPSHOT.jar $BACKEND_PATH/backup/shark-fitness-system-0.0.1-SNAPSHOT.jar.bak 2>/dev/null || true"

# Deploy backend
print_info "Uploading backend JAR..."
scp "$BACKEND_JAR" "$REMOTE_SERVER:$BACKEND_PATH/"

# Restart backend service
print_info "Restarting backend service..."
ssh "$REMOTE_SERVER" "sudo systemctl restart shark-fitness"

# Wait for service to start
sleep 3

# Check if service is running
if ssh "$REMOTE_SERVER" "sudo systemctl is-active --quiet shark-fitness"; then
    print_success "Backend deployed and running"
else
    print_error "Backend service failed to start"
    print_warning "Check logs with: sudo journalctl -u shark-fitness -n 50"
    exit 1
fi

echo ""
print_info "=========================================="
print_info "Deploying Frontend"
print_info "=========================================="

# Backup current frontend
print_info "Creating backup of current frontend..."
ssh "$REMOTE_SERVER" "mkdir -p $FRONTEND_PATH.bak && cp -r $FRONTEND_PATH/* $FRONTEND_PATH.bak/ 2>/dev/null || true"

# Deploy frontend
print_info "Uploading frontend files..."
ssh "$REMOTE_SERVER" "mkdir -p $FRONTEND_PATH"
scp -r "$FRONTEND_DIST"/* "$REMOTE_SERVER:$FRONTEND_PATH/"

# Restart Nginx
print_info "Restarting Nginx..."
ssh "$REMOTE_SERVER" "sudo systemctl restart nginx"

# Check if Nginx is running
if ssh "$REMOTE_SERVER" "sudo systemctl is-active --quiet nginx"; then
    print_success "Frontend deployed and Nginx running"
else
    print_error "Nginx failed to start"
    print_warning "Check logs with: sudo tail -f /var/log/nginx/error.log"
    exit 1
fi

echo ""
print_success "=========================================="
print_success "Deployment Completed Successfully!"
print_success "=========================================="
echo ""
print_info "Deployment Summary:"
echo "  Backend: Deployed to $BACKEND_PATH"
echo "  Frontend: Deployed to $FRONTEND_PATH"
echo ""
print_info "Verification:"
echo "  Backend logs: ssh $REMOTE_SERVER 'sudo journalctl -u shark-fitness -f'"
echo "  Nginx logs: ssh $REMOTE_SERVER 'sudo tail -f /var/log/nginx/error.log'"
echo ""
print_info "Rollback (if needed):"
echo "  Backend: ssh $REMOTE_SERVER 'cp $BACKEND_PATH/backup/shark-fitness-system-0.0.1-SNAPSHOT.jar.bak $BACKEND_PATH/shark-fitness-system-0.0.1-SNAPSHOT.jar && sudo systemctl restart shark-fitness'"
echo "  Frontend: ssh $REMOTE_SERVER 'rm -rf $FRONTEND_PATH && mv $FRONTEND_PATH.bak $FRONTEND_PATH && sudo systemctl restart nginx'"
echo ""

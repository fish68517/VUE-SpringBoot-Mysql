#!/bin/bash

# Complete build script for Little Shark Fitness Management System
# This script builds both backend and frontend applications

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

print_status "Starting build process..."
print_status "Script directory: $SCRIPT_DIR"
echo ""

# Check prerequisites
print_status "Checking prerequisites..."

if ! command -v java &> /dev/null; then
    print_error "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven."
    exit 1
fi

if ! command -v node &> /dev/null; then
    print_error "Node.js is not installed. Please install Node.js."
    exit 1
fi

if ! command -v npm &> /dev/null; then
    print_error "npm is not installed. Please install npm."
    exit 1
fi

print_status "Java version: $(java -version 2>&1 | head -n 1)"
print_status "Maven version: $(mvn -version | head -n 1)"
print_status "Node version: $(node --version)"
print_status "npm version: $(npm --version)"
echo ""

# Build backend
print_status "=========================================="
print_status "Building Backend Application"
print_status "=========================================="
cd "$SCRIPT_DIR/SpringBoot"

print_status "Cleaning previous builds..."
mvn clean

print_status "Building Spring Boot application..."
mvn package -DskipTests

BACKEND_JAR="$SCRIPT_DIR/SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar"
if [ -f "$BACKEND_JAR" ]; then
    print_status "Backend build successful!"
    print_status "JAR file: $BACKEND_JAR"
    print_status "JAR size: $(du -h "$BACKEND_JAR" | cut -f1)"
else
    print_error "Backend build failed! JAR file not found."
    exit 1
fi
echo ""

# Build frontend
print_status "=========================================="
print_status "Building Frontend Application"
print_status "=========================================="
cd "$SCRIPT_DIR/VUE"

print_status "Installing dependencies..."
npm install

print_status "Building Vue 3 application..."
npm run build

FRONTEND_DIST="$SCRIPT_DIR/VUE/dist"
if [ -d "$FRONTEND_DIST" ]; then
    print_status "Frontend build successful!"
    print_status "Output directory: $FRONTEND_DIST"
    print_status "Number of files: $(find "$FRONTEND_DIST" -type f | wc -l)"
else
    print_error "Frontend build failed! dist directory not found."
    exit 1
fi
echo ""

# Summary
print_status "=========================================="
print_status "Build Process Completed Successfully!"
print_status "=========================================="
echo ""
print_status "Build Artifacts:"
echo "  Backend JAR: $BACKEND_JAR"
echo "  Frontend dist: $FRONTEND_DIST"
echo ""
print_status "Next Steps:"
echo "  1. Backend deployment:"
echo "     - Copy JAR to server: scp $BACKEND_JAR user@server:/opt/shark-fitness/"
echo "     - Start service: sudo systemctl start shark-fitness"
echo ""
echo "  2. Frontend deployment:"
echo "     - Copy dist to server: scp -r $FRONTEND_DIST/* user@server:/var/www/shark-fitness/frontend/"
echo "     - Restart Nginx: sudo systemctl restart nginx"
echo ""
print_status "For detailed deployment instructions, see DEPLOYMENT.md"
echo ""

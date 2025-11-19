#!/bin/bash

# Build script for Little Shark Fitness Management System - Frontend
# This script builds the Vue 3 frontend application

set -e

echo "=========================================="
echo "Building Little Shark Fitness Frontend"
echo "=========================================="

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "Error: Node.js is not installed. Please install Node.js first."
    exit 1
fi

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "Error: npm is not installed. Please install npm first."
    exit 1
fi

# Navigate to VUE directory
cd VUE

echo "Node version: $(node --version)"
echo "npm version: $(npm --version)"
echo ""

echo "Installing dependencies..."
npm install

echo ""
echo "Building production bundle..."
npm run build

echo ""
echo "=========================================="
echo "Build completed successfully!"
echo "=========================================="
echo ""
echo "Output directory: VUE/dist"
echo ""
echo "To preview the build locally:"
echo "  npm run preview"
echo ""
echo "To deploy to production:"
echo "  Copy contents of VUE/dist to your web server"
echo ""

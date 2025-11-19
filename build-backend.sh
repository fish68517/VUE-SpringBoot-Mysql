#!/bin/bash

# Build script for Little Shark Fitness Management System - Backend
# This script builds the Spring Boot backend application

set -e

echo "=========================================="
echo "Building Little Shark Fitness Backend"
echo "=========================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven first."
    exit 1
fi

# Navigate to SpringBoot directory
cd SpringBoot

echo "Cleaning previous builds..."
mvn clean

echo "Building application..."
mvn package -DskipTests

echo ""
echo "=========================================="
echo "Build completed successfully!"
echo "=========================================="
echo ""
echo "JAR file location:"
echo "  SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar"
echo ""
echo "To run the application:"
echo "  java -jar SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar"
echo ""
echo "To run with production profile:"
echo "  java -jar SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod"
echo ""

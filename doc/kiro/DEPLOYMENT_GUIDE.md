# Travel Memory System - Deployment Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Production Environment Setup](#production-environment-setup)
4. [Database Setup](#database-setup)
5. [Backend Deployment](#backend-deployment)
6. [Frontend Deployment](#frontend-deployment)
7. [Docker Deployment](#docker-deployment)
8. [Monitoring and Maintenance](#monitoring-and-maintenance)
9. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements
- **OS**: Linux (Ubuntu 20.04+), macOS, or Windows with WSL2
- **CPU**: 2+ cores
- **RAM**: 4GB minimum (8GB recommended)
- **Disk**: 20GB free space

### Required Software
- **Java**: JDK 17 or higher
- **Node.js**: v16 or higher
- **npm**: v8 or higher
- **MySQL**: 8.0 or higher
- **Git**: Latest version
- **Docker** (optional): For containerized deployment

## Development Environment Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd travel-memory-system
```

### 2. Backend Setup

#### Install Java Dependencies
```bash
cd SpringBoot
mvn clean install
```

#### Configure Application Properties
Edit `src/main/resources/application.properties`:
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/travel_memory_system?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your-development-secret-key-change-in-production
jwt.expiration=86400000

# File Upload Configuration
file.upload.path=uploads/
file.upload.max-size=524288000
```

#### Initialize Database
```bash
mysql -u root -p < database-init.sql
mysql -u root -p < sample-data.sql
```

#### Run Backend
```bash
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080/api`

### 3. Frontend Setup

#### Install Dependencies
```bash
cd VUE
npm install
```

#### Configure API Endpoint
Edit `src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

#### Run Development Server
```bash
npm run dev
```

The frontend will be available at `http://localhost:5173`

## Production Environment Setup

### 1. Backend Production Configuration

#### Create Production Properties File
Create `src/main/resources/application-prod.properties`:
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# MySQL Database Configuration (Use managed database service)
spring.datasource.url=jdbc:mysql://prod-db-host:3306/travel_memory_system?useSSL=true&serverTimezone=UTC
spring.datasource.username=prod_user
spring.datasource.password=${DB_PASSWORD}
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Logging
logging.level.root=WARN
logging.level.com.travelMemory=INFO
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30

# JWT Configuration (Use strong secret)
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# File Upload Configuration
file.upload.path=/var/uploads/
file.upload.max-size=524288000

# HTTPS Configuration
server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
```

#### Build Production JAR
```bash
cd SpringBoot
mvn clean package -DskipTests -Pprod
```

### 2. Frontend Production Build

#### Build Production Bundle
```bash
cd VUE
npm run build
```

#### Configure Production API Endpoint
Edit `src/services/api.js` for production:
```javascript
const API_BASE_URL = 'https://api.travelmemory.com/api';
```

## Database Setup

### 1. Create Database User
```sql
CREATE USER 'travel_user'@'localhost' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON travel_memory_system.* TO 'travel_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Initialize Database Schema
```bash
mysql -u travel_user -p travel_memory_system < database-init.sql
```

### 3. Load Sample Data (Optional)
```bash
mysql -u travel_user -p travel_memory_system < sample-data.sql
```

### 4. Database Backup
```bash
# Daily backup
mysqldump -u travel_user -p travel_memory_system > backup_$(date +%Y%m%d).sql

# Automated backup with cron
0 2 * * * mysqldump -u travel_user -p travel_memory_system > /backups/travel_memory_$(date +\%Y\%m\%d).sql
```

## Backend Deployment

### 1. Using Systemd Service (Linux)

#### Create Service File
Create `/etc/systemd/system/travel-memory.service`:
```ini
[Unit]
Description=Travel Memory System Backend
After=network.target

[Service]
Type=simple
User=travel
WorkingDirectory=/opt/travel-memory
ExecStart=/usr/bin/java -jar travel-memory-system-1.0.0.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

Environment="JAVA_OPTS=-Xmx2g -Xms1g"
Environment="DB_PASSWORD=your_db_password"
Environment="JWT_SECRET=your_jwt_secret"

[Install]
WantedBy=multi-user.target
```

#### Enable and Start Service
```bash
sudo systemctl daemon-reload
sudo systemctl enable travel-memory
sudo systemctl start travel-memory
sudo systemctl status travel-memory
```

### 2. Using Nginx as Reverse Proxy

#### Create Nginx Configuration
Create `/etc/nginx/sites-available/travel-memory`:
```nginx
upstream travel_backend {
    server localhost:8080;
}

server {
    listen 80;
    server_name api.travelmemory.com;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.travelmemory.com;
    
    ssl_certificate /etc/letsencrypt/live/api.travelmemory.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.travelmemory.com/privkey.pem;
    
    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-XSS-Protection "1; mode=block" always;
    
    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;
    gzip_min_length 1000;
    
    location / {
        proxy_pass http://travel_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

#### Enable Nginx Configuration
```bash
sudo ln -s /etc/nginx/sites-available/travel-memory /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## Frontend Deployment

### 1. Using Nginx to Serve Static Files

#### Create Nginx Configuration
Create `/etc/nginx/sites-available/travel-memory-frontend`:
```nginx
server {
    listen 80;
    server_name travelmemory.com www.travelmemory.com;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name travelmemory.com www.travelmemory.com;
    
    ssl_certificate /etc/letsencrypt/live/travelmemory.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/travelmemory.com/privkey.pem;
    
    root /var/www/travel-memory/dist;
    index index.html;
    
    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    
    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;
    
    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # SPA routing
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

### 2. Deploy Frontend Build
```bash
# Build frontend
cd VUE
npm run build

# Copy to web server
sudo cp -r dist/* /var/www/travel-memory/dist/

# Set permissions
sudo chown -R www-data:www-data /var/www/travel-memory/dist
```

## Docker Deployment

### 1. Create Dockerfile for Backend

Create `SpringBoot/Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/travel-memory-system-1.0.0.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx2g -Xms1g"

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Create Dockerfile for Frontend

Create `VUE/Dockerfile`:
```dockerfile
FROM node:16-alpine as builder

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 3. Create Docker Compose File

Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: travel_memory_system
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./SpringBoot/database-init.sql:/docker-entrypoint-initdb.d/init.sql

  backend:
    build:
      context: ./SpringBoot
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/travel_memory_system
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root_password
      JWT_SECRET: your_jwt_secret
    depends_on:
      - mysql

  frontend:
    build:
      context: ./VUE
      dockerfile: Dockerfile
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

### 4. Deploy with Docker Compose
```bash
docker-compose up -d
```

## Monitoring and Maintenance

### 1. Application Logs
```bash
# View backend logs
sudo journalctl -u travel-memory -f

# View Nginx logs
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

### 2. Database Monitoring
```bash
# Check database size
SELECT table_schema, ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS size_mb
FROM information_schema.tables
WHERE table_schema = 'travel_memory_system'
GROUP BY table_schema;

# Monitor slow queries
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
```

### 3. Performance Monitoring
- Use tools like Prometheus and Grafana for metrics collection
- Monitor CPU, memory, and disk usage
- Set up alerts for critical metrics

### 4. Regular Maintenance
- Update dependencies monthly
- Review and optimize slow queries
- Clean up old logs and temporary files
- Perform regular database backups

## Troubleshooting

### Backend Issues

#### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

#### Database Connection Error
```bash
# Test MySQL connection
mysql -h localhost -u travel_user -p travel_memory_system

# Check MySQL service
sudo systemctl status mysql
```

#### JWT Token Issues
- Verify JWT secret is consistent across environments
- Check token expiration time
- Ensure Authorization header format: `Bearer <token>`

### Frontend Issues

#### Build Errors
```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install
npm run build
```

#### API Connection Issues
- Verify API endpoint in `src/services/api.js`
- Check CORS configuration in backend
- Verify network connectivity

### Database Issues

#### Backup and Recovery
```bash
# Restore from backup
mysql -u travel_user -p travel_memory_system < backup_20240101.sql
```

#### Performance Issues
- Run `ANALYZE TABLE` on large tables
- Check and optimize indexes
- Monitor slow query log

## Support and Documentation

- **API Documentation**: Available at `http://localhost:8080/api/swagger-ui.html`
- **GitHub Issues**: Report bugs and feature requests
- **Email Support**: support@travelmemory.com

## Version History

- **v1.0.0** (2024-01-XX): Initial release
  - User authentication and management
  - Travel record management
  - Multimedia file upload
  - Map footprints
  - Social interactions
  - API documentation with Swagger


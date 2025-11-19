# Deployment Guide - Little Shark Fitness Management System

## Overview

This guide provides instructions for deploying the Little Shark Fitness Management System to a production environment. The system consists of a Spring Boot backend and Vue 3 frontend.

## Prerequisites

- Java 17 or higher
- Node.js 16+ and npm
- MySQL 8.0+
- Nginx or Apache (for reverse proxy)
- Linux server (Ubuntu 20.04+ recommended)

## Backend Deployment

### 1. Build the Backend

```bash
cd SpringBoot
mvn clean package -DskipTests
```

This creates a JAR file at `SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar`

### 2. Prepare Server Environment

Create necessary directories and set permissions:

```bash
# Create application directory
sudo mkdir -p /opt/shark-fitness
sudo mkdir -p /var/www/shark-fitness/uploads
sudo mkdir -p /var/log/shark-fitness

# Set permissions
sudo chown -R appuser:appuser /opt/shark-fitness
sudo chown -R appuser:appuser /var/www/shark-fitness
sudo chown -R appuser:appuser /var/log/shark-fitness
sudo chmod -R 755 /var/www/shark-fitness/uploads
```

### 3. Configure Production Database

Create MySQL database and user:

```sql
CREATE DATABASE shark_fitness CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'shark_user'@'localhost' IDENTIFIED BY 'secure_password_here';
GRANT ALL PRIVILEGES ON shark_fitness.* TO 'shark_user'@'localhost';
FLUSH PRIVILEGES;
```

### 4. Deploy Backend JAR

Copy the JAR file to the server:

```bash
scp SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar appuser@your-server:/opt/shark-fitness/
```

### 5. Create Systemd Service File

Create `/etc/systemd/system/shark-fitness.service`:

```ini
[Unit]
Description=Little Shark Fitness Management System
After=network.target mysql.service

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/shark-fitness
ExecStart=/usr/bin/java -jar shark-fitness-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

# Environment variables for production
Environment="DB_HOST=localhost"
Environment="DB_PORT=3306"
Environment="DB_NAME=shark_fitness"
Environment="DB_USERNAME=shark_user"
Environment="DB_PASSWORD=secure_password_here"
Environment="FILE_UPLOAD_PATH=/var/www/shark-fitness/uploads"
Environment="CORS_ALLOWED_ORIGINS=https://sharkfitness.com,https://www.sharkfitness.com"
Environment="LOG_FILE_PATH=/var/log/shark-fitness/application.log"

[Install]
WantedBy=multi-user.target
```

### 6. Start the Backend Service

```bash
sudo systemctl daemon-reload
sudo systemctl enable shark-fitness
sudo systemctl start shark-fitness

# Check status
sudo systemctl status shark-fitness

# View logs
sudo journalctl -u shark-fitness -f
```

## Frontend Deployment

### 1. Build the Frontend

```bash
cd VUE
npm install
npm run build
```

This creates optimized production files in the `dist` directory.

### 2. Deploy Frontend Files

Copy the dist folder to your web server:

```bash
scp -r VUE/dist/* appuser@your-server:/var/www/shark-fitness/frontend/
```

### 3. Configure Nginx

Create `/etc/nginx/sites-available/shark-fitness`:

```nginx
# Redirect HTTP to HTTPS
server {
    listen 80;
    server_name sharkfitness.com www.sharkfitness.com;
    return 301 https://$server_name$request_uri;
}

# HTTPS server block
server {
    listen 443 ssl http2;
    server_name sharkfitness.com www.sharkfitness.com;

    # SSL certificates (use Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/sharkfitness.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/sharkfitness.com/privkey.pem;

    # SSL configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css text/javascript application/json application/javascript;
    gzip_min_length 1000;

    # Frontend static files
    location / {
        root /var/www/shark-fitness/frontend;
        try_files $uri $uri/ /index.html;
        expires 1d;
        add_header Cache-Control "public, immutable";
    }

    # API proxy to backend
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # File uploads proxy
    location /uploads/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        expires 30d;
        add_header Cache-Control "public";
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
}
```

Enable the site:

```bash
sudo ln -s /etc/nginx/sites-available/shark-fitness /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 4. Set Up SSL Certificate (Let's Encrypt)

```bash
sudo apt-get install certbot python3-certbot-nginx
sudo certbot certonly --nginx -d sharkfitness.com -d www.sharkfitness.com
```

## Database Backup and Maintenance

### Automated Backup

Create `/opt/shark-fitness/backup.sh`:

```bash
#!/bin/bash
BACKUP_DIR="/var/backups/shark-fitness"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/shark_fitness_$DATE.sql"

mkdir -p $BACKUP_DIR

mysqldump -u shark_user -p'secure_password_here' shark_fitness > $BACKUP_FILE
gzip $BACKUP_FILE

# Keep only last 30 days of backups
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete

echo "Backup completed: $BACKUP_FILE.gz"
```

Add to crontab for daily backups at 2 AM:

```bash
0 2 * * * /opt/shark-fitness/backup.sh >> /var/log/shark-fitness/backup.log 2>&1
```

## Monitoring and Logging

### Application Logs

View real-time logs:

```bash
sudo journalctl -u shark-fitness -f
```

View logs from the past hour:

```bash
sudo journalctl -u shark-fitness --since "1 hour ago"
```

### System Monitoring

Monitor resource usage:

```bash
# CPU and memory
top

# Disk usage
df -h

# Network connections
netstat -tuln | grep 8080
```

## Troubleshooting

### Backend won't start

1. Check logs: `sudo journalctl -u shark-fitness -n 50`
2. Verify database connection: `mysql -u shark_user -p shark_fitness`
3. Check file permissions: `ls -la /var/www/shark-fitness/uploads`

### Frontend not loading

1. Check Nginx logs: `sudo tail -f /var/log/nginx/error.log`
2. Verify frontend files: `ls -la /var/www/shark-fitness/frontend/`
3. Clear browser cache and reload

### Database connection issues

1. Verify MySQL is running: `sudo systemctl status mysql`
2. Check credentials in systemd service file
3. Verify database exists: `mysql -u shark_user -p -e "SHOW DATABASES;"`

## Performance Optimization

### Backend Optimization

1. Increase JVM heap size in systemd service:
   ```
   ExecStart=/usr/bin/java -Xmx2g -Xms1g -jar shark-fitness-system-0.0.1-SNAPSHOT.jar
   ```

2. Enable database connection pooling (already configured in Spring Boot)

3. Monitor slow queries in MySQL

### Frontend Optimization

1. Verify gzip compression is enabled in Nginx
2. Check that static assets are cached properly
3. Monitor bundle size: `npm run build` shows bundle analysis

## Rollback Procedure

If deployment fails:

```bash
# Stop current service
sudo systemctl stop shark-fitness

# Restore previous JAR
cp /opt/shark-fitness/backup/shark-fitness-system-0.0.1-SNAPSHOT.jar.bak /opt/shark-fitness/

# Restore previous frontend
rsync -av /var/www/shark-fitness/frontend.bak/ /var/www/shark-fitness/frontend/

# Start service
sudo systemctl start shark-fitness
```

## Security Checklist

- [ ] SSL/TLS certificates installed and valid
- [ ] Database user has limited privileges
- [ ] File upload directory is outside web root
- [ ] Firewall rules configured (only 80, 443 open)
- [ ] SSH key-based authentication enabled
- [ ] Regular backups scheduled and tested
- [ ] Log rotation configured
- [ ] Security headers configured in Nginx
- [ ] CORS origins restricted to production domain
- [ ] Database backups encrypted and stored securely

## Support and Maintenance

For issues or questions:
1. Check application logs
2. Review this deployment guide
3. Consult the project documentation
4. Contact the development team

---

**Last Updated:** 2024
**Version:** 1.0

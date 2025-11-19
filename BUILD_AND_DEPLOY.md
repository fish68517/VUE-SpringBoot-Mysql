# Build and Deployment Scripts

This document describes the build and deployment scripts for the Little Shark Fitness Management System.

## Quick Start

### Build Everything

```bash
chmod +x build-all.sh
./build-all.sh
```

This script will:
1. Check prerequisites (Java, Maven, Node.js, npm)
2. Build the Spring Boot backend
3. Build the Vue 3 frontend
4. Display build artifacts and next steps

### Deploy to Production

```bash
chmod +x deploy.sh
./deploy.sh
```

This script will:
1. Verify build artifacts exist
2. Prompt for server details
3. Backup current deployment
4. Upload and deploy backend
5. Upload and deploy frontend
6. Restart services
7. Verify deployment success

## Individual Build Scripts

### Backend Build Only

```bash
chmod +x build-backend.sh
./build-backend.sh
```

**Output:** `SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar`

**To run locally:**
```bash
java -jar SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar
```

**To run with production profile:**
```bash
java -jar SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Frontend Build Only

```bash
chmod +x build-frontend.sh
./build-frontend.sh
```

**Output:** `VUE/dist/` directory

**To preview locally:**
```bash
cd VUE
npm run preview
```

## Manual Deployment Steps

If you prefer to deploy manually, follow these steps:

### Backend Deployment

1. **Build the backend:**
   ```bash
   cd SpringBoot
   mvn clean package -DskipTests
   ```

2. **Copy JAR to server:**
   ```bash
   scp SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar user@server:/opt/shark-fitness/
   ```

3. **SSH into server and restart service:**
   ```bash
   ssh user@server
   sudo systemctl restart shark-fitness
   sudo systemctl status shark-fitness
   ```

4. **Check logs:**
   ```bash
   sudo journalctl -u shark-fitness -f
   ```

### Frontend Deployment

1. **Build the frontend:**
   ```bash
   cd VUE
   npm install
   npm run build
   ```

2. **Copy dist to server:**
   ```bash
   scp -r VUE/dist/* user@server:/var/www/shark-fitness/frontend/
   ```

3. **SSH into server and restart Nginx:**
   ```bash
   ssh user@server
   sudo systemctl restart nginx
   sudo systemctl status nginx
   ```

4. **Check logs:**
   ```bash
   sudo tail -f /var/log/nginx/error.log
   ```

## Environment Configuration

### Backend Configuration

The backend uses Spring profiles for environment-specific configuration:

- **Development** (default): `application.properties`
  - Database: localhost:3306
  - Logging: DEBUG level
  - CORS: localhost:5173, localhost:3000

- **Production**: `application-prod.properties`
  - Database: Configured via environment variables
  - Logging: INFO level
  - CORS: Configured via environment variables

**Environment Variables for Production:**
```bash
DB_HOST=your-db-host
DB_PORT=3306
DB_NAME=shark_fitness
DB_USERNAME=shark_user
DB_PASSWORD=your-secure-password
FILE_UPLOAD_PATH=/var/www/shark-fitness/uploads
CORS_ALLOWED_ORIGINS=https://sharkfitness.com,https://www.sharkfitness.com
LOG_FILE_PATH=/var/log/shark-fitness/application.log
```

### Frontend Configuration

The frontend uses environment files for configuration:

- **Development** (`.env.development`):
  ```
  VITE_API_BASE_URL=http://localhost:8080
  ```

- **Production** (`.env.production`):
  ```
  VITE_API_BASE_URL=https://api.sharkfitness.com
  VITE_SOURCE_MAP=false
  ```

## Build Optimization

### Backend Optimization

The Maven build includes:
- Dependency management
- Spring Boot packaging
- Executable JAR creation

To customize build:
```bash
cd SpringBoot
mvn clean package -DskipTests -Dspring.profiles.active=prod
```

### Frontend Optimization

The Vite build includes:
- Code splitting (Vue, Element Plus, ECharts)
- Minification with Terser
- Gzip compression ready
- Source map generation (optional)

Build output analysis:
```bash
cd VUE
npm run build
# Check dist/ directory size
du -sh dist/
```

## Troubleshooting

### Build Fails

**Backend build fails:**
- Check Java version: `java -version` (requires 17+)
- Check Maven: `mvn -version`
- Clear Maven cache: `mvn clean`
- Check disk space: `df -h`

**Frontend build fails:**
- Check Node version: `node --version` (requires 16+)
- Clear npm cache: `npm cache clean --force`
- Delete node_modules: `rm -rf VUE/node_modules`
- Reinstall: `cd VUE && npm install`

### Deployment Fails

**Backend won't start:**
- Check database connection
- Verify environment variables
- Check logs: `sudo journalctl -u shark-fitness -n 50`
- Verify file permissions: `ls -la /var/www/shark-fitness/uploads`

**Frontend not loading:**
- Check Nginx configuration: `sudo nginx -t`
- Verify frontend files: `ls -la /var/www/shark-fitness/frontend/`
- Check Nginx logs: `sudo tail -f /var/log/nginx/error.log`
- Clear browser cache

### Rollback

**Rollback backend:**
```bash
ssh user@server
sudo cp /opt/shark-fitness/backup/shark-fitness-system-0.0.1-SNAPSHOT.jar.bak /opt/shark-fitness/shark-fitness-system-0.0.1-SNAPSHOT.jar
sudo systemctl restart shark-fitness
```

**Rollback frontend:**
```bash
ssh user@server
sudo rm -rf /var/www/shark-fitness/frontend
sudo mv /var/www/shark-fitness/frontend.bak /var/www/shark-fitness/frontend
sudo systemctl restart nginx
```

## Performance Tips

### Backend Performance

1. **Increase JVM heap size:**
   ```bash
   java -Xmx2g -Xms1g -jar shark-fitness-system-0.0.1-SNAPSHOT.jar
   ```

2. **Enable database connection pooling** (already configured)

3. **Monitor slow queries:**
   ```sql
   SET GLOBAL slow_query_log = 'ON';
   SET GLOBAL long_query_time = 2;
   ```

### Frontend Performance

1. **Enable Gzip compression** (configured in Nginx)

2. **Set cache headers** (configured in Nginx)

3. **Monitor bundle size:**
   ```bash
   cd VUE
   npm run build
   # Check dist/ directory
   ```

## Monitoring

### Backend Monitoring

```bash
# Check service status
sudo systemctl status shark-fitness

# View recent logs
sudo journalctl -u shark-fitness -n 100

# Follow logs in real-time
sudo journalctl -u shark-fitness -f

# Check resource usage
ps aux | grep java
```

### Frontend Monitoring

```bash
# Check Nginx status
sudo systemctl status nginx

# View Nginx error logs
sudo tail -f /var/log/nginx/error.log

# View Nginx access logs
sudo tail -f /var/log/nginx/access.log

# Check Nginx configuration
sudo nginx -t
```

## Continuous Deployment

For automated deployments, you can integrate these scripts with CI/CD tools:

### GitHub Actions Example

```yaml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build
        run: ./build-all.sh
      - name: Deploy
        env:
          DEPLOY_KEY: ${{ secrets.DEPLOY_KEY }}
          DEPLOY_HOST: ${{ secrets.DEPLOY_HOST }}
        run: ./deploy.sh
```

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review application logs
3. Consult DEPLOYMENT.md for detailed information
4. Contact the development team

---

**Last Updated:** 2024
**Version:** 1.0

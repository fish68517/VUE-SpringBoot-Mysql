# Deployment Checklist - Little Shark Fitness Management System

Use this checklist to ensure all steps are completed before and after deployment.

## Pre-Deployment Checklist

### Prerequisites
- [ ] Java 17 or higher installed on build machine
- [ ] Maven 3.6+ installed
- [ ] Node.js 16+ installed
- [ ] npm 8+ installed
- [ ] Git repository up to date
- [ ] All code changes committed

### Code Review
- [ ] Backend code reviewed and tested
- [ ] Frontend code reviewed and tested
- [ ] No console errors or warnings
- [ ] No security vulnerabilities identified
- [ ] Database migrations reviewed

### Server Preparation
- [ ] Production server provisioned
- [ ] Java 17+ installed on server
- [ ] MySQL 8.0+ installed and running
- [ ] Nginx installed and configured
- [ ] SSL certificates obtained (Let's Encrypt)
- [ ] Firewall rules configured (80, 443 open)
- [ ] SSH key-based authentication enabled
- [ ] Sufficient disk space available (check with `df -h`)

### Database Preparation
- [ ] Database created: `shark_fitness`
- [ ] Database user created: `shark_user`
- [ ] User permissions granted
- [ ] Character set set to UTF8MB4
- [ ] Backup location prepared
- [ ] Backup script tested

### Configuration Preparation
- [ ] Production database credentials prepared
- [ ] Production API URL configured
- [ ] CORS origins configured
- [ ] File upload path prepared
- [ ] Logging path prepared
- [ ] Environment variables documented

## Build Checklist

### Backend Build
- [ ] Run `./build-backend.sh` successfully
- [ ] JAR file created at `SpringBoot/target/shark-fitness-system-0.0.1-SNAPSHOT.jar`
- [ ] JAR file size reasonable (typically 50-100MB)
- [ ] No build errors or warnings
- [ ] JAR file tested locally (optional)

### Frontend Build
- [ ] Run `./build-frontend.sh` successfully
- [ ] Dist directory created at `VUE/dist/`
- [ ] All assets present (JS, CSS, images)
- [ ] No build errors or warnings
- [ ] Bundle size acceptable

### Combined Build
- [ ] Run `./build-all.sh` successfully
- [ ] Both backend and frontend built
- [ ] All artifacts verified
- [ ] Build logs reviewed

## Deployment Checklist

### Pre-Deployment Backup
- [ ] Current backend JAR backed up
- [ ] Current frontend files backed up
- [ ] Database backed up
- [ ] Backup locations verified
- [ ] Backup restoration tested

### Backend Deployment
- [ ] JAR file copied to server
- [ ] File permissions set correctly
- [ ] Systemd service file in place
- [ ] Environment variables set
- [ ] Service started: `sudo systemctl start shark-fitness`
- [ ] Service status verified: `sudo systemctl status shark-fitness`
- [ ] Service logs checked: `sudo journalctl -u shark-fitness -n 50`
- [ ] Service enabled for auto-start: `sudo systemctl enable shark-fitness`

### Frontend Deployment
- [ ] Dist files copied to server
- [ ] File permissions set correctly
- [ ] Nginx configuration in place
- [ ] Nginx configuration tested: `sudo nginx -t`
- [ ] Nginx restarted: `sudo systemctl restart nginx`
- [ ] Nginx status verified: `sudo systemctl status nginx`
- [ ] Nginx enabled for auto-start: `sudo systemctl enable nginx`

### SSL/TLS Configuration
- [ ] SSL certificates installed
- [ ] Certificate paths correct in Nginx config
- [ ] Certificate auto-renewal configured
- [ ] HTTPS redirect working
- [ ] SSL test passed (https://www.ssllabs.com/ssltest/)

## Post-Deployment Verification

### Backend Verification
- [ ] Service running: `sudo systemctl status shark-fitness`
- [ ] Port 8080 listening: `sudo netstat -tuln | grep 8080`
- [ ] Database connection working
- [ ] Logs show no errors
- [ ] Health check endpoint responds

### Frontend Verification
- [ ] Website loads: `https://sharkfitness.com`
- [ ] All pages accessible
- [ ] No 404 errors in console
- [ ] No CORS errors
- [ ] Images and assets loading
- [ ] Responsive design working

### API Verification
- [ ] Login endpoint working: `POST /api/auth/login`
- [ ] User profile endpoint working: `GET /api/users/profile`
- [ ] Resource listing working: `GET /api/resources`
- [ ] File upload working: `POST /api/upload/image`
- [ ] Error handling working (test with invalid data)

### Security Verification
- [ ] HTTPS enforced (no HTTP access)
- [ ] Security headers present
- [ ] CORS properly configured
- [ ] No sensitive data in logs
- [ ] File upload directory protected
- [ ] Database credentials not exposed

### Performance Verification
- [ ] Page load time acceptable (< 3 seconds)
- [ ] API response time acceptable (< 500ms)
- [ ] No memory leaks (check with `top`)
- [ ] CPU usage normal
- [ ] Disk usage normal

## Monitoring Setup

### Logging
- [ ] Application logs configured
- [ ] Log rotation configured
- [ ] Log files accessible
- [ ] Log monitoring tool installed (optional)

### Backups
- [ ] Automated backup script running
- [ ] Backup schedule verified
- [ ] Backup retention policy set
- [ ] Backup restoration tested

### Monitoring Tools (Optional)
- [ ] Prometheus installed (optional)
- [ ] Grafana installed (optional)
- [ ] Alerts configured (optional)
- [ ] Uptime monitoring configured (optional)

## Rollback Checklist

### If Deployment Fails
- [ ] Identify failure cause from logs
- [ ] Stop current services
- [ ] Restore from backup
- [ ] Verify restoration
- [ ] Restart services
- [ ] Verify functionality
- [ ] Document issue for future reference

### Rollback Steps
- [ ] Backend rollback: `cp backup/shark-fitness-system-0.0.1-SNAPSHOT.jar.bak /opt/shark-fitness/shark-fitness-system-0.0.1-SNAPSHOT.jar`
- [ ] Frontend rollback: `rm -rf /var/www/shark-fitness/frontend && mv /var/www/shark-fitness/frontend.bak /var/www/shark-fitness/frontend`
- [ ] Restart services: `sudo systemctl restart shark-fitness && sudo systemctl restart nginx`
- [ ] Verify services running
- [ ] Check logs for errors

## Post-Deployment Tasks

### Documentation
- [ ] Deployment date recorded
- [ ] Version number recorded
- [ ] Deployment notes documented
- [ ] Known issues documented
- [ ] Configuration changes documented

### Communication
- [ ] Team notified of deployment
- [ ] Users notified if applicable
- [ ] Stakeholders updated
- [ ] Support team briefed

### Monitoring
- [ ] Monitor logs for errors
- [ ] Monitor system resources
- [ ] Monitor user feedback
- [ ] Monitor performance metrics

### Maintenance
- [ ] Schedule next backup
- [ ] Schedule next update
- [ ] Review security logs
- [ ] Plan for scaling if needed

## Troubleshooting Quick Reference

### Backend Issues
```bash
# Check service status
sudo systemctl status shark-fitness

# View recent logs
sudo journalctl -u shark-fitness -n 50

# Check database connection
mysql -u shark_user -p shark_fitness

# Check port
sudo netstat -tuln | grep 8080

# Restart service
sudo systemctl restart shark-fitness
```

### Frontend Issues
```bash
# Check Nginx status
sudo systemctl status nginx

# Test Nginx config
sudo nginx -t

# View Nginx error logs
sudo tail -f /var/log/nginx/error.log

# Check frontend files
ls -la /var/www/shark-fitness/frontend/

# Restart Nginx
sudo systemctl restart nginx
```

### Database Issues
```bash
# Check MySQL status
sudo systemctl status mysql

# Connect to database
mysql -u shark_user -p shark_fitness

# Check database size
SELECT table_schema, ROUND(SUM(data_length+index_length)/1024/1024,2) FROM information_schema.tables GROUP BY table_schema;

# Backup database
mysqldump -u shark_user -p shark_fitness > backup.sql
```

## Sign-Off

- [ ] Deployment completed successfully
- [ ] All verification checks passed
- [ ] No critical issues identified
- [ ] System ready for production use
- [ ] Deployment team sign-off

**Deployed By:** ___________________

**Date:** ___________________

**Version:** ___________________

**Notes:** 
```
_________________________________________________________________

_________________________________________________________________

_________________________________________________________________
```

---

**Last Updated:** 2024
**Version:** 1.0

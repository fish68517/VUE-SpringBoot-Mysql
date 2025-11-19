# Administrator Guide

Welcome to the Little Shark Fitness Management System Administrator Portal. This guide covers system management, user administration, content moderation, and platform oversight.

## Table of Contents

1. [Getting Started as an Administrator](#getting-started-as-an-administrator)
2. [Admin Dashboard](#admin-dashboard)
3. [User Management](#user-management)
4. [Resource Management](#resource-management)
5. [Content Moderation](#content-moderation)
6. [System Statistics](#system-statistics)
7. [Troubleshooting](#troubleshooting)
8. [FAQ](#faq)

## Getting Started as an Administrator

### Default Admin Account

The system includes a default administrator account for initial setup:
- **Username**: admin
- **Password**: admin123

**Important**: Change this password immediately after first login for security.

### Changing Admin Password

1. Log in with the default admin account
2. Click your avatar in the top-right corner
3. Select **Profile**
4. Click **Edit**
5. Update your password
6. Click **Save**

### Creating Additional Admin Accounts

1. Go to **User Management**
2. Click **Create User**
3. Fill in user details:
   - Username
   - Password
   - Role: Select **Admin**
4. Click **Create**
5. The new admin account is active immediately

## Admin Dashboard

### Dashboard Overview

The admin dashboard provides a comprehensive view of system operations:

**Key Metrics:**
- **Total Users**: Number of registered users
- **Active Coaches**: Number of coaches with students
- **Total Resources**: Number of fitness resources
- **Community Posts**: Number of published posts
- **Pending Moderation**: Content awaiting review

**Quick Actions:**
- View all users
- Manage resources
- Review moderation queue
- View system statistics

### Navigation Menu

The admin sidebar includes:
- **Dashboard**: Overview and statistics
- **User Management**: Manage user accounts
- **Resource Management**: Manage fitness resources
- **Moderation**: Review and approve content
- **Statistics**: View system analytics

## User Management

### Viewing All Users

1. Click **User Management** in the sidebar
2. See a list of all registered users
3. Each user entry shows:
   - Username
   - Role (User, Coach, Admin)
   - Registration date
   - Last login date
   - Status (Active/Inactive)

### Searching and Filtering Users

**By Username:**
1. Enter username in the search box
2. Results filter automatically

**By Role:**
1. Click the **Role** filter dropdown
2. Select: All, User, Coach, or Admin
3. Results update to show matching users

**By Status:**
1. Click the **Status** filter dropdown
2. Select: All, Active, or Inactive
3. Results update accordingly

### Viewing User Details

1. Click on a user in the list
2. See comprehensive user information:
   - Profile details (avatar, gender, intro)
   - Account information (username, role, dates)
   - Activity statistics
   - For coaches: student count and content count

### Updating User Information

1. Click on a user to view details
2. Click **Edit** button
3. Modify user information:
   - Avatar
   - Gender
   - Introduction
   - Role (change user type)
4. Click **Save**
5. Changes are applied immediately

### Changing User Role

1. Open a user's details
2. Click **Edit**
3. Change the **Role** dropdown:
   - User: Regular user access
   - Coach: Coaching capabilities
   - Admin: Full administrative access
4. Click **Save**
5. The user's permissions update immediately

### Deactivating a User

1. Open a user's details
2. Click **Deactivate** button
3. Confirm the action
4. The user cannot log in
5. Their data is preserved

### Reactivating a User

1. Open a deactivated user's details
2. Click **Reactivate** button
3. Confirm the action
4. The user can log in again

### Deleting a User

1. Open a user's details
2. Click **Delete** button
3. Confirm the deletion (this is permanent)
4. The user account and associated data are removed
5. Note: This action cannot be undone

### User Statistics

1. Click **Statistics** in the sidebar
2. View system-wide user statistics:
   - Total users by role
   - New users this month
   - Active users (logged in last 30 days)
   - User growth trends

## Resource Management

### Viewing All Resources

1. Click **Resource Management** in the sidebar
2. See all fitness resources in the system
3. Each resource shows:
   - Title and description
   - Content type (Video, Article, Document)
   - Creator (coach name)
   - View count
   - Publication date

### Filtering Resources

**By Content Type:**
1. Click the **Type** filter dropdown
2. Select: All, Video, Article, or Document
3. Results update accordingly

**By Creator:**
1. Enter creator name in the search box
2. Results filter to show their resources

### Viewing Resource Details

1. Click on a resource
2. See full information:
   - Title and description
   - Content type and file information
   - Creator details
   - View count and engagement
   - Publication and modification dates

### Editing Resources

1. Click on a resource
2. Click **Edit** button
3. Modify:
   - Title
   - Description
   - Content (for articles)
4. Click **Save**

### Deleting Resources

1. Click on a resource
2. Click **Delete** button
3. Confirm the deletion
4. The resource is removed from the system
5. Users can no longer access it

### Uploading New Resources

1. Click **Resource Management**
2. Click **Upload Resource**
3. Select content type: Video, Article, or Document
4. Fill in details:
   - Title
   - Description
   - Content or file
5. Click **Upload**
6. The resource is available to all users

## Content Moderation

### Understanding Moderation

The moderation system helps maintain community standards by reviewing user-generated content:
- Community posts
- Comments
- User profiles

### Accessing the Moderation Queue

1. Click **Moderation** in the sidebar
2. See all content pending review
3. Each item shows:
   - Content preview
   - Author information
   - Submission date
   - Current status

### Reviewing Content

1. Click on a content item in the queue
2. View the full content:
   - Text
   - Images (if any)
   - Author details
   - Submission date

### Approving Content

1. Open a content item for review
2. Click **Approve** button
3. Confirm the action
4. The content becomes visible to all users
5. The author receives a notification

### Rejecting Content

1. Open a content item for review
2. Click **Reject** button
3. Enter a reason for rejection (optional)
4. Confirm the action
5. The content is hidden from public view
6. The author receives a notification with the reason

### Viewing Moderation History

1. Click **Moderation** in the sidebar
2. Click **History** tab
3. See all moderation actions:
   - Content reviewed
   - Action taken (Approved/Rejected)
   - Moderator who took action
   - Date and time
   - Reason (if rejected)

### Flagging Inappropriate Content

**Automatic Flagging:**
- Content with prohibited keywords is automatically flagged
- Appears in the moderation queue for review

**Manual Flagging:**
1. Find problematic content
2. Click the **Flag** button
3. Select reason for flagging
4. The content moves to the moderation queue

### Setting Moderation Policies

1. Click **Settings** in the admin menu
2. Go to **Moderation Policies**
3. Configure:
   - Prohibited keywords list
   - Auto-flagging rules
   - Moderation queue priority
4. Click **Save**

## System Statistics

### Viewing System Statistics

1. Click **Statistics** in the sidebar
2. See comprehensive system analytics:

**User Statistics:**
- Total users by role
- New users this month
- Active users (last 30 days)
- User growth trend

**Content Statistics:**
- Total resources by type
- Total community posts
- Average engagement (likes, comments)
- Most viewed resources

**Activity Statistics:**
- Daily check-ins
- Diet records logged
- Training plans created
- Community interactions

**System Health:**
- Database size
- File storage usage
- API response times
- Error rates

### Exporting Statistics

1. Open the Statistics page
2. Click **Export** button
3. Choose format: PDF or CSV
4. The report downloads to your computer

### Generating Reports

1. Click **Reports** in the sidebar
2. Select report type:
   - User Activity Report
   - Content Performance Report
   - Moderation Activity Report
   - System Health Report
3. Choose date range
4. Click **Generate**
5. The report is created and can be downloaded

## System Configuration

### Database Settings

1. Click **Settings** in the admin menu
2. Go to **Database**
3. View current database configuration:
   - Connection status
   - Database size
   - Last backup date

### File Storage Settings

1. Click **Settings** in the admin menu
2. Go to **File Storage**
3. Configure:
   - Upload directory path
   - Maximum file sizes
   - Allowed file types
4. Click **Save**

### Email Configuration

1. Click **Settings** in the admin menu
2. Go to **Email**
3. Configure email settings for notifications:
   - SMTP server
   - Email address
   - Authentication credentials
4. Click **Test** to verify settings
5. Click **Save**

### Security Settings

1. Click **Settings** in the admin menu
2. Go to **Security**
3. Configure:
   - Password requirements
   - Session timeout
   - IP whitelist (optional)
   - Two-factor authentication (optional)
4. Click **Save**

## Troubleshooting

### Common Issues

**Users Cannot Log In**
1. Check if user account is active
2. Verify database connection
3. Check application logs for errors
4. Restart the backend service if needed

**File Upload Failures**
1. Check file storage directory permissions
2. Verify disk space availability
3. Check file size limits in configuration
4. Review application logs

**Slow Performance**
1. Check database query performance
2. Review system resource usage (CPU, memory)
3. Check for large file uploads in progress
4. Consider database optimization

**Content Not Appearing**
1. Verify content is approved (not in moderation queue)
2. Check user permissions
3. Clear browser cache
4. Verify database connection

### Accessing System Logs

1. Navigate to the backend application directory
2. Check `logs/` directory for application logs
3. Review logs for error messages and warnings
4. Common log files:
   - `application.log` - Main application log
   - `error.log` - Error messages
   - `access.log` - API access log

### Database Maintenance

**Backing Up the Database:**
```bash
mysqldump -u root -p sharkfitness > backup_$(date +%Y%m%d).sql
```

**Restoring from Backup:**
```bash
mysql -u root -p sharkfitness < backup_20231115.sql
```

**Optimizing Tables:**
1. Connect to MySQL
2. Run: `OPTIMIZE TABLE user, fitness_resource, dynamic, comment;`

## FAQ

### User Management

**Q: Can I create multiple admin accounts?**
A: Yes, you can create as many admin accounts as needed.

**Q: What happens when I delete a user?**
A: The user account and all associated data are permanently removed.

**Q: Can I recover a deleted user?**
A: No, deletion is permanent. Ensure you have backups before deleting users.

**Q: How do I change a user's role?**
A: Open the user's details, click Edit, change the Role dropdown, and Save.

### Resource Management

**Q: Can I upload resources directly as an admin?**
A: Yes, you can upload resources through Resource Management.

**Q: What file formats are supported?**
A: Videos: MP4, AVI; Documents: PDF and common formats; Articles: Text.

**Q: Can I edit resources created by coaches?**
A: Yes, admins can edit any resource in the system.

### Content Moderation

**Q: How long should I wait before reviewing flagged content?**
A: Review within 24 hours to maintain community standards.

**Q: Can I undo a moderation decision?**
A: Yes, you can change a rejected post to approved or vice versa.

**Q: What should I do about spam?**
A: Flag it, reject it, and consider deactivating the user if it's repeated.

### System Administration

**Q: How often should I back up the database?**
A: Daily backups are recommended for production systems.

**Q: What's the recommended database size?**
A: The system can handle databases up to several GB without issues.

**Q: Can I migrate to a different database?**
A: Yes, but it requires technical expertise. Contact support for assistance.

**Q: How do I monitor system performance?**
A: Check the Statistics page regularly and review system logs.

## Best Practices

### User Management
1. **Regular Audits**: Review user accounts monthly
2. **Deactivate Inactive**: Deactivate users inactive for 6+ months
3. **Role Assignment**: Assign roles carefully based on qualifications
4. **Documentation**: Keep records of user changes

### Content Moderation
1. **Consistent Standards**: Apply moderation rules consistently
2. **Timely Review**: Review flagged content within 24 hours
3. **Clear Communication**: Provide reasons when rejecting content
4. **Community Guidelines**: Maintain and publish clear guidelines

### System Maintenance
1. **Regular Backups**: Back up database daily
2. **Log Monitoring**: Review logs weekly for issues
3. **Performance Checks**: Monitor system performance monthly
4. **Security Updates**: Keep software updated

### Security
1. **Strong Passwords**: Use complex passwords for admin accounts
2. **Access Control**: Limit admin access to necessary personnel
3. **Audit Trails**: Review moderation and user management logs
4. **Data Protection**: Ensure sensitive data is protected

## Getting Help

If you encounter issues or have questions:
1. Check this guide for answers
2. Review the FAQ section
3. Check application logs for error details
4. Contact technical support for complex issues

Effective administration ensures a safe, engaging platform for all users!

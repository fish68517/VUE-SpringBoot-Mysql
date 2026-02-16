# Database Setup Guide

## Overview

The Campus Activity Crowdfunding Platform uses MySQL 8.0 as the database. The database schema is automatically created and managed by Hibernate through JPA entity definitions.

## Database Configuration

### Connection Details

The database connection is configured in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_activity?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
```

**Default Configuration:**
- Host: localhost
- Port: 3306
- Database: campus_activity
- Username: root
- Password: root

### Changing Database Configuration

To use a different database or credentials, update the `application.yml` file:

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-host:3306/your-database?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your-username
    password: your-password
```

## Database Setup Steps

### 1. Create Database

```sql
CREATE DATABASE campus_activity CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Automatic Table Creation

When the Spring Boot application starts, Hibernate will automatically:
- Create all tables based on JPA entity definitions
- Create indexes as specified in entity annotations
- Create foreign key relationships

**No manual SQL execution is required** - Hibernate handles everything automatically with `ddl-auto: update`.

### 3. Manual Table Creation (Optional)

If you prefer to manually create tables, execute the SQL script:

```bash
mysql -u root -p campus_activity < src/main/resources/db/schema.sql
```

### 4. Load Sample Data (Optional)

To load sample data for testing:

```bash
mysql -u root -p campus_activity < src/main/resources/db/data.sql
```

## Database Schema

### Tables Overview

| Table | Purpose | Key Fields |
|-------|---------|-----------|
| users | User accounts and profiles | id, username, email, role, status |
| activities | Campus activities | id, organizer_id, title, status, crowdfunding_target |
| registrations | User activity registrations | id, activity_id, user_id, status |
| crowdfunding_supports | Crowdfunding contributions | id, activity_id, user_id, amount, payment_status |
| feedback | Activity feedback and reviews | id, activity_id, user_id, rating, content |
| audit_logs | Audit trail for approvals/rejections | id, auditor_id, target_id, audit_type, audit_status |
| fund_proofs | Fund usage proof submissions | id, activity_id, organizer_id, file_url, status |

### Entity Relationships

```
User (1) ──────────────────────── (N) Activity
  │                                    │
  │                                    ├─── (N) Registration
  │                                    ├─── (N) CrowdfundingSupport
  │                                    ├─── (N) Feedback
  │                                    └─── (N) FundProof
  │
  ├─── (N) Registration
  ├─── (N) CrowdfundingSupport
  ├─── (N) Feedback
  └─── (N) AuditLog
```

## JPA Entity Definitions

All database tables are defined through JPA entity classes in `src/main/java/com/campus/entity/`:

- **BaseEntity.java** - Base class with common fields (id, createdAt, updatedAt)
- **User.java** - User accounts with roles and status
- **Activity.java** - Campus activities with crowdfunding configuration
- **Registration.java** - Activity registrations
- **CrowdfundingSupport.java** - Crowdfunding contributions
- **Feedback.java** - Activity feedback and reviews
- **AuditLog.java** - Audit trail
- **FundProof.java** - Fund usage proofs

## Indexes

The following indexes are automatically created for performance optimization:

### Users Table
- `idx_username` - For fast username lookups
- `idx_email` - For fast email lookups

### Activities Table
- `idx_organizer_id` - For filtering by organizer
- `idx_status` - For filtering by activity status
- `idx_start_time` - For sorting by start time

### Registrations Table
- `idx_activity_id` - For finding registrations by activity
- `idx_user_id` - For finding registrations by user
- `unique_registration` - Ensures one registration per user per activity

### CrowdfundingSupport Table
- `idx_activity_id` - For finding supports by activity
- `idx_user_id` - For finding supports by user

### Feedback Table
- `idx_activity_id` - For finding feedback by activity
- `idx_user_id` - For finding feedback by user

### AuditLog Table
- `idx_auditor_id` - For finding audits by auditor
- `idx_target_id` - For finding audits by target
- `idx_audit_type` - For filtering by audit type

### FundProof Table
- `idx_activity_id` - For finding proofs by activity
- `idx_organizer_id` - For finding proofs by organizer
- `idx_status` - For filtering by proof status

## Enumerations

### User Roles
- `USER` - Regular user
- `ORGANIZER` - Activity organizer
- `ADMIN` - Administrator

### Account Status
- `ACTIVE` - Active account
- `DISABLED` - Disabled account

### Activity Status
- `DRAFT` - Draft activity
- `PENDING_AUDIT` - Awaiting admin approval
- `APPROVED` - Approved by admin
- `REJECTED` - Rejected by admin
- `ONGOING` - Currently happening
- `ENDED` - Finished
- `ARCHIVED` - Archived

### Registration Status
- `REGISTERED` - User registered
- `CANCELLED` - Registration cancelled

### Payment Status
- `PENDING` - Payment pending
- `COMPLETED` - Payment completed
- `FAILED` - Payment failed

### Proof Status
- `PENDING_AUDIT` - Awaiting audit
- `APPROVED` - Approved
- `REJECTED` - Rejected

### Audit Type
- `ACTIVITY` - Activity audit
- `FUND_PROOF` - Fund proof audit

### Audit Status
- `APPROVED` - Approved
- `REJECTED` - Rejected

## Hibernate Configuration

The application uses the following Hibernate settings:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Automatically update schema
    show-sql: false     # Don't log SQL statements
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        use_sql_comments: true
    open-in-view: false
```

### DDL Auto Options

- `create` - Drop and recreate tables on startup
- `create-drop` - Drop tables on shutdown
- `update` - Update schema if needed (recommended for development)
- `validate` - Validate schema matches entities
- `none` - Do nothing

## Troubleshooting

### Connection Issues

If you get connection errors:

1. Ensure MySQL is running
2. Verify database exists: `SHOW DATABASES;`
3. Check credentials in `application.yml`
4. Verify MySQL port (default 3306)

### Table Creation Issues

If tables aren't created:

1. Check application logs for Hibernate errors
2. Verify entity annotations are correct
3. Ensure `ddl-auto: update` is set
4. Check database user has CREATE TABLE permissions

### Sample Data Issues

If sample data fails to load:

1. Ensure tables exist first
2. Check foreign key constraints
3. Verify user IDs match in data.sql
4. Check for duplicate unique values

## Backup and Restore

### Backup Database

```bash
mysqldump -u root -p campus_activity > backup.sql
```

### Restore Database

```bash
mysql -u root -p campus_activity < backup.sql
```

## Performance Optimization

### Recommended Indexes

All necessary indexes are already defined in entity annotations. Additional indexes can be added if needed:

```java
@Table(name = "activities", indexes = {
    @Index(name = "idx_organizer_id", columnList = "organizer_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_start_time", columnList = "start_time")
})
```

### Query Optimization

- Use pagination for large result sets
- Use appropriate fetch strategies (LAZY/EAGER)
- Create custom repository methods for complex queries
- Use database indexes effectively

## Security Considerations

1. **Change default credentials** - Update username and password in production
2. **Use SSL** - Enable SSL for database connections in production
3. **Limit permissions** - Create database user with minimal required permissions
4. **Backup regularly** - Implement automated backup strategy
5. **Monitor access** - Enable MySQL audit logging

## Development vs Production

### Development
- Use `ddl-auto: update` for automatic schema updates
- Use local MySQL instance
- Enable SQL logging for debugging

### Production
- Use `ddl-auto: validate` to prevent accidental schema changes
- Use managed database service (RDS, etc.)
- Disable SQL logging
- Use strong credentials
- Enable SSL connections
- Implement backup strategy

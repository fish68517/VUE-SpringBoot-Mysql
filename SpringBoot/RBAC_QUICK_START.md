# RBAC Quick Start Guide

## 5-Minute Setup

### Step 1: Understand the Three Roles

```
┌─────────────────────────────────────────────────────────┐
│                    USER ROLES                           │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  USER              ORGANIZER           ADMIN           │
│  ────              ─────────           ─────           │
│  • Browse          • Create activities • Audit         │
│  • Register        • Manage activities • Manage users  │
│  • Feedback        • View stats        • View stats    │
│  • Support         • Reply feedback    • Approve       │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### Step 2: Add @RequireRole to Your Controller

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Admin only
    @GetMapping
    @RequireRole(UserRole.ADMIN)
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users", users));
    }

    // Organizer or Admin
    @PostMapping("/{id}/disable")
    @RequireRole({UserRole.ADMIN, UserRole.ORGANIZER})
    public ResponseEntity<ApiResponse<UserDTO>> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User disabled", user));
    }

    // Any authenticated user
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profile", user));
    }
}
```

### Step 3: Test with cURL

```bash
# 1. Login to get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' \
  | jq -r '.data.token')

# 2. Test admin endpoint
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN"

# 3. Test with user token (should fail)
USER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}' \
  | jq -r '.data.token')

curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $USER_TOKEN"
# Returns: 403 Forbidden
```

## Common Patterns

### Pattern 1: Admin Only
```java
@RequireRole(UserRole.ADMIN)
public ResponseEntity<...> adminOperation() { }
```

### Pattern 2: Organizer Only
```java
@RequireRole(UserRole.ORGANIZER)
public ResponseEntity<...> organizerOperation() { }
```

### Pattern 3: User Only
```java
@RequireRole(UserRole.USER)
public ResponseEntity<...> userOperation() { }
```

### Pattern 4: Multiple Roles
```java
@RequireRole({UserRole.ADMIN, UserRole.ORGANIZER})
public ResponseEntity<...> adminOrOrganizerOperation() { }
```

### Pattern 5: Any Authenticated User
```java
// No @RequireRole annotation needed
public ResponseEntity<...> publicOperation() { }
```

## Error Responses

### Unauthorized (No Token)
```
Status: 403 Forbidden
{
  "code": 403,
  "message": "User is not authenticated",
  "data": null
}
```

### Insufficient Permissions
```
Status: 403 Forbidden
{
  "code": 403,
  "message": "User does not have required role to access this resource",
  "data": null
}
```

## Test Endpoints

Ready-to-use test endpoints:

```
GET /api/test/admin-only              → Requires ADMIN
GET /api/test/organizer-only          → Requires ORGANIZER
GET /api/test/user-only               → Requires USER
GET /api/test/admin-or-organizer      → Requires ADMIN or ORGANIZER
GET /api/test/authenticated-users     → Requires any authenticated user
```

## Troubleshooting

### Issue: 403 Forbidden on all endpoints
**Solution**: Check if token is included in Authorization header
```bash
# Wrong
curl http://localhost:8080/api/users

# Correct
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/users
```

### Issue: 403 Forbidden with correct token
**Solution**: Check if user has required role
```bash
# Check token claims
curl http://localhost:8080/api/auth/verify-token?token=<token>

# Verify role in response
```

### Issue: @RequireRole annotation not working
**Solution**: Ensure Spring AOP is enabled
```yaml
# application.yml
spring:
  aop:
    auto: true
```

## Integration Checklist

- [ ] Add @RequireRole to controller methods
- [ ] Test with different user roles
- [ ] Verify error responses
- [ ] Check audit logs
- [ ] Update API documentation
- [ ] Train team on role assignments

## Next Steps

1. **Review Documentation**
   - Read `RBAC_IMPLEMENTATION.md` for details
   - Check `RBAC_USAGE_EXAMPLES.md` for examples

2. **Integrate into Controllers**
   - Add @RequireRole to existing methods
   - Test with different roles

3. **Monitor and Maintain**
   - Check logs for access attempts
   - Update role assignments as needed

## Quick Reference

| Role | Can Do | Cannot Do |
|------|--------|-----------|
| USER | Browse, Register, Feedback, Support | Create activities, Audit, Manage users |
| ORGANIZER | Create activities, View stats, Reply feedback | Audit, Manage users, View platform stats |
| ADMIN | Everything | Nothing (full access) |

## Support

- **Documentation**: See `RBAC_IMPLEMENTATION.md`
- **Examples**: See `RBAC_USAGE_EXAMPLES.md`
- **Verification**: See `RBAC_VERIFICATION.md`
- **Troubleshooting**: Check application logs

---

**Ready to use!** Start adding @RequireRole to your controllers.

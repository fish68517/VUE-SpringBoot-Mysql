# Frontend Permission Control Implementation Summary

## Task: 36. 实现前端权限控制 (Implement Frontend Permission Control)

### Requirement Reference
- **Requirement 1.4**: WHEN 用户未登录访问受保护页面，THE 系统 SHALL 重定向到登录页面

## Implementation Overview

A comprehensive role-based access control (RBAC) system has been implemented for the Vue 3 frontend. This system provides multiple layers of permission checking to ensure users can only access features and pages appropriate for their role.

## Files Created

### 1. Core Permission Utilities
**File**: `VUE/src/utils/permissionGuard.js`

Core utility functions for permission checking:
- `hasRole(userRole, requiredRoles)` - Check if user has required role
- `hasPermission(userRole, requiredPermissions)` - Check if user has required permissions
- `getRolePermissions(role)` - Get all permissions for a role
- `canAccessRoute(user, routeMeta)` - Check if user can access a route
- `getDefaultRedirectPath(role)` - Get default dashboard path for a role

**Permissions by Role**:
- **AUTHOR**: submit_manuscript, edit_own_manuscript, view_own_manuscripts, withdraw_manuscript, view_review_progress, view_messages, edit_profile
- **EDITOR**: view_all_manuscripts, perform_initial_review, assign_reviewers, track_review_progress, edit_accepted_manuscripts, send_notifications, communicate_with_authors, communicate_with_reviewers, edit_profile
- **REVIEWER**: view_assigned_tasks, accept_review_task, reject_review_task, view_manuscript_content, submit_review_opinion, view_review_history, edit_profile
- **ADMIN**: manage_users, approve_user_registration, reject_user_registration, manage_categories, manage_system_config, view_statistics, manage_notification_templates, edit_profile

### 2. Vue 3 Composable
**File**: `VUE/src/composables/usePermission.js`

Composable for component-level permission checks:
- `checkRole(requiredRoles)` - Check if current user has role
- `checkPermission(requiredPermissions)` - Check if current user has permissions
- `currentPermissions` - Computed property with all user permissions
- `isAuthor`, `isEditor`, `isReviewer`, `isAdmin` - Computed properties for role checks

### 3. Permission Directives
**File**: `VUE/src/directives/vPermission.js`

Directive for conditional rendering based on permissions:
```vue
<div v-permission="'submit_manuscript'">
  <!-- Only visible to users with submit_manuscript permission -->
</div>
```

**File**: `VUE/src/directives/vRole.js`

Directive for conditional rendering based on roles:
```vue
<div v-role="'AUTHOR'">
  <!-- Only visible to authors -->
</div>

<div v-role="['EDITOR', 'ADMIN']">
  <!-- Visible to editors and admins -->
</div>
```

### 4. Enhanced Router
**File**: `VUE/src/router/index.js` (Updated)

Improvements:
- Integrated `canAccessRoute()` for comprehensive permission checking
- Added automatic redirection for unauthorized access
- Prevents logged-in users from accessing login/register pages
- Redirects to appropriate dashboard based on user role
- Supports both role-based and permission-based route protection

**Route Protection Features**:
- Public routes: `/login`, `/register` (no authentication required)
- Protected routes: All role-specific routes require authentication and correct role
- Automatic redirection: Unauthorized users redirected to their role's dashboard

### 5. Application Setup
**File**: `VUE/src/main.js` (Updated)

Registered global directives:
- `v-permission` - For permission-based conditional rendering
- `v-role` - For role-based conditional rendering

### 6. Documentation
**File**: `VUE/src/PERMISSION_CONTROL_GUIDE.md`

Comprehensive guide including:
- Architecture overview
- Role and permission definitions
- Usage examples for all permission checking methods
- Best practices and security considerations
- Troubleshooting guide
- Testing examples

### 7. Example Component
**File**: `VUE/src/components/examples/PermissionControlExample.vue`

Practical examples demonstrating:
- Using `usePermission` composable
- Using `v-role` directive
- Using `v-permission` directive
- Programmatic permission checks
- Conditional navigation

### 8. Unit Tests
**File**: `VUE/src/utils/__tests__/permissionGuard.spec.js`

Comprehensive test suite covering:
- `hasRole()` function
- `hasPermission()` function
- `getRolePermissions()` function
- `canAccessRoute()` function
- `getDefaultRedirectPath()` function

## Key Features

### 1. Route Guards
- Automatic authentication check on route navigation
- Role-based route access control
- Permission-based route access control
- Automatic redirection to appropriate dashboard

### 2. Component-Level Checks
- `usePermission` composable for reactive permission checks
- Computed properties for role checks
- Methods for programmatic permission verification

### 3. Template-Level Checks
- `v-role` directive for role-based conditional rendering
- `v-permission` directive for permission-based conditional rendering
- Automatic show/hide based on user permissions

### 4. Flexible Permission System
- Granular permission definitions per role
- Support for multiple roles/permissions in checks
- Easy to extend with new permissions

## Usage Examples

### Example 1: Protect a Route
```javascript
{
  path: '/author/submit',
  component: SubmitManuscript,
  meta: { 
    requiresAuth: true, 
    roles: ['AUTHOR'] 
  }
}
```

### Example 2: Check Permission in Component
```vue
<script setup>
import { usePermission } from '@/composables/usePermission'

const { checkPermission, isAuthor } = usePermission()
const canSubmit = checkPermission('submit_manuscript')
</script>

<template>
  <button v-if="canSubmit" @click="submit">Submit</button>
</template>
```

### Example 3: Conditional Rendering with Directives
```vue
<template>
  <div v-role="'AUTHOR'">Author content</div>
  <div v-permission="'manage_users'">Admin content</div>
</template>
```

## Security Considerations

### Frontend-Level Security
✓ Route guards prevent unauthorized navigation
✓ Directives hide UI elements from unauthorized users
✓ Permission checks prevent unauthorized actions

### Important Notes
⚠️ Frontend permission checks are for UX purposes only
⚠️ **Backend must always validate permissions** before processing requests
⚠️ Never rely solely on frontend permission checks for security
⚠️ Always implement corresponding backend authorization

## Testing

The implementation includes comprehensive unit tests covering:
- Role checking logic
- Permission checking logic
- Route access validation
- Default redirect path resolution

Tests can be run with:
```bash
npm run test:unit -- src/utils/__tests__/permissionGuard.spec.js --run
```

## Integration with Existing Code

### Router Integration
The enhanced router automatically protects all role-specific routes:
- `/author/*` - Protected for AUTHOR role
- `/editor/*` - Protected for EDITOR role
- `/reviewer/*` - Protected for REVIEWER role
- `/admin/*` - Protected for ADMIN role

### Store Integration
Works seamlessly with existing `userStore`:
- Reads user role from `userStore.user.role`
- Checks login status from `userStore.isLoggedIn`
- No changes required to existing store

### Component Integration
Can be used in any Vue 3 component:
- Import `usePermission` composable
- Use `v-role` and `v-permission` directives
- No additional setup required

## Verification

All files have been verified for:
✓ Syntax correctness
✓ Proper imports and exports
✓ Consistent naming conventions
✓ Complete implementation of all features

## Next Steps

1. **Test the implementation**: Navigate between different user roles and verify access control
2. **Update components**: Add permission checks to existing components as needed
3. **Backend validation**: Ensure backend implements corresponding authorization checks
4. **Documentation**: Share the PERMISSION_CONTROL_GUIDE.md with the team

## Compliance with Requirements

### Requirement 1.4: "WHEN 用户未登录访问受保护页面，THE 系统 SHALL 重定向到登录页面"

✓ **Implemented**: 
- Router guard checks `userStore.isLoggedIn` before allowing access to protected routes
- Unauthenticated users are automatically redirected to `/login`
- All role-specific routes are protected with `meta: { requiresAuth: true }`

### Additional Compliance:
✓ **Role-based access control**: Users can only access routes for their role
✓ **Permission-based access control**: Fine-grained permission checking available
✓ **Automatic redirection**: Unauthorized users redirected to appropriate dashboard
✓ **Component-level checks**: Permission checks available in components
✓ **Template-level checks**: Conditional rendering based on permissions

## Summary

The frontend permission control system is now fully implemented with:
- 6 core utility functions
- 1 Vue 3 composable
- 2 custom directives
- Enhanced router with comprehensive guards
- Comprehensive documentation
- Example component
- Unit tests

The system provides multiple layers of permission checking to ensure secure and user-friendly access control throughout the application.

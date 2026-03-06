# Frontend Permission Control Guide

## Overview

The frontend permission control system provides comprehensive role-based access control (RBAC) for the online submission system. It includes route guards, permission directives, and composables for checking user permissions in components.

## Architecture

### Components

1. **Permission Guard Utility** (`utils/permissionGuard.js`)
   - Core permission checking logic
   - Role-to-permission mapping
   - Route access validation

2. **Permission Composable** (`composables/usePermission.js`)
   - Vue 3 composable for component-level permission checks
   - Reactive permission properties
   - Role checking methods

3. **Permission Directives**
   - `v-permission`: Conditionally render based on permissions
   - `v-role`: Conditionally render based on roles

4. **Router Guards** (`router/index.js`)
   - Global route protection
   - Automatic redirection for unauthorized access
   - Login/register route protection

## User Roles

The system supports four user roles:

- **AUTHOR**: Content creators who submit manuscripts
- **EDITOR**: Editorial staff who review and manage submissions
- **REVIEWER**: Expert reviewers who evaluate manuscripts
- **ADMIN**: System administrators who manage users and configuration

## Role Permissions

### AUTHOR Permissions
- `view_own_manuscripts`: View their submitted manuscripts
- `submit_manuscript`: Submit new manuscripts
- `edit_own_manuscript`: Edit unreviewed manuscripts
- `withdraw_manuscript`: Withdraw submitted manuscripts
- `view_review_progress`: Track manuscript review status
- `view_messages`: Access message center
- `edit_profile`: Update personal information

### EDITOR Permissions
- `view_all_manuscripts`: View all manuscripts in the system
- `perform_initial_review`: Conduct initial manuscript review
- `assign_reviewers`: Assign reviewers to manuscripts
- `track_review_progress`: Monitor review progress
- `edit_accepted_manuscripts`: Edit accepted manuscripts
- `send_notifications`: Send notifications to users
- `communicate_with_authors`: Message authors
- `communicate_with_reviewers`: Message reviewers
- `edit_profile`: Update personal information

### REVIEWER Permissions
- `view_assigned_tasks`: View assigned review tasks
- `accept_review_task`: Accept review assignments
- `reject_review_task`: Decline review assignments
- `view_manuscript_content`: Read manuscript content
- `submit_review_opinion`: Submit review feedback
- `view_review_history`: Access past reviews
- `edit_profile`: Update personal information

### ADMIN Permissions
- `manage_users`: Manage user accounts
- `approve_user_registration`: Approve new user registrations
- `reject_user_registration`: Reject user registrations
- `manage_categories`: Manage submission categories
- `manage_system_config`: Configure system settings
- `view_statistics`: Access system statistics
- `manage_notification_templates`: Manage notification templates
- `edit_profile`: Update personal information

## Usage

### 1. Route Protection

Routes are automatically protected using the `meta.requiresAuth` and `meta.roles` properties:

```javascript
{
  path: '/author',
  name: 'AuthorLayout',
  component: () => import('../views/layouts/AuthorLayout.vue'),
  meta: { 
    requiresAuth: true, 
    roles: ['AUTHOR'] 
  },
  children: [
    // child routes
  ]
}
```

The router guard automatically:
- Redirects unauthenticated users to login
- Redirects users without required roles to their default dashboard
- Allows access to users with correct roles

### 2. Component-Level Permission Checks

Use the `usePermission` composable in components:

```vue
<script setup>
import { usePermission } from '@/composables/usePermission'

const { checkRole, checkPermission, isAuthor, isEditor } = usePermission()

// Check specific role
const canReview = checkRole('REVIEWER')

// Check specific permission
const canSubmit = checkPermission('submit_manuscript')

// Use computed properties
const isAuthorUser = isAuthor.value
</script>

<template>
  <div v-if="isAuthorUser">
    <!-- Author-only content -->
  </div>
</template>
```

### 3. Conditional Rendering with Directives

#### v-role Directive

Show/hide elements based on user role:

```vue
<template>
  <!-- Show only to authors -->
  <button v-role="'AUTHOR'">Submit Manuscript</button>

  <!-- Show to multiple roles -->
  <div v-role="['EDITOR', 'ADMIN']">
    Admin Panel
  </div>
</template>
```

#### v-permission Directive

Show/hide elements based on permissions:

```vue
<template>
  <!-- Show only if user has permission -->
  <button v-permission="'submit_manuscript'">Submit</button>

  <!-- Show if user has any of the permissions -->
  <div v-permission="['manage_users', 'manage_categories']">
    Management Tools
  </div>
</template>
```

### 4. Programmatic Permission Checks

In any component or service:

```javascript
import { hasRole, hasPermission, getRolePermissions } from '@/utils/permissionGuard'

// Check role
if (hasRole(userRole, 'EDITOR')) {
  // User is an editor
}

// Check permission
if (hasPermission(userRole, 'submit_manuscript')) {
  // User can submit manuscripts
}

// Get all permissions for a role
const permissions = getRolePermissions('AUTHOR')
```

## Route Access Control

### Public Routes
- `/login` - Login page
- `/register` - Registration page

### Protected Routes

#### Author Routes (`/author/*`)
- Requires: `AUTHOR` role
- Includes: Dashboard, Profile, Manuscripts, Messages

#### Editor Routes (`/editor/*`)
- Requires: `EDITOR` role
- Includes: Dashboard, Profile, Review, Manuscript Management

#### Reviewer Routes (`/reviewer/*`)
- Requires: `REVIEWER` role
- Includes: Dashboard, Profile, Tasks, History

#### Admin Routes (`/admin/*`)
- Requires: `ADMIN` role
- Includes: Dashboard, Profile, Users, Categories, Configuration, Statistics

## Security Considerations

### Frontend-Level Security
- Route guards prevent unauthorized navigation
- Directives hide UI elements from unauthorized users
- Permission checks prevent unauthorized actions

### Important Notes
- Frontend permission checks are for UX purposes only
- **Backend must always validate permissions** before processing requests
- Never rely solely on frontend permission checks for security
- Always implement corresponding backend authorization

## Best Practices

1. **Always validate on the backend**: Frontend checks are for UX, not security
2. **Use composables in components**: Prefer `usePermission()` over direct utility calls
3. **Use directives for UI**: Use `v-role` and `v-permission` for conditional rendering
4. **Consistent role naming**: Always use uppercase role names (AUTHOR, EDITOR, etc.)
5. **Granular permissions**: Use specific permissions rather than just role checks when possible
6. **Error handling**: Provide user-friendly messages when access is denied

## Examples

### Example 1: Author Dashboard with Permission Checks

```vue
<script setup>
import { usePermission } from '@/composables/usePermission'

const { isAuthor, checkPermission } = usePermission()
const canSubmit = checkPermission('submit_manuscript')
</script>

<template>
  <div v-if="isAuthor" class="author-dashboard">
    <h1>Author Dashboard</h1>
    
    <button v-if="canSubmit" @click="submitManuscript">
      Submit New Manuscript
    </button>
    
    <div v-permission="'view_own_manuscripts'">
      <!-- Manuscript list -->
    </div>
  </div>
</template>
```

### Example 2: Admin Panel with Role-Based Access

```vue
<script setup>
import { usePermission } from '@/composables/usePermission'

const { isAdmin } = usePermission()
</script>

<template>
  <div v-if="isAdmin" class="admin-panel">
    <div v-role="'ADMIN'" class="admin-controls">
      <section v-permission="'manage_users'">
        <!-- User management -->
      </section>
      
      <section v-permission="'manage_categories'">
        <!-- Category management -->
      </section>
      
      <section v-permission="'view_statistics'">
        <!-- Statistics -->
      </section>
    </div>
  </div>
</template>
```

### Example 3: Conditional Navigation

```vue
<script setup>
import { usePermission } from '@/composables/usePermission'

const { isEditor, isReviewer, isAdmin } = usePermission()
</script>

<template>
  <nav>
    <router-link v-if="isEditor" to="/editor/dashboard">
      Editor Dashboard
    </router-link>
    
    <router-link v-if="isReviewer" to="/reviewer/dashboard">
      Reviewer Dashboard
    </router-link>
    
    <router-link v-if="isAdmin" to="/admin/dashboard">
      Admin Dashboard
    </router-link>
  </nav>
</template>
```

## Testing Permission Control

### Testing Route Guards

```javascript
import { canAccessRoute } from '@/utils/permissionGuard'

describe('Permission Guard', () => {
  it('should allow author to access author routes', () => {
    const user = { role: 'AUTHOR' }
    const routeMeta = { requiresAuth: true, roles: ['AUTHOR'] }
    expect(canAccessRoute(user, routeMeta)).toBe(true)
  })

  it('should deny author access to editor routes', () => {
    const user = { role: 'AUTHOR' }
    const routeMeta = { requiresAuth: true, roles: ['EDITOR'] }
    expect(canAccessRoute(user, routeMeta)).toBe(false)
  })
})
```

### Testing Permissions

```javascript
import { hasPermission } from '@/utils/permissionGuard'

describe('Permission Check', () => {
  it('should grant author submit permission', () => {
    expect(hasPermission('AUTHOR', 'submit_manuscript')).toBe(true)
  })

  it('should deny author manage_users permission', () => {
    expect(hasPermission('AUTHOR', 'manage_users')).toBe(false)
  })
})
```

## Troubleshooting

### Issue: User can access unauthorized routes
- Check that route has `meta: { requiresAuth: true, roles: [...] }`
- Verify user role is correctly set in userStore
- Check browser console for router guard errors

### Issue: Directives not hiding elements
- Ensure directives are registered in main.js
- Check that user is logged in (userStore.user is not null)
- Verify permission/role names match exactly

### Issue: Permission checks always fail
- Verify role name is uppercase (AUTHOR, not author)
- Check that user object has role property
- Ensure permission names match those in permissionGuard.js

## Future Enhancements

- Permission caching for performance
- Dynamic permission loading from backend
- Permission audit logging
- Fine-grained permission management UI
- Permission inheritance and hierarchies

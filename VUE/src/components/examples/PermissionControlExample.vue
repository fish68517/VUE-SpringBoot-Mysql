<template>
  <div class="permission-control-example">
    <h2>Permission Control Examples</h2>

    <!-- Example 1: Using usePermission composable -->
    <section class="example-section">
      <h3>1. Using usePermission Composable</h3>
      
      <div class="example-content">
        <p>Current User Role: <strong>{{ userStore.user?.role }}</strong></p>
        
        <div v-if="isAuthor" class="role-info">
          <p>✓ You are an Author</p>
          <button @click="performAction('submit_manuscript')">
            Submit Manuscript
          </button>
        </div>

        <div v-if="isEditor" class="role-info">
          <p>✓ You are an Editor</p>
          <button @click="performAction('perform_initial_review')">
            Perform Initial Review
          </button>
        </div>

        <div v-if="isReviewer" class="role-info">
          <p>✓ You are a Reviewer</p>
          <button @click="performAction('accept_review_task')">
            Accept Review Task
          </button>
        </div>

        <div v-if="isAdmin" class="role-info">
          <p>✓ You are an Admin</p>
          <button @click="performAction('manage_users')">
            Manage Users
          </button>
        </div>
      </div>
    </section>

    <!-- Example 2: Using v-role directive -->
    <section class="example-section">
      <h3>2. Using v-role Directive</h3>
      
      <div class="example-content">
        <div v-role="'AUTHOR'" class="role-section">
          <p>This section is only visible to Authors</p>
          <button>Author Action</button>
        </div>

        <div v-role="'EDITOR'" class="role-section">
          <p>This section is only visible to Editors</p>
          <button>Editor Action</button>
        </div>

        <div v-role="['ADMIN', 'EDITOR']" class="role-section">
          <p>This section is visible to Admins and Editors</p>
          <button>Admin/Editor Action</button>
        </div>
      </div>
    </section>

    <!-- Example 3: Using v-permission directive -->
    <section class="example-section">
      <h3>3. Using v-permission Directive</h3>
      
      <div class="example-content">
        <div v-permission="'submit_manuscript'" class="permission-section">
          <p>You have permission to submit manuscripts</p>
          <button>Submit Manuscript</button>
        </div>

        <div v-permission="'manage_users'" class="permission-section">
          <p>You have permission to manage users</p>
          <button>Manage Users</button>
        </div>

        <div v-permission="['perform_initial_review', 'assign_reviewers']" class="permission-section">
          <p>You have permission to perform initial review or assign reviewers</p>
          <button>Review Management</button>
        </div>
      </div>
    </section>

    <!-- Example 4: Checking permissions programmatically -->
    <section class="example-section">
      <h3>4. Programmatic Permission Checks</h3>
      
      <div class="example-content">
        <div class="permission-check">
          <p>Can submit manuscript: <strong>{{ canSubmit }}</strong></p>
          <p>Can manage users: <strong>{{ canManageUsers }}</strong></p>
          <p>Can perform initial review: <strong>{{ canReview }}</strong></p>
        </div>

        <div class="permissions-list">
          <p>Current user permissions:</p>
          <ul>
            <li v-for="permission in currentPermissions" :key="permission">
              {{ permission }}
            </li>
          </ul>
        </div>
      </div>
    </section>

    <!-- Example 5: Conditional navigation -->
    <section class="example-section">
      <h3>5. Conditional Navigation</h3>
      
      <div class="example-content">
        <nav class="nav-example">
          <router-link v-if="isAuthor" to="/author/dashboard">
            Author Dashboard
          </router-link>
          
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
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { usePermission } from '@/composables/usePermission'

const userStore = useUserStore()
const {
  checkRole,
  checkPermission,
  currentPermissions,
  isAuthor,
  isEditor,
  isReviewer,
  isAdmin
} = usePermission()

// Computed properties for permission checks
const canSubmit = computed(() => checkPermission('submit_manuscript'))
const canManageUsers = computed(() => checkPermission('manage_users'))
const canReview = computed(() => checkPermission('perform_initial_review'))

// Method to perform actions
const performAction = (permission) => {
  if (checkPermission(permission)) {
    console.log(`Action performed: ${permission}`)
    // Perform the action
  } else {
    console.log(`Permission denied: ${permission}`)
  }
}
</script>

<style scoped>
.permission-control-example {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.example-section {
  margin-bottom: 30px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
  background-color: #f9f9f9;
}

.example-section h3 {
  margin-top: 0;
  color: #333;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.example-content {
  margin-top: 15px;
}

.role-info,
.role-section,
.permission-section {
  padding: 10px;
  margin: 10px 0;
  background-color: #fff;
  border-left: 4px solid #409eff;
  border-radius: 2px;
}

.role-info p,
.role-section p,
.permission-section p {
  margin: 0 0 10px 0;
}

.role-info button,
.role-section button,
.permission-section button {
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.role-info button:hover,
.role-section button:hover,
.permission-section button:hover {
  background-color: #66b1ff;
}

.permission-check {
  background-color: #fff;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.permission-check p {
  margin: 5px 0;
}

.permissions-list {
  background-color: #fff;
  padding: 10px;
  border-radius: 4px;
}

.permissions-list ul {
  margin: 10px 0;
  padding-left: 20px;
}

.permissions-list li {
  padding: 5px 0;
  color: #666;
}

.nav-example {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.nav-example a {
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-example a:hover {
  background-color: #66b1ff;
}
</style>

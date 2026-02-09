<template>
  <div class="main-layout">
    <!-- Navigation Bar -->
    <nav class="navbar">
      <div class="navbar-container">
        <!-- Logo/Brand -->
        <div class="navbar-brand">
          <router-link to="/dashboard" class="brand-link">
            <span class="brand-icon">🎓</span>
            <span class="brand-text">Postgraduate School Selection</span>
          </router-link>
        </div>

        <!-- Navigation Links -->
        <div class="navbar-menu">
          <ul class="nav-links">
            <!-- User Navigation -->
            <li v-if="isUser" class="nav-item">
              <router-link to="/app/dashboard" class="nav-link" :class="{ active: isActive('/dashboard') }">
                首页
              </router-link>
            </li>
            <li v-if="isUser" class="nav-item">
              <router-link to="/app/schools" class="nav-link" :class="{ active: isActive('/schools') }">
                院校列表
              </router-link>
            </li>
            <li v-if="isUser" class="nav-item">
              <router-link to="/app/favorites" class="nav-link" :class="{ active: isActive('/favorites') }">
                我的收藏
              </router-link>
            </li>
            <li v-if="isUser" class="nav-item">
              <router-link to="/app/announcements" class="nav-link" :class="{ active: isActive('/announcements') }">
                通知公告
              </router-link>
            </li>
            <li v-if="isUser" class="nav-item">
              <router-link to="/app/feedback" class="nav-link" :class="{ active: isActive('/feedback') }">
                意见反馈
              </router-link>
            </li>

            <!-- Admin Navigation -->
            <li v-if="isAdmin" class="nav-item dropdown">
              <a href="#" class="nav-link dropdown-toggle" @click.prevent="toggleAdminMenu">
                Admin
                <span class="dropdown-icon">▼</span>
              </a>
              <ul v-if="showAdminMenu" class="dropdown-menu">
                <li>
                  <router-link to="/admin/users" class="dropdown-link" :class="{ active: isActive('/admin/users') }">
                    用户管理
                  </router-link>
                </li>
                <li>
                  <router-link to="/admin/schools" class="dropdown-link" :class="{ active: isActive('/admin/schools') }">
                    院校管理
                  </router-link>
                </li>
                <li>
                  <router-link to="/admin/announcements" class="dropdown-link" :class="{ active: isActive('/admin/announcements') }">
                    通知公告
                  </router-link>
                </li>
                <li>
                  <router-link to="/admin/feedback" class="dropdown-link" :class="{ active: isActive('/admin/feedback') }">
                    意见反馈
                  </router-link>
                </li>
              </ul>
            </li>
          </ul>
        </div>

        <!-- User Menu -->
        <div class="navbar-user">
          <div class="user-menu">
            <button class="user-button" @click="toggleUserMenu">
              <span class="user-icon">👤</span>
              <span class="user-name">{{ user?.username }}</span>
              <span class="menu-icon">▼</span>
            </button>
            <ul v-if="showUserMenu" class="user-dropdown">
              <li>
                <router-link to="/profile" class="user-dropdown-link" :class="{ active: isActive('/profile') }">
                  我的资料
                </router-link>
              </li>
              <li class="divider"></li>
              <li>
                <a href="#" class="user-dropdown-link logout-link" @click.prevent="handleLogout">
                  退出登录
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="main-content">
      <router-view></router-view>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="footer-content">
        <p>&copy; 2024 Postgraduate School Selection System. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import authService from '../services/authService'

export default {
  name: 'MainLayout',
  data() {
    return {
      user: null,
      showUserMenu: false,
      showAdminMenu: false
    }
  },
  computed: {
    /**
     * Check if current user is a regular user
     */
    isUser() {
      return authService.isUser()
    },

    /**
     * Check if current user is an admin
     */
    isAdmin() {
      return authService.isAdmin()
    }
  },
  mounted() {
    this.loadUserData()
    // Close menus when clicking outside
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  methods: {
    /**
     * Load current user data from auth service
     */
    loadUserData() {
      this.user = authService.getUser()
    },

    /**
     * Toggle user dropdown menu
     */
    toggleUserMenu() {
      this.showUserMenu = !this.showUserMenu
      this.showAdminMenu = false
    },

    /**
     * Toggle admin dropdown menu
     */
    toggleAdminMenu() {
      this.showAdminMenu = !this.showAdminMenu
    },

    /**
     * Handle logout
     * Clears authentication data and redirects to login page
     */
    handleLogout() {
      authService.logout()
      this.$router.push('/login')
    },

    /**
     * Check if a route is currently active
     * @param {string} path - The route path to check
     * @returns {boolean} True if the current route matches the path
     */
    isActive(path) {
      return this.$route.path === path || this.$route.path.startsWith(path + '/')
    },

    /**
     * Close menus when clicking outside
     */
    handleClickOutside(event) {
      const navbar = document.querySelector('.navbar-user')
      const adminMenu = document.querySelector('.navbar-menu')
      
      if (navbar && !navbar.contains(event.target)) {
        this.showUserMenu = false
      }
      if (adminMenu && !adminMenu.contains(event.target)) {
        this.showAdminMenu = false
      }
    }
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* Navigation Bar */
.navbar {
  background-color: #2c3e50;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
}

/* Brand */
.navbar-brand {
  flex-shrink: 0;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: white;
  font-weight: 600;
  font-size: 16px;
  transition: opacity 0.3s;
}

.brand-link:hover {
  opacity: 0.8;
}

.brand-icon {
  font-size: 24px;
}

.brand-text {
  display: none;
}

@media (min-width: 768px) {
  .brand-text {
    display: inline;
  }
}

/* Navigation Menu */
.navbar-menu {
  flex: 1;
  display: flex;
  justify-content: center;
  margin: 0 20px;
}

.nav-links {
  display: flex;
  list-style: none;
  gap: 5px;
  flex-wrap: wrap;
  justify-content: center;
}

.nav-item {
  position: relative;
}

.nav-link {
  display: block;
  padding: 10px 15px;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
  font-size: 14px;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-link.active {
  background-color: #3498db;
}

.dropdown-toggle {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.dropdown-icon {
  font-size: 10px;
  transition: transform 0.3s;
}

.nav-item.dropdown:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* Dropdown Menu */
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: #34495e;
  list-style: none;
  border-radius: 4px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  min-width: 200px;
  margin-top: 5px;
  overflow: hidden;
}

.dropdown-link {
  display: block;
  padding: 12px 20px;
  color: white;
  text-decoration: none;
  transition: background-color 0.3s;
  font-size: 14px;
}

.dropdown-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.dropdown-link.active {
  background-color: #3498db;
}

/* User Menu */
.navbar-user {
  flex-shrink: 0;
}

.user-menu {
  position: relative;
}

.user-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background-color: transparent;
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s, border-color 0.3s;
}

.user-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
}

.user-icon {
  font-size: 18px;
}

.user-name {
  display: none;
}

@media (min-width: 768px) {
  .user-name {
    display: inline;
  }
}

.menu-icon {
  font-size: 10px;
  transition: transform 0.3s;
}

.user-button:hover .menu-icon {
  transform: rotate(180deg);
}

/* User Dropdown */
.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: white;
  list-style: none;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 180px;
  margin-top: 8px;
  overflow: hidden;
  z-index: 1000;
}

.user-dropdown-link {
  display: block;
  padding: 12px 20px;
  color: #333;
  text-decoration: none;
  transition: background-color 0.3s;
  font-size: 14px;
}

.user-dropdown-link:hover {
  background-color: #f5f5f5;
}

.user-dropdown-link.active {
  background-color: #e8f4f8;
  color: #3498db;
}

.logout-link {
  color: #e74c3c;
}

.logout-link:hover {
  background-color: #ffe8e8;
}

.divider {
  height: 1px;
  background-color: #eee;
  margin: 5px 0;
}

/* Main Content */
.main-content {
  flex: 1;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: 20px;
}

/* Footer */
.footer {
  background-color: #2c3e50;
  color: white;
  text-align: center;
  padding: 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
}

.footer-content p {
  font-size: 14px;
  opacity: 0.8;
}

/* Responsive Design */
@media (max-width: 768px) {
  .navbar-container {
    height: auto;
    flex-wrap: wrap;
    padding: 10px 15px;
  }

  .navbar-menu {
    order: 3;
    width: 100%;
    margin: 10px 0;
    justify-content: flex-start;
  }

  .nav-links {
    gap: 0;
  }

  .nav-link {
    padding: 8px 12px;
    font-size: 13px;
  }

  .dropdown-menu {
    position: static;
    background-color: #34495e;
    box-shadow: none;
    margin-top: 0;
  }

  .navbar-user {
    order: 2;
    margin-left: auto;
  }

  .user-button {
    padding: 6px 10px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .navbar-container {
    padding: 8px 10px;
  }

  .brand-link {
    gap: 5px;
  }

  .brand-icon {
    font-size: 20px;
  }

  .nav-link {
    padding: 6px 10px;
    font-size: 12px;
  }

  .user-button {
    padding: 5px 8px;
    font-size: 12px;
  }

  .user-icon {
    font-size: 16px;
  }
}
</style>

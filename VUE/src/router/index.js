import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

// 导入页面组件
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import NotFound from '@/views/NotFound.vue'
import Notifications from '@/views/Notifications.vue'

// 学生端页面
import StudentLayout from '@/layouts/StudentLayout.vue'
import StudentPosts from '@/views/student/Posts.vue'
import StudentApplications from '@/views/student/Applications.vue'
import StudentInternship from '@/views/student/Internship.vue'
import StudentProfile from '@/views/student/Profile.vue'

// 带教老师端页面
import TeacherLayout from '@/layouts/TeacherLayout.vue'
import TeacherTasks from '@/views/teacher/Tasks.vue'
import TeacherReports from '@/views/teacher/Reports.vue'
import TeacherEvaluation from '@/views/teacher/Evaluation.vue'
import TeacherProfile from '@/views/teacher/Profile.vue'

// 学校管理员端页面
import SchoolAdminLayout from '@/layouts/SchoolAdminLayout.vue'
import SchoolStudentAuth from '@/views/schoolAdmin/StudentAuth.vue'
import SchoolApplicationReview from '@/views/schoolAdmin/ApplicationReview.vue'
import SchoolDashboard from '@/views/schoolAdmin/Dashboard.vue'
import SchoolAdminProfile from '@/views/schoolAdmin/Profile.vue'

// 医院管理员端页面
import HospitalAdminLayout from '@/layouts/HospitalAdminLayout.vue'
import HospitalPostManagement from '@/views/hospitalAdmin/PostManagement.vue'
import HospitalApplicationReview from '@/views/hospitalAdmin/ApplicationReview.vue'
import HospitalDashboard from '@/views/hospitalAdmin/Dashboard.vue'
import HospitalAdminProfile from '@/views/hospitalAdmin/Profile.vue'

// 系统管理员端页面
import AdminLayout from '@/layouts/AdminLayout.vue'
import AdminOrganization from '@/views/admin/Organization.vue'
import AdminUserAuth from '@/views/admin/UserAuth.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false },
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false },
  },
  {
    path: '/student',
    component: StudentLayout,
    meta: { requiresAuth: true, roles: ['STUDENT'] },
    children: [
      {
        path: 'posts',
        name: 'StudentPosts',
        component: StudentPosts,
      },
      {
        path: 'applications',
        name: 'StudentApplications',
        component: StudentApplications,
      },
      {
        path: 'internship',
        name: 'StudentInternship',
        component: StudentInternship,
      },
      {
        path: 'notifications',
        name: 'StudentNotifications',
        component: Notifications,
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: StudentProfile,
      },
    ],
  },
  {
    path: '/teacher',
    component: TeacherLayout,
    meta: { requiresAuth: true, roles: ['TEACHER'] },
    children: [
      {
        path: 'tasks',
        name: 'TeacherTasks',
        component: TeacherTasks,
      },
      {
        path: 'reports',
        name: 'TeacherReports',
        component: TeacherReports,
      },
      {
        path: 'evaluation',
        name: 'TeacherEvaluation',
        component: TeacherEvaluation,
      },
      {
        path: 'notifications',
        name: 'TeacherNotifications',
        component: Notifications,
      },
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: TeacherProfile,
      },
    ],
  },
  {
    path: '/school-admin',
    component: SchoolAdminLayout,
    meta: { requiresAuth: true, roles: ['SCHOOL_ADMIN'] },
    children: [
      {
        path: 'student-auth',
        name: 'SchoolStudentAuth',
        component: SchoolStudentAuth,
      },
      {
        path: 'application-review',
        name: 'SchoolApplicationReview',
        component: SchoolApplicationReview,
      },
      {
        path: 'dashboard',
        name: 'SchoolDashboard',
        component: SchoolDashboard,
      },
      {
        path: 'notifications',
        name: 'SchoolAdminNotifications',
        component: Notifications,
      },
      {
        path: 'profile',
        name: 'SchoolAdminProfile',
        component: SchoolAdminProfile,
      },
    ],
  },
  {
    path: '/hospital-admin',
    component: HospitalAdminLayout,
    meta: { requiresAuth: true, roles: ['HOSPITAL_ADMIN'] },
    children: [
      {
        path: 'post-management',
        name: 'HospitalPostManagement',
        component: HospitalPostManagement,
      },
      {
        path: 'application-review',
        name: 'HospitalApplicationReview',
        component: HospitalApplicationReview,
      },
      {
        path: 'dashboard',
        name: 'HospitalDashboard',
        component: HospitalDashboard,
      },
      {
        path: 'notifications',
        name: 'HospitalAdminNotifications',
        component: Notifications,
      },
      {
        path: 'profile',
        name: 'HospitalAdminProfile',
        component: HospitalAdminProfile,
      },
    ],
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    children: [
      {
        path: 'organization',
        name: 'AdminOrganization',
        component: AdminOrganization,
      },
      {
        path: 'user-auth',
        name: 'AdminUserAuth',
        component: AdminUserAuth,
      },
      {
        path: 'notifications',
        name: 'AdminNotifications',
        component: Notifications,
      },
    ],
  },
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 恢复用户信息
  if (!userStore.user) {
    userStore.restoreUser()
  }

  const requiresAuth = to.meta.requiresAuth !== false
  const allowedRoles = to.meta.roles

  // 检查是否需要认证
  if (requiresAuth && !userStore.isAuthenticated) {
    next('/login')
    return
  }

  // 检查角色权限
  if (allowedRoles && userStore.user && !allowedRoles.includes(userStore.user.role)) {
    next('/login')
    return
  }

  next()
})

export default router

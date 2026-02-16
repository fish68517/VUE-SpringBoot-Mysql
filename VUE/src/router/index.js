import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../pages/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../pages/Register.vue')
  },
  {
    path: '/activities/create',
    name: 'ActivityCreate',
    component: () => import('../pages/ActivityForm.vue')
  },
  {
    path: '/activities/:id/edit',
    name: 'ActivityEdit',
    component: () => import('../pages/ActivityForm.vue')
  },
  {
    path: '/activities',
    name: 'ActivityList',
    component: () => import('../pages/ActivityList.vue')
  },
  {
    path: '/activity/:id',
    name: 'ActivityDetail',
    component: () => import('../pages/ActivityDetail.vue')
  },
  {
    path: '/my-activities',
    name: 'MyActivities',
    component: () => import('../pages/MyActivities.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../pages/Profile.vue')
  },
  {
    path: '/crowdfunding/:activityId/payment',
    name: 'CrowdfundingPayment',
    component: () => import('../pages/CrowdfundingPayment.vue')
  },
  {
    path: '/payment/success',
    name: 'PaymentSuccess',
    component: () => import('../pages/PaymentSuccess.vue')
  },
  {
    path: '/my-crowdfunding',
    name: 'MyCrowdfunding',
    component: () => import('../pages/MyCrowdfunding.vue')
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: () => import('../pages/Feedback.vue')
  },
  {
    path: '/organizer/feedback-summary',
    name: 'OrganizerFeedbackSummary',
    component: () => import('../pages/OrganizerFeedbackSummary.vue')
  },
  {
    path: '/admin/activity-audit',
    name: 'ActivityAudit',
    component: () => import('../pages/ActivityAudit.vue')
  },
  {
    path: '/admin/fund-audit',
    name: 'FundAudit',
    component: () => import('../pages/FundProofAudit.vue')
  },
  {
    path: '/admin/users',
    name: 'UserManagement',
    component: () => import('../pages/UserManagement.vue')
  },
  {
    path: '/organizer/statistics',
    name: 'OrganizerStatistics',
    component: () => import('../pages/OrganizerStatistics.vue')
  },
  {
    path: '/admin/statistics',
    name: 'AdminStatistics',
    component: () => import('../pages/AdminStatistics.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

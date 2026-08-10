import { createRouter, createWebHistory } from 'vue-router'

const FrontLayout = () => import('../layout/FrontLayout.vue')
const AdminLayout = () => import('../layout/AdminLayout.vue')
const HomeView = () => import('../views/home/HomeView.vue')
const AdminDashboard = () => import('../views/admin/AdminDashboard.vue')
const AdminCrudView = () => import('../views/admin/AdminCrudView.vue')
const LoginView = () => import('../views/LoginView.vue')
const StationMapView = () => import('../views/station/StationMapView.vue')
const StationDetailView = () => import('../views/station/StationDetailView.vue')
const StationGuideView = () => import('../views/guide/StationGuideView.vue')
const StatisticsView = () => import('../views/statistics/StatisticsView.vue')
const NoticeListView = () => import('../views/notice/NoticeListView.vue')
const NoticeDetailView = () => import('../views/notice/NoticeDetailView.vue')
const PersonalCenterView = () => import('../views/user/PersonalCenterView.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: FrontLayout,
      children: [
        { path: '', name: 'home', component: HomeView },
        { path: 'stations', name: 'stations', component: StationMapView },
        { path: 'stations/:id', name: 'station-detail', component: StationDetailView, props: true },
        { path: 'stations/:id/guide', name: 'station-guide', component: StationGuideView, props: true },
        { path: 'statistics', name: 'statistics', component: StatisticsView },
        { path: 'notices', name: 'notices', component: NoticeListView },
        { path: 'notices/:id', name: 'notice-detail', component: NoticeDetailView, props: true },
        { path: 'profile', name: 'profile', component: PersonalCenterView }
      ]
    },
    {
      path: '/admin',
      component: AdminLayout,
      children: [
        { path: '', name: 'admin-dashboard', component: AdminDashboard },
        { path: 'users', name: 'admin-users', component: AdminCrudView, props: { module: 'users' } },
        { path: 'regions', name: 'admin-regions', component: AdminCrudView, props: { module: 'regions' } },
        { path: 'stations', name: 'admin-stations', component: AdminCrudView, props: { module: 'stations' } },
        { path: 'piles', name: 'admin-piles', component: AdminCrudView, props: { module: 'piles' } },
        { path: 'status', name: 'admin-status', component: AdminCrudView, props: { module: 'status' } },
        { path: 'usage-records', name: 'admin-usage-records', component: AdminCrudView, props: { module: 'usage-records' } },
        { path: 'statistics', name: 'admin-statistics', component: AdminCrudView, props: { module: 'statistics' } },
        { path: 'guide-points', name: 'admin-guide-points', component: AdminCrudView, props: { module: 'guide-points' } },
        { path: 'notices', name: 'admin-notices', component: AdminCrudView, props: { module: 'notices' } },
        { path: 'feedback', name: 'admin-feedback', component: AdminCrudView, props: { module: 'feedback' } }
      ]
    },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/:pathMatch(.*)*', redirect: '/' }
  ],
  scrollBehavior: () => ({ top: 0 })
})

export default router

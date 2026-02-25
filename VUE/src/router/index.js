import { createRouter, createWebHistory } from 'vue-router'

// Tourist views
import Home from '../views/tourist/Home.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import Profile from '../views/tourist/Profile.vue'
import Attractions from '../views/tourist/Attractions.vue'
import AttractionDetail from '../views/tourist/AttractionDetail.vue'
import AttractionBooking from '../views/tourist/AttractionBooking.vue'
import Hotels from '../views/tourist/Hotels.vue'
import HotelDetail from '../views/tourist/HotelDetail.vue'
import HotelBooking from '../views/tourist/HotelBooking.vue'
import Products from '../views/tourist/Products.vue'
import ProductDetail from '../views/tourist/ProductDetail.vue'
import ProductBooking from '../views/tourist/ProductBooking.vue'
import Orders from '../views/tourist/Orders.vue'
import Favorites from '../views/tourist/Favorites.vue'
import Routes from '../views/tourist/Routes.vue'

// Admin views
import AdminUsers from '../views/admin/Users.vue'
import AdminAttractions from '../views/admin/Attractions.vue'
import AdminHotels from '../views/admin/Hotels.vue'
import AdminOrders from '../views/admin/Orders.vue'
import AdminComments from '../views/admin/Comments.vue'
import AdminAnnouncements from '../views/admin/Announcements.vue'
import AdminRoutes from '../views/admin/Routes.vue'

const routes = [
  // Auth routes
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },

  // Tourist routes
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: false }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true, role: 'tourist' }
  },
  {
    path: '/personal-center',
    name: 'PersonalCenter',
    component: Profile,
    meta: { requiresAuth: true, role: 'tourist' }
  },
  {
    path: '/attractions',
    name: 'Attractions',
    component: Attractions,
    meta: { requiresAuth: false }
  },
  {
    path: '/attractions/:id',
    name: 'AttractionDetail',
    component: AttractionDetail,
    meta: { requiresAuth: false }
  },
  {
    path: '/attractions/:id/booking',
    name: 'AttractionBooking',
    component: AttractionBooking,
    meta: { requiresAuth: true, role: 'tourist' }
  },
  {
    path: '/hotels',
    name: 'Hotels',
    component: Hotels,
    meta: { requiresAuth: false }
  },
  {
    path: '/hotels/:id',
    name: 'HotelDetail',
    component: HotelDetail,
    meta: { requiresAuth: false }
  },
  {
    path: '/hotels/:id/booking',
    name: 'HotelBooking',
    component: HotelBooking,
    meta: { requiresAuth: true, role: 'tourist' }
  },
  {
    path: '/products',
    name: 'Products',
    component: Products,
    meta: { requiresAuth: false }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail,
    meta: { requiresAuth: false }
  },
  {
    path: '/product/:id/booking',
    name: 'ProductBooking',
    component: ProductBooking,
    meta: { requiresAuth: true, role: 'tourist' }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: Orders,
    meta: { requiresAuth: true, role: 'tourist' }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: Favorites,
    meta: { requiresAuth: true, role: 'tourist' }
  },
  {
    path: '/routes',
    name: 'Routes',
    component: Routes,
    meta: { requiresAuth: false }
  },

  // Admin routes
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: AdminUsers,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/attractions',
    name: 'AdminAttractions',
    component: AdminAttractions,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/hotels',
    name: 'AdminHotels',
    component: AdminHotels,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: AdminOrders,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/comments',
    name: 'AdminComments',
    component: AdminComments,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/announcements',
    name: 'AdminAnnouncements',
    component: AdminAnnouncements,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/routes',
    name: 'AdminRoutes',
    component: AdminRoutes,
    meta: { requiresAuth: true, role: 'admin' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for authentication
router.beforeEach((to, _from, next) => {
  const user = localStorage.getItem('user')
  const userInfo = user ? JSON.parse(user) : null

  if (to.meta.requiresAuth) {
    if (!userInfo) {
      next('/login')
    } else if (to.meta.role && userInfo.role !== to.meta.role) {
      next('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router

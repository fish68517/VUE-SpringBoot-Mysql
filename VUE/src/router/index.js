import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/userStore";

import Login from "@/views/Login.vue";
import Register from "@/views/Register.vue";
import Home from "@/views/Home.vue";
import Profile from "@/views/Profile.vue";
import ProductList from "@/views/user/ProductList.vue";
import ProductDetail from "@/views/user/ProductDetail.vue";
import Cart from "@/views/user/Cart.vue";
import OrderConfirm from "@/views/user/OrderConfirm.vue";
import OrderList from "@/views/user/OrderList.vue";
import OrderDetail from "@/views/user/OrderDetail.vue";
import ReviewForm from "@/views/user/ReviewForm.vue";
import PostList from "@/views/community/PostList.vue";
import PostDetail from "@/views/community/PostDetail.vue";
import PostForm from "@/views/community/PostForm.vue";
import ShopLayout from "@/views/shop/ShopLayout.vue";
import ShopDashboard from "@/views/shop/ShopDashboard.vue";
import ShopInfo from "@/views/shop/ShopInfo.vue";
import ShopProductList from "@/views/shop/ProductList.vue";
import ShopProductForm from "@/views/shop/ProductForm.vue";
import ShopOrderList from "@/views/shop/OrderList.vue";
import ShopOrderDetail from "@/views/shop/OrderDetail.vue";
import ShopCouponList from "@/views/shop/CouponList.vue";
import ShopCouponForm from "@/views/shop/CouponForm.vue";
import AdminLayout from "@/views/admin/AdminLayout.vue";
import AdminDashboard from "@/views/admin/Dashboard.vue";
import AdminUserManage from "@/views/admin/UserManage.vue";
import AdminShopManage from "@/views/admin/ShopManage.vue";
import AdminProductAudit from "@/views/admin/ProductAudit.vue";
import AdminPostManage from "@/views/admin/PostManage.vue";

const routes = [
  { path: "/", component: Home },
  { path: "/login", component: Login, meta: { requiresAuth: false } },
  { path: "/register", component: Register, meta: { requiresAuth: false } },
  { path: "/profile", component: Profile, meta: { requiresAuth: true } },
  { path: "/products", component: ProductList },
  { path: "/product/:id", component: ProductDetail },
  { path: "/cart", component: Cart, meta: { requiresAuth: true } },
  { path: "/order-confirm", component: OrderConfirm, meta: { requiresAuth: true } },
  { path: "/orders", component: OrderList, meta: { requiresAuth: true } },
  { path: "/order/:id", component: OrderDetail, meta: { requiresAuth: true } },
  { path: "/review/form/:productId", component: ReviewForm, meta: { requiresAuth: true } },
  { path: "/community", component: PostList },
  { path: "/community/post/:id", component: PostDetail },
  { path: "/community/create", component: PostForm, meta: { requiresAuth: true } },
  {
    path: "/shop",
    component: ShopLayout,
    meta: { requiresAuth: true },
    children: [
      { path: "dashboard", component: ShopDashboard },
      { path: "info", component: ShopInfo },
      { path: "products", component: ShopProductList },
      { path: "products/create", component: ShopProductForm },
      { path: "products/edit/:id", component: ShopProductForm },
      { path: "orders", component: ShopOrderList },
      { path: "orders/:id", component: ShopOrderDetail },
      { path: "coupons", component: ShopCouponList },
      { path: "coupons/create", component: ShopCouponForm },
      { path: "coupons/edit/:id", component: ShopCouponForm }
    ]
  },
  {
    path: "/admin",
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: "dashboard", component: AdminDashboard },
      { path: "users", component: AdminUserManage },
      { path: "shops", component: AdminShopManage },
      { path: "products", component: AdminProductAudit },
      { path: "posts", component: AdminPostManage }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫：检查认证状态
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const requiresAuth = to.meta.requiresAuth;

  if (requiresAuth && !userStore.isLogin) {
    // 需要认证但未登录，重定向到登录页
    next("/login");
  } else if ((to.path === "/login" || to.path === "/register") && userStore.isLogin) {
    // 已登录用户访问登录/注册页，重定向到首页
    next("/");
  } else {
    next();
  }
});

export default router;

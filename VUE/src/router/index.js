import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/userStore";

import Login from "@/views/Login.vue";
import Register from "@/views/Register.vue";

import UserLayout from "@/views/user/UserLayout.vue";
import ShopLayout from "@/views/shop/ShopLayout.vue";
import AdminLayout from "@/views/admin/AdminLayout.vue";

import Profile from "@/views/Profile.vue";
import Cart from "@/views/user/Cart.vue";
import OrderList from "@/views/user/OrderList.vue";
import OrderDetail from "@/views/user/OrderDetail.vue";
import OrderConfirm from "@/views/user/OrderConfirm.vue";
import PetList from "@/views/user/PetList.vue";
import ReviewForm from "@/views/user/ReviewForm.vue";
import CouponExchange from "@/views/user/CouponExchange.vue";
import ProductList from "@/views/user/ProductList.vue";
import ProductDetail from "@/views/user/ProductDetail.vue";
import PostList from "@/views/community/PostList.vue";
import PostDetail from "@/views/community/PostDetail.vue";
import PostForm from "@/views/community/PostForm.vue";

import ShopDashboard from "@/views/shop/ShopDashboard.vue";
import ShopInfo from "@/views/shop/ShopInfo.vue";
import ShopProductList from "@/views/shop/ProductList.vue";
import ShopProductForm from "@/views/shop/ProductForm.vue";
import ShopOrderList from "@/views/shop/OrderList.vue";
import ShopOrderDetail from "@/views/shop/OrderDetail.vue";
import ShopCouponList from "@/views/shop/CouponList.vue";
import ShopCouponForm from "@/views/shop/CouponForm.vue";

import AdminDashboard from "@/views/admin/Dashboard.vue";
import AdminUserManage from "@/views/admin/UserManage.vue";
import AdminShopManage from "@/views/admin/ShopManage.vue";
import AdminProductAudit from "@/views/admin/ProductAudit.vue";
import AdminPostManage from "@/views/admin/PostManage.vue";

const routes = [
  { path: "/", component: Login, meta: { title: "登录" } },
  { path: "/login", component: Login, meta: { requiresAuth: false, title: "登录" } },
  { path: "/register", component: Register, meta: { requiresAuth: false, title: "注册" } },
  {
    path: "/user",
    component: UserLayout,
    meta: { requiresAuth: true, role: "USER" },
    children: [
      { path: "", redirect: "/user/home" },
      { path: "home", component: ProductList, meta: { title: "商城首页" } },
      { path: "product/:id", component: ProductDetail, meta: { title: "商品详情" } },
      { path: "profile", component: Profile, meta: { title: "个人信息" } },
      { path: "pets", component: PetList, meta: { title: "我的宠物" } },
      { path: "cart", component: Cart, meta: { title: "购物车" } },
      { path: "order-confirm", component: OrderConfirm, meta: { title: "确认订单" } },
      { path: "orders", component: OrderList, meta: { title: "我的订单" } },
      { path: "orders/:id", component: OrderDetail, meta: { title: "订单详情" } },
      { path: "coupon-exchange", component: CouponExchange, meta: { title: "优惠券兑换" } },
      { path: "review/form/:productId", component: ReviewForm, meta: { title: "写评价" } },
      { path: "community", component: PostList, meta: { title: "宠物社区" } },
      { path: "community/post/:id", component: PostDetail, meta: { title: "帖子详情" } },
      { path: "community/create", component: PostForm, meta: { title: "发布帖子" } }
    ]
  },
  {
    path: "/shop",
    component: ShopLayout,
    meta: { requiresAuth: true, role: "SHOP" },
    children: [
      { path: "", redirect: "/shop/dashboard" },
      { path: "dashboard", component: ShopDashboard, meta: { title: "店铺概览" } },
      { path: "info", component: ShopInfo, meta: { title: "店铺信息" } },
      { path: "products", component: ShopProductList, meta: { title: "商品管理" } },
      { path: "products/create", component: ShopProductForm, meta: { title: "发布商品" } },
      { path: "products/edit/:id", component: ShopProductForm, meta: { title: "编辑商品" } },
      { path: "orders", component: ShopOrderList, meta: { title: "订单管理" } },
      { path: "orders/:id", component: ShopOrderDetail, meta: { title: "订单详情" } },
      { path: "coupons", component: ShopCouponList, meta: { title: "优惠券" } },
      { path: "coupons/create", component: ShopCouponForm, meta: { title: "创建优惠券" } },
      { path: "coupons/edit/:id", component: ShopCouponForm, meta: { title: "编辑优惠券" } }
    ]
  },
  {
    path: "/admin",
    component: AdminLayout,
    meta: { requiresAuth: true, role: "ADMIN" },
    children: [
      { path: "", redirect: "/admin/dashboard" },
      { path: "dashboard", component: AdminDashboard, meta: { title: "数据看板" } },
      { path: "users", component: AdminUserManage, meta: { title: "用户管理" } },
      { path: "shops", component: AdminShopManage, meta: { title: "店铺管理" } },
      { path: "products", component: AdminProductAudit, meta: { title: "商品审核" } },
      { path: "posts", component: AdminPostManage, meta: { title: "社区管理" } }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const userStore = useUserStore();

  if (to.meta.title) {
    document.title = `${to.meta.title} - 星落宠物商城`;
  }

  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);

  if (requiresAuth && !userStore.isLogin) {
    next({ path: "/login", query: { redirect: to.fullPath } });
    return;
  }

  if ((to.path === "/login" || to.path === "/register") && userStore.isLogin) {
    const role = userStore.userInfo?.role;
    if (role === "SHOP") next("/shop");
    else if (role === "ADMIN") next("/admin");
    else next("/user/home");
    return;
  }

  next();
});

export default router;

import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/store/auth";

import Login from "@/views/auth/Login.vue";
import Register from "@/views/auth/Register.vue";
import MainLayout from "@/layouts/MainLayout.vue";
import Home from "@/views/common/Home.vue";

// jobseeker
import JobPostList from "@/views/jobseeker/JobPostList.vue";
import MyApplications from "@/views/jobseeker/MyApplications.vue";
import MyProfile from "@/views/jobseeker/MyProfile.vue";
import MyMessages from "@/views/jobseeker/MyMessages.vue";
import MyComplaints from "@/views/jobseeker/MyComplaints.vue";

// merchant
import CompanyManage from "@/views/merchant/CompanyManage.vue";
import JobPostManage from "@/views/merchant/JobPostManage.vue";
import ApplicationManage from "@/views/merchant/ApplicationManage.vue";
import MerchantMessages from "@/views/merchant/MerchantMessages.vue";

// admin
import UserManage from "@/views/admin/UserManage.vue";
import CompanyAudit from "@/views/admin/CompanyAudit.vue";
import ComplaintManage from "@/views/admin/ComplaintManage.vue";
import AdminMessages from "@/views/admin/AdminMessages.vue";

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", component: Login },
  { path: "/register", component: Register },

  {
    path: "/app",
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      { path: "", component: Home },

      // jobseeker
      { path: "jobs", component: JobPostList, meta: { role: ["JOBSEEKER"] } },
      { path: "my-applications", component: MyApplications, meta: { role: ["JOBSEEKER"] } },
      { path: "my-profile", component: MyProfile, meta: { role: ["JOBSEEKER"] } },
      { path: "my-messages", component: MyMessages, meta: { role: ["JOBSEEKER"] } },
      { path: "my-complaints", component: MyComplaints, meta: { role: ["JOBSEEKER"] } },

      // merchant
      { path: "merchant-company", component: CompanyManage, meta: { role: ["MERCHANT"] } },
      { path: "merchant-jobs", component: JobPostManage, meta: { role: ["MERCHANT"] } },
      { path: "merchant-applications", component: ApplicationManage, meta: { role: ["MERCHANT"] } },
      { path: "merchant-messages", component: MerchantMessages, meta: { role: ["MERCHANT"] } },

      // admin
      { path: "admin-users", component: UserManage, meta: { role: ["ADMIN"] } },
      { path: "admin-companies", component: CompanyAudit, meta: { role: ["ADMIN"] } },
      { path: "admin-complaints", component: ComplaintManage, meta: { role: ["ADMIN"] } },
      { path: "admin-messages", component: AdminMessages, meta: { role: ["ADMIN"] } },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to) => {
  const auth = useAuthStore();

  if (to.meta.requiresAuth && !auth.isLogin) return "/login";
  if (to.meta.role && auth.role && !to.meta.role.includes(auth.role)) return "/app";
  return true;
});

export default router;

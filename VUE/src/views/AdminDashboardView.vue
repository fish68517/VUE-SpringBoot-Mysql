<template>
  <div class="admin-page">
    <section class="hero">
      <div>
        <p class="eyebrow">管理员总览</p>
        <h2>统一管理用户、旅行内容与社交数据</h2>
      </div>
      <el-button type="primary" @click="loadOverview">刷新数据</el-button>
    </section>

    <div class="card-grid" v-loading="loading">
      <article class="stat-card">
        <span>用户总数</span>
        <strong>{{ overview.totalUsers }}</strong>
      </article>
      <article class="stat-card">
        <span>管理员数量</span>
        <strong>{{ overview.totalAdmins }}</strong>
      </article>
      <article class="stat-card">
        <span>旅行记录</span>
        <strong>{{ overview.totalTravelRecords }}</strong>
      </article>
      <article class="stat-card">
        <span>旅行计划</span>
        <strong>{{ overview.totalTravelPlans }}</strong>
      </article>
      <article class="stat-card">
        <span>评论总数</span>
        <strong>{{ overview.totalComments }}</strong>
      </article>
      <article class="stat-card">
        <span>点赞总数</span>
        <strong>{{ overview.totalLikes }}</strong>
      </article>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminService } from '../services/adminService'

const loading = ref(false)
const overview = reactive({
  totalUsers: 0,
  totalAdmins: 0,
  totalTravelRecords: 0,
  totalTravelPlans: 0,
  totalComments: 0,
  totalLikes: 0
})

const loadOverview = async () => {
  loading.value = true
  try {
    const response = await adminService.getOverview()
    Object.assign(overview, response.data)
  } catch (error) {
    ElMessage.error(error.message || '加载后台概览失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadOverview)
</script>

<style scoped>
.admin-page {
  max-width: 1320px;
  margin: 0 auto;
}

.hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: end;
  margin-bottom: 24px;
}

.eyebrow {
  margin: 0 0 10px;
  color: #3d7ea6;
  font-weight: 700;
}

.hero h2 {
  margin: 0;
  font-size: 32px;
  color: #183b56;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 18px;
}

.stat-card {
  padding: 24px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 12px 30px rgba(17, 45, 78, 0.08);
}

.stat-card span {
  display: block;
  color: #6b7a88;
  margin-bottom: 14px;
}

.stat-card strong {
  font-size: 32px;
  color: #183b56;
}
</style>

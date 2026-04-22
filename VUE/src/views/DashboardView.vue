<template>
  <div class="dashboard-wrap">
    <div class="stat-grid">
      <div class="stat-card bg-blue">
        <h3>系统用户</h3>
        <p>{{ summary.userCount || 0 }}</p>
      </div>
      <div class="stat-card bg-green">
        <h3>设备数量</h3>
        <p>{{ summary.deviceCount || 0 }}</p>
      </div>
      <div class="stat-card bg-orange">
        <h3>巡检任务</h3>
        <p>{{ summary.taskCount || 0 }}</p>
      </div>
      <div class="stat-card bg-red">
        <h3>异常缺陷</h3>
        <p>{{ summary.defectCount || 0 }}</p>
      </div>
      <div class="stat-card bg-indigo">
        <h3>维修工单</h3>
        <p>{{ summary.repairOrderCount || 0 }}</p>
      </div>
      <div class="stat-card bg-teal">
        <h3>巡检记录</h3>
        <p>{{ summary.recordCount || 0 }}</p>
      </div>
    </div>

    <div class="grid-2" style="margin-top: 20px;">
      <section class="page-card panel">
        <div class="toolbar">
          <div class="toolbar-title">角色分布</div>
        </div>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="管理员">{{ summary.adminCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="巡检员">{{ summary.inspectorCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="维护员">{{ summary.maintainerCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="当前角色">{{ roleText }}</el-descriptions-item>
        </el-descriptions>
      </section>

      <section class="page-card panel">
        <div class="toolbar">
          <div class="toolbar-title">系统说明</div>
        </div>
        <el-timeline>
          <el-timeline-item type="primary" timestamp="管理员">
            管理用户、设备、分类、巡检任务、异常派单与工单闭环。
          </el-timeline-item>
          <el-timeline-item type="success" timestamp="巡检员">
            查看个人巡检任务，提交巡检结果，上传图片并触发异常上报。
          </el-timeline-item>
          <el-timeline-item type="warning" timestamp="维护员">
            接收维修工单，填写处理措施和结果，上传维修图片。
          </el-timeline-item>
        </el-timeline>
      </section>
    </div>

    <section class="page-card panel" style="margin-top: 20px;">
      <div class="toolbar">
        <div class="toolbar-title">最新公告</div>
      </div>
      <el-empty v-if="!notices.length" description="暂无公告" />
      <el-row v-else :gutter="16">
        <el-col v-for="item in notices.slice(0, 4)" :key="item.id" :span="12" style="margin-bottom: 16px;">
          <div class="notice-card">
            <div class="notice-card-title">
              {{ item.title }}
              <el-tag v-if="item.pinned" type="danger" size="small">置顶</el-tag>
            </div>
            <div class="notice-card-meta">{{ item.publisherName }} | {{ item.publishTime?.replace('T', ' ') }}</div>
            <div class="notice-card-content">{{ item.content }}</div>
          </div>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import http from '../api/http'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const summary = ref({})
const notices = ref([])

const roleText = computed(() => ({
  ADMIN: '管理员',
  INSPECTOR: '巡检员',
  MAINTAINER: '维护员'
}[authStore.user?.role] || '-'))

async function loadSummary() {
  summary.value = await http.get('/api/dashboard/summary')
  notices.value = await http.get('/api/notices', { params: { onlyActive: true } })
}

onMounted(loadSummary)
</script>

<style scoped>
.dashboard-wrap {
  display: flex;
  flex-direction: column;
}

.panel {
  padding: 24px;
}

.notice-card {
  padding: 18px;
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(246, 250, 255, 0.92), rgba(255, 255, 255, 0.96));
  border: 1px solid rgba(200, 214, 232, 0.9);
}

.notice-card-title {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 17px;
  font-weight: 700;
  color: #1a3552;
}

.notice-card-meta {
  margin: 8px 0 10px;
  color: #6f87a0;
  font-size: 13px;
}

.notice-card-content {
  color: #30485f;
  line-height: 1.8;
}
</style>

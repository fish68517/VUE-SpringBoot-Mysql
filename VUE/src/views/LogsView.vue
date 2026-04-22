<template>
  <section class="page-card" style="padding: 24px;">
    <div class="toolbar">
      <div class="toolbar-title">操作日志</div>
      <div class="toolbar-actions">
        <el-button @click="loadLogs">刷新</el-button>
      </div>
    </div>

    <el-table :data="logs" border>
      <el-table-column type="index" width="60" label="#" />
      <el-table-column prop="module" label="模块" width="120" />
      <el-table-column prop="action" label="操作" width="140" />
      <el-table-column prop="username" label="账号" width="120" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="role" label="角色" width="110" />
      <el-table-column prop="requestMethod" label="请求方式" width="100" />
      <el-table-column prop="requestPath" label="请求路径" min-width="220" />
      <el-table-column prop="ip" label="IP" width="120" />
      <el-table-column label="结果" width="90">
        <template #default="{ row }">
          <el-tag :type="row.success ? 'success' : 'danger'">{{ row.success ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="message" label="信息" min-width="180" />
      <el-table-column prop="createdAt" label="时间" width="180" />
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'

const logs = ref([])

async function loadLogs() {
  logs.value = await http.get('/api/operation-logs')
}

onMounted(loadLogs)
</script>

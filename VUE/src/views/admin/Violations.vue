<template>
  <div class="admin-violations">
    <el-card>
      <template #header>
        <span>违规记录管理</span>
      </template>

      <el-table :data="violations" v-loading="loading" :header-cell-style="{ textAlign: 'center' }" :cell-style="{ textAlign: 'center' }">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="nickname" label="用户" width="120" align="center">
          <template #default="{ row }">
            {{ row.nickname || row.username }}
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="违规类型" width="120" align="center" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip align="center" />
        <el-table-column prop="points" label="扣分" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-dropdown v-if="row.status === 0">
              <el-button type="primary" size="small">
                处理 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleViolation(row, 1)">标记已处理</el-dropdown-item>
                  <el-dropdown-item @click="handleViolation(row, 2)">标记已申诉</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllViolations, handleViolation as handleViolationApi } from '../../api/violation'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const violations = ref([])

const statusType = (status) => {
  const types = { 0: 'danger', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const loadViolations = async () => {
  loading.value = true
  try {
    const res = await getAllViolations()
    violations.value = res.data
  } finally {
    loading.value = false
  }
}

const handleViolation = async (row, status) => {
  await handleViolationApi(row.id, status)
  ElMessage.success('处理成功')
  loadViolations()
}

onMounted(() => {
  loadViolations()
})
</script>

<style scoped>
.admin-violations {
  max-width: 1200px;
  margin: 0 auto;
}
</style>

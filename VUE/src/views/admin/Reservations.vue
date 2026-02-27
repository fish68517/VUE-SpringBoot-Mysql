<template>
  <div class="admin-reservations">
    <el-card>
      <template #header>
        <span>预约管理</span>
      </template>

      <el-form :inline="true" class="filter-form">
        <el-form-item label="日期筛选">
          <el-date-picker
            v-model="filterDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadReservations">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="reservations" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column label="座位" width="100">
          <template #default="{ row }">
            {{ row.area }}-{{ row.seatNo }}
          </template>
        </el-table-column>
        <el-table-column label="时间段" width="150">
          <template #default="{ row }">
            {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到时间" width="180">
          <template #default="{ row }">
            {{ row.checkInTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="签退时间" width="180">
          <template #default="{ row }">
            {{ row.checkOutTime || '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllReservations } from '../../api/reservation'

const loading = ref(false)
const reservations = ref([])
const filterDate = ref('')

const statusType = (status) => {
  const types = { 0: 'info', 1: 'primary', 2: 'success', 3: 'info' }
  return types[status]
}

const statusText = (status) => {
  const texts = { 0: '已取消', 1: '已预约', 2: '使用中', 3: '已完成' }
  return texts[status]
}

const loadReservations = async () => {
  loading.value = true
  try {
    const res = await getAllReservations(filterDate.value || undefined)
    reservations.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadReservations()
})
</script>

<style scoped>
.admin-reservations {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.filter-form {
  margin-bottom: 20px;
}
</style>

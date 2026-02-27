<template>
  <div class="reservations-container">
    <el-card>
      <template #header>
        <span>我的预约</span>
      </template>

      <el-table :data="reservations" v-loading="loading" :header-cell-style="{ textAlign: 'center' }" :cell-style="{ textAlign: 'center' }">
        <el-table-column prop="date" label="日期" width="120" align="center" />
        <el-table-column label="座位" width="100" align="center">
          <template #default="{ row }">
            {{ row.area }}-{{ row.seatNo }}
          </template>
        </el-table-column>
        <el-table-column label="时间段" width="160" align="center">
          <template #default="{ row }">
            {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到时间" width="180" align="center">
          <template #default="{ row }">
            {{ row.checkInTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="签退时间" width="180" align="center">
          <template #default="{ row }">
            {{ row.checkOutTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button v-if="row.status === 1" type="success" size="small" @click="handleCheckIn(row)">
                签到
              </el-button>
              <el-button v-if="row.status === 2" type="warning" size="small" @click="handleCheckOut(row)">
                签退
              </el-button>
              <el-button v-if="row.status === 1" type="primary" size="small" @click="handleChange(row)">
                改签
              </el-button>
              <el-button v-if="row.status === 1" type="danger" size="small" @click="handleCancel(row)">
                取消
              </el-button>
              <span v-if="row.status === 0 || row.status === 3">-</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 改签对话框 -->
    <el-dialog v-model="changeDialogVisible" title="改签预约" width="500px">
      <el-form :model="changeForm" label-width="100px">
        <el-form-item label="原座位">
          <span>{{ currentReservation?.area }}-{{ currentReservation?.seatNo }}</span>
        </el-form-item>
        <el-form-item label="原时间">
          <span>{{ currentReservation?.date }} {{ currentReservation?.startTime }} - {{ currentReservation?.endTime }}</span>
        </el-form-item>
        <el-divider />
        <el-form-item label="新日期">
          <el-date-picker v-model="changeForm.date" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="新开始时间">
          <el-time-select v-model="changeForm.startTime" start="06:00" step="00:30" end="22:00" />
        </el-form-item>
        <el-form-item label="新结束时间">
          <el-time-select v-model="changeForm.endTime" :start="changeForm.startTime || '06:00'" step="00:30" end="22:30" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmChange" :loading="changing">确认改签</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyReservations, checkIn, checkOut, cancelReservation, changeReservation } from '../api/reservation'
import { getUserInfo } from '../api/user'
import { useUserStore } from '../store/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)
const reservations = ref([])
const changeDialogVisible = ref(false)
const currentReservation = ref(null)
const changing = ref(false)

const changeForm = reactive({
  date: '',
  startTime: '',
  endTime: ''
})

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
    const res = await getMyReservations()
    reservations.value = res.data
  } finally {
    loading.value = false
  }
}

const refreshUserInfo = async () => {
  const res = await getUserInfo()
  userStore.setUser(res.data)
}

const handleCheckIn = async (row) => {
  await checkIn(row.id)
  ElMessage.success('签到成功')
  loadReservations()
}

const handleCheckOut = async (row) => {
  const res = await checkOut(row.id)
  ElMessage.success(res.message)
  loadReservations()
  refreshUserInfo()
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', { type: 'warning' })
  await cancelReservation(row.id)
  ElMessage.success('已取消')
  loadReservations()
}

const handleChange = (row) => {
  currentReservation.value = row
  changeForm.date = row.date
  changeForm.startTime = row.startTime
  changeForm.endTime = row.endTime
  changeDialogVisible.value = true
}

const confirmChange = async () => {
  if (!changeForm.date || !changeForm.startTime || !changeForm.endTime) {
    ElMessage.warning('请填写完整信息')
    return
  }
  changing.value = true
  try {
    await changeReservation(currentReservation.value.id, {
      seatId: currentReservation.value.seatId,
      date: changeForm.date,
      startTime: changeForm.startTime,
      endTime: changeForm.endTime
    })
    ElMessage.success('改签成功')
    changeDialogVisible.value = false
    loadReservations()
  } finally {
    changing.value = false
  }
}

onMounted(() => {
  loadReservations()
})
</script>

<style scoped>
.reservations-container {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
</style>

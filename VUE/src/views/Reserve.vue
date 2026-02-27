<template>
  <div class="reserve-container">
    <el-card>
      <template #header>
        <span>座位预约</span>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="queryForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-select
            v-model="queryForm.startTime"
            start="06:00"
            step="00:30"
            end="22:00"
            placeholder="开始时间"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-select
            v-model="queryForm.endTime"
            :start="queryForm.startTime || '06:00'"
            step="00:30"
            end="22:30"
            placeholder="结束时间"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchSeats">查询可用座位</el-button>
        </el-form-item>
      </el-form>

      <div class="legend">
        <span class="legend-item"><span class="seat-demo available"></span> 可预约</span>
        <span class="legend-item"><span class="seat-demo unavailable"></span> 已被预约</span>
        <span class="legend-item"><span class="seat-demo selected"></span> 已选择</span>
      </div>

      <div v-if="searched" class="seat-area">
        <el-tabs v-model="activeArea">
          <el-tab-pane v-for="area in ['A', 'B', 'C', 'D']" :key="area" :label="area + '区'" :name="area">
            <div class="seats-grid">
              <div
                v-for="seat in seatsByArea[area] || []"
                :key="seat.id"
                class="seat-item"
                :class="{
                  available: seat.available,
                  unavailable: !seat.available,
                  selected: selectedSeat?.id === seat.id
                }"
                @click="selectSeat(seat)"
              >
                <div class="seat-no">{{ seat.seatNo }}</div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div v-if="selectedSeat" class="reserve-action">
        <el-divider />
        <p>已选座位：<strong>{{ selectedSeat.area }}区 - {{ selectedSeat.seatNo }}</strong></p>
        <p>预约时间：<strong>{{ queryForm.date }} {{ queryForm.startTime }} - {{ queryForm.endTime }}</strong></p>
        <el-button type="primary" size="large" @click="confirmReserve" :loading="reserving">
          确认预约
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { getAvailableSeats } from '../api/seat'
import { createReservation } from '../api/reservation'
import { ElMessage } from 'element-plus'

const queryForm = reactive({
  date: '',
  startTime: '',
  endTime: ''
})

const searched = ref(false)
const seatsByArea = ref({})
const activeArea = ref('A')
const selectedSeat = ref(null)
const reserving = ref(false)

const disabledDate = (date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const maxDate = new Date()
  maxDate.setDate(maxDate.getDate() + 7) // 最多预约7天内
  return date < today || date > maxDate
}

const searchSeats = async () => {
  if (!queryForm.date || !queryForm.startTime || !queryForm.endTime) {
    ElMessage.warning('请选择完整的日期和时间')
    return
  }
  if (queryForm.startTime >= queryForm.endTime) {
    ElMessage.warning('结束时间必须大于开始时间')
    return
  }

  const res = await getAvailableSeats(queryForm.date, queryForm.startTime, queryForm.endTime)
  seatsByArea.value = res.data.seats
  searched.value = true
  selectedSeat.value = null
}

const selectSeat = (seat) => {
  if (!seat.available) {
    ElMessage.warning('该座位已被预约')
    return
  }
  selectedSeat.value = seat
}

const confirmReserve = async () => {
  reserving.value = true
  try {
    await createReservation({
      seatId: selectedSeat.value.id,
      date: queryForm.date,
      startTime: queryForm.startTime,
      endTime: queryForm.endTime
    })
    ElMessage.success('预约成功！')
    selectedSeat.value = null
    searchSeats() // 刷新座位状态
  } finally {
    reserving.value = false
  }
}
</script>

<style scoped>
.reserve-container {
  max-width: 1000px;
  margin: 0 auto;
  text-align: center;
}

.query-form {
  margin-bottom: 20px;
  justify-content: center;
}

.legend {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding: 10px;
  background: #e0f2fe;
  border-radius: 4px;
  justify-content: center;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.seat-demo {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.seat-demo.available { background: #22c55e; }
.seat-demo.unavailable { background: #94a3b8; }
.seat-demo.selected { background: #3b82f6; }

.seats-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
  padding: 20px;
  justify-items: center;
}

.seat-item {
  position: relative;
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: bold;
}

.seat-item.available {
  background: #22c55e;
  color: white;
}

.seat-item.available:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 14px rgba(34, 197, 94, 0.5);
}

.seat-item.unavailable {
  background: #94a3b8;
  color: white;
  cursor: not-allowed;
}

.seat-item.selected {
  background: #3b82f6;
  color: white;
  transform: scale(1.05);
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.5);
}

.reserve-action {
  text-align: center;
}

.reserve-action p {
  margin: 10px 0;
  font-size: 16px;
}
</style>

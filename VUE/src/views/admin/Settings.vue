<template>
  <div class="admin-settings">
    <el-card>
      <template #header>
        <span>系统设置</span>
      </template>

      <el-form :model="settings" label-width="180px" style="max-width: 600px; margin: 0 auto;">
        <el-divider content-position="left">开放时间</el-divider>
        <el-form-item label="开放时间">
          <el-time-select
            v-model="settings.open_time"
            start="00:00"
            step="00:30"
            end="12:00"
            placeholder="开放时间"
          />
        </el-form-item>
        <el-form-item label="关闭时间">
          <el-time-select
            v-model="settings.close_time"
            start="12:00"
            step="00:30"
            end="23:30"
            placeholder="关闭时间"
          />
        </el-form-item>

        <el-divider content-position="left">预约规则</el-divider>
        <el-form-item label="最大可预约天数">
          <el-input-number v-model.number="settings.max_reserve_days" :min="1" :max="30" />
          <span class="unit">天</span>
        </el-form-item>
        <el-form-item label="单次最大预约时长">
          <el-input-number v-model.number="settings.max_reserve_hours" :min="1" :max="12" />
          <span class="unit">小时</span>
        </el-form-item>
        <el-form-item label="每日最大预约次数">
          <el-input-number v-model.number="settings.max_daily_reserves" :min="1" :max="10" />
          <span class="unit">次</span>
        </el-form-item>
        <el-form-item label="允许提前签到时间">
          <el-input-number v-model.number="settings.checkin_advance_minutes" :min="5" :max="60" />
          <span class="unit">分钟</span>
        </el-form-item>

        <el-divider content-position="left">违规管理</el-divider>
        <el-form-item label="违规次数上限">
          <el-input-number v-model.number="settings.violation_limit" :min="1" :max="10" />
          <span class="unit">次（达到后禁用账号）</span>
        </el-form-item>
        <el-form-item label="预约未到扣除积分">
          <el-input-number v-model.number="settings.no_show_violation_points" :min="1" :max="10" />
          <span class="unit">分</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPublicSettings, batchUpdateSettings } from '../../api/setting'
import { ElMessage } from 'element-plus'

const saving = ref(false)
const settings = reactive({
  open_time: '06:00',
  close_time: '22:30',
  max_reserve_days: 7,
  max_reserve_hours: 8,
  max_daily_reserves: 3,
  checkin_advance_minutes: 15,
  violation_limit: 3,
  no_show_violation_points: 2
})

const loadSettings = async () => {
  const res = await getPublicSettings()
  Object.keys(settings).forEach(key => {
    if (res.data[key] !== undefined) {
      const value = res.data[key]
      // 数字类型的设置需要转换
      if (['max_reserve_days', 'max_reserve_hours', 'max_daily_reserves', 'checkin_advance_minutes', 'violation_limit', 'no_show_violation_points'].includes(key)) {
        settings[key] = parseInt(value, 10)
      } else {
        settings[key] = value
      }
    }
  })
}

const handleSave = async () => {
  saving.value = true
  try {
    // 转换为字符串
    const data = {}
    Object.keys(settings).forEach(key => {
      data[key] = String(settings[key])
    })
    await batchUpdateSettings(data)
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.admin-settings {
  max-width: 800px;
  margin: 0 auto;
}

.unit {
  margin-left: 10px;
  color: #909399;
}
</style>

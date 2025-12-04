<template>
  <div class="task-management-container">
    <div class="page-header">
      <h2>打卡任务管理 (盛京文化)</h2>
    </div>

    <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
      
      <el-tab-pane label="任务配置" name="config">
        <div class="tab-action-bar">
          <el-button type="primary" @click="openTaskDialog()">
            <el-icon><Plus /></el-icon> 新增打卡任务
          </el-button>
          <div class="filters">
            <el-input 
              v-model="filters.taskKeyword" 
              placeholder="搜索任务名称" 
              style="width: 200px;" 
              clearable 
              @clear="loadTasks"
              @keyup.enter="loadTasks"
            >
              <template #append><el-button @click="loadTasks"><el-icon><Search /></el-icon></el-button></template>
            </el-input>
          </div>
        </div>

        <el-alert title="技术说明" type="success" :closable="false" class="mb-20">
          支持 LBS 地理围栏设置。用户在移动端打卡时，系统将校验其 GPS 坐标是否在设定的经纬度半径范围内。
        </el-alert>

        <el-table :data="taskList" border v-loading="loading.tasks">
          <el-table-column label="封面图" width="100">
            <template #default="{ row }">
              <el-image 
                :src="row.coverImage" 
                :preview-src-list="[row.coverImage]" 
                fit="cover" 
                class="table-image"
              />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="任务名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="pointsReward" label="积分奖励" width="100" align="center">
            <template #default="{ row }">
              <el-tag type="warning">+{{ row.pointsReward }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="有效时间" width="300">
            <template #default="{ row }">
              {{ formatDate(row.startTime) }} 至 {{ formatDate(row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column label="打卡地点" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <div><el-icon><Location /></el-icon> {{ row.location?.addressName }}</div>
              <div class="sub-text">
                ( {{ row.location?.latitude }}, {{ row.location?.longitude }} ) 半径 {{ row.location?.radius }}米
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-switch 
                v-model="row.isActive" 
                :loading="row.statusLoading"
                @change="(val) => handleStatusChange(row, val as boolean)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="openTaskDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteTask(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.tasks.page"
            v-model:page-size="pagination.tasks.size"
            layout="total, prev, pager, next"
            :total="pagination.tasks.total"
            @current-change="loadTasks"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="打卡审核" name="audit">
        <div class="tab-action-bar">
          <div class="left-actions">
            <el-select v-model="filters.auditStatus" placeholder="状态筛选" style="width: 150px; margin-right: 10px;" @change="loadRecords">
              <el-option label="待审核" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
            <el-button type="success" :disabled="selectedRecordIds.length === 0" @click="handleBatchAudit('approved')">
              批量通过
            </el-button>
            <el-button type="danger" :disabled="selectedRecordIds.length === 0" @click="handleBatchAudit('rejected')">
              批量拒绝
            </el-button>
          </div>
          <el-button @click="loadRecords"><el-icon><Refresh /></el-icon></el-button>
        </div>

        <el-table 
          :data="recordList" 
          border 
          v-loading="loading.records" 
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="用户" width="150">
            <template #default="{ row }">
              <div>{{ row.userName }}</div>
              <div class="sub-text">ID: {{ row.userId }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="taskName" label="关联任务" min-width="150" />
          <el-table-column label="打卡图片" width="120">
            <template #default="{ row }">
              <el-image 
                :src="row.checkInImage" 
                :preview-src-list="[row.checkInImage]" 
                fit="cover" 
                class="table-image"
              />
            </template>
          </el-table-column>
          <el-table-column prop="checkInTime" label="打卡时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.checkInTime) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <div v-if="row.status === 'pending'">
                <el-button size="small" type="success" @click="handleSingleAudit(row, 'approved')">通过</el-button>
                <el-button size="small" type="danger" @click="handleSingleAudit(row, 'rejected')">拒绝</el-button>
              </div>
              <span v-else class="text-gray">-</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.records.page"
            v-model:page-size="pagination.records.size"
            layout="total, prev, pager, next"
            :total="pagination.records.total"
            @current-change="loadRecords"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog 
      v-model="dialog.task" 
      :title="isEdit ? '编辑打卡任务' : '新增打卡任务'" 
      width="600px"
      @close="resetTaskForm"
    >
      <el-form :model="taskForm" label-width="100px" ref="taskFormRef">
        <el-form-item label="任务名称" required>
          <el-input v-model="taskForm.name" placeholder="例如：故宫打卡" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-input v-model="taskForm.coverImage" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="taskForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="积分奖励" required>
          <el-input-number v-model="taskForm.pointsReward" :min="1" />
        </el-form-item>
        <el-form-item label="有效时间" required>
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        
        <el-divider content-position="left">地理位置配置 (LBS)</el-divider>
        
        <el-form-item label="地点名称" required>
          <el-input v-model="taskForm.location.addressName" placeholder="例如：沈阳故宫正门" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度" required>
              <el-input-number v-model="taskForm.location.longitude" :precision="6" style="width: 100%;" placeholder="Longitude" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" required>
              <el-input-number v-model="taskForm.location.latitude" :precision="6" style="width: 100%;" placeholder="Latitude" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="打卡半径" required>
          <el-input v-model="taskForm.location.radius" placeholder="米">
            <template #append>米</template>
          </el-input>
          <div class="form-tip">用户必须进入该半径范围内才能完成打卡</div>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-switch v-model="taskForm.isActive" active-text="立即上架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.task = false">取消</el-button>
        <el-button type="primary" @click="submitTask" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Location, Refresh } from '@element-plus/icons-vue'
import { taskApi, type CheckInTask, type UserTaskRecord } from '@/api/task'
import { formatDate } from '@/utils/date'

// --- 状态定义 ---
const activeTab = ref('config')
const submitting = ref(false)
const isEdit = ref(false)

const loading = reactive({
  tasks: false,
  records: false
})

const filters = reactive({
  taskKeyword: '',
  auditStatus: 'pending'
})

const pagination = reactive({
  tasks: { page: 1, size: 10, total: 0 },
  records: { page: 1, size: 10, total: 0 }
})

const dialog = reactive({
  task: false
})

const taskList = ref<(CheckInTask & { statusLoading?: boolean })[]>([])
const recordList = ref<UserTaskRecord[]>([])
const selectedRecordIds = ref<number[]>([])

// 表单数据
const timeRange = ref<[string, string]>(['', ''])
const taskForm = reactive<CheckInTask>({
  name: '',
  description: '',
  coverImage: '',
  pointsReward: 10,
  startTime: '',
  endTime: '',
  location: {
    latitude: 41.8057, // 默认沈阳坐标
    longitude: 123.4315,
    radius: 500,
    addressName: ''
  },
  isActive: true
})

// --- 初始化 ---
onMounted(() => {
  loadTasks()
})

const handleTabChange = (name: string) => {
  if (name === 'config') loadTasks()
  if (name === 'audit') loadRecords()
}

// ---------------- 1. 任务配置逻辑 ----------------

const loadTasks = async () => {
  loading.tasks = true
  try {
    const params = {
      page: pagination.tasks.page - 1,
      size: pagination.tasks.size,
      keyword: filters.taskKeyword || undefined
    }
    const res: any = await taskApi.getTasks(params)
    const data = res.data || res
    
    if (data.content) {
      taskList.value = data.content
      pagination.tasks.total = data.totalElements
    } else {
      taskList.value = Array.isArray(data) ? data : []
    }
  } catch (err) {
    ElMessage.error('加载任务列表失败')
  } finally {
    loading.tasks = false
  }
}

const openTaskDialog = (row?: CheckInTask) => {
  if (row) {
    isEdit.value = true
    Object.assign(taskForm, row)
    // 处理时间范围回显
    if (row.startTime && row.endTime) {
      timeRange.value = [row.startTime, row.endTime]
    }
    // 确保 location 对象存在
    if (!taskForm.location) {
      taskForm.location = { latitude: 0, longitude: 0, radius: 200, addressName: '' }
    }
  } else {
    isEdit.value = false
    resetTaskForm()
  }
  dialog.task = true
}

const resetTaskForm = () => {
  taskForm.id = undefined
  taskForm.name = ''
  taskForm.description = ''
  taskForm.coverImage = ''
  taskForm.pointsReward = 50
  taskForm.isActive = true
  taskForm.location = {
    latitude: 41.796767, // 沈阳故宫附近坐标
    longitude: 123.450462,
    radius: 200,
    addressName: ''
  }
  timeRange.value = ['', '']
}

const submitTask = async () => {
  if (!taskForm.name || !taskForm.pointsReward || !timeRange.value) {
    ElMessage.warning('请填写必填项（名称、积分、时间）')
    return
  }
  
  if (!taskForm.location.addressName || !taskForm.location.latitude || !taskForm.location.longitude) {
    ElMessage.warning('请完善地理位置信息')
    return
  }

  // 绑定时间
  taskForm.startTime = timeRange.value[0]
  taskForm.endTime = timeRange.value[1]

  submitting.value = true
  try {
    if (isEdit.value && taskForm.id) {
      await taskApi.updateTask(taskForm.id, taskForm)
      ElMessage.success('更新成功')
    } else {
      await taskApi.createTask(taskForm)
      ElMessage.success('创建成功')
    }
    dialog.task = false
    loadTasks()
  } catch (err) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const handleStatusChange = async (row: CheckInTask & { statusLoading?: boolean }, isActive: boolean) => {
  if (!row.id) return
  row.statusLoading = true
  try {
    await taskApi.updateTaskStatus(row.id, isActive)
    ElMessage.success(isActive ? '任务已上架' : '任务已下架')
  } catch (err) {
    row.isActive = !isActive // 恢复
    ElMessage.error('状态更新失败')
  } finally {
    row.statusLoading = false
  }
}

const handleDeleteTask = (row: CheckInTask) => {
  ElMessageBox.confirm(`确定删除任务 "${row.name}" 吗?`, '警告', { type: 'warning' })
    .then(async () => {
      if (row.id) {
        await taskApi.deleteTask(row.id)
        ElMessage.success('已删除')
        loadTasks()
      }
    })
    .catch(() => {})
}

// ---------------- 2. 审核逻辑 ----------------

const loadRecords = async () => {
  loading.records = true
  try {
    const params = {
      page: pagination.records.page - 1,
      size: pagination.records.size,
      status: filters.auditStatus || undefined
    }
    const res: any = await taskApi.getRecords(params)
    const data = res.data || res

    if (data.content) {
      recordList.value = data.content
      pagination.records.total = data.totalElements
    } else {
      recordList.value = Array.isArray(data) ? data : []
    }
  } catch (err) {
    ElMessage.error('加载打卡记录失败')
  } finally {
    loading.records = false
  }
}

const handleSelectionChange = (val: UserTaskRecord[]) => {
  selectedRecordIds.value = val.map(item => item.id)
}

const handleSingleAudit = (row: UserTaskRecord, status: 'approved' | 'rejected') => {
  const actionText = status === 'approved' ? '通过' : '拒绝'
  
  // 如果是拒绝，需要填写原因
  if (status === 'rejected') {
    ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then(({ value }) => {
      executeAudit([row.id], status, value)
    }).catch(() => {})
  } else {
    ElMessageBox.confirm(`确定${actionText}该用户的打卡记录吗?`, '审核', {
      confirmButtonText: '确定',
      type: 'warning'
    }).then(() => {
      executeAudit([row.id], status)
    }).catch(() => {})
  }
}

const handleBatchAudit = (status: 'approved' | 'rejected') => {
  if (selectedRecordIds.value.length === 0) return
  const actionText = status === 'approved' ? '通过' : '拒绝'

  if (status === 'rejected') {
    ElMessageBox.prompt('请输入拒绝原因', '批量拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then(({ value }) => {
      executeAudit(selectedRecordIds.value, status, value)
    }).catch(() => {})
  } else {
    ElMessageBox.confirm(`确定批量${actionText}选中的 ${selectedRecordIds.value.length} 条记录吗?`, '批量审核', {
      confirmButtonText: '确定',
      type: 'warning'
    }).then(() => {
      executeAudit(selectedRecordIds.value, status)
    }).catch(() => {})
  }
}

const executeAudit = async (ids: number[], status: 'approved' | 'rejected', reason?: string) => {
  try {
    await taskApi.auditRecord(ids, status, reason)
    ElMessage.success('审核完成')
    loadRecords()
    selectedRecordIds.value = [] // 清空选择
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

// --- 辅助函数 ---
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return map[status] || status
}
</script>

<style scoped>
.task-management-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  min-height: 600px;
}

.page-header {
  margin-bottom: 20px;
}

.tab-action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.left-actions {
  display: flex;
  gap: 10px;
}

.mb-20 {
  margin-bottom: 20px;
}

.table-image {
  width: 80px;
  height: 60px;
  border-radius: 4px;
}

.sub-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 5px;
}

.text-gray {
  color: #ccc;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
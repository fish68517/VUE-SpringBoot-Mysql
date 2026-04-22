<template>
  <section class="page-card" style="padding: 24px;">
    <div class="toolbar">
      <div class="toolbar-title">{{ isInspector ? '我的巡检任务' : '巡检任务管理' }}</div>
      <div class="toolbar-actions">
        <el-button v-if="isAdmin" type="primary" @click="openTaskCreate">新增任务</el-button>
        <el-button @click="loadAll">刷新</el-button>
      </div>
    </div>

    <el-table :data="tasks" border :row-class-name="tableRowClassName">
      <el-table-column prop="taskNo" label="任务编号" width="160" />
      <el-table-column prop="title" label="任务标题" />
      <el-table-column prop="deviceName" label="设备" />
      <el-table-column prop="inspectorName" label="巡检员" />
      <el-table-column prop="plannedDate" label="计划日期" width="120" />
      <el-table-column prop="priority" label="优先级" width="90" />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column label="操作" :width="isAdmin ? 240 : 220">
        <template #default="{ row }">
          <el-button link @click="openItems(row)">查看检查项</el-button>
          <el-button v-if="isAdmin" link type="primary" @click="openTaskEdit(row)">编辑</el-button>
          <el-button v-if="isInspector && row.status !== 'COMPLETED'" link type="success" @click="openSubmit(row)">提交巡检</el-button>
          <el-popconfirm v-if="isAdmin" title="确认删除该任务？" @confirm="removeTask(row.id)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="taskDialogVisible" :title="taskForm.id ? '编辑任务' : '新增任务'" width="560px">
      <el-form :model="taskForm" label-width="90px">
        <el-form-item label="任务标题"><el-input v-model="taskForm.title" /></el-form-item>
        <el-form-item label="巡检设备">
          <el-select v-model="taskForm.deviceId" style="width: 100%">
            <el-option v-for="item in deviceOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检员">
          <el-select v-model="taskForm.inspectorId" style="width: 100%">
            <el-option v-for="item in inspectorOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划日期"><el-date-picker v-model="taskForm.plannedDate" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="taskForm.priority" style="width: 100%">
            <el-option label="高" value="高" />
            <el-option label="中" value="中" />
            <el-option label="低" value="低" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="taskForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTask">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="submitDialogVisible" title="提交巡检结果" width="560px">
      <el-form :model="recordForm" label-width="90px">
        <el-form-item label="检查清单">
          <div class="task-items-wrap">
            <div v-if="!recordForm.items.length" class="task-items-empty">当前任务未绑定巡检标准，请先在“巡检标准管理”中维护对应设备分类的标准项。</div>
            <div v-for="item in recordForm.items" :key="item.id" class="task-item-card">
              <div class="task-item-head">
                <div class="task-item-title">{{ item.sortOrder }}. {{ item.itemName }}</div>
                <div class="task-item-meta">标准值：{{ item.standardValue || '-' }}</div>
              </div>
              <div class="task-item-meta">检查方法：{{ item.checkMethod || '-' }}</div>
              <div class="task-item-meta">异常规则：{{ item.abnormalRule || '-' }}</div>
              <div class="task-item-form">
                <el-input v-model="item.checkedValue" placeholder="填写现场检查值/检查结果" />
                <el-select v-model="item.itemResult" placeholder="选择判定结果">
                  <el-option label="正常" value="正常" />
                  <el-option label="异常" value="异常" />
                  <el-option label="需复检" value="需复检" />
                </el-select>
              </div>
              <el-input v-model="item.itemRemark" type="textarea" :rows="2" placeholder="检查项备注" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="巡检结果">
          <el-select v-model="recordForm.result" style="width: 100%">
            <el-option label="正常" value="正常" />
            <el-option label="异常" value="异常" />
            <el-option label="需复检" value="需复检" />
          </el-select>
        </el-form-item>
        <el-form-item label="现场说明"><el-input v-model="recordForm.statusDescription" type="textarea" rows="4" /></el-form-item>
        <el-form-item label="需要维修">
          <el-switch v-model="recordForm.needRepair" />
        </el-form-item>
        <el-form-item v-if="recordForm.needRepair" label="缺陷等级">
          <el-select v-model="recordForm.defectLevel" style="width: 100%">
            <el-option label="高" value="高" />
            <el-option label="中" value="中" />
            <el-option label="低" value="低" />
          </el-select>
        </el-form-item>
        <el-form-item label="现场图片">
          <el-upload :show-file-list="false" :http-request="uploadRecordImage">
            <el-button>上传图片</el-button>
          </el-upload>
          <el-image v-if="recordForm.imageUrl" :src="recordForm.imageUrl" style="width: 60px; height: 60px; margin-left: 12px; border-radius: 10px;" fit="cover" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitInspectionRecord">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="itemsDialogVisible" title="任务检查项" width="720px">
      <el-table :data="taskItems" border>
        <el-table-column prop="sortOrder" label="序号" width="70" />
        <el-table-column prop="itemName" label="检查项" width="160" />
        <el-table-column prop="standardValue" label="标准值" />
        <el-table-column prop="checkMethod" label="检查方法" />
        <el-table-column prop="abnormalRule" label="异常规则" />
        <el-table-column prop="checkedValue" label="检查值" />
        <el-table-column prop="itemResult" label="结果" width="90" />
      </el-table>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'
import { useAuthStore } from '../stores/auth'
import { uploadImageRequest } from '../utils/upload'

const authStore = useAuthStore()
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')
const isInspector = computed(() => authStore.user?.role === 'INSPECTOR')

const tasks = ref([])
const deviceOptions = ref([])
const inspectorOptions = ref([])
const taskDialogVisible = ref(false)
const submitDialogVisible = ref(false)
const itemsDialogVisible = ref(false)
const currentTaskId = ref(null)
const taskItems = ref([])

const taskForm = reactive(defaultTaskForm())
const recordForm = reactive(defaultRecordForm())

function defaultTaskForm() {
  return {
    id: null,
    title: '',
    deviceId: null,
    inspectorId: null,
    plannedDate: '',
    priority: '中',
    remark: ''
  }
}

function defaultRecordForm() {
  return {
    result: '正常',
    statusDescription: '',
    imageName: '',
    imageUrl: '',
    needRepair: false,
    defectLevel: '中',
    items: []
  }
}

function resetTaskForm() {
  Object.assign(taskForm, defaultTaskForm())
}

function resetRecordForm() {
  Object.assign(recordForm, defaultRecordForm())
}

async function loadAll() {
  tasks.value = await http.get('/api/tasks')
  if (isAdmin.value) {
    deviceOptions.value = await http.get('/api/devices/options')
    inspectorOptions.value = await http.get('/api/users/options', { params: { role: 'INSPECTOR' } })
  }
}

function tableRowClassName({ row }) {
  return row.status === 'COMPLETED' ? 'success-row' : row.priority === '高' ? 'warning-row' : ''
}

function openTaskCreate() {
  resetTaskForm()
  taskDialogVisible.value = true
}

function openTaskEdit(row) {
  Object.assign(taskForm, row)
  taskDialogVisible.value = true
}

async function submitTask() {
  if (taskForm.id) {
    await http.put(`/api/tasks/${taskForm.id}`, taskForm)
  } else {
    await http.post('/api/tasks', taskForm)
  }
  ElMessage.success('保存成功')
  taskDialogVisible.value = false
  loadAll()
}

async function removeTask(id) {
  await http.delete(`/api/tasks/${id}`)
  ElMessage.success('删除成功')
  loadAll()
}

function openSubmit(row) {
  currentTaskId.value = row.id
  resetRecordForm()
  loadTaskItems(row.id, true)
  submitDialogVisible.value = true
}

async function openItems(row) {
  await loadTaskItems(row.id, false)
  itemsDialogVisible.value = true
}

async function loadTaskItems(taskId, bindForm) {
  const items = await http.get(`/api/tasks/${taskId}/items`)
  taskItems.value = items
  if (bindForm) {
    recordForm.items = items.map(item => ({
      itemId: item.id,
      id: item.id,
      sortOrder: item.sortOrder,
      itemName: item.itemName,
      standardValue: item.standardValue,
      checkMethod: item.checkMethod,
      abnormalRule: item.abnormalRule,
      checkedValue: item.checkedValue || '',
      itemResult: item.itemResult || '',
      itemRemark: item.itemRemark || ''
    }))
    applyAutoJudgement()
  }
}

function applyAutoJudgement() {
  const hasAbnormal = recordForm.items.some(item => item.itemResult === '异常' || item.itemResult === '需复检')
  if (hasAbnormal) {
    recordForm.result = '异常'
    recordForm.needRepair = true
    if (!recordForm.defectLevel) {
      recordForm.defectLevel = '中'
    }
  }
}

async function uploadRecordImage(option) {
  const data = await uploadImageRequest(option)
  if (data) {
    recordForm.imageName = data.imageName
    recordForm.imageUrl = data.imageUrl
  }
}

async function submitInspectionRecord() {
  await http.post(`/api/tasks/${currentTaskId.value}/submit`, {
    result: recordForm.result,
    statusDescription: recordForm.statusDescription,
    imageName: recordForm.imageName,
    needRepair: recordForm.needRepair,
    defectLevel: recordForm.defectLevel,
    items: recordForm.items.map(item => ({
      itemId: item.itemId,
      checkedValue: item.checkedValue,
      itemResult: item.itemResult,
      itemRemark: item.itemRemark
    }))
  })
  ElMessage.success('巡检提交成功')
  submitDialogVisible.value = false
  loadAll()
}

onMounted(loadAll)

watch(
  () => recordForm.items.map(item => item.itemResult),
  () => {
    applyAutoJudgement()
  },
  { deep: true }
)
</script>

<style scoped>
.task-items-wrap {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.task-items-empty {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f4f8fd;
  color: #68829d;
  line-height: 1.7;
}

.task-item-card {
  padding: 14px;
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(246, 250, 255, 0.95), rgba(255, 255, 255, 0.96));
  border: 1px solid rgba(201, 215, 232, 0.85);
}

.task-item-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.task-item-title {
  font-weight: 700;
  color: #183452;
}

.task-item-meta {
  margin-bottom: 8px;
  color: #6d859d;
  line-height: 1.6;
}

.task-item-form {
  display: grid;
  grid-template-columns: 1fr 160px;
  gap: 12px;
  margin-bottom: 10px;
}
</style>

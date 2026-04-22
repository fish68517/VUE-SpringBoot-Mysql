<template>
  <section class="page-card page-wrap">
    <div class="toolbar">
      <div class="toolbar-title">异常缺陷</div>
      <div class="toolbar-actions">
        <el-button v-if="isAdmin" type="primary" @click="openCreate">新增缺陷</el-button>
        <el-button @click="loadData">刷新</el-button>
      </div>
    </div>

    <el-table :data="defects" border>
      <el-table-column type="index" width="70" label="#" />
      <el-table-column prop="deviceName" label="设备" />
      <el-table-column prop="reporterName" label="上报人" />
      <el-table-column prop="level" label="等级" width="90" />
      <el-table-column prop="status" label="状态" width="130" />
      <el-table-column prop="reportedAt" label="上报时间" width="180" />
      <el-table-column prop="description" label="问题描述" min-width="260" />
      <el-table-column label="图片" width="90">
        <template #default="{ row }">
          <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 46px; height: 46px; border-radius: 10px;" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column v-if="isAdmin" label="操作" width="250">
        <template #default="{ row }">
          <el-button v-if="!hasRepairOrder(row.id)" link type="success" @click="openAssign(row)">派维修单</el-button>
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除该缺陷记录？" @confirm="removeDefect(row.id)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="editDialogVisible" :title="form.id ? '编辑缺陷' : '新增缺陷'" width="620px">
      <el-form :model="form" label-width="96px">
        <el-form-item label="巡检记录">
          <el-select v-model="form.recordId" :disabled="!!form.id" filterable style="width: 100%">
            <el-option v-for="item in availableRecords" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="缺陷等级">
          <el-select v-model="form.level" style="width: 100%">
            <el-option label="高" value="高" />
            <el-option label="中" value="中" />
            <el-option label="低" value="低" />
          </el-select>
        </el-form-item>
        <el-form-item label="缺陷状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="已上报" value="REPORTED" />
            <el-option label="已派单" value="ASSIGNED" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="缺陷描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="缺陷图片">
          <el-upload :show-file-list="false" :http-request="uploadDefectImage">
            <el-button>上传图片</el-button>
          </el-upload>
          <el-image v-if="form.imageUrl" :src="form.imageUrl" style="width: 60px; height: 60px; margin-left: 12px; border-radius: 10px;" fit="cover" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDefect">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignDialogVisible" title="创建维修工单" width="520px">
      <el-form :model="assignForm" label-width="90px">
        <el-form-item label="维护员">
          <el-select v-model="assignForm.maintainerId" style="width: 100%">
            <el-option v-for="item in maintainerOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assignForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确认派单</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'
import { useAuthStore } from '../stores/auth'
import { uploadImageRequest } from '../utils/upload'

const authStore = useAuthStore()
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

const defects = ref([])
const records = ref([])
const orders = ref([])
const maintainerOptions = ref([])

const editDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const currentDefectId = ref(null)

const form = reactive(defaultForm())
const assignForm = reactive({
  maintainerId: null,
  remark: ''
})

function defaultForm() {
  return {
    id: null,
    recordId: null,
    level: '中',
    status: 'REPORTED',
    description: '',
    imageName: '',
    imageUrl: ''
  }
}

const availableRecords = computed(() => {
  const usedRecordIds = new Set(defects.value.filter(item => item.id !== form.id).map(item => item.recordId))
  return records.value
    .filter(item => !usedRecordIds.has(item.id) || item.id === form.recordId)
    .map(item => ({
      id: item.id,
      label: `${item.taskNo} / ${item.deviceName} / ${item.inspectorName}`
    }))
})

function resetForm() {
  Object.assign(form, defaultForm())
}

function hasRepairOrder(defectId) {
  return orders.value.some(item => item.defectId === defectId)
}

async function loadData() {
  defects.value = await http.get('/api/defects')
  if (isAdmin.value) {
    records.value = await http.get('/api/records')
    orders.value = await http.get('/api/repair-orders')
    maintainerOptions.value = await http.get('/api/users/options', { params: { role: 'MAINTAINER' } })
  }
}

function openCreate() {
  resetForm()
  editDialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, {
    id: row.id,
    recordId: row.recordId,
    level: row.level,
    status: row.status,
    description: row.description,
    imageName: row.imageName || '',
    imageUrl: row.imageUrl || ''
  })
  editDialogVisible.value = true
}

async function uploadDefectImage(option) {
  const data = await uploadImageRequest(option)
  if (data) {
    form.imageName = data.imageName
    form.imageUrl = data.imageUrl
  }
}

async function submitDefect() {
  const payload = {
    recordId: form.recordId,
    level: form.level,
    status: form.status,
    description: form.description,
    imageName: form.imageName
  }
  if (form.id) {
    await http.put(`/api/defects/${form.id}`, payload)
  } else {
    await http.post('/api/defects', payload)
  }
  ElMessage.success('保存成功')
  editDialogVisible.value = false
  loadData()
}

async function removeDefect(id) {
  await http.delete(`/api/defects/${id}`)
  ElMessage.success('删除成功')
  loadData()
}

function openAssign(row) {
  currentDefectId.value = row.id
  assignForm.maintainerId = null
  assignForm.remark = ''
  assignDialogVisible.value = true
}

async function submitAssign() {
  await http.post(`/api/defects/${currentDefectId.value}/repair-orders`, assignForm)
  ElMessage.success('派单成功')
  assignDialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-wrap {
  padding: 28px;
}
</style>

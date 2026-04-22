<template>
  <section class="page-card page-wrap">
    <div class="toolbar">
      <div class="toolbar-title">{{ isMaintainer ? '我的维修工单' : '维修工单' }}</div>
      <div class="toolbar-actions">
        <el-button v-if="isAdmin" type="primary" @click="openCreate">新增工单</el-button>
        <el-button @click="loadData">刷新</el-button>
      </div>
    </div>

    <el-table :data="orders" border>
      <el-table-column prop="orderNo" label="工单编号" width="180" />
      <el-table-column prop="deviceName" label="设备" />
      <el-table-column prop="maintainerName" label="维护员" />
      <el-table-column prop="status" label="状态" width="140" />
      <el-table-column prop="finishedAt" label="完成时间" width="180" />
      <el-table-column prop="measures" label="处理措施" min-width="180" />
      <el-table-column prop="result" label="处理结果" min-width="180" />
      <el-table-column label="图片" width="90">
        <template #default="{ row }">
          <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 46px; height: 46px; border-radius: 10px;" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button v-if="isMaintainer && row.status !== 'CLOSED' && row.status !== 'WAITING_REVIEW'" link type="primary" @click="openProcess(row)">处理工单</el-button>
          <el-button v-if="isAdmin" link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="isAdmin && row.status === 'WAITING_REVIEW'" link type="success" @click="closeOrder(row.id)">闭环确认</el-button>
          <el-popconfirm v-if="isAdmin" title="确认删除该工单？" @confirm="removeOrder(row.id)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="editDialogVisible" :title="editForm.id ? '编辑维修工单' : '新增维修工单'" width="640px">
      <el-form :model="editForm" label-width="96px">
        <el-form-item label="缺陷记录">
          <el-select v-model="editForm.defectId" filterable style="width: 100%">
            <el-option v-for="item in defectOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="维护员">
          <el-select v-model="editForm.maintainerId" style="width: 100%">
            <el-option v-for="item in maintainerOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="待复核" value="WAITING_REVIEW" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理措施"><el-input v-model="editForm.measures" type="textarea" rows="3" /></el-form-item>
        <el-form-item label="处理结果"><el-input v-model="editForm.result" type="textarea" rows="3" /></el-form-item>
        <el-form-item label="处理图片">
          <el-upload :show-file-list="false" :http-request="uploadOrderImage">
            <el-button>上传图片</el-button>
          </el-upload>
          <el-image v-if="editForm.imageUrl" :src="editForm.imageUrl" style="width: 60px; height: 60px; margin-left: 12px; border-radius: 10px;" fit="cover" />
        </el-form-item>
        <el-form-item label="完成时间">
          <el-date-picker v-model="editForm.finishedAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="editForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrder">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="processDialogVisible" title="处理维修工单" width="580px">
      <el-form :model="processForm" label-width="96px">
        <el-form-item label="处理措施"><el-input v-model="processForm.measures" type="textarea" rows="3" /></el-form-item>
        <el-form-item label="处理结果"><el-input v-model="processForm.result" type="textarea" rows="3" /></el-form-item>
        <el-form-item label="处理图片">
          <el-upload :show-file-list="false" :http-request="uploadProcessImage">
            <el-button>上传图片</el-button>
          </el-upload>
          <el-image v-if="processForm.imageUrl" :src="processForm.imageUrl" style="width: 60px; height: 60px; margin-left: 12px; border-radius: 10px;" fit="cover" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="processForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProcess">提交处理</el-button>
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
const isMaintainer = computed(() => authStore.user?.role === 'MAINTAINER')
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

const orders = ref([])
const defects = ref([])
const maintainerOptions = ref([])
const editDialogVisible = ref(false)
const processDialogVisible = ref(false)
const currentOrderId = ref(null)

const editForm = reactive(defaultEditForm())
const processForm = reactive(defaultProcessForm())

function defaultEditForm() {
  return {
    id: null,
    defectId: null,
    maintainerId: null,
    status: 'PENDING',
    measures: '',
    result: '',
    imageName: '',
    imageUrl: '',
    finishedAt: '',
    remark: ''
  }
}

function defaultProcessForm() {
  return {
    measures: '',
    result: '',
    imageName: '',
    imageUrl: '',
    remark: ''
  }
}

const defectOptions = computed(() => {
  const usedDefectIds = new Set(orders.value.filter(item => item.id !== editForm.id).map(item => item.defectId))
  return defects.value
    .filter(item => !usedDefectIds.has(item.id) || item.id === editForm.defectId)
    .map(item => ({
      id: item.id,
      label: `${item.deviceName} / ${item.description}`
    }))
})

function resetEditForm() {
  Object.assign(editForm, defaultEditForm())
}

function resetProcessForm() {
  Object.assign(processForm, defaultProcessForm())
}

async function loadData() {
  orders.value = await http.get('/api/repair-orders')
  if (isAdmin.value) {
    defects.value = await http.get('/api/defects')
    maintainerOptions.value = await http.get('/api/users/options', { params: { role: 'MAINTAINER' } })
  }
}

function openCreate() {
  resetEditForm()
  editDialogVisible.value = true
}

function openEdit(row) {
  Object.assign(editForm, {
    id: row.id,
    defectId: row.defectId,
    maintainerId: row.maintainerId,
    status: row.status,
    measures: row.measures || '',
    result: row.result || '',
    imageName: row.imageName || '',
    imageUrl: row.imageUrl || '',
    finishedAt: row.finishedAt || '',
    remark: row.remark || ''
  })
  editDialogVisible.value = true
}

function openProcess(row) {
  currentOrderId.value = row.id
  resetProcessForm()
  processDialogVisible.value = true
}

async function uploadOrderImage(option) {
  const data = await uploadImageRequest(option)
  if (data) {
    editForm.imageName = data.imageName
    editForm.imageUrl = data.imageUrl
  }
}

async function uploadProcessImage(option) {
  const data = await uploadImageRequest(option)
  if (data) {
    processForm.imageName = data.imageName
    processForm.imageUrl = data.imageUrl
  }
}

async function submitOrder() {
  const payload = {
    defectId: editForm.defectId,
    maintainerId: editForm.maintainerId,
    status: editForm.status,
    measures: editForm.measures,
    result: editForm.result,
    imageName: editForm.imageName,
    finishedAt: editForm.finishedAt || null,
    remark: editForm.remark
  }
  if (editForm.id) {
    await http.put(`/api/repair-orders/${editForm.id}`, payload)
  } else {
    await http.post('/api/repair-orders', payload)
  }
  ElMessage.success('保存成功')
  editDialogVisible.value = false
  loadData()
}

async function submitProcess() {
  await http.put(`/api/repair-orders/${currentOrderId.value}/process`, {
    measures: processForm.measures,
    result: processForm.result,
    imageName: processForm.imageName,
    remark: processForm.remark
  })
  ElMessage.success('处理已提交')
  processDialogVisible.value = false
  loadData()
}

async function closeOrder(id) {
  await http.put(`/api/repair-orders/${id}/close`)
  ElMessage.success('闭环成功')
  loadData()
}

async function removeOrder(id) {
  await http.delete(`/api/repair-orders/${id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-wrap {
  padding: 28px;
}
</style>

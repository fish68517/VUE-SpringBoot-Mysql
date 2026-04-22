<template>
  <div class="grid-2">
    <section class="page-card" style="padding: 24px;">
      <div class="toolbar">
        <div class="toolbar-title">设备分类</div>
        <div class="toolbar-actions">
          <el-button type="primary" @click="openCategoryCreate">新增分类</el-button>
        </div>
      </div>

      <el-table :data="categories" border>
        <el-table-column prop="code" label="分类编码" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="description" label="说明" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="openCategoryEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该分类？" @confirm="removeCategory(row.id)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <section class="page-card" style="padding: 24px;">
      <div class="toolbar">
        <div class="toolbar-title">设备档案</div>
        <div class="toolbar-actions">
          <el-button type="primary" @click="openDeviceCreate">新增设备</el-button>
          <el-button @click="loadAll">刷新</el-button>
        </div>
      </div>

      <el-table :data="devices" border>
        <el-table-column prop="deviceCode" label="设备编号" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 42px; height: 42px; border-radius: 8px;" fit="cover" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDeviceEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该设备？" @confirm="removeDevice(row.id)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="categoryDialogVisible" :title="categoryForm.id ? '编辑分类' : '新增分类'" width="520px">
      <el-form :model="categoryForm" label-width="90px">
        <el-form-item label="分类编码"><el-input v-model="categoryForm.code" /></el-form-item>
        <el-form-item label="分类名称"><el-input v-model="categoryForm.name" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="categoryForm.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCategory">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="deviceDialogVisible" :title="deviceForm.id ? '编辑设备' : '新增设备'" width="560px">
      <el-form :model="deviceForm" label-width="90px">
        <el-form-item label="设备分类">
          <el-select v-model="deviceForm.categoryId" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备编号"><el-input v-model="deviceForm.deviceCode" /></el-form-item>
        <el-form-item label="设备名称"><el-input v-model="deviceForm.deviceName" /></el-form-item>
        <el-form-item label="安装位置"><el-input v-model="deviceForm.location" /></el-form-item>
        <el-form-item label="设备状态"><el-input v-model="deviceForm.status" /></el-form-item>
        <el-form-item label="设备图片">
          <el-upload :show-file-list="false" :http-request="uploadDeviceImage">
            <el-button>上传图片</el-button>
          </el-upload>
          <el-image v-if="deviceForm.imageUrl" :src="deviceForm.imageUrl" style="width: 60px; height: 60px; margin-left: 12px; border-radius: 10px;" fit="cover" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="deviceForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deviceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDevice">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'
import { uploadImageRequest } from '../utils/upload'

const categories = ref([])
const devices = ref([])

const categoryDialogVisible = ref(false)
const deviceDialogVisible = ref(false)

const categoryForm = reactive(defaultCategoryForm())
const deviceForm = reactive(defaultDeviceForm())

function defaultCategoryForm() {
  return { id: null, code: '', name: '', description: '' }
}

function defaultDeviceForm() {
  return {
    id: null,
    categoryId: null,
    deviceCode: '',
    deviceName: '',
    location: '',
    status: '运行中',
    imageName: '',
    imageUrl: '',
    remark: ''
  }
}

function resetCategoryForm() {
  Object.assign(categoryForm, defaultCategoryForm())
}

function resetDeviceForm() {
  Object.assign(deviceForm, defaultDeviceForm())
}

async function loadAll() {
  categories.value = await http.get('/api/device-categories')
  devices.value = await http.get('/api/devices')
}

function openCategoryCreate() {
  resetCategoryForm()
  categoryDialogVisible.value = true
}

function openCategoryEdit(row) {
  Object.assign(categoryForm, row)
  categoryDialogVisible.value = true
}

async function submitCategory() {
  if (categoryForm.id) {
    await http.put(`/api/device-categories/${categoryForm.id}`, categoryForm)
  } else {
    await http.post('/api/device-categories', categoryForm)
  }
  ElMessage.success('保存成功')
  categoryDialogVisible.value = false
  loadAll()
}

async function removeCategory(id) {
  await http.delete(`/api/device-categories/${id}`)
  ElMessage.success('删除成功')
  loadAll()
}

function openDeviceCreate() {
  resetDeviceForm()
  deviceDialogVisible.value = true
}

function openDeviceEdit(row) {
  Object.assign(deviceForm, row)
  deviceDialogVisible.value = true
}

async function uploadDeviceImage(option) {
  const data = await uploadImageRequest(option)
  if (data) {
    deviceForm.imageName = data.imageName
    deviceForm.imageUrl = data.imageUrl
  }
}

async function submitDevice() {
  const payload = {
    categoryId: deviceForm.categoryId,
    deviceCode: deviceForm.deviceCode,
    deviceName: deviceForm.deviceName,
    location: deviceForm.location,
    status: deviceForm.status,
    imageName: deviceForm.imageName,
    remark: deviceForm.remark
  }
  if (deviceForm.id) {
    await http.put(`/api/devices/${deviceForm.id}`, payload)
  } else {
    await http.post('/api/devices', payload)
  }
  ElMessage.success('保存成功')
  deviceDialogVisible.value = false
  loadAll()
}

async function removeDevice(id) {
  await http.delete(`/api/devices/${id}`)
  ElMessage.success('删除成功')
  loadAll()
}

onMounted(loadAll)
</script>

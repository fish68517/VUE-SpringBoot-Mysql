<template>
  <section class="page-card" style="padding: 24px;">
    <div class="toolbar">
      <div class="toolbar-title">巡检标准管理</div>
      <div class="toolbar-actions">
        <el-button type="primary" @click="openCreate">新增标准</el-button>
        <el-button @click="loadData">刷新</el-button>
      </div>
    </div>

    <el-table :data="standards" border>
      <el-table-column type="index" width="60" label="#" />
      <el-table-column prop="categoryName" label="设备分类" width="120" />
      <el-table-column prop="itemName" label="巡检项目" width="160" />
      <el-table-column prop="standardValue" label="标准值" />
      <el-table-column prop="checkMethod" label="检查方法" />
      <el-table-column prop="cycleDays" label="周期(天)" width="100" />
      <el-table-column prop="abnormalRule" label="异常判定规则" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除该标准？" @confirm="removeStandard(row.id)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑巡检标准' : '新增巡检标准'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="设备分类">
          <el-select v-model="form.categoryId" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检项目"><el-input v-model="form.itemName" /></el-form-item>
        <el-form-item label="标准值"><el-input v-model="form.standardValue" /></el-form-item>
        <el-form-item label="检查方法"><el-input v-model="form.checkMethod" /></el-form-item>
        <el-form-item label="周期(天)"><el-input-number v-model="form.cycleDays" :min="1" style="width: 100%" /></el-form-item>
        <el-form-item label="异常规则"><el-input v-model="form.abnormalRule" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const standards = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const form = reactive(defaultForm())

function defaultForm() {
  return {
    id: null,
    categoryId: null,
    itemName: '',
    standardValue: '',
    checkMethod: '',
    cycleDays: 7,
    abnormalRule: '',
    remark: ''
  }
}

function resetForm() {
  Object.assign(form, defaultForm())
}

async function loadData() {
  standards.value = await http.get('/api/standards')
  categories.value = await http.get('/api/device-categories')
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submitForm() {
  const payload = { ...form }
  if (form.id) {
    await http.put(`/api/standards/${form.id}`, payload)
  } else {
    await http.post('/api/standards', payload)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function removeStandard(id) {
  await http.delete(`/api/standards/${id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

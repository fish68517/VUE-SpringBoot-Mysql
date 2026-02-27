<template>
  <div class="admin-page-container">
    <el-card shadow="never" class="box-card">
      <template #header>
        <div class="card-header">
          <span>预警信息管理</span>
          <el-button type="danger" icon="Plus" @click="handleAdd">发布紧急预警</el-button>
        </div>
      </template>

      <el-table :data="warnings" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="warningType" label="预警类型" width="120">
          <template #default="{ row }">
            <el-tag type="danger" effect="dark">{{ row.warningType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="region" label="受影响地区" width="150" />
        <el-table-column prop="severity" label="等级" width="100" />
        <el-table-column prop="description" label="预警描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '生效中' : '已过期' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改预警信息' : '发布新的气象预警'" width="550px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="预警类型" required v-if="!isEdit">
          <el-select v-model="form.warningType" placeholder="如：暴雨、冰雹" style="width: 100%;">
            <el-option label="暴雨" value="暴雨" />
            <el-option label="冰雹" value="冰雹" />
            <el-option label="大风" value="大风" />
            <el-option label="高温" value="高温" />
            <el-option label="霜冻" value="霜冻" />
          </el-select>
        </el-form-item>
        <el-form-item label="受灾地区" required v-if="!isEdit">
          <el-input v-model="form.region" placeholder="请输入受影响的省市区" />
        </el-form-item>
        <el-form-item label="严重等级" required v-if="!isEdit">
          <el-select v-model="form.severity" style="width: 100%;">
            <el-option label="低 (LOW)" value="LOW" />
            <el-option label="中 (MEDIUM)" value="MEDIUM" />
            <el-option label="高 (HIGH)" value="HIGH" />
            <el-option label="极高 (CRITICAL)" value="CRITICAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" required v-if="!isEdit">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预警描述" required>
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入具体的防范指南..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="danger" @click="submitForm" :loading="submitLoading">确定发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { warningAPI } from '@/api/warning'

const loading = ref(false)
const submitLoading = ref(false)
const warnings = ref([])

// 弹窗控制
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const form = ref({
  warningType: '',
  region: '',
  severity: 'MEDIUM',
  startTime: '',
  endTime: '',
  description: ''
})

// 获取数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await warningAPI.getWarnings()
    // 假设后端返回按时间倒序的数据
    warnings.value = res.data || res || []
  } catch (error) {
    ElMessage.error('获取预警列表失败')
  } finally {
    loading.value = false
  }
}

// 新增预警
const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  form.value = { warningType: '', region: '', severity: 'MEDIUM', startTime: '', endTime: '', description: '' }
  dialogVisible.value = true
}

// 编辑预警
const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  form.value = {
    warningType: row.warningType,
    region: row.region,
    severity: row.severity,
    startTime: row.startTime,
    endTime: row.endTime,
    description: row.description
  }
  dialogVisible.value = true
}

// 提交表单 (根据后端逻辑，编辑只能修改描述和结束时间)
const submitForm = async () => {
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await warningAPI.updateWarning(currentId.value, {
        description: form.value.description,
        endTime: form.value.endTime
      })
      ElMessage.success('预警修改成功')
    } else {
      await warningAPI.createWarning(form.value)
      ElMessage.success('紧急预警发布成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(isEdit.value ? '修改失败' : '发布失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除预警
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条预警记录吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await warningAPI.deleteWarning(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-page-container {
  padding: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}
</style>
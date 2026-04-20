<template>
  <div class="admin-page">
    <div class="page-head">
      <div>
        <h2>旅行计划管理</h2>
        <p>查看和修改所有用户的出行计划。</p>
      </div>
      <el-button @click="loadPlans">刷新</el-button>
    </div>

    <el-table :data="plans" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="destination" label="目的地" min-width="140" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="budget" label="预算" width="120" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="removePlan(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="编辑旅行计划" width="720px">
      <el-form :model="form" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="目的地">
          <el-input v-model="form.destination" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预算">
          <el-input-number v-model="form.budget" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminService } from '../services/adminService'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const currentId = ref(null)
const plans = ref([])
const form = reactive({
  title: '',
  destination: '',
  startDate: '',
  endDate: '',
  budget: 0,
  description: ''
})

const loadPlans = async () => {
  loading.value = true
  try {
    const response = await adminService.getPlans()
    plans.value = response.data.content || []
  } catch (error) {
    ElMessage.error(error.message || '加载旅行计划失败')
  } finally {
    loading.value = false
  }
}

const openEditDialog = (row) => {
  currentId.value = row.id
  form.title = row.title
  form.destination = row.destination
  form.startDate = row.startDate
  form.endDate = row.endDate
  form.budget = row.budget || 0
  form.description = row.description || ''
  dialogVisible.value = true
}

const submitForm = async () => {
  saving.value = true
  try {
    await adminService.updatePlan(currentId.value, { ...form })
    ElMessage.success('旅行计划更新成功')
    dialogVisible.value = false
    await loadPlans()
  } catch (error) {
    ElMessage.error(error.message || '更新旅行计划失败')
  } finally {
    saving.value = false
  }
}

const removePlan = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除旅行计划「${row.title}」吗？`, '删除确认', { type: 'warning' })
    await adminService.deletePlan(row.id)
    ElMessage.success('旅行计划删除成功')
    await loadPlans()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除旅行计划失败')
    }
  }
}

onMounted(loadPlans)
</script>

<style scoped>
.admin-page {
  max-width: 1320px;
  margin: 0 auto;
}

.page-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.page-head h2 {
  margin: 0 0 8px;
  color: #183b56;
}

.page-head p {
  margin: 0;
  color: #6b7a88;
}
</style>

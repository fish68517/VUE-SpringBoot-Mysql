<template>
  <div class="warnings-container">
    <Card title="预警信息管理">
      <el-row :gutter="20" class="filter-row">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
            v-if="false"
            v-model="filters.region"
            placeholder="请输入地区"
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
            v-model="filters.severity"
            placeholder="选择严重程度"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" />
            <el-option label="严重" value="CRITICAL" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
            v-model="filters.status"
            placeholder="选择状态"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="过期" value="EXPIRED" />
            <el-option label="取消" value="CANCELLED" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="showCreateDialog = true">新增预警</el-button>
        </el-col>
      </el-row>

      <Table
        :data="warnings"
        :columns="columns"
        :show-pagination="true"
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        @edit="handleEdit"
        @delete="handleDelete"
        @page-change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      />
    </Card>

    <Modal
      v-model="showCreateDialog"
      title="新增预警"
      @confirm="handleCreateWarning"
    >
      <el-form :model="warningForm" label-width="100px">
        <el-form-item label="预警类型">
          <el-input v-model="warningForm.warningType" />
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="warningForm.region" />
        </el-form-item>
        <el-form-item label="严重程度">
          <el-select v-model="warningForm.severity">
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" />
            <el-option label="严重" value="CRITICAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="warningForm.description" type="textarea" />
        </el-form-item>
      </el-form>
    </Modal>


    <el-dialog v-model="editDialogVisible" title="编辑预警信息" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="预警描述">
          <el-input 
            v-model="editForm.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入预警描述补充" 
          />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker
            v-model="editForm.endTime"
            type="datetime"
            placeholder="选择新的结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">确定更新</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { warningAPI } from '@/api/warning'
import Card from '@/components/common/Card.vue'
import Table from '@/components/common/Table.vue'
import Modal from '@/components/common/Modal.vue'

const filters = ref({
  region: '',
  severity: '',
  status: '',
})

const warnings = ref([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0,
})

const showCreateDialog = ref(false)
const warningForm = ref({
  warningType: '',
  region: '',
  severity: 'medium',
  description: '',
})

const columns = [
  { prop: 'warningType', label: '预警类型' },
  { prop: 'region', label: '地区' },
  { prop: 'severity', label: '严重程度' },
  { prop: 'description', label: '描述' },
  { prop: 'status', label: '状态' },
]

const handleSearch = async () => {
  try {
    const response = await warningAPI.getWarnings({
      region: filters.value.region,
      severity: filters.value.severity,
      status: filters.value.status,
      page: pagination.value.current,
      size: pagination.value.size,
    })
    warnings.value = response || []
    pagination.value.total = response || 0
  } catch (error) {
    ElMessage.error('获取预警信息失败')
  }
}

const handlePageChange = (page) => {
  pagination.value.current = page
  handleSearch()
}

const handlePageSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  handleSearch()
}

// 1. 定义控制弹窗显示和表单数据的响应式变量
const editDialogVisible = ref(false)
const editForm = ref({
  id: null,
  description: '',
  endTime: ''
})

// 2. 点击编辑按钮，回显数据并打开弹窗
const handleEdit = (row) => {
  editForm.value = {
    id: row.id,
    description: row.description || '',
    endTime: row.endTime || ''
  }
  editDialogVisible.value = true
}

// 3. 提交编辑数据给后端
const submitEdit = async () => {
  if (!editForm.value.endTime) {
    ElMessage.warning('结束时间不能为空')
    return
  }
  
  try {
    // 调用 warningAPI.updateWarning (已在 warning.js 中定义)
    await warningAPI.updateWarning(editForm.value.id, {
      description: editForm.value.description,
      endTime: editForm.value.endTime
    })
    
    ElMessage.success('预警信息更新成功')
    editDialogVisible.value = false // 关闭弹窗
    handleSearch() // 重新拉取列表数据刷新页面
  } catch (error) {
    console.error(error)
    ElMessage.error('更新预警信息失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该预警吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await warningAPI.deleteWarning(row.id)
        ElMessage.success('删除成功')
        handleSearch()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

const handleCreateWarning = async () => {
  try {
    await warningAPI.createWarning(warningForm.value)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    warningForm.value = {
      warningType: '',
      region: '',
      severity: 'medium',
      description: '',
    }
    handleSearch()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.warnings-container {
  padding: 20px;
}

.filter-row {
  margin-bottom: 20px;
}
</style>

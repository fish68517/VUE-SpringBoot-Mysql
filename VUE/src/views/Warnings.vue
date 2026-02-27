<template>
  <div class="warnings-container">
    <Card title="预警信息管理">
      <el-row :gutter="20" class="filter-row">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
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
            <el-option label="活跃" value="active" />
            <el-option label="过期" value="expired" />
            <el-option label="取消" value="cancelled" />
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

const handleEdit = (row) => {
  ElMessage.info('编辑功能开发中')
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

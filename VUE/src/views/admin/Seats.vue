<template>
  <div class="admin-seats">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>座位管理</span>
          <el-button type="primary" @click="showAddDialog">添加座位</el-button>
        </div>
      </template>

      <el-tabs v-model="activeArea">
        <el-tab-pane v-for="area in ['A', 'B', 'C', 'D']" :key="area" :label="area + '区'" :name="area">
          <el-table :data="seatsByArea[area] || []" v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="seatNo" label="座位编号" width="120" />
            <el-table-column prop="area" label="区域" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '可用' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-switch
                  v-model="row.status"
                  :active-value="1"
                  :inactive-value="0"
                  @change="handleStatusChange(row)"
                  style="margin-right: 10px"
                />
                <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="addDialogVisible" title="添加座位" width="400px">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="座位编号">
          <el-input v-model="addForm.seatNo" placeholder="如: 01" />
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="addForm.area" placeholder="选择区域">
            <el-option label="A区" value="A" />
            <el-option label="B区" value="B" />
            <el-option label="C区" value="C" />
            <el-option label="D区" value="D" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd" :loading="adding">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getSeatList, updateSeatStatus, addSeat, deleteSeat } from '../../api/seat'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const activeArea = ref('A')
const seatsByArea = ref({})
const addDialogVisible = ref(false)
const adding = ref(false)

const addForm = reactive({
  seatNo: '',
  area: 'A'
})

const loadSeats = async () => {
  loading.value = true
  try {
    const res = await getSeatList()
    const grouped = { A: [], B: [], C: [], D: [] }
    res.data.forEach(seat => {
      if (grouped[seat.area]) {
        grouped[seat.area].push(seat)
      }
    })
    seatsByArea.value = grouped
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row) => {
  try {
    await updateSeatStatus(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已启用' : '已停用')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除这个座位吗？', '提示', { type: 'warning' })
  await deleteSeat(row.id)
  ElMessage.success('删除成功')
  loadSeats()
}

const showAddDialog = () => {
  addForm.seatNo = ''
  addForm.area = activeArea.value
  addDialogVisible.value = true
}

const handleAdd = async () => {
  if (!addForm.seatNo || !addForm.area) {
    ElMessage.warning('请填写完整信息')
    return
  }
  adding.value = true
  try {
    await addSeat(addForm)
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    loadSeats()
  } finally {
    adding.value = false
  }
}

onMounted(() => {
  loadSeats()
})
</script>

<style scoped>
.admin-seats {
  max-width: 1000px;
  margin: 0 auto;
  text-align: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

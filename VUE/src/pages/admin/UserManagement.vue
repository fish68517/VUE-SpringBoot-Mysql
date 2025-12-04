<template>
  <div class="user-management-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索手机号 / 姓名" 
          style="width: 240px; margin-right: 12px;" 
          clearable 
          @clear="loadUsers"
          @keyup.enter="loadUsers"
        >
          <template #append>
            <el-button @click="loadUsers">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-button type="success" @click="handleExport">
          <el-icon><Download /></el-icon> 导出Excel
        </el-button>
      </div>
    </div>

    <el-table :data="userList" border style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" sortable />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" show-overflow-tooltip />
      <el-table-column label="实名信息" min-width="180">
        <template #default="{ row }">
          <div v-if="row.realName">
            {{ row.realName }} 
            <span class="id-number">({{ row.idNumber || '未获取' }})</span>
          </div>
          <el-tag v-else type="info" size="small">未实名</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="points" label="积分" width="100" sortable>
        <template #default="{ row }">
          <span class="points-text">{{ row.points }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
            {{ row.status === 'active' ? '正常' : '封禁' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="170">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetailDialog(row)">详情</el-button>
          <el-button size="small" type="warning" @click="openPointsDialog(row)">积分</el-button>
          
          <el-popconfirm 
            v-if="row.status === 'active'"
            title="确定要封禁该用户吗？"
            @confirm="handleBanUser(row)"
          >
            <template #reference>
              <el-button size="small" type="danger">封禁</el-button>
            </template>
          </el-popconfirm>
          
          <el-popconfirm 
            v-else
            title="确定要解封该用户吗？"
            @confirm="handleUnbanUser(row)"
          >
            <template #reference>
              <el-button size="small" type="success">解封</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadUsers"
        @current-change="loadUsers"
      />
    </div>

    <el-dialog v-model="dialog.detail" title="用户详情" width="600px">
      <div v-if="currentUser" class="user-detail-content">
        <el-descriptions title="基础信息" :column="2" border>
          <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUser.status === 'active' ? 'success' : 'danger'">
              {{ currentUser.status === 'active' ? '正常' : '封禁' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatDateTime(currentUser.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{ formatDateTime(currentUser.lastLoginTime) }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="实名信息" :column="1" border class="mt-20">
          <el-descriptions-item label="真实姓名">{{ currentUser.realName || '未认证' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ currentUser.idNumber || '未认证' }}</el-descriptions-item>
        </el-descriptions>

        <div class="mt-20">
          <h3>近期登录日志</h3>
          <el-table :data="loginLogs" border size="small" height="200px">
            <el-table-column prop="loginTime" label="登录时间" width="160" />
            <el-table-column prop="ipAddress" label="IP地址" width="140" />
            <el-table-column prop="device" label="设备信息" show-overflow-tooltip />
          </el-table>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="dialog.points" title="调整积分" width="400px">
      <el-form :model="pointsForm" label-position="top">
        <el-form-item label="当前用户">
          <strong>{{ currentUser?.nickname }} ({{ currentUser?.phone }})</strong>
          <span class="ml-10">当前积分: <span class="points-text">{{ currentUser?.points }}</span></span>
        </el-form-item>
        <el-form-item label="调整方式">
          <el-radio-group v-model="pointsForm.type">
            <el-radio label="add">增加积分</el-radio>
            <el-radio label="reduce">扣除积分</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整数量" required>
          <el-input-number v-model="pointsForm.amount" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="调整原因" required>
          <el-input 
            v-model="pointsForm.reason" 
            type="textarea" 
            placeholder="请输入调整原因（如：系统补偿、违规扣除）" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleFreezePoints" type="warning" plain>冻结积分功能</el-button>
          <div>
            <el-button @click="dialog.points = false">取消</el-button>
            <el-button type="primary" @click="submitPointsAdjustment" :loading="submitting">确认调整</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Download } from '@element-plus/icons-vue'
import { adminUserApi, type AdminUser } from '@/api/adminUser'
import { formatDate } from '@/utils/date'

// --- 状态定义 ---
const loading = ref(false)
const submitting = ref(false)
const searchKeyword = ref('')
const userList = ref<AdminUser[]>([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialog = reactive({
  detail: false,
  points: false
})

const currentUser = ref<AdminUser | null>(null)
const loginLogs = ref<any[]>([])

const pointsForm = reactive({
  type: 'add',
  amount: 0,
  reason: ''
})

// --- 初始化 ---
onMounted(() => {
  loadUsers()
})

// --- 核心逻辑 ---

// 1. 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      keyword: searchKeyword.value || undefined
    }
    const res: any = await adminUserApi.getUsers(params)
    const data = res.data || res
    
    if (data.content) {
      userList.value = data.content
      pagination.total = data.totalElements
    } else {
      userList.value = Array.isArray(data) ? data : []
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 2. 导出用户
const handleExport = async () => {
  try {
    const blob = await adminUserApi.exportUsers({ keyword: searchKeyword.value })
    const url = window.URL.createObjectURL(new Blob([blob as any]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `users_export_${Date.now()}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('导出开始')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 3. 用户详情
const openDetailDialog = async (row: AdminUser) => {
  currentUser.value = row // 先展示基础数据
  dialog.detail = true
  
  // 获取完整详情（可能包含脱敏数据的完整版）
  try {
    const res: any = await adminUserApi.getUserDetail(row.id)
    currentUser.value = res.data || res
    
    // 获取登录日志
    const logsRes: any = await adminUserApi.getLoginLogs(row.id, { size: 10 })
    loginLogs.value = (logsRes.data || logsRes).content || []
  } catch (error) {
    console.error('获取详情失败')
  }
}

// 4. 封禁/解封
const handleBanUser = async (row: AdminUser) => {
  try {
    await adminUserApi.banUser(row.id)
    ElMessage.success('用户已封禁')
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleUnbanUser = async (row: AdminUser) => {
  try {
    await adminUserApi.unbanUser(row.id)
    ElMessage.success('用户已解封')
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 5. 积分管理
const openPointsDialog = (row: AdminUser) => {
  currentUser.value = row
  pointsForm.type = 'add'
  pointsForm.amount = 10
  pointsForm.reason = ''
  dialog.points = true
}

const submitPointsAdjustment = async () => {
  if (!currentUser.value) return
  if (!pointsForm.reason) {
    ElMessage.warning('请输入调整原因')
    return
  }

  submitting.value = true
  try {
    // 根据类型计算最终分数变动值
    const points = pointsForm.type === 'add' ? pointsForm.amount : -pointsForm.amount
    await adminUserApi.adjustPoints(currentUser.value.id, points, pointsForm.reason)
    
    ElMessage.success('积分调整成功')
    dialog.points = false
    loadUsers()
  } catch (error) {
    ElMessage.error('调整失败')
  } finally {
    submitting.value = false
  }
}

const handleFreezePoints = async () => {
  if (!currentUser.value) return
  ElMessageBox.confirm('确定要冻结该用户的积分功能吗？冻结后用户将无法使用积分。', '积分冻结', {
    confirmButtonText: '确定冻结',
    type: 'warning'
  }).then(async () => {
    try {
      await adminUserApi.freezePoints(currentUser.value!.id)
      ElMessage.success('积分功能已冻结')
      dialog.points = false
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }).catch(() => {})
}

// --- 辅助函数 ---
const formatDateTime = (val: string) => formatDate(val, 'YYYY-MM-DD HH:mm:ss')
</script>

<style scoped>
.user-management-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  min-height: 600px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.id-number {
  color: #909399;
  font-size: 12px;
  margin-left: 5px;
}

.points-text {
  color: #e6a23c;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.mt-20 {
  margin-top: 20px;
}

.ml-10 {
  margin-left: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
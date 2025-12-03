
<template>
  <Layout>
    <div class="student-list">
      <div class="page-header">
        <h2>我的学员</h2>
        <el-button type="primary" @click="showAddStudentDialog = true">
          <el-icon><Plus /></el-icon>
          添加学员
        </el-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>

      <!-- 空状态 -->
      <div v-else-if="students.length === 0" class="empty-state">
        <el-empty description="暂无学员">
          <el-button type="primary" @click="showAddStudentDialog = true">添加您的第一位学员</el-button>
        </el-empty>
      </div>

      <!-- 学员表格 -->
      <el-card v-else>
        <el-table :data="students" style="width: 100%">
          <el-table-column label="学员信息" min-width="200">
            <template #default="{ row }">
              <div class="student-info">
                <el-avatar :src="row.avatar" :size="40">
                  {{ row.student.username.charAt(0).toUpperCase() }}
                </el-avatar>
                <div class="student-details">
                  <div class="student-name">{{ row.student.username }}</div>
                  <div class="student-intro">{{ row.student.intro || '暂无简介' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="gender" label="性别" width="100">
            <template #default="{ row }">
              {{ formatGender(row.student.gender) }}
            </template>
          </el-table-column>
          <el-table-column label="加入时间" width="150">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250">
            <template #default="{ row }">
              <el-button size="small" @click="viewStudentDetails(row)" v-if="false">查看详情</el-button>
              <el-button size="small" type="danger" @click="confirmRemoveStudent(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 添加学员弹窗 -->
      <el-dialog
        v-model="showAddStudentDialog"
        title="添加学员"
        width="500px"
      >
        <el-form :model="addStudentForm" label-width="100px">
          <el-form-item label="用户名">
            <el-input
              v-model="addStudentForm.username"
              placeholder="请输入学员用户名"
              @keyup.enter="searchUser"
            >
              <template #append>
                <el-button @click="searchUser" :loading="searching">搜索</el-button>
              </template>
            </el-input>
          </el-form-item>
          
          <div v-if="searchedUser" class="searched-user">
            <el-alert type="success" :closable="false">
              <div class="user-found">
                <el-avatar :src="searchedUser.avatar" :size="50">
                  {{ searchedUser.username.charAt(0).toUpperCase() }}
                </el-avatar>
                <div class="user-info">
                  <div class="username">{{ searchedUser.username }}</div>
                  <div class="user-intro">{{ searchedUser.intro || '暂无简介' }}</div>
                </div>
              </div>
            </el-alert>
          </div>
        </el-form>
        
        <template #footer>
          <el-button @click="showAddStudentDialog = false">取消</el-button>
          <el-button
            type="primary"
            @click="addStudent"
            :disabled="!searchedUser"
            :loading="adding"
          >
            确认添加
          </el-button>
        </template>
      </el-dialog>

      <!-- 学员详情弹窗 -->
      <el-dialog
        v-model="showDetailsDialog"
        :title="`${selectedStudent?.username} 的详细信息`"
        width="800px"
      >
        <el-tabs v-model="activeTab">
          <el-tab-pane label="打卡记录" name="checkins">
            <div v-if="loadingDetails" class="loading-container">
              <el-icon class="is-loading"><Loading /></el-icon>
            </div>
            <div v-else-if="studentCheckIns.length === 0" class="empty-state">
              <el-empty description="暂无打卡记录" />
            </div>
            <el-table v-else :data="studentCheckIns" style="width: 100%">
              <el-table-column label="日期" width="150">
                <template #default="{ row }">
                  {{ formatDate(row.checkDate) }}
                </template>
              </el-table-column>
              <el-table-column label="时间" width="150">
                <template #default="{ row }">
                  {{ formatTime(row.checkTime) }}
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          
          <el-tab-pane label="饮食记录" name="diet">
            <div v-if="loadingDetails" class="loading-container">
              <el-icon class="is-loading"><Loading /></el-icon>
            </div>
            <div v-else-if="studentDietRecords.length === 0" class="empty-state">
              <el-empty description="暂无饮食记录" />
            </div>
            <el-table v-else :data="studentDietRecords" style="width: 100%">
              <el-table-column label="日期" width="120">
                <template #default="{ row }">
                  {{ formatDate(row.mealDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="mealType" label="用餐类型" width="120">
                 <template #default="{ row }">
                  {{ formatMealType(row.mealType) }}
                </template>
              </el-table-column>
              <el-table-column prop="foodItems" label="食物内容" min-width="200" />
              <el-table-column prop="calories" label="热量 (千卡)" width="100" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-dialog>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Loading } from '@element-plus/icons-vue'
import Layout from '@/components/common/Layout.vue'
import { getMyStudents, addStudent as addStudentApi, removeStudent, getStudentCheckIns, getStudentDietRecords } from '@/api/coach'
import { getProfileByName } from '@/api/user'
import { showSuccess, showError, showWarning, confirmRemove } from '@/utils/feedback'

const router = useRouter()

const students = ref([])
const loading = ref(false)
const showAddStudentDialog = ref(false)
const showDetailsDialog = ref(false)
const selectedStudent = ref(null)
const activeTab = ref('checkins')

const addStudentForm = ref({
  username: ''
})
const searchedUser = ref(null)
const searching = ref(false)
const adding = ref(false)

const studentCheckIns = ref([])
const studentDietRecords = ref([])
const loadingDetails = ref(false)

const fetchStudents = async () => {
  loading.value = true
  try {
    const response = await getMyStudents()
    students.value = response
  } catch (error) {
    showError('加载学员列表失败')
    console.error('Fetch students error:', error)
  } finally {
    loading.value = false
  }
}

const searchUser = async () => {
  if (!addStudentForm.value.username.trim()) {
    showWarning('请输入用户名')
    return
  }

  searching.value = true
  try {
    // Search for user by username - using profile endpoint as proxy
    // In real implementation, there should be a search user endpoint
    // 打印学生
    console.log('搜索学生：', addStudentForm.value.username)

    const response = await getProfileByName(addStudentForm.value.username)
    // 打印返回结果以便调试
    console.log('搜索用户成功，返回数据：', JSON.stringify(response))
    searchedUser.value = response
  } catch (error) {
    showError('未找到该用户')
    searchedUser.value = null
  } finally {
    searching.value = false
  }
}

const addStudent = async () => {
  if (!searchedUser.value) return

  adding.value = true
  try {
    await addStudentApi(searchedUser.value.id)
    showSuccess('添加学员成功')
    showAddStudentDialog.value = false
    addStudentForm.value.username = ''
    searchedUser.value = null
    fetchStudents()
  } catch (error) {
    showError(error.message || '添加学员失败')
  } finally {
    adding.value = false
  }
}

const confirmRemoveStudent = async (student) => {
  try {
    await confirmRemove(`将学员 ${student.username} 从您的列表中移除`)
    await removeStudent(student.id)
    showSuccess('移除学员成功')
    fetchStudents()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      showError('移除学员失败')
    }
  }
}

const viewStudentDetails = async (student) => {
  selectedStudent.value = student
  showDetailsDialog.value = true
  activeTab.value = 'checkins'
  await fetchStudentDetails()
}

const fetchStudentDetails = async () => {
  if (!selectedStudent.value) return

  loadingDetails.value = true
  try {
    // Fetch check-ins
    const checkInsResponse = await getStudentCheckIns(selectedStudent.value.id)
    studentCheckIns.value = checkInsResponse

    // Fetch diet records
    const dietResponse = await getStudentDietRecords(selectedStudent.value.id, {})
    studentDietRecords.value = dietResponse
  } catch (error) {
    showError('加载学员详情失败')
    console.error('Fetch student details error:', error)
  } finally {
    loadingDetails.value = false
  }
}

// 格式化性别
const formatGender = (gender) => {
  const map = {
    'Male': '男',
    'Female': '女',
    'Other': '其他'
  }
  return map[gender] || gender || '未设置'
}

// 格式化餐点类型
const formatMealType = (type) => {
  const map = {
    'breakfast': '早餐',
    'lunch': '午餐',
    'dinner': '晚餐',
    'snack': '加餐/零食'
  }
  return map[type] || type
}

const formatDate = (dateString) => {
  if (!dateString) return '暂无'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' })
}

const formatTime = (dateString) => {
  if (!dateString) return '暂无'
  const date = new Date(dateString)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

watch(showAddStudentDialog, (newVal) => {
  if (!newVal) {
    addStudentForm.value.username = ''
    searchedUser.value = null
  }
})

onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
/* 样式保持不变 */
.student-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px;
  color: #409EFF;
}

.empty-state {
  padding: 40px;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-details {
  flex: 1;
}

.student-name {
  font-weight: 500;
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.student-intro {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.searched-user {
  margin-top: 15px;
}

.user-found {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  flex: 1;
}

.user-info .username {
  font-weight: 500;
  font-size: 16px;
  margin-bottom: 4px;
}

.user-info .user-intro {
  font-size: 14px;
  color: #606266;
}

@media (max-width: 768px) {
  .student-list {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .page-header .el-button {
    width: 100%;
  }
}
</style>
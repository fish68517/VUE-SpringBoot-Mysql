<template>
  <Layout>
    <div class="student-list">
      <div class="page-header">
        <h2>My Students</h2>
        <el-button type="primary" @click="showAddStudentDialog = true">
          <el-icon><Plus /></el-icon>
          Add Student
        </el-button>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>

      <!-- Empty State -->
      <div v-else-if="students.length === 0" class="empty-state">
        <el-empty description="No students yet">
          <el-button type="primary" @click="showAddStudentDialog = true">Add Your First Student</el-button>
        </el-empty>
      </div>

      <!-- Students Table -->
      <el-card v-else>
        <el-table :data="students" style="width: 100%">
          <el-table-column label="Student" min-width="200">
            <template #default="{ row }">
              <div class="student-info">
                <el-avatar :src="row.avatar" :size="40">
                  {{ row.username.charAt(0).toUpperCase() }}
                </el-avatar>
                <div class="student-details">
                  <div class="student-name">{{ row.username }}</div>
                  <div class="student-intro">{{ row.intro || 'No introduction' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="gender" label="Gender" width="100" />
          <el-table-column label="Joined" width="150">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="250">
            <template #default="{ row }">
              <el-button size="small" @click="viewStudentDetails(row)">View Details</el-button>
              <el-button size="small" type="danger" @click="confirmRemoveStudent(row)">Remove</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- Add Student Dialog -->
      <el-dialog
        v-model="showAddStudentDialog"
        title="Add Student"
        width="500px"
      >
        <el-form :model="addStudentForm" label-width="100px">
          <el-form-item label="Username">
            <el-input
              v-model="addStudentForm.username"
              placeholder="Enter student username"
              @keyup.enter="searchUser"
            >
              <template #append>
                <el-button @click="searchUser" :loading="searching">Search</el-button>
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
                  <div class="user-intro">{{ searchedUser.intro || 'No introduction' }}</div>
                </div>
              </div>
            </el-alert>
          </div>
        </el-form>
        
        <template #footer>
          <el-button @click="showAddStudentDialog = false">Cancel</el-button>
          <el-button
            type="primary"
            @click="addStudent"
            :disabled="!searchedUser"
            :loading="adding"
          >
            Add Student
          </el-button>
        </template>
      </el-dialog>

      <!-- Student Details Dialog -->
      <el-dialog
        v-model="showDetailsDialog"
        :title="`${selectedStudent?.username}'s Details`"
        width="800px"
      >
        <el-tabs v-model="activeTab">
          <el-tab-pane label="Check-ins" name="checkins">
            <div v-if="loadingDetails" class="loading-container">
              <el-icon class="is-loading"><Loading /></el-icon>
            </div>
            <div v-else-if="studentCheckIns.length === 0" class="empty-state">
              <el-empty description="No check-in records" />
            </div>
            <el-table v-else :data="studentCheckIns" style="width: 100%">
              <el-table-column label="Date" width="150">
                <template #default="{ row }">
                  {{ formatDate(row.checkDate) }}
                </template>
              </el-table-column>
              <el-table-column label="Time" width="150">
                <template #default="{ row }">
                  {{ formatTime(row.checkTime) }}
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          
          <el-tab-pane label="Diet Records" name="diet">
            <div v-if="loadingDetails" class="loading-container">
              <el-icon class="is-loading"><Loading /></el-icon>
            </div>
            <div v-else-if="studentDietRecords.length === 0" class="empty-state">
              <el-empty description="No diet records" />
            </div>
            <el-table v-else :data="studentDietRecords" style="width: 100%">
              <el-table-column label="Date" width="120">
                <template #default="{ row }">
                  {{ formatDate(row.mealDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="mealType" label="Meal Type" width="120" />
              <el-table-column prop="foodItems" label="Food Items" min-width="200" />
              <el-table-column prop="calories" label="Calories" width="100" />
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
import { getProfile } from '@/api/user'
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
    showError('Failed to load students')
    console.error('Fetch students error:', error)
  } finally {
    loading.value = false
  }
}

const searchUser = async () => {
  if (!addStudentForm.value.username.trim()) {
    showWarning('Please enter a username')
    return
  }

  searching.value = true
  try {
    // Search for user by username - using profile endpoint as proxy
    // In real implementation, there should be a search user endpoint
    const response = await getProfile(addStudentForm.value.username)
    searchedUser.value = response
  } catch (error) {
    showError('User not found')
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
    showSuccess('Student added successfully')
    showAddStudentDialog.value = false
    addStudentForm.value.username = ''
    searchedUser.value = null
    fetchStudents()
  } catch (error) {
    showError(error.message || 'Failed to add student')
  } finally {
    adding.value = false
  }
}

const confirmRemoveStudent = async (student) => {
  try {
    await confirmRemove(`${student.username} from your students`)
    await removeStudent(student.id)
    showSuccess('Student removed successfully')
    fetchStudents()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      showError('Failed to remove student')
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
    showError('Failed to load student details')
    console.error('Fetch student details error:', error)
  } finally {
    loadingDetails.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

const formatTime = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
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

<template>
  <div class="tasks-container">
    <h2>任务管理</h2>

    <!-- 学生列表 -->
    <el-card class="students-card">
      <template #header>
        <div class="card-header">
          <span>分配给我的学生</span>
        </div>
      </template>

      <el-empty v-if="students.length === 0" description="暂无分配学生" />

      <el-table v-else :data="students" stripe>
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="postTitle" label="岗位名称" />
        <el-table-column prop="department" label="科室" />
        <el-table-column label="实习状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.internshipStatus === 'ONGOING'" type="success">进行中</el-tag>
            <el-tag v-else-if="row.internshipStatus === 'COMPLETED'" type="info">已完成</el-tag>
            <el-tag v-else type="danger">已终止</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showStudentDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="学生详情" width="700px">
      <div v-if="selectedStudent" class="student-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="1" border class="detail-section">
          <el-descriptions-item label="学生姓名">
            {{ selectedStudent.studentName }}
          </el-descriptions-item>
          <el-descriptions-item label="学号">
            {{ selectedStudent.studentId }}
          </el-descriptions-item>
          <el-descriptions-item label="岗位名称">
            {{ selectedStudent.postTitle }}
          </el-descriptions-item>
          <el-descriptions-item label="科室">
            {{ selectedStudent.department }}
          </el-descriptions-item>
          <el-descriptions-item label="开始日期">
            {{ formatDate(selectedStudent.startDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="结束日期">
            {{ formatDate(selectedStudent.endDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="实习状态">
            <el-tag v-if="selectedStudent.internshipStatus === 'ONGOING'" type="success">进行中</el-tag>
            <el-tag v-else-if="selectedStudent.internshipStatus === 'COMPLETED'" type="info">已完成</el-tag>
            <el-tag v-else type="danger">已终止</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 标签页 -->
        <el-tabs class="detail-tabs">
          <!-- 任务标签页 -->
          <el-tab-pane label="任务">
            <div class="tab-content">
              <el-button type="primary" @click="showTaskForm" v-if="selectedStudent.internshipStatus === 'ONGOING'">
                创建任务
              </el-button>
              <el-empty v-if="tasks.length === 0" description="暂无任务" />
              <el-table v-else :data="tasks" stripe style="margin-top: 20px">
                <el-table-column prop="title" label="任务标题" />
                <el-table-column prop="content" label="任务内容" show-overflow-tooltip />
                <el-table-column label="截止时间" width="150">
                  <template #default="{ row }">
                    {{ formatDate(row.deadline) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row }">
                    <el-button type="primary" size="small" @click="showTaskDetail(row)">
                      查看
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>

          <!-- 周记标签页 -->
          <el-tab-pane label="周记">
            <div class="tab-content">
              <el-empty v-if="weeklyReports.length === 0" description="暂无周记" />
              <el-table v-else :data="weeklyReports" stripe>
                <el-table-column prop="weekNumber" label="周次" width="80" />
                <el-table-column label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag v-if="row.status === 'SUBMITTED'" type="warning">待批阅</el-tag>
                    <el-tag v-else-if="row.status === 'REVIEWED'" type="success">已批阅</el-tag>
                    <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已打回</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="{ row }">
                    <el-button type="primary" size="small" @click="goToReports(row)">
                      批阅
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 创建任务对话框 -->
    <el-dialog v-model="taskFormVisible" title="创建任务" width="600px" @close="resetTaskForm">
      <el-form :model="taskForm" label-width="100px">
        <el-form-item label="任务标题" required>
          <el-input v-model="taskForm.title" placeholder="请输入任务标题" />
        </el-form-item>
        <el-form-item label="任务内容" required>
          <el-input
            v-model="taskForm.content"
            type="textarea"
            rows="4"
            placeholder="请输入任务内容"
          />
        </el-form-item>
        <el-form-item label="截止时间" required>
          <el-date-picker
            v-model="taskForm.deadline"
            type="datetime"
            placeholder="选择截止时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTask" :loading="taskLoading">
          创建
        </el-button>
      </template>
    </el-dialog>

    <!-- 任务详情对话框 -->
    <el-dialog v-model="taskDetailVisible" title="任务详情" width="600px">
      <div v-if="selectedTask" class="task-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="任务标题">
            {{ selectedTask.title }}
          </el-descriptions-item>
          <el-descriptions-item label="任务内容">
            {{ selectedTask.content }}
          </el-descriptions-item>
          <el-descriptions-item label="截止时间">
            {{ formatDate(selectedTask.deadline) }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(selectedTask.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="taskDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getStudentsByTeacher,
  getStudentDetail,
  getTasksByStudent,
  createTask,
  getWeeklyReportsByStudent,
} from '@/api/teacher'

const router = useRouter()

// 数据
const students = ref([])
const selectedStudent = ref(null)
const detailDialogVisible = ref(false)

const tasks = ref([])
const selectedTask = ref(null)
const taskFormVisible = ref(false)
const taskDetailVisible = ref(false)
const taskLoading = ref(false)

const weeklyReports = ref([])

const taskForm = ref({
  title: '',
  content: '',
  deadline: '',
})

// 获取分配给老师的学生列表
const fetchStudents = async () => {
  try {
    const response = await getStudentsByTeacher()
    students.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取学生列表失败')
  }
}

// 显示学生详情
const showStudentDetail = async (student) => {
  try {
    const response = await getStudentDetail(student.internshipId)
    selectedStudent.value = response.data
    detailDialogVisible.value = true

    // 加载任务和周记
    await fetchTasks(student.internshipId)
    await fetchWeeklyReports(student.internshipId)
  } catch (error) {
    ElMessage.error(error.message || '获取学生详情失败')
  }
}

// 获取学生的任务列表
const fetchTasks = async (internshipId) => {
  try {
    const response = await getTasksByStudent(internshipId)
    tasks.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取任务列表失败')
  }
}

// 显示创建任务表单
const showTaskForm = () => {
  taskFormVisible.value = true
}

// 重置任务表单
const resetTaskForm = () => {
  taskForm.value.title = ''
  taskForm.value.content = ''
  taskForm.value.deadline = ''
}

// 提交任务
const submitTask = async () => {
  if (!taskForm.value.title.trim()) {
    ElMessage.warning('请输入任务标题')
    return
  }
  if (!taskForm.value.content.trim()) {
    ElMessage.warning('请输入任务内容')
    return
  }
  if (!taskForm.value.deadline) {
    ElMessage.warning('请选择截止时间')
    return
  }

  taskLoading.value = true
  try {
    await createTask(selectedStudent.value.internshipId, {
      title: taskForm.value.title,
      content: taskForm.value.content,
      deadline: taskForm.value.deadline,
    })
    ElMessage.success('任务创建成功')
    taskFormVisible.value = false
    resetTaskForm()
    await fetchTasks(selectedStudent.value.internshipId)
  } catch (error) {
    ElMessage.error(error.message || '任务创建失败')
  } finally {
    taskLoading.value = false
  }
}

// 显示任务详情
const showTaskDetail = (task) => {
  selectedTask.value = task
  taskDetailVisible.value = true
}

// 获取学生的周记列表
const fetchWeeklyReports = async (internshipId) => {
  try {
    const response = await getWeeklyReportsByStudent(internshipId)
    weeklyReports.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取周记列表失败')
  }
}

// 跳转到周记批阅页面
const goToReports = (report) => {
  router.push({
    name: 'TeacherReports',
    query: { internshipId: selectedStudent.value.internshipId, reportId: report.id },
  })
  detailDialogVisible.value = false
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// 初始化
onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.tasks-container {
  padding: 20px;
}

.students-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.student-detail {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-tabs {
  margin-top: 20px;
}

.tab-content {
  padding: 20px 0;
}

.task-detail {
  padding: 20px 0;
}
</style>

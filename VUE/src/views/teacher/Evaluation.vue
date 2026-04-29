<template>
  <div class="evaluation-container">
    <h2>学生评价</h2>

    <!-- 学生列表 -->
    <el-card class="students-card">
      <template #header>
        <div class="card-header">
          <span>我的学生</span>
        </div>
      </template>

      <el-empty v-if="students.length === 0" description="暂无学生" />

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
            <el-button type="primary" size="small" @click="showEvaluationForm(row)">
              评价
            </el-button>
            <el-button type="info" size="small" @click="showEvaluationHistory(row)">
              历史评价
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 评价表单对话框 -->
    <el-dialog v-model="evaluationFormVisible" title="学生评价" width="600px" @close="resetEvaluationForm">
      <div v-if="selectedStudent" class="evaluation-form">
        <el-descriptions :column="1" border class="student-info">
          <el-descriptions-item label="学生姓名">
            {{ selectedStudent.studentName }}
          </el-descriptions-item>
          <el-descriptions-item label="岗位名称">
            {{ selectedStudent.postTitle }}
          </el-descriptions-item>
          <el-descriptions-item label="科室">
            {{ selectedStudent.department }}
          </el-descriptions-item>
        </el-descriptions>

        <el-form :model="evaluationForm" label-width="100px" style="margin-top: 20px">
          <el-form-item label="评分" required>
            <el-rate v-model="evaluationForm.score" :max="5" />
          </el-form-item>
          <el-form-item label="评价内容" required>
            <el-input
              v-model="evaluationForm.comment"
              type="textarea"
              rows="4"
              placeholder="请输入对学生的评价"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="evaluationFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluation" :loading="evaluationLoading">
          提交评价
        </el-button>
      </template>
    </el-dialog>

    <!-- 历史评价对话框 -->
    <el-dialog v-model="historyDialogVisible" title="历史评价" width="600px">
      <div v-if="selectedStudent" class="evaluation-history">
        <el-empty v-if="evaluationHistory.length === 0" description="暂无评价记录" />

        <el-timeline v-else>
          <el-timeline-item
            v-for="evaluation in evaluationHistory"
            :key="evaluation.id"
            :timestamp="formatDate(evaluation.createdAt)"
            placement="top"
          >
            <div class="evaluation-item">
              <div class="evaluation-score">
                <el-rate v-model="evaluation.score" disabled :max="5" />
              </div>
              <div class="evaluation-comment">
                {{ evaluation.comment }}
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
      <template #footer>
        <el-button @click="historyDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getStudentsByTeacher,
  submitStudentEvaluation,
  getStudentEvaluationHistory,
} from '@/api/teacher'

// 数据
const students = ref([])
const selectedStudent = ref(null)
const evaluationFormVisible = ref(false)
const historyDialogVisible = ref(false)
const evaluationLoading = ref(false)

const evaluationForm = ref({
  score: 5,
  comment: '',
})

const evaluationHistory = ref([])

// 获取分配给老师的学生列表
const fetchStudents = async () => {
  try {
    const response = await getStudentsByTeacher()
    students.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取学生列表失败')
  }
}

// 显示评价表单
const showEvaluationForm = (student) => {
  selectedStudent.value = student
  evaluationForm.value.score = 5
  evaluationForm.value.comment = ''
  evaluationFormVisible.value = true
}

// 重置评价表单
const resetEvaluationForm = () => {
  evaluationForm.value.score = 5
  evaluationForm.value.comment = ''
}

// 提交评价
const submitEvaluation = async () => {
  if (!evaluationForm.value.comment.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }

  evaluationLoading.value = true
  try {
    await submitStudentEvaluation(selectedStudent.value.internshipId, {
      score: evaluationForm.value.score,
      comment: evaluationForm.value.comment,
      evaluatorType: 'TEACHER',
    })
    ElMessage.success('评价提交成功')
    evaluationFormVisible.value = false
    resetEvaluationForm()
  } catch (error) {
    ElMessage.error(error.message || '评价提交失败')
  } finally {
    evaluationLoading.value = false
  }
}

// 显示历史评价
const showEvaluationHistory = async (student) => {
  try {
    selectedStudent.value = student
    const response = await getStudentEvaluationHistory(student.internshipId)
    evaluationHistory.value = response.data || []
    historyDialogVisible.value = true
  } catch (error) {
    ElMessage.error(error.message || '获取评价历史失败')
  }
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
.evaluation-container {
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

.evaluation-form {
  padding: 20px 0;
}

.student-info {
  margin-bottom: 20px;
}

.evaluation-history {
  padding: 20px 0;
}

.evaluation-item {
  padding: 10px 0;
}

.evaluation-score {
  margin-bottom: 10px;
}

.evaluation-comment {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  color: #606266;
}
</style>

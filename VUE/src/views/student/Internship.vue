<template>
  <div class="internship-container">
    <h2>实习过程</h2>

    <!-- 实习记录列表 -->
    <el-card class="internship-list-card">
      <el-empty v-if="internships.length === 0" description="暂无实习记录" />
      
      <el-table v-else :data="internships" stripe>
        <el-table-column prop="id" label="实习ID" width="80" />
        <el-table-column prop="post.title" label="岗位名称" />
      <el-table-column prop="post.department" label="科室" />
      <el-table-column prop="teacher.username" label="带教老师" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ONGOING'" type="success">进行中</el-tag>
            <el-tag v-else-if="row.status === 'COMPLETED'" type="info">已完成</el-tag>
            <el-tag v-else type="danger">已终止</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showInternshipDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 实习详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="实习详情" width="700px">
      <div v-if="selectedInternship" class="internship-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="1" border class="detail-section">
          <el-descriptions-item label="岗位名称">
            {{ selectedInternship.post.title }}
          </el-descriptions-item>
          <el-descriptions-item label="科室">
            {{ selectedInternship.post.department }}
          </el-descriptions-item>
          <el-descriptions-item label="带教老师">
            {{ selectedInternship.teacher.username }}
          </el-descriptions-item>
          <el-descriptions-item label="开始日期">
            {{ formatDate(selectedInternship.startDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="结束日期">
            {{ formatDate(selectedInternship.endDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="selectedInternship.status === 'ONGOING'" type="success">进行中</el-tag>
            <el-tag v-else-if="selectedInternship.status === 'COMPLETED'" type="info">已完成</el-tag>
            <el-tag v-else type="danger">已终止</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 标签页 -->
        <el-tabs class="detail-tabs">
          <!-- 周记标签页 -->
          <el-tab-pane label="周记">
            <div class="tab-content">
              <el-button type="primary" @click="showWeeklyReportForm" v-if="selectedInternship.status === 'ONGOING'">
                提交周记
              </el-button>
              <el-empty v-if="weeklyReports.length === 0" description="暂无周记" />
              <el-table v-else :data="weeklyReports" stripe style="margin-top: 20px">
                <el-table-column prop="weekNumber" label="周次" width="80" />
                <el-table-column label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag v-if="row.status === 'SUBMITTED'" type="warning">已提交</el-tag>
                    <el-tag v-else-if="row.status === 'REVIEWED'" type="success">已批阅</el-tag>
                    <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已打回</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="评分" width="80">
                  <template #default="{ row }">
                    {{ row.teacherScore || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="{ row }">
                    <el-button type="primary" size="small" @click="showWeeklyReportDetail(row)">
                      查看
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>

          <!-- 评价标签页 -->
          <el-tab-pane label="评价">
            <div class="tab-content">
              <el-button type="primary" @click="showEvaluationForm" v-if="selectedInternship.status === 'ONGOING'">
                提交评价
              </el-button>
              <el-empty v-if="evaluations.length === 0" description="暂无评价" />
              <el-table v-else :data="evaluations" stripe style="margin-top: 20px">
                <el-table-column prop="evaluatorName" label="评价人" />
                <el-table-column prop="evaluatorType" label="评价人类型" width="100">
                  <template #default="{ row }">
                    {{ row.evaluatorType === 'TEACHER' ? '老师' : '学生' }}
                  </template>
                </el-table-column>
                <el-table-column prop="score" label="评分" width="80" />
                <el-table-column label="操作" width="150">
                  <template #default="{ row }">
                    <el-button type="primary" size="small" @click="showEvaluationDetail(row)">
                      查看
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

    <!-- 周记表单对话框 -->
    <el-dialog v-model="weeklyReportFormVisible" title="提交周记" width="600px" @close="resetWeeklyReportForm">
      <el-form :model="weeklyReportForm" label-width="100px">
        <el-form-item label="周次" required>
          <el-input-number v-model="weeklyReportForm.weekNumber" :min="1" />
        </el-form-item>
        <el-form-item label="周记内容" required>
          <el-input
            v-model="weeklyReportForm.content"
            type="textarea"
            rows="6"
            placeholder="请填写周记内容（工作总结、收获、问题、下周计划）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="weeklyReportFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitWeeklyReportHandler" :loading="weeklyReportLoading">
          提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 周记详情对话框 -->
    <el-dialog v-model="weeklyReportDetailVisible" title="周记详情" width="600px">
      <div v-if="selectedWeeklyReport" class="weekly-report-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="周次">
            {{ selectedWeeklyReport.weekNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="周记内容">
            {{ selectedWeeklyReport.content }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="selectedWeeklyReport.status === 'SUBMITTED'" type="warning">已提交</el-tag>
            <el-tag v-else-if="selectedWeeklyReport.status === 'REVIEWED'" type="success">已批阅</el-tag>
            <el-tag v-else-if="selectedWeeklyReport.status === 'REJECTED'" type="danger">已打回</el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedWeeklyReport.teacherComment" label="老师评语">
            {{ selectedWeeklyReport.teacherComment }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedWeeklyReport.teacherScore" label="评分">
            {{ selectedWeeklyReport.teacherScore }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="weeklyReportDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 评价表单对话框 -->
    <el-dialog v-model="evaluationFormVisible" title="提交评价" width="600px" @close="resetEvaluationForm">
      <el-form :model="evaluationForm" label-width="100px">
        <el-form-item label="评价对象" required>
          <el-select v-model="evaluationForm.evaluatorType" placeholder="请选择评价对象">
            <el-option label="评价老师" value="TEACHER" />
            <el-option label="评价医院" value="HOSPITAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="评分" required>
          <el-rate v-model="evaluationForm.score" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容" required>
          <el-input
            v-model="evaluationForm.comment"
            type="textarea"
            rows="4"
            placeholder="请填写评价内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evaluationFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluationHandler" :loading="evaluationLoading">
          提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 评价详情对话框 -->
    <el-dialog v-model="evaluationDetailVisible" title="评价详情" width="600px">
      <div v-if="selectedEvaluation" class="evaluation-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="评价人">
            {{ selectedEvaluation.evaluatorName }}
          </el-descriptions-item>
          <el-descriptions-item label="评价人类型">
            {{ selectedEvaluation.evaluatorType === 'TEACHER' ? '老师' : '学生' }}
          </el-descriptions-item>
          <el-descriptions-item label="评分">
            {{ selectedEvaluation.score }}
          </el-descriptions-item>
          <el-descriptions-item label="评价内容">
            {{ selectedEvaluation.comment }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="evaluationDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getInternships,
  getInternshipDetail,
  getWeeklyReports,
  submitWeeklyReport,
  getEvaluations,
  submitEvaluation,
} from '@/api/internship'

// 数据
const internships = ref([])
const selectedInternship = ref(null)
const detailDialogVisible = ref(false)

const weeklyReports = ref([])
const selectedWeeklyReport = ref(null)
const weeklyReportFormVisible = ref(false)
const weeklyReportDetailVisible = ref(false)
const weeklyReportLoading = ref(false)

const evaluations = ref([])
const selectedEvaluation = ref(null)
const evaluationFormVisible = ref(false)
const evaluationDetailVisible = ref(false)
const evaluationLoading = ref(false)

const weeklyReportForm = ref({
  weekNumber: 1,
  content: '',
})

const evaluationForm = ref({
  evaluatorType: 'TEACHER',
  score: 5,
  comment: '',
})

// 获取实习记录列表
const fetchInternships = async () => {
  try {
    const response = await getInternships()
    internships.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取实习记录失败')
  }
}

// 显示实习详情
const showInternshipDetail = async (internship) => {
  try {
    const response = await getInternshipDetail(internship.id)
    selectedInternship.value = response.data
    detailDialogVisible.value = true
    
    // 加载周记和评价
    await fetchWeeklyReports(internship.id)
    await fetchEvaluations(internship.id)
  } catch (error) {
    ElMessage.error(error.message || '获取实习详情失败')
  }
}

// 获取周记列表
const fetchWeeklyReports = async (internshipId) => {
  try {
    const response = await getWeeklyReports(internshipId)
    weeklyReports.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取周记列表失败')
  }
}

// 显示周记表单
const showWeeklyReportForm = () => {
  weeklyReportFormVisible.value = true
}

// 重置周记表单
const resetWeeklyReportForm = () => {
  weeklyReportForm.value.weekNumber = 1
  weeklyReportForm.value.content = ''
}

// 提交周记
const submitWeeklyReportHandler = async () => {
  if (!weeklyReportForm.value.content.trim()) {
    ElMessage.warning('请填写周记内容')
    return
  }

  weeklyReportLoading.value = true
  try {
    await submitWeeklyReport(selectedInternship.value.id, {
      weekNumber: weeklyReportForm.value.weekNumber,
      content: weeklyReportForm.value.content,
    })
    ElMessage.success('周记提交成功')
    weeklyReportFormVisible.value = false
    resetWeeklyReportForm()
    await fetchWeeklyReports(selectedInternship.value.id)
  } catch (error) {
    ElMessage.error(error.message || '周记提交失败')
  } finally {
    weeklyReportLoading.value = false
  }
}

// 显示周记详情
const showWeeklyReportDetail = (report) => {
  selectedWeeklyReport.value = report
  weeklyReportDetailVisible.value = true
}

// 获取评价列表
const fetchEvaluations = async (internshipId) => {
  try {
    const response = await getEvaluations(internshipId)
    evaluations.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取评价列表失败')
  }
}

// 显示评价表单
const showEvaluationForm = () => {
  evaluationFormVisible.value = true
}

// 重置评价表单
const resetEvaluationForm = () => {
  evaluationForm.value.evaluatorType = 'TEACHER'
  evaluationForm.value.score = 5
  evaluationForm.value.comment = ''
}

// 提交评价
const submitEvaluationHandler = async () => {
  if (!evaluationForm.value.comment.trim()) {
    ElMessage.warning('请填写评价内容')
    return
  }

  evaluationLoading.value = true
  try {
    await submitEvaluation(selectedInternship.value.id, {
      evaluatorType: evaluationForm.value.evaluatorType,
      score: evaluationForm.value.score,
      comment: evaluationForm.value.comment,
    })
    ElMessage.success('评价提交成功')
    evaluationFormVisible.value = false
    resetEvaluationForm()
    await fetchEvaluations(selectedInternship.value.id)
  } catch (error) {
    ElMessage.error(error.message || '评价提交失败')
  } finally {
    evaluationLoading.value = false
  }
}

// 显示评价详情
const showEvaluationDetail = (evaluation) => {
  selectedEvaluation.value = evaluation
  evaluationDetailVisible.value = true
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

// 绑定提交函数


// 初始化
onMounted(() => {
  fetchInternships()
})
</script>

<style scoped>
.internship-container {
  padding: 20px;
}

.internship-list-card {
  margin-bottom: 20px;
}

.internship-detail {
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

.weekly-report-detail,
.evaluation-detail {
  padding: 20px 0;
}
</style>

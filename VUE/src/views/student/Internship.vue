<template>
  <div class="internship-container">
    <div class="header-wrapper">
      <h2>实习过程</h2>
      <el-button v-if="isStudent" type="success" @click="showAddInternshipForm">
        + 添加实习记录
      </el-button>
    </div>

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

    <el-dialog v-model="addDialogVisible" title="手动添加实习记录" width="600px" @close="resetAddForm">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="关联申请" required>
          <el-select v-model="addForm.applicationId" placeholder="请选择已通过的申请" style="width: 100%;">
            <el-option 
              v-for="app in applicationOptions" 
              :key="app.id" 
              :label="`[申请ID:${app.id}] ${app.postTitle} - ${app.hospitalName}`" 
              :value="app.id" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="关联岗位" required>
          <el-select v-model="addForm.postId" placeholder="请选择实习岗位" style="width: 100%;">
            <el-option 
              v-for="post in postOptions" 
              :key="post.id" 
              :label="`${post.title} (${post.department})`" 
              :value="post.id" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="带教老师" required>
          <el-select v-model="addForm.teacherId" placeholder="请选择带教老师" style="width: 100%;">
            <el-option 
              v-for="teacher in teacherOptions" 
              :key="teacher.id" 
              :label="`${teacher.username} (老师)`" 
              :value="teacher.id" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="实习日期" required>
          <el-date-picker
            v-model="addForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="实习状态" required>
          <el-select v-model="addForm.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="进行中 (ONGOING)" value="ONGOING" />
            <el-option label="已完成 (COMPLETED)" value="COMPLETED" />
            <el-option label="已终止 (TERMINATED)" value="TERMINATED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddInternship" :loading="addLoading">
          确认添加
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="实习详情" width="700px">
      <div v-if="selectedInternship" class="internship-detail">
        <el-descriptions :column="1" border class="detail-section">
          <el-descriptions-item label="岗位名称">{{ selectedInternship.post.title }}</el-descriptions-item>
          <el-descriptions-item label="科室">{{ selectedInternship.post.department }}</el-descriptions-item>
          <el-descriptions-item label="带教老师">{{ selectedInternship.teacher.username }}</el-descriptions-item>
          <el-descriptions-item label="开始日期">{{ formatDate(selectedInternship.startDate) }}</el-descriptions-item>
          <el-descriptions-item label="结束日期">{{ formatDate(selectedInternship.endDate) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="selectedInternship.status === 'ONGOING'" type="success">进行中</el-tag>
            <el-tag v-else-if="selectedInternship.status === 'COMPLETED'" type="info">已完成</el-tag>
            <el-tag v-else type="danger">已终止</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-tabs class="detail-tabs">
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

          <el-tab-pane label="评价">
            <div class="tab-content">
              <el-button type="primary" @click="showEvaluationForm" v-if="selectedInternship.status === 'ONGOING'">
                提交评价
              </el-button>
              <el-empty v-if="evaluations.length === 0" description="暂无评价" />
              <el-table v-else :data="evaluations" stripe style="margin-top: 20px">
                <el-table-column prop="evaluator.username" label="评价人" />
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

    <el-dialog v-model="weeklyReportFormVisible" title="提交周记" width="600px" @close="resetWeeklyReportForm">
      <el-form :model="weeklyReportForm" label-width="100px">
        <el-form-item label="周次" required>
          <el-input-number v-model="weeklyReportForm.weekNumber" :min="1" />
        </el-form-item>
        <el-form-item label="周记内容" required>
          <el-input v-model="weeklyReportForm.content" type="textarea" rows="6" placeholder="请填写周记内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="weeklyReportFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitWeeklyReportHandler" :loading="weeklyReportLoading">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="weeklyReportDetailVisible" title="周记详情" width="600px">
      <div v-if="selectedWeeklyReport">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="周次">{{ selectedWeeklyReport.weekNumber }}</el-descriptions-item>
          <el-descriptions-item label="内容">{{ selectedWeeklyReport.content }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ selectedWeeklyReport.status }}</el-descriptions-item>
          <el-descriptions-item v-if="selectedWeeklyReport.teacherComment" label="老师评语">{{ selectedWeeklyReport.teacherComment }}</el-descriptions-item>
          <el-descriptions-item v-if="selectedWeeklyReport.teacherScore" label="评分">{{ selectedWeeklyReport.teacherScore }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

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
          <el-input v-model="evaluationForm.comment" type="textarea" rows="4" placeholder="请填写评价内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evaluationFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluationHandler" :loading="evaluationLoading">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="evaluationDetailVisible" title="评价详情" width="600px">
      <div v-if="selectedEvaluation">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="评价人">{{ selectedEvaluation.evaluator.username }}</el-descriptions-item>
          <el-descriptions-item label="评价人类型">{{ selectedEvaluation.evaluatorType === 'TEACHER' ? '老师' : '学生' }}</el-descriptions-item>
          <el-descriptions-item label="评分">{{ selectedEvaluation.score }}</el-descriptions-item>
          <el-descriptions-item label="评价内容">{{ selectedEvaluation.comment }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore' 

// 👇 引入你刚才在 internship.js 中新增的 createInternship 接口
import {
  getInternships,
  getInternshipDetail,
  getWeeklyReports,
  submitWeeklyReport,
  getEvaluations,
  submitEvaluation,
  createInternship
} from '@/api/internship'

// 👇 新增：引入申请和岗位相关的 API
import { getApplications } from '@/api/application'
import { getPostDetail } from '@/api/post'

// 角色判断
const userStore = useUserStore()
const isStudent = computed(() => userStore.user?.role === 'STUDENT')

// =========== 添加实习功能相关数据和逻辑 ===========
const addDialogVisible = ref(false)
const addLoading = ref(false)
const applicationOptions = ref([])
const postOptions = ref([])
const teacherOptions = ref([])

const addForm = ref({
  applicationId: null,
  postId: null,
  teacherId: null,
  dateRange: [], 
  status: 'ONGOING'
})

// 重置添加表单
const resetAddForm = () => {
  addForm.value = {
    applicationId: null,
    postId: null,
    teacherId: null,
    dateRange: [],
    status: 'ONGOING'
  }
}

// // 加载下拉框静态测试数据
// const loadOptions = async () => {
//   try {
//     applicationOptions.value = [
//       { id: 1, postTitle: '心内科实习生', hospitalName: '广东省人民医院' },
//       { id: 2, postTitle: '普外科实习生', hospitalName: '广州市第一人民医院' }
//     ]
//     postOptions.value = [
//       { id: 1, title: '心内科实习生', department: '心血管内科' },
//       { id: 2, title: '普外科实习生', department: '普外科' }
//     ]
//     teacherOptions.value = [
//       { id: 6, username: 'teacher_zhang' } 
//     ]
//   } catch (error) {
//     console.error("加载下拉框数据失败", error)
//   }
// }

// 👇 重写：动态加载下拉框业务数据
const loadOptions = async () => {
  try {
    // 1. 获取当前登录学生的所有申请记录
    // (后端 SessionInterceptor 已经自动拦截并提取了当前登录学生的 ID，前端无需传参)
    const appsRes = await getApplications()
    const myApplications = appsRes.data || []
    
    const loadedAppOptions = []
    const loadedPostOptions = []
    const postIdSet = new Set() // 配合集合去重，避免同一个岗位请求多次

    for (const app of myApplications) {
      // 业务逻辑优化：通常只有医院审批通过(APPROVED)的申请，才能去创建实习记录
      if (app.hospitalStatus === 'APPROVED') {
        // 组装 关联申请 下拉框数据
        loadedAppOptions.push({
          id: app.id,
          postTitle: app.postTitle || (app.post && app.post.title),
          hospitalName: app.hospitalName || (app.post && app.post.hospital && app.post.hospital.name)
        })

        // 提取 post_id
        const postId = app.post ? app.post.id : null

        // 2. 根据取出的 post_id 查询 post表
        if (postId && !postIdSet.has(postId)) {
          postIdSet.add(postId) // 记录已查询过的 postId，避免重复发送网络请求
          
          // 调用 api/post.js 的接口获取岗位详情
          const postRes = await getPostDetail(postId)
          const postData = postRes.data
          
          if (postData) {
            loadedPostOptions.push({
              id: postData.id,
              title: postData.title,
              department: postData.department
            })
          }
        }
      }
    }

    // 赋值给前端响应式变量以渲染下拉框
    applicationOptions.value = loadedAppOptions
    postOptions.value = loadedPostOptions

    // 关于带教老师：如果你后端有按照医院ID查询老师的接口，可以在这里继续补充。
    // 这里暂时保留我们数据库里初始化好的测试张老师数据供你跑通流程
    teacherOptions.value = [
      { id: 6, username: 'teacher_zhang' } 
    ]
    
    // 如果没有可用的申请，给出友好提示
    if (loadedAppOptions.length === 0) {
      ElMessage.warning('您暂无已通过审批的岗位申请，无法添加实习记录')
    }

  } catch (error) {
    console.error("加载下拉框数据失败", error)
    ElMessage.error("加载关联数据失败，请重试")
  }
}

// 打开弹窗并加载选项
const showAddInternshipForm = () => {
  loadOptions()
  addDialogVisible.value = true
}

// 提交添加实习请求
const submitAddInternship = async () => {
  if (!addForm.value.applicationId || !addForm.value.postId || !addForm.value.teacherId || !addForm.value.dateRange || addForm.value.dateRange.length === 0) {
    ElMessage.warning('请将必填信息填写完整')
    return
  }

  addLoading.value = true
  try {
    const requestData = {
      applicationId: addForm.value.applicationId,
      postId: addForm.value.postId,
      teacherId: addForm.value.teacherId,
      startDate: addForm.value.dateRange[0], 
      endDate: addForm.value.dateRange[1],
      status: addForm.value.status
    }

    // 调用我们在 API 中封装的方法
    await createInternship(requestData)

    ElMessage.success('实习记录手动添加成功')
    addDialogVisible.value = false
    fetchInternships() // 刷新列表
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '添加实习失败')
  } finally {
    addLoading.value = false
  }
}

// =========== 原有数据和逻辑 ===========
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

const weeklyReportForm = ref({ weekNumber: 1, content: '' })
const evaluationForm = ref({ evaluatorType: 'TEACHER', score: 5, comment: '' })

const fetchInternships = async () => {
  try {
    const response = await getInternships()
    internships.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取实习记录失败')
  }
}

const showInternshipDetail = async (internship) => {
  try {
    const response = await getInternshipDetail(internship.id)
    selectedInternship.value = response.data
    detailDialogVisible.value = true
    await fetchWeeklyReports(internship.id)
    await fetchEvaluations(internship.id)
  } catch (error) {
    ElMessage.error(error.message || '获取实习详情失败')
  }
}

const fetchWeeklyReports = async (internshipId) => {
  try {
    const response = await getWeeklyReports(internshipId)
    weeklyReports.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取周记列表失败')
  }
}

const showWeeklyReportForm = () => { weeklyReportFormVisible.value = true }

const resetWeeklyReportForm = () => {
  weeklyReportForm.value.weekNumber = 1
  weeklyReportForm.value.content = ''
}

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

const showWeeklyReportDetail = (report) => {
  selectedWeeklyReport.value = report
  weeklyReportDetailVisible.value = true
}

const fetchEvaluations = async (internshipId) => {
  try {
    const response = await getEvaluations(internshipId)
    evaluations.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取评价列表失败')
  }
}

const showEvaluationForm = () => { evaluationFormVisible.value = true }

const resetEvaluationForm = () => {
  evaluationForm.value.evaluatorType = 'TEACHER'
  evaluationForm.value.score = 5
  evaluationForm.value.comment = ''
}

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

const showEvaluationDetail = (evaluation) => {
  selectedEvaluation.value = evaluation
  evaluationDetailVisible.value = true
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

onMounted(() => {
  fetchInternships()
})
</script>

<style scoped>
.internship-container {
  padding: 20px;
}
.header-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header-wrapper h2 {
  margin: 0;
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
</style>
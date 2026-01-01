<template>
  <div class="feedback-page">
    <!-- 左侧：学员列表 -->
    <div class="student-sidebar">
      <div class="sidebar-header">
        <h3>我的学员</h3>
        <el-input
          v-model="studentSearch"
          placeholder="搜索学员姓名..."
          prefix-icon="Search"
          clearable
        />
      </div>
      
      <div class="student-list" v-loading="loadingStudents">
        <div 
          v-for="student in filteredStudents" 
          :key="student.id"
          class="student-item"
          :class="{ active: currentStudent?.id === student.id }"
          @click="selectStudent(student)"
        >
          <el-avatar :size="40" :src="student.avatar || '/default-avatar.png'">
            {{ student.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <div class="student-info">
            <div class="name">{{ student.student.username }}</div>
        
          </div>
          <el-icon v-if="currentStudent?.id === student.id"><ArrowRight /></el-icon>
        </div>
        
        <el-empty v-if="filteredStudents.length === 0" description="未找到学员" :image-size="60" />
      </div>
    </div>

    <!-- 右侧：反馈详情区域 -->
    <div class="feedback-main">
      <!-- 顶部学员信息栏 -->
      <div class="main-header" v-if="currentStudent">
        <div class="header-left">
          <h2>{{ currentStudent.student.username }} 的训练反馈</h2>
          <el-tag type="info" size="small">共 {{ feedbackList.length }} 条记录</el-tag>
        </div>
      </div>

      <!-- 反馈列表内容 -->
      <div class="feedback-content" v-loading="loadingFeedback">
        <!-- 空状态 -->
        <div v-if="!currentStudent" class="empty-placeholder">
          <el-empty description="请在左侧选择一名学员查看反馈" />
        </div>
        <div v-else-if="feedbackList.length === 0" class="empty-placeholder">
          <el-empty description="该学员暂无训练反馈" />
        </div>

        <!-- 时间轴展示反馈 -->
        <el-timeline v-else>
          <el-timeline-item
            v-for="item in feedbackList"
            :key="item.id"
            :timestamp="formatDate(item.feedback_date || item.created_at)"
            placement="top"
            :type="getFeelingColor(item.feeling)"
            size="large"
          >
            <el-card class="feedback-card">
              <!-- 反馈头部：评分与体感 -->
              <div class="card-header-row">
                <div class="meta-left">
                  <el-rate
                    v-model="item.rating"
                    disabled
                    show-score
                    text-color="#ff9900"
                  />
                
                </div>
                <div class="meta-right">
                  <el-button 
                    type="danger" 
                    link 
                    icon="Delete" 
                    @click="handleDelete(item)"
                  >删除</el-button>
                </div>
              </div>

              <!-- 学员具体内容 -->
              <div class="feedback-text">
                <strong>学员反馈：</strong> {{ item.content }}
              </div>

               <div class="feedback-text">
                <strong>教练回复：</strong> {{ item.coachReply }}
              </div>

              <!-- 教练回复区域 -->
              <div class="reply-section">
                <!-- 已回复状态 -->
                <div v-if="item.coach_reply" class="reply-box answered">
                  <div class="reply-header">
                    <span class="coach-label"><el-icon><Avatar /></el-icon> 我的回复：</span>
                    <span class="reply-time">{{ formatDate(item.reply_at, true) }}</span>
                  </div>
                  <div class="reply-content">{{ item.coach_reply }}</div>
                  <!-- 允许修改回复 -->
                  <el-button type="primary" link size="small" @click="openReplyDialog(item)">修改回复</el-button>
                </div>

                <!-- 未回复状态 -->
                <div v-else class="reply-box un-answered">
                  <el-button type="primary" plain size="small" @click="openReplyDialog(item)">
                    <el-icon><ChatLineSquare /></el-icon> 立即回复
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>

    <!-- 回复弹窗 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="回复学员反馈"
      width="500px"
      append-to-body
    >
      <el-form :model="replyForm">
        <el-form-item>
          <div class="original-content-preview">
            学员说: "{{ currentReplyItem?.content }}"
          </div>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入给学员的建议或鼓励..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply" :loading="submittingReply">发送回复</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ArrowRight, Delete, ChatLineSquare, Avatar } from '@element-plus/icons-vue'
import { getUserInfo } from '@/utils/auth.js'
// 假设你有这些 API，如果没有请根据后端接口调整
import { getMyStudents } from '@/api/coach.js' 
import { getFeedbackListByStudent, replyFeedback, deleteFeedback } from '@/api/training.js'

// --- 数据状态 ---
const loadingStudents = ref(false)
const loadingFeedback = ref(false)
const studentSearch = ref('')
const students = ref([])
const currentStudent = ref(null)
const feedbackList = ref([]) // 存储当前学员的反馈列表

// 回复弹窗相关
const replyDialogVisible = ref(false)
const submittingReply = ref(false)
const currentReplyItem = ref(null)
const replyForm = reactive({
  content: ''
})

// --- 计算属性 ---
const filteredStudents = computed(() => {
  if (!studentSearch.value) return students.value
  return students.value.filter(s => 
    s.username.toLowerCase().includes(studentSearch.value.toLowerCase())
  )
})

// --- 方法 ---

// 1. 获取教练名下的学员列表
const fetchStudents = async () => {
  loadingStudents.value = true
  try {
    const userInfo = getUserInfo()
    // 这里的 API 需要返回教练关联的 student 列表
    // 包含 id, username, avatar, currentPlanName 等字段
    const res = await getMyStudents(userInfo.userId) 
    students.value = res || [] // 根据实际接口结构调整
  } catch (error) {
    console.error(error)
    ElMessage.error('获取学员列表失败')
  } finally {
    loadingStudents.value = false
  }
}

// 2. 选中学员，加载反馈
const selectStudent = async (student) => {
  currentStudent.value = student
  loadingFeedback.value = true
  try {
    const userInfo = getUserInfo()
    // 调用接口查询：WHERE coach_id = me AND student_id = target
    const res = await getFeedbackListByStudent({
      coachId: userInfo.userId,
      studentId: student.student.id
    })
    feedbackList.value = res || []
  } catch (error) {
    console.error(error)
    ElMessage.error('加载反馈数据失败')
  } finally {
    loadingFeedback.value = false
  }
}

// 3. 打开回复弹窗
const openReplyDialog = (item) => {
  currentReplyItem.value = item
  replyForm.content = item.coach_reply || '' // 如果已有回复，则回显
  replyDialogVisible.value = true
}

// 4. 提交回复
const submitReply = async () => {
  if (!replyForm.content.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  submittingReply.value = true
  try {
    await replyFeedback({
      id: currentReplyItem.value.id, // 反馈ID
      coach_reply: replyForm.content
    })
    
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    
    // 刷新当前列表
    await selectStudent(currentStudent.value)
    
  } catch (error) {
    console.error(error)
    ElMessage.error('回复失败')
  } finally {
    submittingReply.value = false
  }
}

// 5. 删除反馈
const handleDelete = (item) => {
  ElMessageBox.confirm(
    '确定要删除这条学员反馈吗？此操作无法恢复。',
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteFeedback(item.id)
      ElMessage.success('删除成功')
      // 从本地列表移除，避免重新请求
      feedbackList.value = feedbackList.value.filter(f => f.id !== item.id)
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// --- 工具函数 ---
const formatDate = (dateStr, time = false) => {
  if (!dateStr) return ''
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' }
  if(time) {
      options.hour = '2-digit'
      options.minute = '2-digit'
  }
  return new Date(dateStr).toLocaleString('zh-CN', options)
}

const getFeelingColor = (feeling) => {
  const map = {
    'easy': 'success',
    'normal': 'primary',
    'hard': 'warning',
    'exhausted': 'danger'
  }
  return map[feeling] || 'info'
}

const getFeelingText = (feeling) => {
  const map = {
    'easy': '轻松',
    'normal': '适中',
    'hard': '困难',
    'exhausted': '力竭'
  }
  return map[feeling] || feeling
}

onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.feedback-page {
  display: flex;
  height: calc(100vh - 84px); /* 减去顶部导航栏高度，适应全屏 */
  background-color: #f5f7fa;
  margin: -24px; /* 抵消可能的外部 padding */
}

/* 左侧侧边栏 */
.student-sidebar {
  width: 300px;
  background: white;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 50px;
  border-bottom: 1px solid #f0f0f0;
}

.sidebar-header h3 {
  margin: 0 0 15px 0;
  color: #303133;
}

.student-list {
  flex: 1;
  overflow-y: auto;
  padding: 50px;
}

.student-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 5px;
}

.student-item:hover {
  background-color: #f5f7fa;
}

.student-item.active {
  background-color: #ecf5ff;
  border-right: 3px solid #409EFF;
}

.student-info {
  margin-left: 12px;
  flex: 1;
  overflow: hidden;
}

.student-info .name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.student-info .plan-info {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 右侧主内容区 */
.feedback-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.main-header {
  background: white;
  padding: 50px 24px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.main-header h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
}

.feedback-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.empty-placeholder {
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: center;
}

/* 反馈卡片样式 */
.feedback-card {
  margin-bottom: 10px;
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 30px;
  border-bottom: 5px solid #f0f0f0;
}

.meta-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.feeling-tag {
  min-width: 60px;
  text-align: center;
}

.feedback-text {
  font-size: 15px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 20px;
  background: #f9f9f9;
  padding: 12px;
  border-radius: 6px;
}

/* 回复区域 */
.reply-section {
  margin-top: 15px;
}

.reply-box.answered {
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
  padding: 15px;
  border-radius: 6px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
  color: #67c23a;
}

.coach-label {
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 5px;
}

.reply-content {
  color: #606266;
  font-size: 14px;
  margin-bottom: 10px;
}

.original-content-preview {
  background: #f4f4f5;
  padding: 8px;
  border-radius: 4px;
  color: #909399;
  font-style: italic;
  font-size: 13px;
}
</style>
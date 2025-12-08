
<template>
  <el-dialog
    :model-value="visible"
    :title="`反馈: ${plan.name}`"
    width="600px"
    :close-on-click-modal="false"
    @update:modelValue="$emit('update:visible', $event)"
  >
    <div class="feedback-dialog-content">
      <!-- 新反馈表单 -->
      <el-form :model="newFeedbackForm" label-position="top" class="new-feedback-form">
        <el-form-item label="训练日期">
          <el-date-picker
            v-model="newFeedbackForm.feedbackDate"
            type="date"
            placeholder="选择反馈日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="训练感受 (1-5星)">
          <el-rate v-model="newFeedbackForm.rating" />
        </el-form-item>
        <el-form-item label="文字反馈">
          <el-input
            v-model="newFeedbackForm.content"
            type="textarea"
            :rows="4"
            placeholder="请详细描述本次训练的感受、遇到的问题或取得的进步..."
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitFeedback" :loading="isSubmitting">
            提交反馈
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 历史反馈 -->
      <el-divider>历史反馈记录</el-divider>
      <div v-if="isLoading" class="loading-container"><el-skeleton :rows="2" animated /></div>
      <el-timeline v-else-if="feedbackHistory.length > 0" class="feedback-timeline">
        <el-timeline-item
          v-for="item in feedbackHistory"
          :key="item.id"
          :timestamp="item.feedbackDate"
          placement="top"
        >
          <el-card>
            <div class="feedback-item-header">
              <el-rate :model-value="item.rating" disabled />
            </div>
            <p>{{ item.content }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无历史反馈记录" />
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch, reactive } from 'vue';
// 假设 API 文件已更新
import { getFeedbackForPlan, submitNewFeedback } from '@/api/feedback'; 
import { showSuccess, showError } from '@/utils/feedback';
import { getUserInfo } from '../../utils/auth';

const props = defineProps({
  visible: Boolean,
  plan: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['update:visible']);

const isLoading = ref(false);
const isSubmitting = ref(false);
const feedbackHistory = ref([]);
const newFeedbackForm = reactive({
  feedbackDate: new Date().toISOString().split('T')[0], // 默认今天
  rating: 0,
  content: '',
});

// 获取历史反馈
const fetchHistory = async () => {
  if (!props.plan?.id) return;
  isLoading.value = true;
  try {
    const response = await getFeedbackForPlan(props.plan.id); // 使用建议的 API
    feedbackHistory.value = response || [];
  } catch (error) {
    console.error('获取反馈历史失败:', error);
    showError('获取反馈历史失败');
  } finally {
    isLoading.value = false;
  }
};

// 提交新反馈
const submitFeedback = async () => {
  if (!newFeedbackForm.content.trim()) {
    showError('请输入反馈内容');
    return;
  }
  isSubmitting.value = true;
  try {
    const payload = {
      planId: props.plan.id,
      studentId: props.plan.studentId,
      ...newFeedbackForm,
    };
    await submitNewFeedback(payload); // 使用建议的 API
    showSuccess('反馈提交成功！');
    // 清空表单并重新加载历史
    newFeedbackForm.rating = 0;
    newFeedbackForm.content = '';
    fetchHistory();
  } catch (error) {
    console.error('提交反馈失败:', error);
    showError('提交反馈失败');
  } finally {
    isSubmitting.value = false;
  }
};

// 当弹窗打开时，获取历史记录
watch(() => props.visible, (isVisible) => {
  // 打印日志
  console.log('弹窗状态改变:', isVisible);
  if (isVisible) {
    fetchHistory();
  }
});

// 判断是否进入 此Dialog
watch(() => props.enterDialog, (isEnter) => { 
  console.log('进入此Dialog:', isEnter);
});
</script>

<style scoped>
.feedback-dialog-content {
  max-height: 65vh;
  overflow-y: auto;
  padding: 0 10px;
}
.new-feedback-form {
  margin-bottom: 24px;
}
.feedback-timeline {
  padding-left: 10px;
}
.feedback-item-header {
  margin-bottom: 8px;
}
</style>

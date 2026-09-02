<template>
  <div class="work-order-detail">
    <div class="modal-header">
      <div class="header-title">工单详情</div>
      <button class="close-btn" @click="handleClose"></button>
    </div>

    <div class="modal-body">
      <div class="detail-layout">
        <!-- 左侧：字段展示 -->
        <aside class="info-side">
          <div class="section-title"><span class="section-title-text">工单信息</span></div>
          <div class="info-content">
            <div class="info-row" v-for="item in infoFields" :key="item.label">
              <span class="info-label">{{ item.label }}：</span>
              <span v-if="item.multi" class="info-value info-value--multi">
                <span
                  v-for="(line, idx) in item.value"
                  :key="idx"
                  class="multi-line"
                >{{ line }}</span>
              </span>
              <span v-else class="info-value">{{ item.value }}</span>
            </div>
          </div>
        </aside>

        <!-- 右侧：流程图 -->
        <main class="flow-side">
          <div class="section-title"><span class="section-title-text">处置流程</span></div>
          <div class="flow-content">
            <div class="timeline">
              <div
                v-for="(step, idx) in flowSteps"
                :key="idx"
                class="timeline-item"
              >
                <div class="dot"></div>
                <div class="step-card">
                  <div class="step-title">{{ step.title }}</div>
                  <div class="step-content">{{ step.content }}</div>
                  <div class="step-time">{{ step.time }}</div>
                  <div v-if="step.images && step.images.length" class="step-images">
                    <img
                      v-for="(img, imgIdx) in step.images"
                      :key="imgIdx"
                      :src="img"
                      class="step-image"
                      @click="previewImage(img)"
                      alt="处置图片"
                    >
                  </div>
                </div>
              </div>
              <div v-if="!flowSteps.length" class="flow-empty">暂无流程数据</div>
            </div>
          </div>
        </main>
      </div>
    </div>

    <!-- 图片预览 -->
    <div v-if="previewVisible" class="image-preview-mask" @click="previewVisible = false">
      <img :src="previewSrc" class="image-preview-img" alt="预览">
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  rowData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close'])

const previewVisible = ref(false)
const previewSrc = ref('')

function previewImage(src) {
  previewSrc.value = src
  previewVisible.value = true
}

// 左侧字段展示
const infoFields = computed(() => {
  const d = props.rowData || {}
  // 巡查事项可能有多项，按换行符或逗号分割
  let inspectionMatters = []
  if (d.inspectionMatters) {
    inspectionMatters = String(d.inspectionMatters)
      .split(/[\n,，;；]/)
      .map(s => s.trim())
      .filter(s => s)
  }
  return [
    { label: '工单编号', value: d.code || d.orderNo || '-' },
    { label: '所在区域', value: d.districtName || d.county || '-' },
    { label: '工单类型', value: d.workType || d.orderType || '-' },
    { label: '工单地址', value: d.workOrderAddress || d.positionDesc || '-' },
    { label: '负责班组', value: d.reTeamName || d.team || '-' },
    { label: '班组组长', value: d.teamLeader || '-' },
    { label: '组长联系电话', value: d.teamLeaderPhone || '-' },
    { label: '巡查事项', value: inspectionMatters.length ? inspectionMatters : ['-'], multi: true }
  ]
})

// 右侧流程图
const flowSteps = computed(() => {
  const d = props.rowData || {}
  const steps = []
  // 创建工单
  if (d.createDate || d.createTime) {
    steps.push({
      title: '创建工单',
      content: d.workOrderDescription || d.describe || d.workOrderAddress || '-',
      time: d.createDate || d.createTime || '-',
      images: parseImages(d.image)
    })
  }
  // 派遣
  if (d.dispatchDate) {
    steps.push({
      title: '派遣',
      content: `${d.dispatchPersonName || '-'} 派遣`,
      time: d.dispatchDate || '-',
      images: []
    })
  }
  // 响应定位
  if (d.responsePositioningTime) {
    steps.push({
      title: '响应定位',
      content: '已响应定位',
      time: d.responsePositioningTime || '-',
      images: []
    })
  }
  // 到场
  if (d.responseTime) {
    steps.push({
      title: '到场',
      content: '已到场',
      time: d.responseTime || '-',
      images: parseImages(d.arrivalSystemFile)
    })
  }
  // 处置
  if (d.dealDate) {
    steps.push({
      title: '处置',
      content: d.dealRemark || '已处置',
      time: d.dealDate || '-',
      images: parseImages(d.disposeSystemFile)
    })
  }
  // 审核
  if (d.reviewDate) {
    steps.push({
      title: '审核',
      content: `${d.reviewPersonName || '-'} 审核通过`,
      time: d.reviewDate || '-',
      images: []
    })
  }
  // 结束
  if (d.responseTimeEnd || d.warningTimeEnd) {
    steps.push({
      title: '工单结束',
      content: '工单已结束',
      time: d.responseTimeEnd || d.warningTimeEnd || '-',
      images: []
    })
  }
  return steps
})

function parseImages(raw) {
  if (!raw) return []
  if (Array.isArray(raw)) return raw
  try {
    const parsed = JSON.parse(raw)
    if (Array.isArray(parsed)) return parsed
    return [String(parsed)]
  } catch {
    return String(raw).split(/[,，;；\s]+/).filter(s => s)
  }
}

function handleClose() {
  emit('close')
}
</script>

<script>
export default {
  name: 'WorkOrderDetail'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'Alibaba PuHuiTi 2.0';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
}

@font-face {
  font-family: 'Alibaba PuHuiTi 2.0';
  src: url('../font/Alibaba_PuHuiTi_2.0_75_SemiBold_75_SemiBold.ttf') format('truetype');
  font-weight: 600;
  font-style: normal;
}

.work-order-detail {
  font-family: 'Alibaba PuHuiTi 2.0', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1002;
  width: 1200px;
  height: 700px;
  transform: translate(-50%, -50%);
  color: #d8ecff;
  background: url('../img/Rectangle-346242153.png') no-repeat center / 100% 100%;
  border: 1px solid #a6ceff4d;
  box-shadow: 0 16px 56px rgba(0, 0, 0, 0.55);
  overflow: hidden;
  pointer-events: auto;
}

.modal-header {
  position: relative;
  width: calc(100% - 8px);
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    linear-gradient(90deg, #0b3b7000 0.2%, #2e6bdc59 50.04%, #0c2d5c00 99.87%),
    url('../img/waterlog_title_bg.png') no-repeat center / 100% 100%;
}

.header-title {
  text-align: center;
  font-size: 20px;
  font-weight: 500;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.close-btn {
  position: absolute;
  right: 20px;
  top: 8px;
  width: 34px;
  height: 34px;
  border: none;
  background: url('../img/close.png') no-repeat center / 100% 100%;
  cursor: pointer;
}

.modal-body {
  height: calc(100% - 48px);
  padding: 20px 28px;
  box-sizing: border-box;
  overflow: hidden;
}

.detail-layout {
  display: flex;
  gap: 20px;
  height: 100%;
}

/* 左侧 */
.info-side {
  width: 420px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%;
  overflow: hidden;
}

.section-title {
  height: 36px;
  line-height: 36px;
  padding-left: 16px;
  background: url('../img/liquid_device_title.png') no-repeat center / 100% 100%;
  flex-shrink: 0;
}

.section-title-text {
  font-size: 14px;
  font-weight: 500;
  line-height: 36px;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.info-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;

  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    border-radius: 2px;
    background: rgba(13, 155, 255, 0.45);
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

.info-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
  font-size: 14px;
  line-height: 22px;
}

.info-label {
  flex-shrink: 0;
  width: 110px;
  color: #749dc0;
}

.info-value {
  flex: 1;
  color: #ffffff;
  word-break: break-all;
}

.info-value--multi {
  display: flex;
  flex-direction: column;
}

.multi-line {
  display: block;
  text-indent: 2em;
  margin-bottom: 4px;

  &:last-child {
    margin-bottom: 0;
  }
}

/* 右侧流程图 */
.flow-side {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%;
  overflow: hidden;
}

.flow-content {
  flex: 1;
  overflow: hidden;
}

.timeline {
  position: relative;
  height: 100%;
  padding: 16px 15px 20px 42px;
  box-sizing: border-box;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    border-radius: 2px;
    background: rgba(13, 155, 255, 0.45);
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::before {
    content: '';
    position: absolute;
    left: 25px;
    top: 18px;
    bottom: 0;
    width: 4px;
    background: url('../img/waterlog_right_line.png') repeat-y center;
    background-size: 100% 100%;
  }
}

.timeline-item {
  position: relative;
  margin-bottom: 15px;
}

.dot {
  position: absolute;
  left: -23px;
  top: 2px;
  width: 16px;
  height: 16px;
  background: url('../img/waterlog_right_icon.png') no-repeat center;
  background-size: 100% 100%;
}

.step-card {
  padding: 14px 16px 12px;
  box-sizing: border-box;
  background: url('../img/waterlog_right_cardbg.png') no-repeat center;
  background-size: 100% 100%;
  border: none;
}

.step-title {
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 8px;
}

.step-content {
  color: #d8ecff;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 6px;
}

.step-time {
  color: #749dc0;
  font-size: 13px;
}

.step-images {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.step-image {
  width: 120px;
  height: 90px;
  object-fit: cover;
  border: 1px solid rgba(95, 188, 255, 0.4);
  border-radius: 3px;
  cursor: pointer;
  transition: border-color 0.2s;

  &:hover {
    border-color: rgba(95, 188, 255, 0.9);
  }
}

.flow-empty {
  color: #749dc0;
  font-size: 14px;
  text-align: center;
  padding: 40px 0;
}

/* 图片预览 */
.image-preview-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.8);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.image-preview-img {
  max-width: 80%;
  max-height: 80%;
  object-fit: contain;
  border: 2px solid rgba(95, 188, 255, 0.5);
  border-radius: 4px;
}
</style>

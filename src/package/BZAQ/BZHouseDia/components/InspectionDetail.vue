<template>
  <div class="inspection-detail-popup">
    <!-- 顶部标题栏 -->
    <div class="popup-header">
      <h3 class="title">巡查详情</h3>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <div class="inspection-detail">
      <!-- 基础信息 -->
      <div class="detail-section">
        <div class="detail-section__title">基础信息</div>
        <div class="detail-grid">
          <div class="detail-field">
            <span class="detail-label">巡查类型</span>
            <span class="detail-value">{{ detail.inspectionType }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">企业名称</span>
            <span class="detail-value">{{ detail.companyName }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">企业地址</span>
            <span class="detail-value">{{ detail.companyAddress }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">巡查时间</span>
            <span class="detail-value">{{ detail.inspectionTime }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">巡查人</span>
            <span class="detail-value">{{ detail.inspector }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">处置状态</span>
            <span class="detail-value">{{ detail.handleStatus }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">创建时间</span>
            <span class="detail-value">{{ detail.createTime }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">巡查创建人</span>
            <span class="detail-value">{{ detail.createBy }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">最近巡查时间</span>
            <span class="detail-value">{{ detail.lastInspectionTime }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">巡查次数</span>
            <span class="detail-value">{{ detail.inspectionCount }}</span>
          </div>
        </div>
      </div>

      <!-- 巡查内容 -->
      <div class="detail-section">
        <div class="detail-section__title">巡查内容</div>
        <div class="detail-grid detail-grid--single">
          <div class="detail-field">
            <span class="detail-label">巡查内容</span>
            <span class="detail-value detail-value--content">{{ detail.inspectionContent }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">现场情况</span>
            <span class="detail-value detail-value--content">{{ detail.sceneSituation }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">问题类别</span>
            <span class="detail-value">{{ detail.problemCategory }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">问题描述</span>
            <span class="detail-value detail-value--content">{{ detail.problemDescription }}</span>
          </div>
        </div>
      </div>

      <!-- 巡查图片 -->
      <div class="detail-section">
        <div class="detail-section__title">巡查图片</div>
        <div class="detail-image-area">
          <template v-if="detail.inspectionImages && detail.inspectionImages.length">
            <div class="detail-image-list">
              <el-image
                v-for="(img, idx) in detail.inspectionImages"
                :key="idx"
                class="detail-image"
                :src="img"
                :preview-src-list="detail.inspectionImages"
                :initial-index="idx"
                fit="cover"
                preview-teleported
              />
            </div>
          </template>
          <div v-else class="no-image">暂无图片</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElImage } from 'element-plus'
import axios from 'axios'
import closeIcon from '../img/close.png'

const props = defineProps({
  detailData: {
    type: Object,
    default: () => ({})
  }
})

defineEmits(['close'])

const detail = ref({
  inspectionType: '',
  companyName: '',
  companyAddress: '',
  inspectionTime: '',
  inspector: '',
  handleStatus: '',
  createTime: '',
  createBy: '',
  lastInspectionTime: '',
  inspectionCount: '',
  inspectionContent: '',
  sceneSituation: '',
  problemCategory: '',
  problemDescription: '',
  inspectionImages: []
})

// 接口地址
const API_URL = 'http://23.210.227.34:23343/ywtg/api/boot/system/resiComm/bizToResiHouseEventStatusDetail'

// 查询详情
async function fetchDetail(inspectionTaskId) {
  if (!inspectionTaskId) return
  try {
    const res = await axios.post(API_URL, {
      index: 1,
      size: 10,
      inspectionTaskId: inspectionTaskId
    })
    console.log('商改住巡查上报详情查询返回:', res)
    const data = res.data?.data || {}
    // 巡查图片URL（多个逗号分隔）转数组
    const imageUrls = data.imageUrls ? data.imageUrls.split(',').filter(url => url.trim()) : []
    detail.value = {
      inspectionType: data.inspectionType || '--',
      companyName: data.projectName || '--',
      companyAddress: data.projectAddress || '--',
      inspectionTime: data.actualTime || '--',
      inspector: data.inspectionUserName || '--',
      handleStatus: data.inspectionStatus || '--',
      createTime: data.createdTime || '--',
      createBy: data.createdName || '--',
      lastInspectionTime: data.planTime || '--',
      inspectionCount: String(data.abnormalCount ?? '--'),
      inspectionContent: data.description || '--',
      sceneSituation: data.checkResult || '--',
      problemCategory: data.problemType || '--',
      problemDescription: data.checkRemark || '--',
      inspectionImages: imageUrls
    }
  } catch (error) {
    console.error('商改住巡查上报详情查询失败:', error)
  }
}

watch(() => props.detailData, (val) => {
  if (val && val.inspectionTaskId) {
    fetchDetail(val.inspectionTaskId)
  }
}, { immediate: true })
</script>

<style lang="scss" scoped>
.inspection-detail-popup {
  width: 1284px;
  height: 756px;
  background: url('../img/diaBg.png') no-repeat center / 100% 100%;
  transform: translate(-50%, -50%);
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1002;
  padding: 10px 25px;
  color: #fff;
  font-family: 'Microsoft YaHei';

  // 顶部标题栏
  .popup-header {
    height: 50px;
    line-height: 50px;
    padding: 0 25px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transform: translateY(10px);

    .title {
      font-size: 26px;
      font-weight: bold;
      margin: 0;
      color: #fff;
    }

    .close-btn {
      width: 40px;
      height: 40px;
      cursor: pointer;
    }
  }
}

.inspection-detail {
  padding: 15px 25px;
  margin-top: 10px;
  height: calc(100% - 70px);
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }

  .detail-section {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

    &__title {
      font-size: 14px;
      font-weight: 600;
      color: #04bcfa;
      margin-bottom: 12px;
      padding-left: 8px;
      border-left: 3px solid #04bcfa;
    }
  }

  .detail-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px 20px;

    &--single {
      grid-template-columns: 1fr;
    }
  }

  .detail-field {
    display: flex;
    align-items: flex-start;
    gap: 8px;

    .detail-label {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.5);
      white-space: nowrap;
      flex-shrink: 0;
      min-width: 80px;
    }

    .detail-value {
      font-size: 13px;
      color: #fff;
      word-break: break-all;
      flex: 1;

      &--content {
        line-height: 1.6;
        min-height: 40px;
        padding: 8px 12px;
        background: rgba(255, 255, 255, 0.05);
        border-radius: 4px;
        border: 1px solid rgba(255, 255, 255, 0.08);
      }
    }
  }

  .detail-image-area {
    .no-image {
      color: rgba(255, 255, 255, 0.4);
      font-size: 13px;
      text-align: center;
      padding: 20px;
    }
  }

  .detail-image-list {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .detail-image {
    width: 120px;
    height: 90px;
    border-radius: 4px;
    border: 1px solid rgba(0, 160, 255, 0.2);
    cursor: pointer;

    :deep(.el-image__inner) {
      border-radius: 4px;
    }
  }
}
</style>

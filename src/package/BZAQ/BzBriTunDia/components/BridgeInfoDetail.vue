<template>
  <div class="bridge-info-detail">
    <!-- 顶部标题栏 -->
    <div class="popup-header">
      <div class="title-bg">
        <h3 class="title">{{ detailTitle }}</h3>
      </div>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <!-- 弹窗内容区 -->
    <div class="popup-body">
      <!-- 第一行：基础信息+图片 -->
      <div class="top-section">
        <!-- 左侧基础信息 -->
        <div class="info-list">
          <div class="info-item">
            <img class="item-icon" :src="jcIcon" alt="监测测点" />
            <span class="item-label">监测测点：</span>
            <span class="item-value">{{ monitorDetail.pointCount }}</span>
          </div>
          <div class="info-item">
            <img class="item-icon" :src="hsIcon" alt="黄色预警" />
            <span class="item-label">黄色预警：</span>
            <span class="item-value yellow">{{ monitorDetail.yellowWarning }}</span>
          </div>
          <div class="info-item">
            <img class="item-icon" :src="redIcon" alt="红色预警" />
            <span class="item-label">红色预警：</span>
            <span class="item-value red">{{ monitorDetail.redWarning }}</span>
          </div>
          <div class="info-item">
            <img class="item-icon" :src="healthIcon" alt="健康得分" />
            <span class="item-label">健康得分：</span>
            <span class="item-value">{{ monitorDetail.healthyPoints }}</span>
          </div>
        </div>

        <!-- 右侧图片 -->
        <div class="info-img">
          <img :src="monitorDetail.url || itemImg" alt="设施图片" />
        </div>
      </div>

      <!-- 第二行：异常信息+查看报告按钮 -->
      <div class="bottom-section">
        <!-- 左侧异常信息 -->
        <div class="warn-info">
          <img class="warn-icon" :src="itemIconImg" alt="异常信息" />
          <span class="warn-text">{{ monitorDetail.message }}</span>
        </div>

        <!-- 右侧查看报告按钮 -->
        <!-- <div class="report-btn" @click="$emit('open-pdf-preview')">
          <span>查看</span>
          <span>报告</span>
        </div> -->
      </div>

      <!-- 第三行：按钮 -->
      <div class="btn-row">
        <div class="action-btn" @click="openModelIframe">监测可视化</div>
        <div class="action-btn" @click="openFacilityDetail">设施详情</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import axios from 'axios'
import closeIcon from '../img/close.png'
import itemImg from '../img/item.png'
import jcIcon from '../img/jcIcon.png'
import hsIcon from '../img/hsIcon.png'
import redIcon from '../img/redIcon.png'
import healthIcon from '../img/healthIcon.png'
import itemIconImg from '../img/itemIcon.png'

const props = defineProps<{
  payload?: Record<string, any>
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'open-pdf-preview'): void
  (e: 'open-three-preview'): void
  (e: 'open-model-iframe', payload: Record<string, any>): void
  (e: 'open-facility-detail', payload: Record<string, any>): void
}>()

const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/bridge'
const monitorDetail = ref({
  pointCount: '-',
  yellowWarning: '-',
  redWarning: '-',
  healthyPoints: '暂无得分',
  message: '暂无分析',
  url:''
})

const detailTitle = computed(() => props.payload?.structName || '桥隧监测详情')

function getResponseData(data: any) {
  return data?.data ?? data ?? {}
}

function getResponseList(data: any) {
  const payload = getResponseData(data)
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.content)) return payload.content
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload?.list)) return payload.list
  if (Array.isArray(payload?.rows)) return payload.rows
  return []
}

function normalizeMonitorWindow(data: any) {
  return {
    pointCount: data?.pointCount ?? '-',
    yellowWarning: data?.yellowWarning ?? '-',
    redWarning: data?.redWarning ?? '-',
    healthyPoints: data?.healthyPoints || '暂无得分',
    message: data?.message || props.payload?.message || '暂无分析',
    url:data?.structSchematicDiagram?.url
  }
}

async function resolveStructId() {
  if (props.payload?.structId) return props.payload.structId
  const structName = props.payload?.structName
  if (!structName) return ''

  try {
    const res = await axios.get(`${BASE_URL}/list`, {
      params: {
        structType: props.payload?.structType,
        structName,
        pageIndex: 1,
        pageSize: 10
      }
    })
    const list = getResponseList(res?.data)
    const matched = list.find(item => item?.structName === structName)
      || list.find(item => String(item?.structName || '').includes(structName) || structName.includes(String(item?.structName || '')))
      || list[0]
    return matched?.structId || ''
  } catch (error) {
    console.error('查询桥隧构筑物ID失败:', error)
    return ''
  }
}

async function fetchMonitorWindow() {
  const structId = await resolveStructId()
  if (!structId) {
    monitorDetail.value = normalizeMonitorWindow({})
    return
  }

  try {
    const res = await axios.get(`${BASE_URL}/prjStructReal/window`, {
      params: { structId }
    })
    monitorDetail.value = normalizeMonitorWindow(getResponseData(res?.data))
  } catch (error) {
    console.error('获取桥隧实时监测窗口失败:', error)
    monitorDetail.value = normalizeMonitorWindow({})
  }
}

async function openModelIframe() {
  const structId = await resolveStructId()
  if (!structId) {
    console.warn('未获取到构筑物ID，无法打开监测可视化')
    return
  }

  try {
    const res = await axios.get(`${BASE_URL}/model`, {
      params: { structId }
    })
    const url = getResponseData(res?.data)
    if (!url) {
      console.warn('模型地址为空，无法打开监测可视化')
      return
    }
    emit('open-model-iframe', {
      url,
      title: detailTitle.value,
      structId
    })
  } catch (error) {
    console.error('获取桥隧模型地址失败:', error)
  }
}

async function openFacilityDetail() {
  const structId = await resolveStructId()
  if (!structId) {
    console.warn('未获取到构筑物ID，无法打开设施详情')
    return
  }
  emit('open-facility-detail', {
    ...props.payload,
    diaName: 'facility-monitor-detail',
    structId
  })
}

watch(() => props.payload, fetchMonitorWindow, { deep: true })

onMounted(() => {
  fetchMonitorWindow()
})
</script>

<script lang="ts">
export default {
  name: 'BridgeInfoDetail'
}
</script>

<style lang="scss" scoped>
.bridge-info-detail {
  width: 516px;
  height: 354px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  background: url('../img/diaBg.png') no-repeat center center;
  background-size: 100% 100%;
  display: flex;
  flex-direction: column;
  pointer-events: auto;

  // 顶部标题栏
  .popup-header {
    width: 100%;
    height: 50px;
    position: relative;
    flex-shrink: 0;

    .title-bg {
      width: 100%;
      height: 100%;
      background: url('../img/diaTitleBg.png') no-repeat center center;
      background-size: 100% 100%;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .title {
      text-align: center;
      font-family: 'Alibaba PuHuiTi 2.0';
      font-size: 20px;
      font-style: normal;
      font-weight: 500;
      line-height: normal;
      background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
      background-clip: text;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      margin: 0;
    }

    .close-btn {
      width: 40px;
      height: 40px;
      cursor: pointer;
      position: absolute;
      right: 20px;
      top: 50%;
      transform: translateY(-50%);
    }
  }

  // 内容区
  .popup-body {
    width: 100%;
    height: calc(100% - 50px);
    padding: 12px 25px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;

    .top-section {
      width: 100%;
      height: 137px;
      display: flex;
      justify-content: space-between;
      gap: 20px;

      .info-list {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 10px;
        padding-top: 8px;

        .info-item {
          display: flex;
          align-items: center;
          gap: 8px;

          .item-icon {
            width: 14px;
            height: 14px;
            flex-shrink: 0;
          }

          .item-label {
            color: #8BBCE6;
            font-size: 16px;
          }

          .item-value {
            color: #fff;
            font-size: 14px;
            flex: 1;

            &.yellow {
              color: #FFD039;
            }

            &.red {
              color: #FF6B6B;
            }
          }
        }
      }

      .info-img {
        width: 233px;
        height: 137px;
        flex-shrink: 0;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
    }

    .bottom-section {
      width: 100%;
      display: flex;
      gap: 10px;
      margin-top: 24px;

      .warn-info {
        width: 522px;
        height: 52px;
        background: url('../img/itemwarn.png') no-repeat center center;
        background-size: 100% 100%;
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 0 12px;
        box-sizing: border-box;

        .warn-icon {
          width: 14px;
          height: 14px;
          flex-shrink: 0;
        }

        .warn-text {
          font-feature-settings: 'liga' off, 'clig' off;
          font-family: 'Alibaba PuHuiTi 2.0';
          font-size: 16px;
          font-style: normal;
          font-weight: 700;
          line-height: normal;
          letter-spacing: 0.32px;
          background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
          background-clip: text;
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
        }
      }

      .report-btn {
        width: 56px;
        height: 52px;
        background: url('../img/itemwarn.png') no-repeat center center;
        background-size: 100% 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        flex-shrink: 0;

        span {
          color: #72B5F5;
          font-size: 16px;
          line-height: 1.2;
        }
      }
    }

    .btn-row {
      width: 100%;
      display: flex;
      justify-content: space-evenly;
      gap: 16px;
      margin-top: 20px;

      .action-btn {
        width: 110px;
        height: 36px;
        background: url('../img/buttonBg.png') no-repeat center center;
        background-size: 100% 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        color: #fff;
        cursor: pointer;
      }
    }
  }
}
</style>

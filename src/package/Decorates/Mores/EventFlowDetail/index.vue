<template>
  <div class="event-flow-detail" :style="containerStyle">
    <header class="detail-header">
      <h1>{{ eventInfo.title }}</h1>
      <p>事件编号：{{ eventInfo.eventNo }}</p>
    </header>

    <main class="detail-main">
      <section class="basic-section">
        <div class="section-label">事件基本信息</div>
        <div class="basic-grid">
          <div
            v-for="item in basicInfo"
            :key="item.label"
            class="basic-item"
            :class="{ 'basic-item--wide': item.wide }"
          >
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
        <div class="event-content-card">
          <span>事件内容</span>
          <p>{{ eventInfo.content }}</p>
        </div>
        <div class="event-attachment-card">
          <span>事件附件</span>
          <img :src="feedbackImage" alt="" />
        </div>
      </section>

      <section class="process-section">
        <div class="section-label">流转流程</div>
        <div class="flow-timeline">
          <article
            v-for="item in processItems"
            :key="`${item.title}-${item.time}`"
            class="flow-node"
          >
            <span class="flow-dot"></span>
            <div class="flow-content">
              <h2>{{ item.title }}</h2>
              <p>{{ item.content }}</p>
              <time>{{ item.time }}</time>
            </div>
          </article>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps } from 'vue'

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  }
})

const feedbackImage = new URL('./img/example.png', import.meta.url).href

const eventInfo = {
  title: '鹅岭栈桥-两路口片区佛图关公园鹅岭栈桥上154发现吸烟检测【AI智能预警】',
  eventNo: 'SHYZDK20260802151304410283',
  status: '已办结',
  address: '鹅岭栈桥-两路口片区佛图关公园鹅岭栈桥上154',
  type: '设施运行/园林绿化/城市公园/鹅岭栈桥景区抽烟处置（渝中区）',
  level: '一般',
  eventTime: '2026-08-02 15:13:04',
  reportTime: '2026-08-02 15:13:04',
  area: '两路口',
  content: '大客流综合场景接收到AI告警消息，吸烟检测上报治理中心，请相关单位研判处置'
}

const basicInfo = [
  { label: '事件状态', value: eventInfo.status },
  { label: '事件等级', value: eventInfo.level },
  { label: '所属区域', value: eventInfo.area },
  { label: '事发时间', value: eventInfo.eventTime },
  { label: '上报时间', value: eventInfo.reportTime },
  { label: '事发地址', value: eventInfo.address, wide: true },
  { label: '事件类型', value: eventInfo.type, wide: true }
]

const processItems = [
  {
    title: '业务事项完结',
    time: '2026-08-03 13:29:39',
    content: '安全应急科'
  },
  {
    title: '已签收',
    time: '2026-08-03 13:29:10',
    content: '安全应急科'
  },
  {
    title: '业务事项启动',
    time: '2026-08-02 15:13:07',
    content: '渝中区数字化城市运行和治理中心'
  },
  {
    title: '业务事项启动',
    time: '2026-08-02 15:13:07',
    content: '渝中区城管局'
  },
  {
    title: '事件接收',
    time: '2026-08-02 15:13:05',
    content: '-'
  }
]

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '540px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '360px'
  }
})
</script>

<script lang="ts">
export default {
  name: 'EventFlowDetail',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.event-flow-detail {
  box-sizing: border-box;
  padding: 10px 8px 12px;
  overflow: hidden;
  color: #d8efff;
  font-family: Microsoft YaHei, PingFang SC, Arial, sans-serif;
  background:
    radial-gradient(circle at 50% 0, rgba(41, 133, 217, 0.34), transparent 34%),
    linear-gradient(180deg, rgba(5, 45, 92, 0.96), rgba(2, 24, 58, 0.98));
  border: 1px solid rgba(24, 132, 239, 0.88);
  box-shadow: inset 0 0 24px rgba(39, 146, 225, 0.28);
}

.detail-header {
  padding: 0 0 7px;
  border-bottom: 1px solid rgba(95, 188, 255, 0.18);

  h1 {
    margin: 0;
    color: #ffffff;
    font-size: 13px;
    font-weight: 700;
    line-height: 1.42;
    letter-spacing: 0;
  }

  p {
    margin: 8px 0 0;
    color: #7fcaff;
    font-size: 10px;
    line-height: 1;
  }
}

.detail-main {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 10px;
  height: calc(100% - 48px);
  min-height: 0;
  margin-top: 8px;
}

.basic-section {
  min-width: 0;
  min-height: 0;
  overflow: hidden;
}

.basic-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 5px;
}

.basic-item,
.event-content-card,
.event-attachment-card {
  box-sizing: border-box;
  background:
    linear-gradient(90deg, rgba(26, 97, 168, 0.42), rgba(14, 67, 129, 0.18)),
    repeating-linear-gradient(-22deg, rgba(95, 188, 255, 0.06) 0 1px, transparent 1px 7px);
  border: 1px solid rgba(39, 146, 225, 0.34);
}

.basic-item {
  min-height: 34px;
  padding: 5px 7px;

  span,
  strong {
    display: block;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  span {
    color: #82c8ff;
    font-size: 10px;
    line-height: 1;
  }

  strong {
    margin-top: 4px;
    color: #ffffff;
    font-size: 11px;
    font-weight: 500;
    line-height: 1.15;
  }
}

.basic-item--wide {
  grid-column: span 2;
}

.event-content-card {
  margin-top: 6px;
  padding: 6px 7px;

  span {
    color: #82c8ff;
    font-size: 10px;
  }

  p {
    margin: 4px 0 0;
    color: #d8efff;
    font-size: 11px;
    line-height: 1.38;
  }
}

.event-attachment-card {
  display: grid;
  grid-template-columns: 54px 1fr;
  gap: 8px;
  align-items: center;
  margin-top: 6px;
  padding: 6px;

  span {
    color: #82c8ff;
    font-size: 10px;
  }

  img {
    display: block;
    width: 72px;
    height: 52px;
    object-fit: cover;
    border: 1px solid rgba(95, 188, 255, 0.45);
  }
}

.process-section {
  box-sizing: border-box;
  min-width: 0;
  min-height: 0;
  padding: 6px 6px 8px;
  background:
    linear-gradient(90deg, rgba(26, 97, 168, 0.42), rgba(14, 67, 129, 0.18)),
    repeating-linear-gradient(-22deg, rgba(95, 188, 255, 0.08) 0 1px, transparent 1px 7px);
  border: 1px solid rgba(39, 146, 225, 0.34);
  box-shadow: inset 0 0 14px rgba(33, 132, 219, 0.18);
}

.flow-timeline {
  position: relative;
  height: calc(100% - 17px);
  padding: 3px 0 0 20px;
  box-sizing: border-box;
  overflow: hidden;
}

.flow-timeline::before {
  content: '';
  position: absolute;
  left: 6px;
  top: 11px;
  bottom: 18px;
  width: 1px;
  background: linear-gradient(180deg, rgba(151, 209, 255, 0.9), rgba(95, 188, 255, 0.18));
}

.flow-node {
  position: relative;
  min-height: 48px;
  padding-bottom: 6px;
}

.flow-dot {
  position: absolute;
  left: -19px;
  top: 4px;
  width: 10px;
  height: 10px;
  box-sizing: border-box;
  border: 2px solid #b9d9ed;
  border-radius: 50%;
  background: #08305f;
  box-shadow: 0 0 8px rgba(95, 188, 255, 0.48);
}

.flow-content {
  h2,
  p,
  time {
    display: block;
    margin: 0;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  h2 {
    color: #ffffff;
    font-size: 12px;
    font-weight: 700;
    line-height: 1.2;
    white-space: nowrap;
  }

  p {
    margin-top: 5px;
    color: #d8efff;
    font-size: 10px;
    line-height: 1.25;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  time {
    margin-top: 4px;
    color: #88abc8;
    font-size: 10px;
    line-height: 1.1;
    white-space: nowrap;
  }
}

.media-section {
  margin-top: 10px;
}

.section-label {
  height: 17px;
  color: #9ed9ff;
  font-size: 11px;
  line-height: 17px;
}

.feedback-image {
  display: block;
  width: 100%;
  height: 82px;
  object-fit: cover;
  border: 1px solid rgba(95, 188, 255, 0.45);
}

.attachment-section {
  margin-top: 8px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 100%;
  height: 24px;
  padding: 0 8px;
  box-sizing: border-box;
  color: #bfe7ff;
  font-size: 11px;
  text-align: left;
  background: linear-gradient(90deg, rgba(16, 83, 154, 0.72), rgba(11, 64, 125, 0.42));
  border: 1px solid rgba(39, 146, 225, 0.26);
  cursor: pointer;
}

.file-icon {
  position: relative;
  width: 9px;
  height: 12px;
  border: 1px solid #5fbcff;
  box-sizing: border-box;

  &::after {
    content: '';
    position: absolute;
    right: -1px;
    top: -1px;
    width: 4px;
    height: 4px;
    background: #0b407a;
    border-left: 1px solid #5fbcff;
    border-bottom: 1px solid #5fbcff;
  }
}
</style>

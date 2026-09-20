<template>
  <view class="panel">
    <view class="panel-title">
      视频中心
      <text class="subtle">本地合成演示素材</text>
    </view>
    <view class="toolbar">
      <SelectField
        v-model="layout"
        :options="[
          { value: '1', label: '单画面' },
          { value: '4', label: '四宫格' },
        ]"
      />
      <SelectField
        v-if="layout === '1'"
        v-model="selected"
        :options="channels.map((c) => ({ value: c.id, label: c.name }))"
      />
    </view>
    <view :class="layout === '4' ? 'equal-columns' : ''">
      <view v-for="c in visible" :key="c.id" class="video-card">
        <view class="panel-title">{{ c.name }}</view>
        <video
          v-if="active && c.online"
          :id="c.id"
          :src="c.src"
          :controls="true"
          :show-center-play-btn="true"
          :autoplay="false"
          :loop="true"
          @error="failed[c.id] = true"
        />
        <view v-if="!c.online || failed[c.id]" class="empty">
          {{ c.online ? '本地视频加载失败，请检查打包资源' : '通道离线 · 无视频信号' }}
        </view>
        <text class="subtle">{{ c.online ? '可播放本地视频，未连接真实摄像头' : '预设离线状态' }}</text>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useDemo } from '../stores/demo'
import SelectField from './SelectField.vue'
defineProps<{ active: boolean }>()
const demo = useDemo(),
  layout = ref('4'),
  channels = computed(() =>
    demo.state.phase2.videos.filter((c) => demo.user?.regionIds.includes(c.regionId)),
  ),
  selected = ref(channels.value[0]?.id || ''),
  failed = reactive<Record<string, boolean>>({}),
  visible = computed(() =>
    layout.value === '4' ? channels.value : channels.value.filter((c) => c.id === selected.value),
  )
</script>
<style scoped>
.video-card {
  min-width: 0;
  padding: 12px;
  background: #f4f8fc;
  border-radius: 8px;
  margin-bottom: 16px;
}
.video-card video {
  width: 100%;
  height: 260px;
}
.video-card .empty {
  height: 230px;
  background: #10283e;
  color: #c0d4e8;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>

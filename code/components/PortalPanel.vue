<template>
  <view v-if="mode === 'portalDetail'" class="panel">
    <template v-if="article">
      <view class="panel-title">{{ article.title }}</view>
      <text class="subtle">{{ article.date }} · 发布信息</text>
      <view class="divider" />
      <text class="content-text">{{ article.content }}</text>
    </template>
    <view v-else class="empty">
      信息不存在
      <text class="link block" @click="go('portal')">返回工作门户</text>
    </view>
  </view>
  <view v-else>
    <view class="portal-greeting">
      <view>
        <text class="greeting-title">{{ demo.user?.displayName }}，工作顺利</text>
        <text class="subtle">
          {{ formatTime(demo.state.simulationTime).slice(0, 10) }} · 关注运行，及时处置，每一步都有记录。
        </text>
      </view>
      <button class="button secondary" @click="go('alarms')">进入报警中心 ↗</button>
    </view>
    <view class="toolbar">
      <button class="button secondary" @click="go('advancedSettings')">定制门户</button>
      <button class="button secondary" @click="go('mobileReport')">事件上报</button>
    </view>
    <view class="kpi-grid">
      <view v-for="k in customKpis" :key="k.label" class="kpi" @click="go(k.page)">
        <text class="kpi-label">{{ k.label }}</text>
        <text class="kpi-number">{{ k.value }}</text>
        <text class="kpi-note">点击查看详情</text>
        <text class="kpi-icon">{{ k.icon }}</text>
      </view>
    </view>
    <view class="columns">
      <view class="stack">
        <view class="panel">
          <view class="panel-title">
            我的业务
            <text class="link" @click="go('orders')">全部工单 ↗</text>
          </view>
          <view class="tabs">
            <view
              v-for="t in ['待办', '已办', '我的申请']"
              :key="t"
              :class="['tab', { active: tab === t }]"
              @click="tab = t"
            >
              {{ t }}
            </view>
          </view>
          <view
            v-for="w in myOrders.slice(0, 6)"
            :key="w.id"
            class="list-item"
            @click="go('orderDetail', { id: w.id })"
          >
            <text class="list-item-title">{{ w.title }}</text>
            <view class="list-item-meta">
              <text>{{ w.id }} · {{ formatTime(w.createdAt) }}</text>
              <StatusTag :value="w.status" kind="order" />
            </view>
          </view>
          <view v-if="!myOrders.length" class="empty">暂无{{ tab }}工单</view>
        </view>
        <view class="panel">
          <view class="panel-title">
            我的巡检任务
            <text class="link" @click="go('inspection')">任务台账 ↗</text>
          </view>
          <view
            v-for="t in myTasks.slice(0, 4)"
            :key="t.id"
            class="list-item"
            @click="go('taskDetail', { id: t.id })"
          >
            <text class="list-item-title">{{ t.name }}</text>
            <view class="list-item-meta">
              <text>{{ t.checks.filter((c) => c.result).length }}/{{ t.checks.length }} 处设施已检查</text>
              <StatusTag :value="t.status" kind="task" />
            </view>
          </view>
          <view v-if="!myTasks.length" class="empty">暂无分配给您的任务</view>
        </view>
      </view>
      <view class="stack">
        <view class="panel">
          <view class="panel-title">通知与动态</view>
          <view
            v-for="n in portal.announcements"
            :key="n.id"
            class="list-item"
            @click="go('portalDetail', { type: 'notice', id: n.id })"
          >
            <text class="list-item-title">{{ n.title }}</text>
            <text class="subtle">{{ n.date }} · 通知公告</text>
          </view>
          <view
            v-for="n in portal.news"
            :key="n.id"
            class="list-item"
            @click="go('portalDetail', { type: 'news', id: n.id })"
          >
            <text class="list-item-title">{{ n.title }}</text>
            <text class="subtle">{{ n.date }} · 公司动态</text>
          </view>
        </view>
        <view class="panel">
          <view class="panel-title">
            通讯录
            <text class="subtle">内线电话</text>
          </view>
          <input class="field" v-model="contactKeyword" placeholder="搜索姓名 / 部门" />
          <view v-for="c in contacts" :key="c.id" class="list-item">
            <text class="list-item-title">
              {{ c.name }}
              <text class="contact-extension">{{ c.extension }}</text>
            </text>
            <text class="subtle">{{ c.department }}</text>
          </view>
          <view v-if="!contacts.length" class="empty">没有匹配的联系人</view>
        </view>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { computed, ref } from 'vue'
import { useDemo } from '../stores/demo'
import { portal } from '../repositories/seed'
import { formatTime } from '../domain/metrics'
import { go } from '../navigation/routeMap'
import StatusTag from './StatusTag.vue'
const props = defineProps<{ mode: string; query: Record<string, string> }>(),
  demo = useDemo(),
  tab = ref('待办'),
  contactKeyword = ref('')
const article = computed(() =>
  (props.query.type === 'news' ? portal.news : portal.announcements).find((a) => a.id === props.query.id),
)
const myOrders = computed(() =>
  demo.orders.filter((w) =>
    tab.value === '我的申请'
      ? w.createdBy === demo.user?.id
      : (demo.user?.roleId === 'admin' || w.assigneeId === demo.user?.id) &&
        (tab.value === '待办' ? w.status !== 'completed' : w.status === 'completed'),
  ),
)
const myTasks = computed(() =>
  demo.tasks.filter((t) => demo.user?.roleId === 'admin' || t.assigneeId === demo.user?.id),
)
const contacts = computed(() =>
  portal.contacts.filter((c) => (c.name + c.department).includes(contactKeyword.value)),
)
const kpis = computed(() => [
  { label: '待闭环告警', value: demo.metrics.alarms, page: 'alarms', icon: '◉' },
  { label: '进行中工单', value: demo.metrics.orders, page: 'orders', icon: '▤' },
  {
    label: '我的巡检',
    value: myTasks.value.filter((t) => t.status !== 'completed').length,
    page: 'inspection',
    icon: '⌁',
  },
  { label: '管网设施', value: demo.metrics.facilities, page: 'map', icon: '◇' },
])
const customKpis = computed(() =>
  (demo.state.phase2.portalCards[demo.user?.id || ''] || ['alarms', 'orders', 'inspection', 'map']).map(
    (page) =>
      kpis.value.find((k) => k.page === page) || {
        page,
        label:
          ({ video: '视频中心', energy: '能耗与运行', mobileReport: '事件上报' } as Record<string, string>)[
            page
          ] || page,
        value: '↗',
        icon: '◇',
      },
  ),
)
</script>
<style scoped>
.portal-greeting {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 4px 0 26px;
}
.greeting-title {
  display: block;
  font-size: 20px;
  margin-bottom: 10px;
  font-weight: 500;
}
.contact-extension {
  float: right;
  font-size: 10px;
  color: #599ad5;
}
@media (max-width: 900px) {
  .portal-greeting {
    align-items: flex-start;
    gap: 16px;
  }
  .portal-greeting .button {
    font-size: 10px;
    padding: 10px;
  }
  .greeting-title {
    font-size: 17px;
  }
}
</style>

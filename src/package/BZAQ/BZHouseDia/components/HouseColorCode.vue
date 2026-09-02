<template>
  <div class="color-code-popup">
    <!-- 顶部标题栏 -->
    <div class="popup-header">
      <h3 class="title">四色赋码</h3>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <div class="color-code-content">
      <!-- 赋码分类 -->
      <div class="code-category-row">
        <div
          v-for="item in codeCategories"
          :key="item.key"
          class="code-category-item"
          :style="{ '--code-color': item.color }"
          @click="switchCategory(item.key)"
        >
          <div class="code-category-left">
            <img class="code-icon" :src="item.icon" alt="" />
            <span class="code-name">{{ item.label }}</span>
          </div>
          <div class="code-count">{{ item.count }}</div>
        </div>
      </div>

      <!-- AI 打字机 -->
      <div class="ai-talk-wrapper">
        <div ref="aiTextRef" class="ai-talk-content">{{ aiDisplayText }}</div>
      </div>

      <!-- 房屋列表 -->
      <div class="house-list-wrapper">
        <!-- 表格 -->
        <div class="table-container">
          <n-data-table
            class="house-table"
            :columns="columns"
            :data="tableData"
            :bordered="false"
            :single-line="false"
            size="small"
          />
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <n-pagination
            :page="pagination.current"
            :page-count="pagination.totalPage"
            :page-slot="5"
            @update:page="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { h, ref, computed, nextTick, onMounted, onBeforeUnmount, watch } from 'vue'
import { NDataTable, NPagination } from 'naive-ui'
import axios from 'axios'
import closeIcon from '../img/close.png'
import fmBg from '../img/fmBg.png'
import lvmaIcon from '../img/lvma.png'
import lanmaIcon from '../img/lanma.png'
import huangmaIcon from '../img/huangma.png'
import hongmaIcon from '../img/hongma.png'

defineEmits(['close'])

const props = defineProps({
  houseType: {
    type: String,
    default: '网约房'
  },
  bus: {
    type: Object,
    required: false,
    default: null
  }
})

// 事件总线触发房屋详情弹窗
function openHouseDetail(row) {
  if (!props.bus) return
  props.bus.emit('yz-house-detail-open', {
    data: {
      houseId: row._houseId || '',
      houseType: currentHouseType.value || '',
      houseAddressCode: row._houseAddressCode || row._dzbm || '',
      source: '房屋'
    },
    source: 'HouseColorCode'
  })
}

const BZHOUSE_BASE_URL = 'http://23.210.227.34:23343/ywtg'
// 安全监管房码统计接口（返回所有房屋类型的统计数据）
const SUPERVISION_COUNT_API = 'http://23.99.16.86:11001/api/v1/system/cockpit/supervision/count'
// 商改住四色评价接口
const SGZ_FOUR_COLOR_API = `${BZHOUSE_BASE_URL}/api/boot/system/resiComm/bizToResiHouseFourColor`
// 网约房四色评价接口
const ONLINE_FOUR_COLOR_API = `${BZHOUSE_BASE_URL}/api/boot/system/resiComm/onlineHouseFourColor`
// AI分析四色赋码接口
const AI_ANALYSE_API = 'http://23.99.16.86:11001/api/v1/system/cockpit/ai/analyseSsfm'
// 网约房纳管房屋信息列表接口
const WYF_SUPERVISION_LIST_API = 'http://23.99.16.86:11001/api/v1/system/wyf/supervision/list'
// 群租空间纳管房屋信息列表接口
const QZKJ_SUPERVISION_LIST_API = 'http://23.99.16.86:11001/api/v1/system/qzkj/supervision/list'
// 商改住纳管房屋信息列表接口
const SGZ_SUPERVISION_LIST_API = 'http://23.99.16.86:11001/api/v1/system/supervision/sgz/safeList'

/* ===================== 赋码分类 ===================== */
const codeCategories = ref([
  { key: 'green', label: '绿码', icon: lvmaIcon, count: 0, color: '#00d97e' },
  { key: 'blue', label: '蓝码', icon: lanmaIcon, count: 0, color: '#04a3fa' },
  { key: 'yellow', label: '黄码', icon: huangmaIcon, count: 0, color: '#f5a623' },
  { key: 'red', label: '红码', icon: hongmaIcon, count: 0, color: '#f5222d' }
])

const activeCategory = ref('green')

const currentHouseType = ref(props.houseType || '网约房')

// 四色统计数据缓存（按房屋类型分组）
const supervisionDataMap = ref({})

// 获取四色统计数据
async function fetchSupervisionCount() {
  if (currentHouseType.value === '网约房') {
    await fetchOnlineFourColorCount()
    return
  }
  if (currentHouseType.value === '商改住') {
    await fetchSgzFourColorCount()
    return
  }
  try {
    const res = await axios.get(SUPERVISION_COUNT_API)
    const data = res.data?.data
    if (Array.isArray(data)) {
      const map = {}
      data.forEach(item => {
        const from = item.from || ''
        map[from] = item
      })
      supervisionDataMap.value = map
      updateCodeCategories(currentHouseType.value)
    }
  } catch (error) {
    console.error('四色统计数据请求失败:', error)
  }
}

function updateFourColorCategories(data) {
  codeCategories.value[0].count = data.greenCodeCount || 0
  codeCategories.value[1].count = data.blueCodeCount || 0
  codeCategories.value[2].count = data.yellowCodeCount || 0
  codeCategories.value[3].count = data.redCodeCount || 0
}

async function fetchOnlineFourColorCount() {
  try {
    const res = await axios.get(ONLINE_FOUR_COLOR_API)
    updateFourColorCategories(res.data?.data || {})
  } catch (error) {
    console.error('网约房四色统计数据请求失败:', error)
  }
}

async function fetchSgzFourColorCount() {
  try {
    const res = await axios.get(SGZ_FOUR_COLOR_API)
    updateFourColorCategories(res.data?.data || {})
  } catch (error) {
    console.error('商改住四色统计数据请求失败:', error)
  }
}

// 根据房屋类型更新四色统计数量
function updateCodeCategories(from) {
  const item = supervisionDataMap.value[from]
  if (!item) return
  codeCategories.value[0].count = item.greenCode || 0
  codeCategories.value[1].count = item.blueCode || 0
  codeCategories.value[2].count = item.yellowCode || 0
  codeCategories.value[3].count = item.redCode || 0
}

/* ===================== AI 打字机 ===================== */
const aiTextRef = ref(null)
const aiDisplayText = ref('')
let typingTimer = null
const typingSpeed = 15

function startTyping(text) {
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
  aiDisplayText.value = ''
  if (!text) return
  let index = 0
  typingTimer = window.setInterval(async () => {
    aiDisplayText.value += text[index]
    index++
    await nextTick()
    if (aiTextRef.value) {
      aiTextRef.value.scrollTop = aiTextRef.value.scrollHeight
    }
    if (index >= text.length) {
      clearInterval(typingTimer)
      typingTimer = null
    }
  }, typingSpeed)
}

function stopTyping() {
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
}

// 获取AI分析文本
async function fetchAiText(type) {
  try {
    const res = await axios.get(AI_ANALYSE_API, { params: { type }, timeout: 50000 })
    const responseText = res.data?.data || '暂无AI分析内容'
    const text = String(responseText).replace(/[*#]/g, '')
    stopTyping()
    startTyping(text)
  } catch (error) {
    console.error('AI分析文本请求失败:', error)
    stopTyping()
    startTyping('暂无AI分析内容')
  }
}

// 四色分类key与中文名称映射
const codeColorMap = {
  green: '绿码',
  blue: '蓝码',
  yellow: '黄码',
  red: '红码'
}

function switchCategory(key) {
  // 再次点击同一种颜色则取消筛选
  if (activeCategory.value === key) {
    activeCategory.value = ''
    codeColor.value = ''
  } else {
    activeCategory.value = key
    codeColor.value = codeColorMap[key] || ''
  }
  pagination.value.current = 1
  fetchTableData()
}

// 切换房屋类型
function switchHouseType(from) {
  currentHouseType.value = from
  if (from === '网约房') {
    fetchOnlineFourColorCount()
  } else if (from === '商改住') {
    fetchSgzFourColorCount()
  } else {
    updateCodeCategories(from)
  }
  fetchAiText(from)
  // 重置颜色筛选和分页
  activeCategory.value = ''
  codeColor.value = ''
  pagination.value.current = 1
  fetchTableData()
}

onMounted(() => {
  // 初始化当前房屋类型的数据
  fetchSupervisionCount()
  fetchAiText(currentHouseType.value)
  fetchTableData()
})

// 监听 houseType prop 变化
watch(() => props.houseType, (newType) => {
  if (newType && newType !== currentHouseType.value) {
    switchHouseType(newType)
  }
})

onBeforeUnmount(() => {
  stopTyping()
})

/* ===================== 房屋列表 ===================== */
// 房屋类型与API的映射
const houseTypeApiMap = {
  '网约房': WYF_SUPERVISION_LIST_API,
  '群租空间': QZKJ_SUPERVISION_LIST_API,
  '商改住': SGZ_SUPERVISION_LIST_API
}

// 赋码颜色查询参数
const codeColor = ref('绿码')

// 赋码颜色渲染
const codeColorColumn = {
  title: '赋码颜色',
  key: 'codeColor',
  width: 100,
  align: 'center',
  render(row) {
    const colorMap = {
      '绿码': '#00d97e',
      '蓝码': '#04a3fa',
      '黄码': '#f5a623',
      '红码': '#f5222d'
    }
    const color = colorMap[row.codeColor] || '#fff'
    return h('span', { style: { color } }, row.codeColor)
  }
}

// 操作列
const actionColumn = {
  title: '操作',
  key: 'action',
  width: 80,
  align: 'center',
  render(row) {
    return h('a', {
      style: { color: '#04bcfa', cursor: 'pointer', textDecoration: 'none' },
      onClick: () => openHouseDetail(row)
    }, '详情')
  }
}

// 网约房表头
const columnsWangyue = [
  { title: '出租类别', key: 'rentType', align: 'center', ellipsis: { tooltip: true } },
  { title: '房型', key: 'houseType', align: 'center', ellipsis: { tooltip: true } },
  { title: '所属街道', key: 'street', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '房间数量', key: 'roomCount', align: 'center', width: 100, ellipsis: { tooltip: true } },
  { title: '房屋是否租赁', key: 'isRental', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '租赁经营负责人', key: 'rentalManager', align: 'center', width: 140, ellipsis: { tooltip: true } },
  { title: '房屋位置', key: 'houseAddress', align: 'center', ellipsis: { tooltip: true } },
  { title: '状态', key: 'status', align: 'center', width: 100, ellipsis: { tooltip: true } },
  codeColorColumn,
  actionColumn
]

// 群租空间表头
const columnsQunzu = [
  { title: '所属街道', key: 'street', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '所属社区', key: 'community', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '小区名称', key: 'communityName', align: 'center', ellipsis: { tooltip: true } },
  { title: '房屋位置', key: 'houseAddress', align: 'center', ellipsis: { tooltip: true } },
  { title: '产权人', key: 'owner', align: 'center', width: 100, ellipsis: { tooltip: true } },
  { title: '居住人', key: 'resident', align: 'center', width: 100, ellipsis: { tooltip: true } },
  { title: '居住人联系电话', key: 'residentPhone', align: 'center', width: 140, ellipsis: { tooltip: true } },
  { title: '状态', key: 'status', align: 'center', width: 100, ellipsis: { tooltip: true } },
  codeColorColumn,
  actionColumn
]

// 商改住表头
const columnsShanggaizhu = [
  { title: '项目名称', key: 'projectName', align: 'center', ellipsis: { tooltip: true } },
  { title: '所属街道', key: 'street', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '房屋地址', key: 'houseAddress', align: 'center', ellipsis: { tooltip: true } },
  { title: '房屋户型', key: 'houseType', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '出租状态', key: 'rentStatus', align: 'center', width: 100, ellipsis: { tooltip: true } },
  { title: '承租人', key: 'tenant', align: 'center', width: 100, ellipsis: { tooltip: true } },
  { title: '纳管状态', key: 'naguanStatus', align: 'center', width: 100, ellipsis: { tooltip: true } },
  codeColorColumn,
  actionColumn
]

// 房屋类型与表头的映射
const houseTypeColumnMap = {
  '网约房': columnsWangyue,
  '群租空间': columnsQunzu,
  '商改住': columnsShanggaizhu
}

const columns = computed(() => {
  return houseTypeColumnMap[currentHouseType.value] || columnsWangyue
})

// 表格数据
const tableData = ref([])

// 分页
const pagination = ref({
  current: 1,
  totalPage: 1,
  size: 10,
  total: 0
})

// 获取表格数据
async function fetchTableData() {
  const api = houseTypeApiMap[currentHouseType.value]
  if (!api) return
  try {
    const params = {
      index: pagination.value.current,
      size: pagination.value.size,
      codeColor: codeColor.value || undefined
    }
    const res = await axios.post(api, params)
    const pageData = res.data?.data
    if (pageData) {
      const content = pageData.content || []
      // 根据房屋类型映射响应数据
      if (currentHouseType.value === '网约房') {
        tableData.value = content.map(item => ({
          rentType: item.type || '',
          houseType: item.roomType || '',
          street: item.street || '',
          roomCount: item.roomCount ?? '',
          isRental: item.rent || '',
          rentalManager: item.rentManager || '',
          houseAddress: item.houseAddress || '',
          status: item.status || '',
          codeColor: item.codeColor || '',
          _houseId: item.id || item.houseId || '',
          _houseAddressCode: item.houseAddressCode || item.dzbm || ''
        }))
      } else if (currentHouseType.value === '群租空间') {
        tableData.value = content.map(item => ({
          street: item.street || '',
          community: item.community || '',
          communityName: item.xqName || '',
          houseAddress: item.houseAddress || '',
          owner: item.ownerName || '',
          resident: item.livePerson || '',
          residentPhone: item.livePersonTel || '',
          status: item.status || '',
          codeColor: item.codeColor || '',
          _houseId: item.id || item.houseId || '',
          _houseAddressCode: item.houseAddressCode || item.dzbm || ''
        }))
      } else if (currentHouseType.value === '商改住') {
        tableData.value = content.map(item => ({
          projectName: item.projectName || '',
          street: item.street || '',
          houseAddress: item.houseAddress || '',
          houseType: item.housingLayout || '',
          rentStatus: item.rentalStatus || '',
          tenant: item.tenantName || '',
          naguanStatus: item.status || '',
          codeColor: item.codeColor || '',
          _houseId: item.id || item.houseId || '',
          _houseAddressCode: item.houseAddressCode || item.dzbm || ''
        }))
      }
      const page = pageData.page || {}
      pagination.value.total = page.total || 0
      pagination.value.totalPage = Math.ceil((page.total || 0) / pagination.value.size) || 1
    }
  } catch (error) {
    console.error('纳管房屋信息列表请求失败:', error)
  }
}

function handlePageChange(page) {
  pagination.value.current = page
  fetchTableData()
}
</script>

<style lang="scss" scoped>
.color-code-popup {
  width: 1284px;
  height: 756px;
  background: url('../img/diaBg.png') no-repeat center / 100% 100%;
  transform: translate(-50%, -50%);
  position: absolute;
  left: 50%;
  top: 50%;
  padding: 10px 25px;
  color: #fff;
  font-family: 'Microsoft YaHei';

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

.color-code-content {
  padding: 15px 25px;
  margin-top: 10px;
  height: calc(100% - 70px);
  display: flex;
  flex-direction: column;
  gap: 12px;

  /* 赋码分类 */
  .code-category-row {
    display: flex;
    gap: 16px;

    .code-category-item {
      flex: 1;
      height: 70px;
      background: url('../img/fmBg.png') no-repeat center / 100% 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;
      cursor: pointer;
      position: relative;
      overflow: hidden;

      &::after {
        content: none;
      }

      .code-category-left {
        display: flex;
        align-items: center;
        gap: 10px;
        position: relative;
        z-index: 1;

        .code-icon {
          width: 32px;
          height: 32px;
        }

        .code-name {
          font-size: 16px;
          font-weight: 600;
          background: linear-gradient(to top, #ffffff, var(--code-color));
          -webkit-background-clip: text;
          background-clip: text;
          -webkit-text-fill-color: transparent;
        }
      }

      .code-count {
        font-size: 22px;
        font-weight: bold;
        font-family: 'DIN', 'Microsoft YaHei', sans-serif;
        background: linear-gradient(to top, #ffffff, var(--code-color));
        -webkit-background-clip: text;
        background-clip: text;
        -webkit-text-fill-color: transparent;
        position: relative;
        z-index: 1;
      }
    }
  }

  /* AI 打字机 */
  .ai-talk-wrapper {
    height: 160px;
    background: rgba(4, 188, 250, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 4px;
    padding: 12px 16px;
    overflow: hidden;

    .ai-talk-content {
      height: 100%;
      overflow-y: auto;
      font-size: 13px;
      line-height: 1.6;
      color: rgba(255, 255, 255, 0.85);
      white-space: pre-wrap;
      scrollbar-width: none;
      -ms-overflow-style: none;

      &::-webkit-scrollbar {
        display: none;
      }
    }
  }

  /* 房屋列表 */
  .house-list-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;

    .category-tabs {
      display: flex;
      gap: 8px;
      margin-bottom: 8px;

      .category-tab {
        padding: 4px 16px;
        font-size: 14px;
        color: rgba(255, 255, 255, 0.6);
        cursor: pointer;
        border-radius: 4px;
        transition: all 0.3s;

        &:hover {
          color: #fff;
        }

        &.active {
          color: #fff;
          background: rgba(4, 188, 250, 0.2);
          border: 1px solid rgba(4, 188, 250, 0.4);
        }
      }
    }

    .table-container {
      flex: 1;
      overflow: hidden;

      :deep(.n-data-table) {
        height: 100%;
        background: transparent;
        color: #fff;

        .n-data-table-th {
          background: rgba(4, 188, 250, 0.15);
          color: #fff;
          font-size: 13px;
        }

        .n-data-table-td {
          background: transparent;
          color: rgba(255, 255, 255, 0.85);
          font-size: 13px;
        }

        .n-data-table-tr:hover .n-data-table-td {
          background: rgba(255, 255, 255, 0.05);
        }
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      padding: 8px 0;
    }
  }
}
</style>

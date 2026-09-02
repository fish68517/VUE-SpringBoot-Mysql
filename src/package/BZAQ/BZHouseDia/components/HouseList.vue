<template>
  <div class="house-list-popup">
    <!-- 顶部标题栏 -->
    <div class="popup-header">
      <h3 class="title">房屋列表</h3>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-item">
        <label>小区名称：</label>
        <el-input v-model="searchForm.communityName" class="search-input" placeholder="请输入" />
      </div>
      <div class="search-item">
        <label>房屋位置：</label>
        <el-input v-model="searchForm.houseAddress" class="search-input" placeholder="请输入" />
      </div>
      <div class="search-item">
        <label>产权人：</label>
        <el-input v-model="searchForm.owner" class="search-input" placeholder="请输入" />
      </div>
      <div class="search-item">
        <label>经营者：</label>
        <el-input v-model="searchForm.operator" class="search-input" placeholder="请输入" />
      </div>
      <div class="search-item">
        <label>当前状态：</label>
        <el-select v-model="searchForm.status" class="search-select" popper-class="house-select-popper" placeholder="请选择">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
      <div class="search-item search-btn-item">
        <button class="search-btn" @click="handleSearch">查询</button>
        <button class="search-btn reset-btn" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <!-- 表头分类 -->
      <div class="table-header">
        <div class="header-left">房屋基础信息</div>
        <div class="header-right">操作栏</div>
      </div>

      <n-config-provider :locale="zhCN">
        <n-data-table
          class="house-table"
          :columns="columns"
          :data="tableData"
          :bordered="false"
          :single-line="false"
          size="small"
        />
      </n-config-provider>
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
</template>

<script setup>
import { h, ref, computed, onMounted, watch } from 'vue'
import { NDataTable, NPagination, NConfigProvider, zhCN } from 'naive-ui'
import { ElInput, ElSelect, ElOption } from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import closeIcon from '../img/close.png'

const emits = defineEmits(['close', 'show-inspection-detail'])

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


// 房屋类型映射
const houseTypeMap = {
  wangyue: '网约房',
  qunzu: '群租空间',
  shanggaizhu: '商改住'
}

// 事件总线触发房屋详情弹窗
function openHouseDetail(row) {
  if (!props.bus) return
  props.bus.emit('yz-house-detail-open', {
    data: {
      houseId: row._houseId || '',
      houseType: houseTypeMap[currentCategory.value] || '',
      houseAddressCode: row._houseAddressCode || row._dzbm || '',
      source: '房屋'
    },
    source: 'HouseList'
  })
}

// 网约房接口
const WYF_HOUSE_LIST_API = 'http://23.99.16.86:11001/api/v1/system/management/wyfHouseList'
// 群租空间接口
const QZKJ_HOUSE_LIST_API = 'http://23.99.16.86:11001/api/v1/system/management/qzKjHouseList'
// 商改住接口
const SGZ_HOUSE_LIST_API = 'http://23.99.16.86:11001/api/v1/system/management/sgzHouseList'

// 状态下拉选项
const statusOptions = [
  { label: '已纳管', value: '2' },
  { label: '申请中', value: '1' },
  { label: '已注销', value: '3' }
]

// 搜索表单
const searchForm = ref({
  communityName: '',
  houseAddress: '',
  owner: '',
  operator: '',
  status: ''
})

// 状态颜色映射
const statusColorMap = {
  '申报中': '#f1b736',
  '申报成功': '#47dea2',
  '已注销': '#f14936',
  '申报失败': '#f14936',
  '申报驳回': '#f14936'
}

// 操作列（共用）
const actionColumn = {
  title: '操作',
  key: 'action',
  width: 122,
  align: 'center',
  render(row) {
    return h('a', {
      style: { color: '#04bcfa', cursor: 'pointer' },
      onClick: () => openHouseDetail(row)
    }, '详情')
  }
}

function showHouseDetail(row) {
  emits('show-inspection-detail', {
    inspectionType: `${props.houseType}房屋详情`,
    companyName: row.venueName || row.operator || row.operatorContact || '--',
    companyAddress: row.houseAddress || row.standardAddress || '--',
    inspectionTime: row.createdTs || row.updateTime || '--',
    inspector: row.gridWorker || '--',
    handleStatus: row.status || row.tenantStatus || '--',
    createTime: row.createdTs || '--',
    createBy: row.ownerContact || row.operator || '--',
    lastInspectionTime: row.lastInspectionTime || '--',
    inspectionCount: row.inspectionCount || '--',
    inspectionContent: `${props.houseType}房屋基础信息查看`,
    sceneSituation: [
      row.rentType ? `出租类别：${row.rentType}` : '',
      row.houseType ? `房型：${row.houseType}` : '',
      row.roomCount !== undefined && row.roomCount !== '' ? `房间数量：${row.roomCount}` : '',
      row.rental ? `房屋租赁：${row.rental}` : '',
      row.street ? `所属街道：${row.street}` : ''
    ].filter(Boolean).join('；') || '--',
    problemCategory: props.houseType,
    problemDescription: row.houseAddress || row.standardAddress || '--',
    inspectionImages: []
  })
}

// 申报状态列（共用，带颜色）
const statusColumn = {
  title: '申报状态',
  key: 'status',
  align: 'center',
  width: 100,
  ellipsis: { tooltip: true },
  render(row) {
    const color = statusColorMap[row.status] || '#fff'
    return h('span', { style: { color } }, row.status)
  }
}

// 表格列定义 - 网约房
const columnsWangyue = [
  { title: '出租类别', key: 'rentType', align: 'center', ellipsis: { tooltip: true } },
  { title: '房型', key: 'houseType', align: 'center', ellipsis: { tooltip: true } },
  { title: '房屋位置', key: 'houseAddress', align: 'center', ellipsis: { tooltip: true } },
  { title: '房间数量', key: 'roomCount', align: 'center', width: 100, ellipsis: { tooltip: true } },
  { title: '房屋租赁', key: 'rental', align: 'center', width: 120, ellipsis: { tooltip: true } },
  statusColumn,
  { title: '经营者', key: 'operator', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '所属街道', key: 'street', align: 'center', width: 120, ellipsis: { tooltip: true } },
  actionColumn
]

// 表格列定义 - 群租空间
const columnsQunzu = [
  { title: '区域', key: 'area', align: 'center', ellipsis: { tooltip: true } },
  { title: '所属街道', key: 'street', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '标准地址', key: 'standardAddress', align: 'center', ellipsis: { tooltip: true } },
  { title: '场所经营名称', key: 'venueName', align: 'center', ellipsis: { tooltip: true } },
  { title: '聚居点类型', key: 'gatherType', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '房主姓名及联系电话', key: 'ownerContact', align: 'center', width: 180, ellipsis: { tooltip: true } },
  { title: '经营者姓名及联系电话', key: 'operatorContact', align: 'center', width: 180, ellipsis: { tooltip: true } },
  { title: '网格员', key: 'gridWorker', align: 'center', width: 120, ellipsis: { tooltip: true } },
  actionColumn
]

// 表格列定义 - 商改住
const columnsShanggaizhu = [
  { title: '出租类别', key: 'rentType', align: 'center', ellipsis: { tooltip: true } },
  { title: '所属街道', key: 'street', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '房屋位置', key: 'houseAddress', align: 'center', ellipsis: { tooltip: true } },
  { title: '房间数量', key: 'roomCount', align: 'center', width: 100, ellipsis: { tooltip: true } },
  { title: '房屋租赁', key: 'rental', align: 'center', width: 120, ellipsis: { tooltip: true } },
  statusColumn,
  { title: '经营者', key: 'operator', align: 'center', width: 120, ellipsis: { tooltip: true } },
  actionColumn
]

const houseTypeCategoryMap = {
  '网约房': 'wangyue',
  '群租空间': 'qunzu',
  '商改住': 'shanggaizhu'
}

// 当前表格类型
const currentCategory = ref(houseTypeCategoryMap[props.houseType] || 'wangyue')

// 动态列
const columns = computed(() => {
  const map = { wangyue: columnsWangyue, qunzu: columnsQunzu, shanggaizhu: columnsShanggaizhu }
  return map[currentCategory.value]
})

// 模拟表格数据
const tableData = ref([])

// 分页
const pagination = ref({
  current: 1,
  totalPage: 1,
  size: 10,
  total: 0
})

// 加载状态
const loading = ref(false)

// 获取网约房列表
async function fetchWyfHouseList() {
  loading.value = true
  try {
    const params = {
      index: pagination.value.current,
      size: pagination.value.size,
      communityName: searchForm.value.communityName || undefined,
      houseLocation: searchForm.value.houseAddress || undefined,
      houseOwner: searchForm.value.owner || undefined,
      houseOperator: searchForm.value.operator || undefined,
      houseStatus: searchForm.value.status || undefined
    }
    const res = await axios.post(WYF_HOUSE_LIST_API, params)
    const pageData = res.data?.data
    if (pageData) {
      const content = pageData.content || []
      tableData.value = content.map(item => ({
        rentType: item.typeDesc || '',
        houseType: item.roomType || '',
        houseAddress: item.houseLocation || '',
        roomCount: item.roomCount ?? '',
        rental: item.rent || '',
        status: item.reportStatusDesc || '',
        operator: item.operator || '',
        street: item.street || '',
        _houseId: item.id || item.houseId || '',
        _houseAddressCode: item.houseAddressCode || item.dzbm || ''
      }))
      const page = pageData.page || {}
      pagination.value.total = page.total || 0
      pagination.value.totalPage = Math.ceil((page.total || 0) / pagination.value.size) || 1
    }
  } catch (error) {
    console.error('网约房列表请求失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取群租空间列表
async function fetchQzKjHouseList() {
  loading.value = true
  try {
    const params = {
      index: pagination.value.current,
      size: pagination.value.size,
      communityName: searchForm.value.communityName || undefined,
      houseLocation: searchForm.value.houseAddress || undefined,
      houseOwner: searchForm.value.owner || undefined,
      houseOperator: searchForm.value.operator || undefined,
      houseStatus: searchForm.value.status || undefined
    }
    const res = await axios.post(QZKJ_HOUSE_LIST_API, params)
    const pageData = res.data?.data
    if (pageData) {
      const content = pageData.content || []
      tableData.value = content.map(item => ({
        area: item.region || '',
        street: item.street || '',
        standardAddress: item.houseLocation || '',
        venueName: item.businessName || '',
        gatherType: item.houseTypeDesc || '',
        ownerContact: [item.owner, item.ownerPhone].filter(Boolean).join(' '),
        operatorContact: [item.operator, item.operatorPhone].filter(Boolean).join(' '),
        gridWorker: item.gridWorker || '',
        _houseId: item.id || item.houseId || '',
        _houseAddressCode: item.houseAddressCode || item.dzbm || ''
      }))
      const page = pageData.page || {}
      pagination.value.total = page.total || 0
      pagination.value.totalPage = Math.ceil((page.total || 0) / pagination.value.size) || 1
    }
  } catch (error) {
    console.error('群租空间列表请求失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取商改住列表
async function fetchSgzHouseList() {
  loading.value = true
  try {
    const params = {
      index: pagination.value.current,
      size: pagination.value.size,
      communityName: searchForm.value.communityName || undefined,
      houseLocation: searchForm.value.houseAddress || undefined,
      houseOwner: searchForm.value.owner || undefined,
      houseOperator: searchForm.value.operator || undefined,
      houseStatus: searchForm.value.status || undefined
    }
    const res = await axios.post(SGZ_HOUSE_LIST_API, params)
    const pageData = res.data?.data
    if (pageData) {
      const content = pageData.content || []
      tableData.value = content.map(item => ({
        rentType: item.typeDesc || '',
        street: item.street || '',
        houseAddress: item.houseLocation || '',
        roomCount: item.roomCount ?? '',
        rental: item.rent || '',
        status: item.reportStatusDesc || '',
        operator: item.operator || '',
        _houseId: item.id || item.houseId || '',
        _houseAddressCode: item.houseAddressCode || item.dzbm || ''
      }))
      const page = pageData.page || {}
      pagination.value.total = page.total || 0
      pagination.value.totalPage = Math.ceil((page.total || 0) / pagination.value.size) || 1
    }
  } catch (error) {
    console.error('商改住列表请求失败:', error)
  } finally {
    loading.value = false
  }
}

// 根据当前分类请求数据
function fetchList() {
  if (currentCategory.value === 'qunzu') {
    fetchQzKjHouseList()
  } else if (currentCategory.value === 'shanggaizhu') {
    fetchSgzHouseList()
  } else {
    fetchWyfHouseList()
  }
}

function handlePageChange(page) {
  pagination.value.current = page
  fetchList()
}

function handleSearch() {
  pagination.value.current = 1
  fetchList()
}

function handleReset() {
  searchForm.value = {
    communityName: '',
    houseAddress: '',
    owner: '',
    operator: '',
    status: ''
  }
  pagination.value.current = 1
  fetchList()
}

onMounted(() => {
  fetchList()
})

watch(() => props.houseType, newType => {
  const nextCategory = houseTypeCategoryMap[newType] || 'wangyue'
  if (nextCategory === currentCategory.value) return
  currentCategory.value = nextCategory
  pagination.value.current = 1
  fetchList()
})
</script>

<style lang="scss" scoped>
.house-list-popup {
  width: 1284px;
  height: 756px;
  background: url('../img/diaBg.png') no-repeat center / 100% 100%;
  transform: translate(-50%, -50%);
  position: relative;
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

  // 搜索栏
  .search-bar {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    row-gap: 16px;
    column-gap: 24px;
    padding: 15px 25px;
    margin-top: 10px;

    .search-item {
      display: flex;
      align-items: center;
      gap: 8px;
      justify-content: flex-start;

      label {
        white-space: nowrap;
        color: #ffffff;
        font-size: 14px;
        font-weight: 400;
        font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;
        line-height: 22px;
      }
    }

    .search-input,
    .search-select {
      width: 240px;
    }

    .search-btn-item {
      justify-content: flex-start;
      gap: 8px;
    }

    .search-btn {
      width: 70px;
      height: 32px;
      border: 1px solid #04bcfa;
      border-radius: 4px;
      background: rgba(4, 188, 250, 0.15);
      color: #04bcfa;
      font-size: 14px;
      cursor: pointer;
      font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;

      &:hover {
        background: rgba(4, 188, 250, 0.3);
      }
    }

    .reset-btn {
      border-color: #ffffff3d;
      background: transparent;
      color: #f5fcff73;

      &:hover {
        background: rgba(255, 255, 255, 0.08);
      }
    }
  }

  // 表格容器
  .table-container {
    padding: 0 25px;
    margin-top: 8px;
    height: calc(100% - 250px);
  }

  // 表头分类
  .table-header {
    display: flex;
    height: 32px;
    line-height: 32px;
    font-size: 16px;
    font-weight: bold;

    .header-left {
      flex: 1;
      text-align: center;
      background: url('../img/矩形.png') no-repeat center / 100% 100%;
    }

    .header-right {
      width: 122px;
      height: 32px;
      text-align: center;
      background: url('../img/back.png') no-repeat center / 100% 100%;
    }
  }

  // 分页容器
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    padding: 10px 0;
  }
}

// Element Plus 输入框样式覆写
.search-input {
  :deep(.el-input__wrapper) {
    background: transparent !important;
    border: 1px solid #ffffff3d !important;
    border-radius: 4px !important;
    height: 32px;
    box-shadow: none !important;
  }

  :deep(.el-input__wrapper:hover) {
    border-color: #ffffff5e !important;
  }

  :deep(.el-input__wrapper.is-focus) {
    border-color: #ffffff5e !important;
  }

  :deep(.el-input__inner) {
    color: #f5fcff73 !important;
    font-size: 14px;
    font-weight: 400;
    font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;
    height: 32px;
    line-height: 32px;
  }

  :deep(.el-input__inner::placeholder) {
    color: #f5fcff73 !important;
  }
}

// Element Plus 下拉框样式覆写
.search-select {
  :deep(.el-select__wrapper) {
    background: transparent !important;
    border: 1px solid #ffffff3d !important;
    border-radius: 4px !important;
    height: 32px;
    box-shadow: none !important;
  }

  :deep(.el-select__wrapper:hover) {
    border-color: #ffffff5e !important;
  }

  :deep(.el-select__wrapper.is-focused) {
    border-color: #ffffff5e !important;
  }

  :deep(.el-select__placeholder),
  :deep(.el-select__selected-item) {
    color: #f5fcff73 !important;
    font-size: 14px;
    font-weight: 400;
    font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;
  }
}

// NaiveUI 表格主题覆写
:deep(.house-table) {
  .n-data-table-wrapper {
    background: transparent;
  }

  .n-data-table-table {
    background: transparent;
    font-size: 14px;
  }

  .n-data-table-th {
    height: 42px;
    padding: 0 12px;
    color: #8bbce6;
    font-weight: 500;
    background: rgba(4, 111, 241, 0.2);
    border: none;
  }

  .n-data-table-td {
    height: 42px;
    padding: 0 12px;
    color: #d7e9ff;
    background: transparent;
    border: none;
  }

  .n-data-table-tr:nth-child(even) .n-data-table-td {
    background: rgba(4, 84, 203, 0.08);
  }

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(4, 111, 241, 0.15);
  }

  .n-scrollbar-rail--vertical {
    width: 6px;
  }

  .n-scrollbar-rail__scrollbar {
    background: rgba(71, 202, 255, 0.68);
  }
}

// NaiveUI 分页主题覆写
.pagination-wrapper {
  :deep(.n-pagination) {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  :deep(.n-pagination-item),
  :deep(.n-pagination-button) {
    min-width: 32px;
    height: 32px;
    border: 1px solid rgba(4, 111, 241, 0.4);
    background: rgba(4, 84, 203, 0.2);
    color: #d7e9ff;
    font-size: 14px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  :deep(.n-pagination-item:hover),
  :deep(.n-pagination-button:hover) {
    color: #fff;
    border-color: #046ff1;
    background: rgba(4, 111, 241, 0.4);
  }

  :deep(.n-pagination-item--active) {
    background: #046ff1 !important;
    border-color: #046ff1 !important;
    color: #fff !important;
  }
}
</style>

<style lang="scss">
// 下拉选项面板样式（popper 渲染在 body 上，不能用 scoped）
.house-select-popper {
  background: rgba(4, 28, 72, 0.95) !important;
  border: 1px solid rgba(4, 111, 241, 0.4) !important;

  .el-select-dropdown__item {
    color: #d7e9ff;
    font-size: 14px;
    font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;
    background: transparent;
  }

  .el-select-dropdown__item.hover,
  .el-select-dropdown__item:hover {
    background: rgba(4, 111, 241, 0.3) !important;
    color: #fff;
  }

  .el-select-dropdown__item.selected {
    color: #04bcfa;
    font-weight: bold;
  }

  .el-popper__arrow::before {
    background: rgba(4, 28, 72, 0.95) !important;
    border-color: rgba(4, 111, 241, 0.4) !important;
  }
}
</style>

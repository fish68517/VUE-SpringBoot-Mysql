
<template>
  <div class="house-list-popup">
    <!-- 顶部标题栏 -->
    <div class="popup-header">
      <h3 class="title">住宿人员列表</h3>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-item">
        <label>房屋地址：</label>
        <el-input v-model="searchForm.houseAddress" class="search-input" placeholder="请输入" />
      </div>
      <div class="search-item">
        <label>人员姓名：</label>
        <el-input v-model="searchForm.personName" class="search-input" placeholder="请输入" />
      </div>
      <div class="search-item">
        <label>人员类型：</label>
        <el-select v-model="searchForm.personType" class="search-select" popper-class="house-select-popper" placeholder="请选择">
          <el-option
            v-for="item in personTypeOptions"
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
      source: '人员'
    },
    source: 'HousePersonList'
  })
}

// 网约房入住人员接口
const WYF_PERSON_LIST_API = 'http://23.99.16.86:11001/api/v1/system/management/wyfPersonList'
// 群租空间纳管人员接口
const QZKJ_PERSON_LIST_API = 'http://23.99.16.86:11001/api/v1/system/management/qzKjPersonList'
// 商改住纳管人员接口
const SGZ_PERSON_LIST_API = 'http://23.99.16.86:11001/api/v1/system/management/sgzPersonList'

// 人员类型下拉选项
const personTypeOptions = [
  { label: '房客', value: '房客' },
  { label: '房东', value: '房东' },
  { label: '经营者', value: '经营者' }
]

// 搜索表单
const searchForm = ref({
  houseAddress: '',
  personName: '',
  personType: ''
})

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

function showPersonDetail(row) {
  emits('show-inspection-detail', {
    inspectionType: `${props.houseType}住宿人员详情`,
    companyName: row.name || '--',
    companyAddress: row.houseAddress || row.relatedPlace || '--',
    inspectionTime: row.checkInTime || '--',
    inspector: row.booker || row.manager || '--',
    handleStatus: row.tenantStatus || '--',
    createTime: row.checkInTime || '--',
    createBy: row.personType || '--',
    lastInspectionTime: row.checkInTime || '--',
    inspectionCount: '--',
    inspectionContent: `${props.houseType}住宿人员信息查看`,
    sceneSituation: [
      row.phone ? `联系电话：${row.phone}` : '',
      row.idCard ? `身份证号：${row.idCard}` : '',
      row.gender ? `性别：${row.gender}` : '',
      row.relatedPlace ? `关联场所：${row.relatedPlace}` : '',
      row.manager ? `经营负责人：${row.manager}` : '',
      row.managerPhone ? `负责人电话：${row.managerPhone}` : ''
    ].filter(Boolean).join('；') || '--',
    problemCategory: props.houseType,
    problemDescription: row.houseAddress || row.relatedPlace || '--',
    inspectionImages: []
  })
}

// 表格列定义 - 网约房住宿人员
const wangyueColumns = [
  { title: '姓名', key: 'name', align: 'center', ellipsis: { tooltip: true } },
  { title: '联系电话', key: 'phone', align: 'center', width: 150, ellipsis: { tooltip: true } },
  { title: '身份证号', key: 'idCard', align: 'center', width: 180, ellipsis: { tooltip: true } },
  { title: '订房人', key: 'booker', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '经营负责人', key: 'manager', align: 'center', width: 120, ellipsis: { tooltip: true } },
  { title: '负责人电话', key: 'managerPhone', align: 'center', width: 150, ellipsis: { tooltip: true } },
  { title: '入住时间', key: 'checkInTime', align: 'center', width: 150, ellipsis: { tooltip: true } },
  { title: '房屋地址', key: 'houseAddress', align: 'center', ellipsis: { tooltip: true } },
  actionColumn
]

// 表格列定义 - 群租空间/商改住住宿人员
const otherColumns = [
  { title: '姓名', key: 'name', align: 'center', ellipsis: { tooltip: true } },
  { title: '身份证号', key: 'idCard', align: 'center', width: 180, ellipsis: { tooltip: true } },
  { title: '性别', key: 'gender', align: 'center', width: 80, ellipsis: { tooltip: true } },
  { title: '关联场所', key: 'relatedPlace', align: 'center', ellipsis: { tooltip: true } },
  { title: '房屋地址', key: 'houseAddress', align: 'center', ellipsis: { tooltip: true } },
  { title: '入住时间', key: 'checkInTime', align: 'center', width: 150, ellipsis: { tooltip: true } },
  { title: '联系电话', key: 'phone', align: 'center', width: 150, ellipsis: { tooltip: true } },
  { title: '租客状态', key: 'tenantStatus', align: 'center', width: 100, ellipsis: { tooltip: true } },
  actionColumn
]

const houseTypeCategoryMap = {
  '网约房': 'wangyue',
  '群租空间': 'qunzu',
  '商改住': 'shanggaizhu'
}

// 当前分类
const currentCategory = ref(houseTypeCategoryMap[props.houseType] || 'wangyue')

// 动态列
const columns = computed(() => {
  if (currentCategory.value === 'wangyue') return wangyueColumns
  return otherColumns
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

// 加载状态
const loading = ref(false)

// 获取网约房入住人员列表
async function fetchWyfPersonList() {
  loading.value = true
  try {
    const params = {
      index: pagination.value.current,
      size: pagination.value.size,
      name: searchForm.value.personName || undefined,
      houseAddress: searchForm.value.houseAddress || undefined
    }
    const res = await axios.post(WYF_PERSON_LIST_API, params)
    const pageData = res.data?.data
    if (pageData) {
      const content = pageData.content || []
      tableData.value = content.map(item => ({
        name: item.name || '',
        phone: item.phone || '',
        idCard: item.idCard || '',
        booker: item.reserveName || '',
        manager: item.managerName || '',
        managerPhone: item.managerPhone || '',
        checkInTime: item.inTime || '',
        houseAddress: item.houseAddress || '',
        checkInTime: item.inTime || '',
        _houseId: item.houseId || '',
        _houseAddressCode: item.houseAddressCode || item.dzbm || ''
      }))
      const page = pageData.page || {}
      pagination.value.total = page.total || 0
      pagination.value.totalPage = Math.ceil((page.total || 0) / pagination.value.size) || 1
    }
  } catch (error) {
    console.error('网约房入住人员列表请求失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取群租空间纳管人员列表
async function fetchQzKjPersonList() {
  loading.value = true
  try {
    const params = {
      index: pagination.value.current,
      size: pagination.value.size,
      name: searchForm.value.personName || undefined,
      houseAddress: searchForm.value.houseAddress || undefined,
      personType: searchForm.value.personType || undefined
    }
    const res = await axios.post(QZKJ_PERSON_LIST_API, params)
    const pageData = res.data?.data
    if (pageData) {
      const content = pageData.content || []
      tableData.value = content.map(item => ({
        name: item.name || '',
        idCard: item.idCard || '',
        gender: item.sexDesc || '',
        relatedPlace: item.businessName || '',
        houseAddress: item.houseAddress || '',
        checkInTime: item.inTime || '',
        phone: item.phone || '',
        tenantStatus: item.tenantStatusDesc || '',
        _houseId: item.houseId || '',
        _houseAddressCode: item.houseAddressCode || item.dzbm || ''
      }))
      const page = pageData.page || {}
      pagination.value.total = page.total || 0
      pagination.value.totalPage = Math.ceil((page.total || 0) / pagination.value.size) || 1
    }
  } catch (error) {
    console.error('群租空间纳管人员列表请求失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取商改住纳管人员列表
async function fetchSgzPersonList() {
  loading.value = true
  try {
    const params = {
      index: pagination.value.current,
      size: pagination.value.size,
      name: searchForm.value.personName || undefined
    }
    const res = await axios.post(SGZ_PERSON_LIST_API, params)
    const pageData = res.data?.data
    if (pageData) {
      const content = pageData.content || []
      tableData.value = content.map(item => ({
        name: item.name || '',
        idCard: item.idCard || '',
        gender: item.sexDesc || '',
        relatedPlace: item.businessName || '',
        houseAddress: item.houseAddress || '',
        checkInTime: item.inTime || '',
        phone: item.phone || '',
        tenantStatus: item.tenantStatusDesc || '',
        _houseId: item.houseId || '',
        _houseAddressCode: item.houseAddressCode || item.dzbm || ''
      }))
      const page = pageData.page || {}
      pagination.value.total = page.total || 0
      pagination.value.totalPage = Math.ceil((page.total || 0) / pagination.value.size) || 1
    }
  } catch (error) {
    console.error('商改住纳管人员列表请求失败:', error)
  } finally {
    loading.value = false
  }
}

// 根据当前分类请求数据
function fetchList() {
  if (currentCategory.value === 'qunzu') {
    fetchQzKjPersonList()
  } else if (currentCategory.value === 'shanggaizhu') {
    fetchSgzPersonList()
  } else {
    fetchWyfPersonList()
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
    houseAddress: '',
    personName: '',
    personType: ''
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
    grid-template-columns: repeat(4, 1fr);
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
      width: 180px;
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

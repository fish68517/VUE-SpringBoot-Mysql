<template>
  <div class="geo-list-modal">
    <div class="modal-header">
      <div class="header-title">风险点位列表</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="query-bar">
      <n-select
        v-model:value="selectedType"
        class="query-select"
        :options="riskTypeOptions"
        :bordered="false"
        @update:value="handleTypeChange"
      />
    </div>

    <div class="modal-content">
      <n-data-table
        class="geo-table"
        :columns="columns"
        :data="tableData"
        :scroll-x="scrollX"
        :bordered="false"
        :single-line="false"
        size="small"
      />
    </div>

    <div class="modal-footer">
      <n-pagination :page="currentPage" :page-count="totalPage" :page-slot="5" @update:page="handlePageChange" />
    </div>
  </div>
</template>

<script setup>
import { computed, h, onMounted, ref } from 'vue'
import { NDataTable, NPagination, NSelect } from 'naive-ui'
import axios from 'axios'

const emit = defineEmits(['close', 'open-small-watershed-detail'])

const LAND_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/land'
const OLD_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/old'
const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 10
const tableData = ref([])
const selectedType = ref('risk-hidden-danger')

const oldRiskTypeMap = {
  'risk-old-retaining-wall': 'retaining_wall',
  'risk-old-slope': 'slope',
  'risk-old-house': 'old_house',
  'risk-old-wall': 'wall',
  'risk-cliff-trail': 'trail'
}

const oldRiskTypeLabelMap = {
  slope: '边坡',
  retaining_wall: '堡坎',
  wall: '围墙',
  old_house: '房屋',
  trail: '步道',
  other: '其他'
}

const greenSpaceRows = [
  { name: '鹅岭公园', address: '鹅岭正街176号', area: '6.5', realTimeFlow: '320', manager: '陈俊', phone: '13452847983' },
  { name: '佛图关公园', address: '长江一路', area: '41.84', realTimeFlow: '560', manager: '封逻', phone: '13908340170' },
  { name: '人民公园', address: '人民路', area: '1.02', realTimeFlow: '180', manager: '刘素芳', phone: '13508389696' },
  { name: '红岩公园', address: '红岩村', area: '18.21', realTimeFlow: '410', manager: '肖镇', phone: '13708367331' },
  { name: '天地湖公园', address: '天地湖', area: '6.12', realTimeFlow: '260', manager: '肖镇', phone: '13708367331' },
  { name: '虎头岩公园', address: '虎头岩', area: '14.82', realTimeFlow: '330', manager: '刘东', phone: '13752953417' },
  { name: '珊瑚公园', address: '珊瑚路', area: '10', realTimeFlow: '290', manager: '梁雪岗', phone: '13808352604' },
  { name: '滨江公园', address: '滨江路', area: '6.28', realTimeFlow: '220', manager: '梁雪岗', phone: '13808352604' }
]

const warnLevelMap = {
  1: { label: '蓝色预警', className: 'warn-level--blue' },
  2: { label: '黄色预警', className: 'warn-level--yellow' },
  3: { label: '橙色预警', className: 'warn-level--orange' },
  4: { label: '红色预警', className: 'warn-level--red' }
}

const yesNoMap = {
  0: '否',
  1: '是'
}

const boolTextMap = {
  true: '是',
  false: '否'
}

const riskTypeOptions = [
  { label: '地灾隐患点', value: 'risk-hidden-danger' },
  { label: '小流域', value: 'risk-small-watershed' },
  { label: '四老一步道', value: 'risk-four-old-trail' },
  { label: '绿地', value: 'risk-green-space' }
]

const indexColumn = {
  title: '序号',
  key: 'index',
  width: 80,
  render(_row, rowIndex) {
    return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
  }
}

const hiddenDangerColumns = [
  indexColumn,
  { title: '隐患点类型', key: 'type', width: 140, ellipsis: { tooltip: true } },
  { title: '隐患点编码', key: 'code', width: 150, ellipsis: { tooltip: true } },
  { title: '隐患点名称', key: 'name', width: 180, ellipsis: { tooltip: true } },
  { title: '省份名称', key: 'provinceName', width: 120, ellipsis: { tooltip: true } },
  { title: '区县名称', key: 'cityName', width: 120, ellipsis: { tooltip: true } },
  { title: '乡镇名称', key: 'townName', width: 140, ellipsis: { tooltip: true } },
  { title: '地理位置', key: 'location', width: 220, ellipsis: { tooltip: true } },
  { title: '经度', key: 'longitude', width: 120 },
  { title: '纬度', key: 'latitude', width: 120 },
  { title: '区县编码', key: 'city', width: 120 },
  { title: '乡镇编码', key: 'town', width: 120 },
  {
    title: '是否销号',
    key: 'isxh',
    width: 110,
    render(row) {
      return boolTextMap[String(row.isxh)] || yesNoMap[Number(row.isxh)] || row.isxh || '--'
    }
  },
  { title: '地址风险点网页地址', key: 'addressUrl', width: 220, ellipsis: { tooltip: true } },
  { title: '是否巡查', key: 'isValid', width: 110 },
  { title: '巡查人', key: 'reportUserName', width: 120, ellipsis: { tooltip: true } },
  {
    title: '巡查结果',
    key: 'isNormal',
    width: 110,
    render(row) {
      if (row.isNormal === '1' || row.isNormal === 1) return '正常'
      if (row.isNormal === '0' || row.isNormal === 0) return '异常'
      return row.isNormal || '--'
    }
  },
  { title: '巡查时间', key: 'reportTime', width: 170 },
  { title: '创建时间', key: 'createdTs', width: 160 },
  { title: '最后修改时间', key: 'lastModifiedTs', width: 160 }
]

const smallWatershedColumns = [
  indexColumn,
  { title: '时间', key: 'createdTs', width: 180 },
  {
    title: '预警等级',
    key: 'warnLevel',
    width: 120,
    render(row) {
      const warnLevel = warnLevelMap[Number(row.warnLevel)]
      if (!warnLevel) return row.warnLevel || '--'
      return h('span', { class: ['warn-level', warnLevel.className] }, warnLevel.label)
    }
  },
  {
    title: '详情',
    key: 'detail',
    width: 100,
    fixed: 'right',
    render(row) {
      return h('span', { class: 'detail-link', onClick: () => openSmallWatershedDetail(row) }, '查看')
    }
  }
]

const oldRiskColumns = [
  { ...indexColumn, fixed: 'left' },
  { title: '类型', key: 'riskTypeLabel', width: 120, fixed: 'left', ellipsis: { tooltip: true } },
  { title: '街道', key: 'street', width: 140, ellipsis: { tooltip: true } },
  { title: '所在地', key: 'location', width: 220, ellipsis: { tooltip: true } },
  { title: '经度', key: 'longitude', width: 120 },
  { title: '纬度', key: 'latitude', width: 120 },
  { title: '隐患描述', key: 'hazardDesc', width: 240, ellipsis: { tooltip: true } },
  {
    title: '是否落图',
    key: 'isMapFlag',
    width: 100,
    render(row) {
      const val = row.isMapFlag
      if (val === 1 || val === '1' || val === '是') return '是'
      if (val === 0 || val === '0' || val === '否') return '否'
      return val != null ? String(val) : '--'
    }
  },
  { title: '落图时间', key: 'mapTime', width: 160 },
  { title: '权属单位', key: 'ownerUnit', width: 180, ellipsis: { tooltip: true } },
  { title: '联系人', key: 'ownerContact', width: 140, ellipsis: { tooltip: true } },
  { title: '联系电话', key: 'ownerPhone', width: 140 },
  { title: '行业监管部门', key: 'supervisorDept', width: 180, ellipsis: { tooltip: true } },
  { title: '联系人', key: 'supervisorContact', width: 140, ellipsis: { tooltip: true } },
  { title: '联系电话', key: 'supervisorPhone', width: 140 },
  { title: '属地街道联系人', key: 'streetContact', width: 140, ellipsis: { tooltip: true } },
  { title: '联系电话', key: 'streetPhone', width: 140 },
  { title: '存在问题', key: 'existingProblem', width: 220, ellipsis: { tooltip: true } },
  { title: '措施建议', key: 'measureSuggestion', width: 220, ellipsis: { tooltip: true } },
  { title: '是否属于地灾', key: 'isGeoDisaster', width: 130 },
  { title: '隐患分级', key: 'hazardLevel', width: 120 },
  { title: '备注', key: 'remark', width: 180, ellipsis: { tooltip: true } },
  { title: '风险等级', key: 'riskLevel', width: 120 }
]

const greenSpaceColumns = [
  indexColumn,
  { title: '公园名称', key: 'name', width: 180, ellipsis: { tooltip: true } },
  { title: '公园面积', key: 'area', width: 120 },
  { title: '实时客流', key: 'realTimeFlow', width: 120 },
  { title: '负责人', key: 'manager', width: 120 },
  { title: '联系电话', key: 'phone', width: 140 }
]

const columns = computed(() => {
  if (selectedType.value === 'risk-small-watershed') return smallWatershedColumns
  if (selectedType.value === 'risk-green-space') return greenSpaceColumns
  if (selectedType.value === 'risk-four-old-trail') return oldRiskColumns
  if (oldRiskTypeMap[selectedType.value]) return oldRiskColumns
  return hiddenDangerColumns
})

const scrollX = computed(() => {
  if (selectedType.value === 'risk-four-old-trail') return 3500
  if (oldRiskTypeMap[selectedType.value]) return 3500
  if (selectedType.value === 'risk-green-space') return 800
  return selectedType.value === 'risk-small-watershed' ? 500 : 2670
})

const getValue = (item, keys) => {
  const key = keys.find(itemKey => item?.[itemKey] !== undefined && item?.[itemKey] !== null && item?.[itemKey] !== '')
  return key ? item[key] : '--'
}

const getHiddenDangerRow = item => ({
  ...item,
  type: getValue(item, ['type']),
  code: getValue(item, ['code']),
  name: getValue(item, ['name']),
  province: getValue(item, ['province']),
  provinceName: getValue(item, ['provinceName']),
  cityName: getValue(item, ['cityName']),
  townName: getValue(item, ['townName']),
  location: getValue(item, ['location']),
  longitude: getValue(item, ['longitude']),
  latitude: getValue(item, ['latitude']),
  city: getValue(item, ['city']),
  town: getValue(item, ['town']),
  isxh: getValue(item, ['isxh']),
  addressUrl: getValue(item, ['addressUrl']),
  isValid: getValue(item, ['isValid']),
  reportUserName: getValue(item, ['reportUserName']),
  isNormal: getValue(item, ['isNormal']),
  reportTime: getValue(item, ['reportTime']),
  createdTs: getValue(item, ['createdTs']),
  lastModifiedTs: getValue(item, ['lastModifiedTs'])
})

const getSmallWatershedRow = item => ({
  ...item,
  createdTs: getValue(item, ['createdTs', 'createTime', 'createdTime']),
  warnLevel: getValue(item, ['warnLevel']),
  fileId: getValue(item, ['fileId', 'fileID', 'file_id', 'imgFileId', 'pictureFileId'])
})

const getOldRiskRow = item => ({
  ...item,
  riskTypeLabel: oldRiskTypeLabelMap[item?.riskType] || item?.riskType || '--',
  street: getValue(item, ['street']),
  location: getValue(item, ['location']),
  longitude: getValue(item, ['longitude']),
  latitude: getValue(item, ['latitude']),
  hazardDesc: getValue(item, ['hazardDesc']),
  isMapFlag: getValue(item, ['isMapFlag', 'isMap']),
  mapTime: getValue(item, ['mapTime', 'mapDate']),
  ownerUnit: getValue(item, ['ownerUnit']),
  ownerContact: getValue(item, ['ownerContact']),
  ownerPhone: getValue(item, ['ownerPhone', 'ownerTel']),
  supervisorDept: getValue(item, ['supervisorDept']),
  supervisorContact: getValue(item, ['supervisorContact']),
  supervisorPhone: getValue(item, ['supervisorPhone', 'supervisorTel']),
  streetContact: getValue(item, ['streetContact']),
  streetPhone: getValue(item, ['streetPhone', 'streetTel']),
  existingProblem: getValue(item, ['existingProblem']),
  measureSuggestion: getValue(item, ['measureSuggestion']),
  isGeoDisaster: getValue(item, ['isGeoDisaster']),
  hazardLevel: getValue(item, ['hazardLevel']),
  remark: getValue(item, ['remark']),
  riskLevel: getValue(item, ['riskLevel'])
})

const getResponseRecords = data => {
  const result = data?.data ?? data
  const list = Array.isArray(result)
    ? result
    : result?.content || result?.page?.content || result?.records || result?.list || result?.rows || []
  return Array.isArray(list) ? list : []
}

const getResponsePage = data => {
  const result = data?.data ?? data
  const total = Number(result?.page?.total ?? result?.total)
  return {
    records: getResponseRecords(data),
    pages: Number(result?.pages) || (total ? Math.ceil(total / pageSize) : 1)
  }
}

const fetchGreenSpace = () => {
  const start = (currentPage.value - 1) * pageSize
  const rows = greenSpaceRows.slice(start, start + pageSize)
  return {
    records: rows,
    pages: Math.max(1, Math.ceil(greenSpaceRows.length / pageSize))
  }
}

const fetchOldRisk = async type => {
  const res = await axios.get(`${OLD_BASE_URL}/risk/detail`, {
    params: {
      pageIndex: currentPage.value,
      pageSize
    }
  })
  return getResponsePage(res?.data)
}

// 四老一步道：接口已调整为不传 type 查询全部类型。
const fetchFourOldTrail = async () => {
  const res = await axios.get(`${OLD_BASE_URL}/risk/detail`, {
    params: {
      pageIndex: currentPage.value,
      pageSize
    }
  })
  return getResponsePage(res?.data)
}

const fetchByType = async type => {
  if (type === 'risk-green-space') return fetchGreenSpace()
  if (type === 'risk-four-old-trail') return fetchFourOldTrail()
  if (oldRiskTypeMap[type]) return fetchOldRisk(type)

  if (type === 'risk-hidden-danger') {
    const res = await axios.get(`${LAND_BASE_URL}/landGeoHazardPointPage`, {
      params: {
        Index: currentPage.value,
        size: pageSize
      }
    })
    return getResponsePage(res?.data)
  }

  if (type === 'risk-small-watershed') {
    const res = await axios.get(`${LAND_BASE_URL}/landRainXlyImagePage`, {
      params: {
        pageNumber: currentPage.value,
        pageSize
      }
    })
    return getResponsePage(res?.data)
  }

  return { records: [], pages: 1 }
}

const toTableRows = records => {
  return records.map((item, index) => {
    const type = item.__riskType || selectedType.value
    const row = type === 'risk-small-watershed'
      ? getSmallWatershedRow(item)
      : type === 'risk-four-old-trail' || oldRiskTypeMap[type]
        ? getOldRiskRow(item)
        : item.__greenSpace || type === 'risk-green-space'
        ? item
        : getHiddenDangerRow(item)
    return {
      ...row,
      key: item.pointId || item.unifiedcode || item.id || item.fileId || item.name || `${type}_${index}`
    }
  })
}

const getRiskPointList = async () => {
  try {
    const pageData = await fetchByType(selectedType.value)
    totalPage.value = pageData.pages || 1
    tableData.value = toTableRows(pageData.records)
    console.log("tableData",tableData.value)
  } catch (error) {
    console.error('获取风险点列表失败', error)
    tableData.value = []
    totalPage.value = 1
  }
}

function handleClose() {
  emit('close')
}

function openSmallWatershedDetail(row) {
  emit('open-small-watershed-detail', row)
}

function handleTypeChange() {
  currentPage.value = 1
  getRiskPointList()
}

function handlePageChange(page) {
  currentPage.value = page
  getRiskPointList()
}

onMounted(() => {
  getRiskPointList()
})
</script>

<script>
export default {
  name: 'GeoRiskPointList'
}
</script>

<style lang="scss" scoped>
@import './geo-list-modal.scss';

:deep(.warn-level) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 72px;
  height: 24px;
  box-sizing: border-box;
  padding: 0 8px;
  border: 1px solid currentColor;
  border-radius: 2px;
  font-size: 13px;
}

:deep(.warn-level--blue) {
  color: #35a8ff;
  background: rgba(53, 168, 255, 0.14);
}

:deep(.warn-level--yellow) {
  color: #ffd84d;
  background: rgba(255, 216, 77, 0.14);
}

:deep(.warn-level--orange) {
  color: #ff9f43;
  background: rgba(255, 159, 67, 0.14);
}

:deep(.warn-level--red) {
  color: #ff5b6b;
  background: rgba(255, 91, 107, 0.14);
}

:deep(.detail-link) {
  color: #35a8ff;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}
</style>

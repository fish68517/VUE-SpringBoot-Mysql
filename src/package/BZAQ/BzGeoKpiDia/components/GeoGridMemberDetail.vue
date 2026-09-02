<template>
  <div class="grid-member-detail">
    <header class="detail-header">
      <span>群测群防员</span>
      <button type="button" class="close-btn" aria-label="关闭" @click="$emit('close')">×</button>
    </header>

    <main class="detail-body">
      <aside class="profile-panel">
        <div class="photo-frame">
          <img v-if="photoUrl && !photoFailed" :src="photoUrl" alt="人员照片" @error="photoFailed = true">
          <div v-else class="photo-placeholder">暂无照片</div>
        </div>
        <div class="profile-name">{{ text('fullName', 'name', 'personName') }}</div>
        <div class="profile-phone">{{ maskPhone(text('telephone', 'phone', 'mobile')) }}</div>

        <div class="member-info">
          <div
            v-for="row in detailRows"
            :key="row.key"
            class="detail-row"
          >
            <span class="row-label">{{ row.label }}：</span>
            <span class="row-value">{{ row.value }}</span>
          </div>
          <div class="detail-row detail-row--danger">
            <span class="row-label">责任隐患点：</span>
            <div class="danger-point-list">
              <button
                v-for="item in disasterItems"
                :key="item.unifiedcode || item.name"
                type="button"
                class="danger-point"
                :class="{ 'is-active': item.unifiedcode && item.unifiedcode === selectedUnifiedCode }"
                :title="item.name"
                @click="selectDisasterPoint(item)"
              >
                <span class="danger-point__name">{{ item.name }}</span>
                <span class="danger-point__icon">⌖</span>
              </button>
              <span v-if="!disasterItems.length" class="row-value">-</span>
            </div>
          </div>
        </div>
      </aside>

      <section class="track-panel">
        <div class="track-header">
          <span class="track-title">巡查轨迹</span>
          <select v-model="periodType" class="period-select" @change="handlePeriodChange">
            <option value="week">近一周</option>
            <option value="month">当月</option>
          </select>
        </div>
        <n-data-table
          class="track-table"
          :columns="recordColumns"
          :data="recordRows"
          :loading="recordLoading"
          :pagination="false"
          :bordered="false"
          :max-height="260"
          :scroll-x="760"
          size="small"
        />
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { NDataTable } from 'naive-ui'
import axios from 'axios'

const props = defineProps<{ detail?: Record<string, any> }>()
defineEmits(['close'])

const LAND_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/land'
const photoFailed = ref(false)
const photoUrl = ref('')
let photoRequestId = 0
const periodType = ref('week')
const personnelPointDetail = ref<Record<string, any>>({})
const selectedUnifiedCode = ref('')
const recordLoading = ref(false)
const recordRows = ref<any[]>([])

const value = (...keys: string[]) => keys.map(key => props.detail?.[key]).find(item => item !== undefined && item !== null && item !== '')
const text = (...keys: string[]) => String(value(...keys) ?? '-')
const photoFileId = computed(() => String(value('headUrl', 'photoUrl', 'avatar', 'imageUrl', 'photo') || ''))

const memberTypeMap: Record<string, string> = {
  '1': '地环站人员',
  '2': '片区负责人',
  '3': '驻守地质队员',
  '4': '群测群防员'
}

const recordColumns = [
  { title: '时间', key: 'reportTime', width: 170, ellipsis: { tooltip: true } },
  { title: '姓名', key: 'reportUserName', width: 130, ellipsis: { tooltip: true } },
  { title: '类型', key: 'typeName', width: 130, ellipsis: { tooltip: true } },
  { title: '隐患点', key: 'disasterName', width: 220, ellipsis: { tooltip: true } },
  { title: '巡查情况', key: 'remark', width: 110, ellipsis: { tooltip: true } }
]

/** 将网格员类型编码转换为弹窗展示的中文名称。 */
const formatMemberType = (type = value('type')) => {
  const key = String(type ?? '')
  return memberTypeMap[key] || String(type ?? '-')
}

/** 将手机号中间四位脱敏，避免弹窗直接暴露完整号码。 */
const maskPhone = (phone: string) => {
  if (!/^1\d{10}$/.test(phone)) return phone || '-'
  return phone.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2')
}

/** 释放头像预览生成的 blob 地址，避免弹窗反复打开造成内存占用。 */
const revokePhotoUrl = () => {
  if (photoUrl.value?.startsWith('blob:')) {
    URL.revokeObjectURL(photoUrl.value)
  }
  photoUrl.value = ''
}

/** 根据 base64 内容前缀判断图片 MIME 类型。 */
const getBase64MimeType = (base64: string) => {
  if (base64.startsWith('/9j/')) return 'image/jpeg'
  if (base64.startsWith('iVBOR')) return 'image/png'
  if (base64.startsWith('R0lGOD')) return 'image/gif'
  if (base64.startsWith('UklGR')) return 'image/webp'
  return 'image/png'
}

/** 兼容 previewFile 返回 blob、JSON 字符串或纯 base64 的情况。 */
const getPreviewPayload = (payload: any): any => {
  if (payload == null) return ''
  if (typeof payload !== 'string') return payload

  const text = payload.trim()
  if (!text) return ''

  try {
    const result = JSON.parse(text)
    return result?.data ?? result?.url ?? result?.fileUrl ?? result?.previewUrl ?? result
  } catch (_error) {
    return text
  }
}

/** 将 base64 图片内容转换为可被 img 标签展示的地址。 */
const base64ToBlobUrl = (payload: any) => {
  const text = String(payload || '').trim()
  if (!text) return ''
  if (text.startsWith('data:image/')) return text

  const base64 = text.replace(/^data:image\/\w+;base64,/, '').replace(/\s/g, '')
  if (!/^[A-Za-z0-9+/]+={0,2}$/.test(base64)) return ''

  const byteCharacters = window.atob(base64)
  const byteArrays = []
  const sliceSize = 1024

  for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
    const slice = byteCharacters.slice(offset, offset + sliceSize)
    const byteNumbers = new Array(slice.length)

    for (let i = 0; i < slice.length; i += 1) {
      byteNumbers[i] = slice.charCodeAt(i)
    }

    byteArrays.push(new Uint8Array(byteNumbers))
  }

  return URL.createObjectURL(new Blob(byteArrays, { type: getBase64MimeType(base64) }))
}

/** 将 previewFile 返回的 Blob 转成头像图片地址。 */
const blobToImageUrl = async (blob: Blob) => {
  if (!blob?.size) return ''

  if (blob.type?.startsWith('image/')) {
    return URL.createObjectURL(blob)
  }

  const payload = getPreviewPayload(await blob.text())

  if (payload instanceof Blob) {
    return blobToImageUrl(payload)
  }

  if (typeof payload === 'string') {
    return base64ToBlobUrl(payload)
  }

  return ''
}

/** 根据网格员 headUrl 文件 id 请求头像文件流。 */
const requestPhotoPreview = async (fileId: string) => {
  const res = await axios.get(`${LAND_BASE_URL}/previewFile`, {
    params: { fileId },
    responseType: 'blob'
  })
  const fileData = getPreviewPayload(res?.data?.data ?? res?.data ?? res)

  if (fileData instanceof Blob) {
    return blobToImageUrl(fileData)
  }

  if (fileData instanceof ArrayBuffer) {
    return fileData.byteLength ? URL.createObjectURL(new Blob([fileData])) : ''
  }

  if (typeof fileData === 'string') {
    return base64ToBlobUrl(fileData)
  }

  return ''
}

/** 加载网格员头像；headUrl 作为文件 id，通过 previewFile 获取真实图片流。 */
const loadPhoto = async () => {
  const fileId = photoFileId.value
  const requestId = ++photoRequestId
  photoFailed.value = false
  revokePhotoUrl()

  if (!fileId) return

  try {
    const imageUrl = await requestPhotoPreview(fileId)
    if (requestId !== photoRequestId) {
      if (imageUrl?.startsWith('blob:')) URL.revokeObjectURL(imageUrl)
      return
    }
    photoUrl.value = imageUrl || ''
  } catch (error) {
    console.error('获取网格员头像失败:', error)
    if (requestId === photoRequestId) photoUrl.value = ''
  }
}

const detailValue = (...keys: string[]) => {
  const detail = personnelPointDetail.value || {}
  const matchedKey = keys.find(key => detail?.[key] !== undefined && detail?.[key] !== null && detail?.[key] !== '')
  return matchedKey ? String(detail[matchedKey]) : '-'
}

const detailRows = computed(() => [
  { key: 'cityName', label: '区县', value: detailValue('cityName', 'city') },
  { key: 'townName', label: '乡镇', value: detailValue('townName', 'town') }
])

/** 将责任隐患点 JSON 字符串解析为可点击列表。 */
const parseDisasterList = (value: any) => {
  if (Array.isArray(value)) return value
  if (typeof value !== 'string' || !value.trim()) return []

  try {
    const list = JSON.parse(value)
    return Array.isArray(list) ? list : []
  } catch (_error) {
    return []
  }
}

const disasterItems = computed(() => {
  const list = parseDisasterList(personnelPointDetail.value?.disasterList)
  return list
    .map((item, index) => ({
      name: item?.name || item?.disasterName || `隐患点${index + 1}`,
      unifiedcode: item?.unifiedcode || item?.unifiedCode || item?.code || ''
    }))
    .filter(item => item.name || item.unifiedcode)
})

/** 格式化日期为接口要求的 yyyy-MM-dd HH:mm:ss。 */
const formatDateTime = (date: Date) => {
  const pad = (num: number) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

/** 根据筛选项计算巡查记录查询时间范围。 */
const getTimeRange = () => {
  const end = new Date()
  const start = new Date(end)

  if (periodType.value === 'month') {
    start.setDate(1)
    start.setHours(0, 0, 0, 0)
  } else {
    start.setDate(start.getDate() - 6)
    start.setHours(0, 0, 0, 0)
  }

  return {
    startTime: formatDateTime(start),
    endTime: formatDateTime(end)
  }
}

/** 兼容接口常见返回结构，提取巡查记录数组。 */
const getResponseList = (data: any) => {
  const payload = data?.data ?? data
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.content)) return payload.content
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload?.list)) return payload.list
  return []
}

/** 兼容接口常见返回结构，提取单条详情对象。 */
const getResponseDetail = (data: any) => {
  const payload = data?.data ?? data
  return payload && !Array.isArray(payload) ? payload : {}
}

/** 根据巡查记录正常/异常状态转换为表格展示文案。 */
const getPatrolStatus = (item: Record<string, any>) => {
  if (item.isNormal === 1 || item.isNormal === '1') return '正常'
  if (item.isNormal === 0 || item.isNormal === '0') return '异常'
  return item.remark || '-'
}

/** 将接口返回的巡查记录转换为表格行数据。 */
const toRecordRow = (item: Record<string, any>, index: number) => ({
  key: item.thirdPartyId || item.id || `${item.reportUserId || 'record'}_${index}`,
  reportTime: item.reportTime || '-',
  reportUserName: item.reportUserName || text('fullName', 'name', 'personName'),
  typeName: formatMemberType(value('type')),
  disasterName: item.disasterName || '-',
  patrolStatus: getPatrolStatus(item),
  raw: item
})

/** 查询网格员巡查点位详情，用于左侧详情字段展示。 */
const fetchPersonnelPointDetail = async () => {
  const id = value('personnelId', 'id', 'externalId', 'reportUserId')

  if (!id) {
    personnelPointDetail.value = {}
    return
  }

  try {
    const res = await axios.post(`${LAND_BASE_URL}/landQcqfPersonnelPoint`, {
      id,
      ...getTimeRange()
    })
    personnelPointDetail.value = getResponseDetail(res?.data)
    const disasterList = parseDisasterList(personnelPointDetail.value?.disasterList)
    const codeList = disasterList.map(item => item?.unifiedcode || item?.unifiedCode || item?.code || '').filter(Boolean)
    if (!codeList.includes(selectedUnifiedCode.value)) {
      selectedUnifiedCode.value = codeList[0] || ''
    }
    fetchPatrolRecords()
  } catch (error) {
    console.error('获取网格员点位详情失败:', error)
    personnelPointDetail.value = {}
    selectedUnifiedCode.value = ''
  }
}

/** 查询网格员巡查记录，支持近一周和当月两种时间范围。 */
const fetchPatrolRecords = async () => {
  const personnelId = value('personnelId', 'id', 'externalId', 'reportUserId')

  if (!personnelId) {
    recordRows.value = []
    return
  }

  recordLoading.value = true
  try {
    const res = await axios.post(`${LAND_BASE_URL}/landQcqfPersonnelRecord`, {
      personnelId: personnelId || '',
      unifiedCode: selectedUnifiedCode.value || '',
      ...getTimeRange()
    })
    recordRows.value = getResponseList(res?.data).map(toRecordRow)
  } catch (error) {
    console.error('获取网格员巡查记录失败:', error)
    recordRows.value = []
  } finally {
    recordLoading.value = false
  }
}

/** 根据当前时间范围刷新左侧详情和右侧巡查记录。 */
const refreshGridMemberData = () => {
  fetchPersonnelPointDetail()
}

/** 点击责任隐患点后，携带该隐患点统一编码刷新右侧巡查记录。 */
const selectDisasterPoint = (item: { name: string, unifiedcode: string }) => {
  selectedUnifiedCode.value = item.unifiedcode || ''
  fetchPatrolRecords()
}

/** 切换近一周/当月时同步刷新详情统计和巡查记录。 */
const handlePeriodChange = () => {
  refreshGridMemberData()
}

watch(() => props.detail, () => {
  loadPhoto()
  selectedUnifiedCode.value = ''
  refreshGridMemberData()
}, { deep: true })

onMounted(() => {
  loadPhoto()
  refreshGridMemberData()
})

onBeforeUnmount(() => {
  photoRequestId += 1
  revokePhotoUrl()
})
</script>

<style scoped lang="scss">
.grid-member-detail {
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1210px;
  height: 430px;
  color: #d9ecff;
  overflow: hidden;
  font-family: "Microsoft YaHei", sans-serif;
  background: url('../img/Rectangle_346242153.png') no-repeat center/cover;
  background-size: 100% 100%;
}

.detail-header {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #b9dcff;
  background: linear-gradient(90deg, transparent, rgba(24, 105, 190, .45), transparent);
  border-bottom: 1px solid rgba(50, 144, 234, .35);
}

.close-btn {
  position: absolute;
  right: 12px;
  top: 7px;
  border: 0;
  background: transparent;
  color: #b9dcff;
  font-size: 30px;
  line-height: 30px;
  cursor: pointer;
}

.detail-body {
  display: grid;
  grid-template-columns: 215px minmax(0, 1fr);
  gap: 10px;
  height: 365px;
  padding: 20px 18px;
}

.profile-panel,
.track-panel {
  background: linear-gradient(90deg, rgba(16, 92, 166, .55), rgba(4, 49, 95, .65));
  border: 1px solid rgba(50, 144, 234, .45);
  box-sizing: border-box;
}

.profile-panel {
  padding: 10px;
  text-align: center;
}

.photo-frame {
  width: 92px;
  height: 112px;
  margin: 0 auto 10px;
  border: 1px solid #71b8f0;
  background: #285f91;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.photo-placeholder {
  height: 100%;
  display: grid;
  place-items: center;
  color: #8cb9dd;
  font-size: 13px;
}

.profile-name {
  color: #f2fbff;
  font-size: 16px;
  font-weight: 700;
  line-height: 28px;
}

.profile-phone {
  color: #f2fbff;
  font-family: 'D-DIN', Arial, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 30px;
}

.member-info {
  margin-top: 10px;
  max-height: 122px;
  overflow: auto;
  text-align: left;
  scrollbar-color: #297fb8 rgba(5, 44, 82, .5);
  scrollbar-width: thin;
}

.detail-row {
  display: grid;
  grid-template-columns: 86px minmax(0, 1fr);
  min-height: 26px;
  align-items: start;
  font-size: 13px;
  line-height: 1.45;
}

.row-label {
  color: #7faed4;
}

.row-value {
  min-width: 0;
  color: #d9ecff;
  word-break: break-all;
}

.detail-row--danger {
  grid-template-columns: 86px minmax(0, 1fr);
}

.danger-point-list {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.danger-point {
  min-width: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
  padding: 0;
  border: 0;
  background: transparent;
  color: #d9ecff;
  font-size: 13px;
  line-height: 18px;
  text-align: left;
  cursor: pointer;
}

.danger-point:hover,
.danger-point.is-active {
  color: #ffd039;
}

.danger-point__name {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.danger-point__icon {
  flex: none;
  color: #56c5ff;
  font-size: 13px;
}

.track-panel {
  min-width: 0;
  padding: 0 10px 10px;
}

.track-header {
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #bceeff;
  font-size: 16px;
  font-weight: 700;
}

.track-title {
  padding-left: 10px;
}

.period-select {
  width: 72px;
  height: 24px;
  color: #8fc8ff;
  background: rgba(21, 89, 151, .85);
  border: 1px solid rgba(92, 173, 255, .65);
  border-radius: 3px;
  font-size: 12px;
  outline: none;
}

.track-table {
  height: 284px;

  :deep(.n-data-table-wrapper) {
    background: transparent;
  }

  :deep(.n-data-table-table) {
    background: transparent;
    font-size: 13px;
  }

  :deep(.n-data-table-th) {
    height: 38px;
    color: #e2f7ff;
    background: linear-gradient(180deg, rgba(38, 106, 224, .72), rgba(20, 91, 164, .72));
    border: 0;
    text-align: center;
  }

  :deep(.n-data-table-td) {
    height: 36px;
    color: #e8f8ff;
    background: rgba(16, 67, 113, .7);
    border: 0;
    text-align: center;
  }

  :deep(.n-data-table-tr:nth-child(even) .n-data-table-td) {
    background: rgba(47, 93, 137, .62);
  }

  :deep(.n-data-table-empty) {
    color: #8bbce6;
    background: rgba(16, 67, 113, .45);
  }

  :deep(.n-scrollbar-rail--vertical) {
    width: 4px;
  }

  :deep(.n-scrollbar-rail__scrollbar) {
    background: rgba(71, 202, 255, .68);
  }
}
</style>

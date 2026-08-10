<template>
  <section class="admin-module-page">
    <div class="admin-page-heading">
      <div>
        <p class="eyebrow">{{ module.toUpperCase() }}</p>
        <h1>{{ config.title }}</h1>
        <p>{{ config.description }}</p>
      </div>
      <div class="admin-heading-actions">
        <el-date-picker v-if="config.regenerate" v-model="statDate" type="date" value-format="YYYY-MM-DD" />
        <el-button v-if="config.regenerate" :loading="specialLoading" @click="handleRegenerate">重新生成统计</el-button>
        <el-button type="primary" @click="openCreate">新增数据</el-button>
      </div>
    </div>

    <div class="admin-query-card">
      <el-input v-model="query.keyword" clearable placeholder="输入名称、编号或关键字" @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
      <span>共 {{ total }} 条数据</span>
    </div>

    <div class="admin-table-card" v-loading="loading">
      <el-table :data="records" stripe height="calc(100vh - 330px)" empty-text="暂无管理数据">
        <el-table-column
          v-for="column in config.columns"
          :key="column.key"
          :prop="column.key"
          :label="column.label"
          :width="column.width"
          :min-width="column.minWidth || 110"
          show-overflow-tooltip
        >
          <template #default="scope">
            <el-tag v-if="column.options" :type="tagType(column.key, scope.row[column.key])" effect="light">
              {{ optionLabel(column.options, scope.row[column.key]) }}
            </el-tag>
            <span v-else-if="column.lookup">{{ lookupLabel(column.lookup, scope.row[column.key]) }}</span>
            <span v-else>{{ scope.row[column.key] ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="removeRow(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="admin-pagination">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? `编辑${config.title}` : `新增${config.title}`" width="760px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="admin-form-grid">
          <el-form-item
            v-for="item in config.fields"
            :key="item.key"
            :label="item.label"
            :prop="item.key"
            :class="{ 'span-two': item.span === 2 }"
          >
            <el-select v-if="item.type === 'select'" v-model="form[item.key]" style="width: 100%">
              <el-option v-for="option in item.options" :key="option.value" :label="option.label" :value="option.value" />
            </el-select>
            <el-select v-else-if="item.type === 'lookup'" v-model="form[item.key]" filterable clearable style="width: 100%">
              <el-option v-if="item.allowZero" label="无上级区域" :value="0" />
              <el-option v-for="option in lookups[item.lookup] || []" :key="option.id" :label="lookupOptionLabel(item.lookup, option)" :value="option.id" />
            </el-select>
            <el-input-number v-else-if="item.type === 'number'" v-model="form[item.key]" :precision="item.precision" :step="item.precision ? 0.01 : 1" controls-position="right" style="width: 100%" />
            <el-date-picker v-else-if="item.type === 'date'" v-model="form[item.key]" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            <el-date-picker v-else-if="item.type === 'datetime'" v-model="form[item.key]" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            <el-input v-else-if="item.type === 'textarea'" v-model="form[item.key]" type="textarea" :rows="3" />
            <el-input v-else v-model="form[item.key]" />
          </el-form-item>
        </div>
        <div v-if="config.geocode" class="geocode-row">
          <el-button :loading="geocoding" @click="geocodeAddress">根据地址识别经纬度</el-button>
          <span>识别结果会自动回填经度和纬度，保存前仍可手工调整。</span>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createAdminItem, deleteAdminItem, getAdminPage, regenerateStatistics, updateAdminItem } from '../../api/admin'
import { loadAmap } from '../../utils/amapLoader'
import { adminModules, lookupLabelKeys } from './adminModules'

const props = defineProps({ module: { type: String, required: true } })
const config = computed(() => adminModules[props.module])
const loading = ref(false)
const saving = ref(false)
const geocoding = ref(false)
const specialLoading = ref(false)
const records = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({})
const formRef = ref()
const lookups = reactive({ users: [], regions: [], stations: [], piles: [] })
const statDate = ref(new Date().toISOString().slice(0, 10))

const rules = computed(() => Object.fromEntries(config.value.fields
  .filter(item => item.required)
  .map(item => [item.key, [{ required: true, message: `请填写${item.label}`, trigger: 'blur' }]])))

const loadData = async () => {
  loading.value = true
  try {
    const data = await getAdminPage(props.module, query)
    records.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error.message || '管理数据加载失败')
  } finally {
    loading.value = false
  }
}

const loadLookups = async () => {
  const needed = new Set(config.value.fields.filter(item => item.lookup).map(item => item.lookup))
  config.value.columns.filter(item => item.lookup).forEach(item => needed.add(item.lookup))
  await Promise.all([...needed].map(async moduleName => {
    try {
      const data = await getAdminPage(moduleName, { pageNum: 1, pageSize: 500 })
      lookups[moduleName] = data.records
    } catch {
      lookups[moduleName] = []
    }
  }))
}

const resetForm = (row = {}) => {
  Object.keys(form).forEach(key => delete form[key])
  config.value.fields.forEach(item => {
    form[item.key] = row[item.key] ?? item.default ?? null
  })
}

const openCreate = () => {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

const openEdit = row => {
  editingId.value = row.id
  resetForm(row)
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

const save = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    if (editingId.value) await updateAdminItem(props.module, editingId.value, form)
    else await createAdminItem(props.module, form)
    ElMessage.success(editingId.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    await loadData()
    await loadLookups()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const removeRow = async row => {
  try {
    await ElMessageBox.confirm(`确定删除 ID 为 ${row.id} 的数据吗？`, '删除确认', { type: 'warning' })
    await deleteAdminItem(props.module, row.id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error(error.message || '删除失败')
  }
}

const geocodeAddress = async () => {
  if (!form.address) {
    ElMessage.warning('请先填写详细地址')
    return
  }
  geocoding.value = true
  try {
    const AMap = await loadAmap()
    const geocoder = new AMap.Geocoder()
    await new Promise((resolve, reject) => {
      geocoder.getLocation(form.address, (status, result) => {
        if (status === 'complete' && result.geocodes?.length) {
          form.longitude = Number(result.geocodes[0].location.lng.toFixed(6))
          form.latitude = Number(result.geocodes[0].location.lat.toFixed(6))
          resolve()
        } else reject(new Error('未识别到有效坐标，请补充城市和区县'))
      })
    })
    ElMessage.success('经纬度识别成功')
  } catch (error) {
    ElMessage.error(error.message || '地址识别失败')
  } finally {
    geocoding.value = false
  }
}

const handleRegenerate = async () => {
  specialLoading.value = true
  try {
    const data = await regenerateStatistics(statDate.value)
    ElMessage.success(`${data.statDate} 统计已重新生成`)
    await loadData()
  } catch (error) {
    ElMessage.error(error.message || '统计生成失败')
  } finally {
    specialLoading.value = false
  }
}

const optionLabel = (options, value) => options.find(item => item.value === value)?.label ?? value ?? '-'
const lookupOptionLabel = (moduleName, row) => (lookupLabelKeys[moduleName] || ['id']).map(key => row[key]).filter(Boolean).join(' · ') || `ID ${row.id}`
const lookupLabel = (moduleName, id) => {
  const row = lookups[moduleName]?.find(item => Number(item.id) === Number(id))
  return row ? lookupOptionLabel(moduleName, row) : (id ?? '-')
}
const tagType = (key, value) => {
  if (['status', 'enableStatus', 'publishStatus', 'processStatus', 'recordStatus'].includes(key)) {
    return value === 1 ? 'success' : value === 2 ? 'warning' : value === 4 ? 'danger' : 'info'
  }
  if (key === 'workStatus') return value === 0 ? 'success' : value === 4 ? 'danger' : value === 1 ? 'warning' : 'info'
  return ''
}
const resetQuery = () => { query.keyword = ''; query.pageNum = 1; loadData() }

const initialize = async () => {
  query.pageNum = 1
  query.keyword = ''
  await Promise.all([loadData(), loadLookups()])
}

watch(() => props.module, initialize)
onMounted(initialize)
</script>

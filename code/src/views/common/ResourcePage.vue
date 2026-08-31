<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Download, EditPen, Plus, Refresh, Search, View } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import StatusTag from '@/components/StatusTag/index.vue'
import { resourceConfigs } from '@/config/resource-config'
import {
  changeResourceStatus,
  createResource,
  deleteResource,
  getResourcePage,
  updateResource,
} from '@/api/resource'
import type { PageQuery } from '@/types/api'
import type { ResourceField, ResourceRecord } from '@/types/system'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const config = computed(() => resourceConfigs[String(route.meta.resourceKey)]!)
const loading = ref(false)
const rows = ref<ResourceRecord[]>([])
const total = ref(0)
const selected = ref<ResourceRecord[]>([])
const query = ref<PageQuery>({ pageNum: 1, pageSize: 10, keyword: '', status: '' })
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit' | 'view'>('create')
const form = ref<ResourceRecord>({ id: 0 })
const formRef = ref<FormInstance>()

const searchFields = computed(() => config.value.fields.filter((field) => field.searchable).slice(0, 2))
const tableFields = computed(() => config.value.fields.filter((field) => !field.hideInTable))
const formFields = computed(() => config.value.fields.filter((field) => !field.hideInForm))
const rules = computed<FormRules>(() => {
  const value: FormRules = {}
  formFields.value.forEach((field) => {
    if (field.required) value[field.prop] = [{ required: true, message: `请输入${field.label}`, trigger: 'blur' }]
  })
  return value
})

function fieldTag(field: ResourceField, row: ResourceRecord) {
  return field.options?.find((item) => item.value === row[field.prop])
}

async function load() {
  loading.value = true
  try {
    const params: PageQuery = { ...query.value }
    searchFields.value.forEach((field) => {
      const value = query.value[field.prop]
      if (value !== undefined && value !== '') params[field.prop] = value
    })
    const result = await getResourcePage(config.value.module, config.value.resource, params)
    rows.value = result.data.records
    total.value = result.data.total
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  query.value = { pageNum: 1, pageSize: 10, keyword: '', status: '' }
  load()
}

function openDialog(mode: 'create' | 'edit' | 'view', row?: ResourceRecord) {
  dialogMode.value = mode
  form.value = row ? { ...row } : { id: 0, status: 1 }
  dialogVisible.value = true
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate()
  if (dialogMode.value === 'create') await createResource(config.value.module, config.value.resource, form.value)
  else await updateResource(config.value.module, config.value.resource, form.value)
  ElMessage.success(dialogMode.value === 'create' ? '新增成功' : '保存成功')
  dialogVisible.value = false
  await load()
}

async function remove(row: ResourceRecord) {
  await ElMessageBox.confirm(`确定删除“${row[config.value.fields[0]?.prop || 'id']}”吗？`, '删除确认', {
    confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning',
  })
  await deleteResource(config.value.module, config.value.resource, row.id)
  ElMessage.success('删除成功')
  await load()
}

async function removeSelected() {
  if (!selected.value.length) return ElMessage.warning('请先选择数据')
  await ElMessageBox.confirm(`确定删除选中的 ${selected.value.length} 条数据吗？`, '批量删除', { type: 'warning' })
  await Promise.all(selected.value.map((row) => deleteResource(config.value.module, config.value.resource, row.id)))
  ElMessage.success('批量删除成功')
  await load()
}

async function toggleStatus(row: ResourceRecord, status: string | number | boolean | undefined) {
  const value = Number(status)
  await changeResourceStatus(config.value.module, config.value.resource, row.id, value)
  row.status = value
  ElMessage.success(value === 1 ? '已启用' : '已停用')
}

function exportData() {
  const headers = tableFields.value.map((field) => field.label)
  const values = rows.value.map((row) => tableFields.value.map((field) => String(row[field.prop] ?? '')))
  const csv = '\ufeff' + [headers, ...values].map((line) => line.map((item) => `"${item.replace(/"/g, '""')}"`).join(',')).join('\n')
  const link = document.createElement('a')
  link.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }))
  link.download = `${config.value.title}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
}

function view(row: ResourceRecord) {
  if (config.value.key === 'event') router.push(`/event/detail/${row.id}`)
  else openDialog('view', row)
}

function pageChange(pageNum: number, pageSize: number) {
  query.value.pageNum = pageNum
  query.value.pageSize = pageSize
  load()
}

onMounted(load)
</script>

<template>
  <section class="resource-page">
    <PageHeader :title="config.title" :description="config.description">
      <el-button :icon="Download" @click="exportData">导出</el-button>
      <el-button v-if="!config.readOnly" v-permission="`${config.permissionPrefix}:add`" type="primary" :icon="Plus" @click="openDialog('create')">
        新增{{ config.primaryLabel }}
      </el-button>
    </PageHeader>

    <div class="search-panel page-card">
      <el-form inline label-position="left" @submit.prevent="load">
        <el-form-item v-for="field in searchFields" :key="field.prop" :label="field.label">
          <el-select v-if="field.kind === 'select'" v-model="query[field.prop]" clearable :placeholder="`请选择${field.label}`" class="query-control">
            <el-option v-for="option in field.options" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
          <el-input v-else v-model="query[field.prop] as string" clearable :placeholder="field.placeholder || `请输入${field.label}`" class="query-control" />
        </el-form-item>
        <el-form-item label="状态" v-if="config.fields.some((field) => field.prop === 'status')">
          <el-select v-model="query.status" clearable placeholder="全部状态" class="status-control">
            <el-option label="启用" :value="1" /><el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-panel page-card">
      <div class="table-toolbar">
        <div>
          <span class="result-title">{{ config.title }}</span>
          <span class="result-count">共 {{ total }} 条记录</span>
        </div>
        <div class="toolbar-actions">
          <el-button v-if="!config.readOnly && config.allowDelete !== false" plain :icon="Delete" :disabled="!selected.length" @click="removeSelected">批量删除</el-button>
          <el-button circle :icon="Refresh" title="刷新" @click="load" />
        </div>
      </div>

      <el-table v-loading="loading" :data="rows" stripe row-key="id" @selection-change="selected = $event">
        <el-table-column v-if="!config.readOnly" type="selection" width="46" />
        <el-table-column type="index" label="序号" width="65" :index="(index: number) => (query.pageNum - 1) * query.pageSize + index + 1" />
        <el-table-column
          v-for="field in tableFields"
          :key="field.prop"
          :prop="field.prop"
          :label="field.label"
          :width="field.width"
          :min-width="field.minWidth || 120"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <el-switch
              v-if="field.prop === 'status' && !config.readOnly && userStore.hasPermission(`${config.permissionPrefix}:update`)"
              :model-value="Number(row[field.prop])"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              active-text="启"
              inactive-text="停"
              @change="(value: string | number | boolean) => toggleStatus(row, value)"
            />
            <el-tag v-else-if="fieldTag(field, row)" :type="fieldTag(field, row)?.tagType" effect="light" round>
              {{ fieldTag(field, row)?.label }}
            </el-tag>
            <StatusTag v-else-if="['processStatus', 'sendStatus', 'runStatus', 'result', 'handled'].includes(field.prop)" :status="row[field.prop] ?? ''" />
            <span v-else>{{ field.formatter ? field.formatter(row) : row[field.prop] ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" :width="config.readOnly ? 92 : 168" align="center">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="view(row)">{{ config.readOnly || config.key === 'event' ? '查看' : '详情' }}</el-button>
            <template v-if="!config.readOnly">
              <el-button v-permission="`${config.permissionPrefix}:update`" link type="primary" :icon="EditPen" @click="openDialog('edit', row)">编辑</el-button>
              <el-button v-if="config.allowDelete !== false" v-permission="`${config.permissionPrefix}:delete`" link type="danger" :icon="Delete" @click="remove(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <Pagination :page-num="query.pageNum" :page-size="query.pageSize" :total="total" @change="pageChange" />
    </div>

    <el-dialog v-model="dialogVisible" :title="`${dialogMode === 'create' ? '新增' : dialogMode === 'edit' ? '编辑' : '查看'}${config.primaryLabel}`" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="98px" :disabled="dialogMode === 'view'">
        <div class="form-grid">
          <el-form-item v-for="field in formFields" :key="field.prop" :label="field.label" :prop="field.prop" :class="{ wide: field.kind === 'textarea' }">
            <el-select v-if="field.kind === 'select'" v-model="form[field.prop]" :placeholder="`请选择${field.label}`" style="width: 100%">
              <el-option v-for="option in field.options" :key="option.value" :label="option.label" :value="option.value" />
            </el-select>
            <el-date-picker v-else-if="field.kind === 'date'" v-model="form[field.prop]" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" style="width: 100%" />
            <el-date-picker v-else-if="field.kind === 'datetime'" v-model="form[field.prop]" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" />
            <el-input-number v-else-if="field.kind === 'number'" v-model="form[field.prop] as number" :min="0" style="width: 100%" />
            <el-input v-else-if="field.kind === 'textarea'" v-model="form[field.prop] as string" type="textarea" :rows="4" :placeholder="`请输入${field.label}`" />
            <el-input v-else v-model="form[field.prop] as string" :placeholder="`请输入${field.label}`" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ dialogMode === 'view' ? '关闭' : '取消' }}</el-button>
        <el-button v-if="dialogMode !== 'view'" type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped lang="scss">
.resource-page { min-height: 100%; }

.search-panel {
  padding: 19px 20px 2px;
  margin-bottom: 14px;
}

.query-control { width: 225px; }
.status-control { width: 145px; }

.table-panel { padding: 18px 19px 17px; }

.table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.result-title { color: #303644; font-size: 15px; font-weight: 650; }
.result-count { margin-left: 11px; color: #9aa1ad; font-size: 12px; }
.toolbar-actions { display: flex; gap: 8px; }

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 12px;

  .wide { grid-column: 1 / -1; }
}
</style>

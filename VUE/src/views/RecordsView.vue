<template>
  <section class="page-card" style="padding: 24px;">
    <div class="toolbar">
      <div class="toolbar-title">{{ isInspector ? '我的巡检记录' : '巡检记录' }}</div>
      <div class="toolbar-actions">
        <el-button @click="loadRecords">刷新</el-button>
      </div>
    </div>

    <el-table :data="records" border>
      <el-table-column type="index" width="60" label="#" />
      <el-table-column prop="taskNo" label="任务编号" width="160" />
      <el-table-column prop="deviceName" label="设备" />
      <el-table-column prop="inspectorName" label="巡检员" />
      <el-table-column prop="result" label="结果" width="100" />
      <el-table-column prop="needRepair" label="需维修" width="100">
        <template #default="{ row }">
          <el-tag :type="row.needRepair ? 'danger' : 'success'">{{ row.needRepair ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="inspectionTime" label="巡检时间" width="180" />
      <el-table-column prop="statusDescription" label="说明" min-width="260" />
      <el-table-column label="图片" width="90">
        <template #default="{ row }">
          <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 42px; height: 42px; border-radius: 8px;" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openItems(row)">查看明细</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="巡检明细" width="760px">
      <el-table :data="recordItems" border>
        <el-table-column prop="sortOrder" label="序号" width="70" />
        <el-table-column prop="itemName" label="检查项" width="160" />
        <el-table-column prop="standardValue" label="标准值" />
        <el-table-column prop="checkedValue" label="检查值" />
        <el-table-column prop="itemResult" label="判定结果" width="100" />
        <el-table-column prop="itemRemark" label="备注" />
      </el-table>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import http from '../api/http'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const isInspector = computed(() => authStore.user?.role === 'INSPECTOR')
const records = ref([])
const dialogVisible = ref(false)
const recordItems = ref([])

async function loadRecords() {
  records.value = await http.get('/api/records')
}

async function openItems(row) {
  recordItems.value = await http.get(`/api/records/${row.id}/items`)
  dialogVisible.value = true
}

onMounted(loadRecords)
</script>

<template>
  <div class="ticket-management-container">
    <div class="page-header">
      <h2>票务管理</h2>
    </div>

    <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
      
      <el-tab-pane label="场次与分区管理" name="session">
        <div class="session-layout">
          <div class="left-panel">
            <div class="panel-header">
              <h3>演出场次</h3>
              <el-button type="primary" size="small" @click="openSessionDialog()">
                <el-icon><Plus /></el-icon> 新增
              </el-button>
            </div>
            
            <div class="session-list" v-loading="loading.sessions">
              <div 
                v-for="session in sessions" 
                :key="session.id" 
                :class="['session-item', { active: currentSession?.id === session.id }]"
                @click="handleSelectSession(session)"
              >
                <div class="session-name">{{ session.name }}</div>
                <div class="session-time">{{ formatDateTime(session.startTime) }}</div>
                <div class="session-status">
                  <el-tag size="small" :type="getSessionStatusType(session.status)">
                    {{ getSessionStatusText(session.status) }}
                  </el-tag>
                  <div class="session-actions" v-if="currentSession?.id === session.id">
                    <el-button type="primary" link @click.stop="openSessionDialog(session)">编辑</el-button>
                    <el-button type="danger" link @click.stop="handleDeleteSession(session)">删除</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="right-panel">
            <template v-if="currentSession">
              <div class="panel-header">
                <h3>{{ currentSession.name }} - 分区列表</h3>
                <el-button type="primary" size="small" @click="openZoneDialog()">
                  <el-icon><Plus /></el-icon> 新增分区
                </el-button>
              </div>

              <el-alert title="库存管理提示" type="info" show-icon :closable="false" class="mb-20">
                可以通过“调库”功能手动增加或减少库存，用于应对预留票释放或锁票场景。
              </el-alert>

              <el-table :data="zones" border v-loading="loading.zones">
                <el-table-column prop="name" label="分区名称" />
                <el-table-column prop="price" label="票价">
                   <template #default="{ row }">¥{{ row.price }}</template>
                </el-table-column>
                <el-table-column label="容量概况" width="180">
                  <template #default="{ row }">
                    <div>总库存: {{ row.totalCapacity }}</div>
                    <div>已售出: {{ row.soldCount }}</div>
                    <div :class="{ 'text-danger': row.remainingCapacity < 10 }">
                      剩余: {{ row.remainingCapacity }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="220">
                  <template #default="{ row }">
                    <el-button size="small" @click="openZoneDialog(row)">编辑</el-button>
                    <el-button size="small" type="warning" @click="openStockDialog(row)">调库</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </template>
            <div v-else class="empty-placeholder">
              <el-empty description="请选择左侧场次以管理分区" />
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="票务订单列表" name="orders">
        <div class="tab-action-bar">
          <div class="filters">
            <el-select v-model="orderFilter.sessionId" placeholder="选择场次" style="width: 200px; margin-right: 10px;" clearable @change="loadOrders">
              <el-option v-for="s in sessions" :key="s.id" :label="s.name" :value="s.id" />
            </el-select>
            <el-input 
              v-model="orderFilter.keyword" 
              placeholder="订单号 / 身份证号 / 手机号" 
              style="width: 250px;" 
              clearable 
              @clear="loadOrders"
              @keyup.enter="loadOrders"
            >
              <template #append><el-button @click="loadOrders"><el-icon><Search /></el-icon></el-button></template>
            </el-input>
          </div>
          <el-button type="success" @click="handleExportOrders">
            <el-icon><Download /></el-icon> 导出Excel
          </el-button>
        </div>

        <el-table :data="orderList" border v-loading="loading.orders">
          <el-table-column prop="orderNo" label="订单号" width="180" show-overflow-tooltip />
          <el-table-column prop="buyerPhone" label="购买账号" width="120" />
          <el-table-column label="票品明细" min-width="250">
            <template #default="{ row }">
              <div v-for="(item, idx) in row.items" :key="idx" class="ticket-item">
                <el-tag size="small" effect="plain">{{ item.zoneName }}</el-tag>
                <span class="ticket-user">{{ item.realName }} ({{ maskIdNumber(item.idNumber) }})</span>
                <span class="ticket-price">¥{{ item.price }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="总金额" width="100">
            <template #default="{ row }">¥{{ row.totalAmount }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'paid' ? 'success' : 'warning'">{{ row.status === 'paid' ? '已支付' : row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="下单时间" width="160">
            <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            layout="total, sizes, prev, pager, next"
            :total="pagination.total"
            @current-change="loadOrders"
            @size-change="loadOrders"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dialog.session" :title="isEdit ? '编辑场次' : '新增场次'" width="500px">
      <el-form :model="sessionForm" label-width="80px">
        <el-form-item label="场次名称" required>
          <el-input v-model="sessionForm.name" />
        </el-form-item>
        <el-form-item label="时间段" required>
          <el-date-picker
            v-model="sessionTimeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="状态" required>
          <el-select v-model="sessionForm.status" style="width: 100%;">
            <el-option label="可购票" value="available" />
            <el-option label="已售罄" value="sold_out" />
            <el-option label="已结束" value="ended" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.session = false">取消</el-button>
        <el-button type="primary" @click="submitSession" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialog.zone" :title="isEdit ? '编辑分区' : '新增分区'" width="500px">
      <el-form :model="zoneForm" label-width="100px">
        <el-form-item label="分区名称" required>
          <el-input v-model="zoneForm.name" placeholder="例：A区 / VIP区" />
        </el-form-item>
        <el-form-item label="票价" required>
          <el-input-number v-model="zoneForm.price" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="总库存" required>
          <el-input-number v-model="zoneForm.totalCapacity" :min="1" :disabled="isEdit" style="width: 100%;" />
          <div v-if="isEdit" class="form-tip">编辑模式下不可直接修改总库存，请使用“调库”功能。</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.zone = false">取消</el-button>
        <el-button type="primary" @click="submitZone" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialog.stock" title="手动调库" width="400px">
      <el-alert title="请谨慎操作" type="warning" description="用于应急处理，如锁票释放或临时加座。" :closable="false" class="mb-20" />
      <el-form :model="stockForm" label-width="80px">
        <el-form-item label="当前分区">
          <el-tag>{{ currentZone?.name }}</el-tag>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="stockForm.action">
            <el-radio label="add">增加库存 (补票)</el-radio>
            <el-radio label="reduce">减少库存 (锁票)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数量" required>
          <el-input-number v-model="stockForm.count" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.stock = false">取消</el-button>
        <el-button type="primary" @click="submitStock" :loading="submitting">确认调整</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Download } from '@element-plus/icons-vue'
import { ticketApi } from '@/api/ticket'
import { adminTicketApi, type AdminTicketOrder } from '@/api/adminTicket'
import { type TicketSession, type TicketZone } from '@/api/ticket'
import { formatDate } from '@/utils/date'
import { maskIdNumber } from '@/utils/string'

// --- 状态 ---
const activeTab = ref('session')
const submitting = ref(false)
const isEdit = ref(false)

const loading = reactive({
  sessions: false,
  zones: false,
  orders: false
})

// 数据源
const sessions = ref<TicketSession[]>([])
const zones = ref<TicketZone[]>([])
const currentSession = ref<TicketSession | null>(null)
const currentZone = ref<TicketZone | null>(null)
const orderList = ref<AdminTicketOrder[]>([])

// 筛选与分页
const orderFilter = reactive({
  sessionId: undefined as number | undefined,
  keyword: ''
})
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 弹窗控制
const dialog = reactive({
  session: false,
  zone: false,
  stock: false
})

// 表单数据
const sessionTimeRange = ref<[string, string]>(['', ''])
const sessionForm = reactive<Partial<TicketSession>>({
  name: '',
  startTime: '',
  endTime: '',
  status: 'available'
})

const zoneForm = reactive<Partial<TicketZone>>({
  name: '',
  price: 0,
  totalCapacity: 100
})

const stockForm = reactive({
  count: 1,
  action: 'add'
})

// --- 初始化 ---
onMounted(() => {
  loadSessions()
})

const handleTabChange = (name: string) => {
  if (name === 'session') loadSessions()
  if (name === 'orders') loadOrders()
}

// ---------------- 1. 场次管理逻辑 ----------------

const loadSessions = async () => {
  loading.sessions = true
  try {
    const res = await ticketApi.getSessions()
    sessions.value = (res as any).data || res
    
    // 如果当前选中的场次仍在列表中，更新它；否则默认选中第一个
    if (sessions.value.length > 0) {
      if (!currentSession.value || !sessions.value.find(s => s.id === currentSession.value!.id)) {
        handleSelectSession(sessions.value[0])
      } else {
        // 刷新选中场次的数据
        const refreshed = sessions.value.find(s => s.id === currentSession.value!.id)
        if(refreshed) handleSelectSession(refreshed)
      }
    } else {
      currentSession.value = null
      zones.value = []
    }
  } catch (err) {
    ElMessage.error('加载场次列表失败')
  } finally {
    loading.sessions = false
  }
}

const handleSelectSession = (session: TicketSession) => {
  currentSession.value = session
  loadZones(session.id)
}

const openSessionDialog = (row?: TicketSession) => {
  if (row) {
    isEdit.value = true
    Object.assign(sessionForm, row)
    sessionTimeRange.value = [row.startTime, row.endTime]
  } else {
    isEdit.value = false
    sessionForm.id = undefined
    sessionForm.name = ''
    sessionForm.status = 'available'
    sessionTimeRange.value = ['', '']
  }
  dialog.session = true
}

const submitSession = async () => {
  if (!sessionForm.name || !sessionTimeRange.value) {
    ElMessage.warning('请填写名称和时间')
    return
  }
  
  sessionForm.startTime = sessionTimeRange.value[0]
  sessionForm.endTime = sessionTimeRange.value[1]
  
  submitting.value = true
  try {
    if (isEdit.value && sessionForm.id) {
      await adminTicketApi.updateSession(sessionForm.id, sessionForm)
      ElMessage.success('更新成功')
    } else {
      await adminTicketApi.createSession(sessionForm)
      ElMessage.success('创建成功')
    }
    dialog.session = false
    loadSessions()
  } catch (err) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDeleteSession = (row: TicketSession) => {
  ElMessageBox.confirm(`确定删除场次 "${row.name}" 吗？关联的分区也将被删除。`, '警告', {
    type: 'warning',
    confirmButtonText: '确定删除'
  }).then(async () => {
    await adminTicketApi.deleteSession(row.id)
    ElMessage.success('已删除')
    loadSessions()
  }).catch(() => {})
}

// ---------------- 2. 分区管理逻辑 ----------------

const loadZones = async (sessionId: number) => {
  loading.zones = true
  try {
    const res = await adminTicketApi.getZonesBySession(sessionId)
    zones.value = (res as any).data || res
  } catch (err) {
    ElMessage.error('加载分区数据失败')
  } finally {
    loading.zones = false
  }
}

const openZoneDialog = (row?: TicketZone) => {
  if (!currentSession.value) return
  if (row) {
    isEdit.value = true
    Object.assign(zoneForm, row)
  } else {
    isEdit.value = false
    zoneForm.id = undefined
    zoneForm.name = ''
    zoneForm.price = 0
    zoneForm.totalCapacity = 100
  }
  dialog.zone = true
}

const submitZone = async () => {
  if (!currentSession.value) return
  if (!zoneForm.name || zoneForm.price === undefined) {
    ElMessage.warning('请填写名称和票价')
    return
  }
  
  submitting.value = true
  try {
    const payload = { ...zoneForm, sessionId: currentSession.value.id }
    
    if (isEdit.value && zoneForm.id) {
      await adminTicketApi.updateZone(zoneForm.id, payload)
      ElMessage.success('更新成功')
    } else {
      await adminTicketApi.createZone(payload)
      ElMessage.success('创建成功')
    }
    dialog.zone = false
    loadZones(currentSession.value.id)
  } catch (err) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const openStockDialog = (row: TicketZone) => {
  currentZone.value = row
  stockForm.count = 1
  stockForm.action = 'add'
  dialog.stock = true
}

const submitStock = async () => {
  if (!currentZone.value) return
  submitting.value = true
  try {
    await adminTicketApi.adjustStock(currentZone.value.id, stockForm.count, stockForm.action)
    ElMessage.success('库存调整成功')
    dialog.stock = false
    if (currentSession.value) loadZones(currentSession.value.id)
  } catch (err) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// ---------------- 3. 订单列表逻辑 ----------------

const loadOrders = async () => {
  loading.orders = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      sessionId: orderFilter.sessionId,
      keyword: orderFilter.keyword || undefined
    }
    const res: any = await adminTicketApi.getOrders(params)
    const data = res.data || res
    
    if (data.content) {
      orderList.value = data.content
      pagination.total = data.totalElements
    } else {
      orderList.value = Array.isArray(data) ? data : []
    }
  } catch (err) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.orders = false
  }
}

const handleExportOrders = async () => {
  try {
    const params = {
      sessionId: orderFilter.sessionId,
      keyword: orderFilter.keyword || undefined
    }
    const blob = await adminTicketApi.exportOrders(params)
    const url = window.URL.createObjectURL(new Blob([blob as any]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `ticket_orders_${Date.now()}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('导出开始')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// --- 辅助函数 ---

const formatDateTime = (val: string) => formatDate(val, 'YYYY-MM-DD HH:mm')

const getSessionStatusType = (status: string) => {
  const map: Record<string, string> = { available: 'success', sold_out: 'danger', ended: 'info' }
  return map[status] || 'info'
}

const getSessionStatusText = (status: string) => {
  const map: Record<string, string> = { available: '可购票', sold_out: '已售罄', ended: '已结束' }
  return map[status] || status
}
</script>

<style scoped>
.ticket-management-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  min-height: 600px;
}

.page-header {
  margin-bottom: 20px;
}

/* 场次布局 */
.session-layout {
  display: flex;
  height: 600px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.left-panel {
  width: 300px;
  border-right: 1px solid #dcdfe6;
  display: flex;
  flex-direction: column;
}

.right-panel {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.panel-header {
  padding: 15px;
  background: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.session-list {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.3s;
}

.session-item:hover {
  background: #f5f7fa;
}

.session-item.active {
  background: #ecf5ff;
  border-left: 3px solid #409eff;
}

.session-name {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.session-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.session-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-20 {
  margin-bottom: 20px;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.empty-placeholder {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 订单列表 */
.tab-action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.filters {
  display: flex;
  align-items: center;
}

.ticket-item {
  font-size: 12px;
  margin-bottom: 4px;
  padding-bottom: 4px;
  border-bottom: 1px dashed #eee;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ticket-item:last-child {
  border-bottom: none;
}

.ticket-user {
  color: #333;
  flex: 1;
}

.ticket-price {
  color: #f56c6c;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.form-tip {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
  margin-top: 5px;
}
</style>
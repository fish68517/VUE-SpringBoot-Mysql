<template>
  <el-card>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom: 12px;">
      <div style="font-weight:700">消息管理（管理员）</div>
      <div style="display:flex; gap:8px;">
        <el-button @click="reload" :loading="loading">刷新</el-button>
        <el-button type="primary" @click="openCreate">发布消息</el-button>
      </div>
    </div>

    <el-alert
      v-if="role !== 'ADMIN'"
      type="warning"
      show-icon
      :closable="false"
      title="该页面仅管理员使用。"
      style="margin-bottom: 12px"
    />

    <div v-else>
      <!-- 筛选栏 -->
      <el-form :inline="true" :model="filters" style="margin-bottom: 10px">
        <el-form-item label="接收人">
          <el-select v-model="filters.toUserId" filterable clearable placeholder="全部用户" style="width: 240px">
            <el-option
              v-for="u in users"
              :key="u.id"
              :label="userLabel(u)"
              :value="u.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="已读">
          <el-select v-model="filters.readFlag" clearable placeholder="全部" style="width: 140px">
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="标题/内容" style="width: 240px" />
        </el-form-item>

        <el-form-item>
          <el-button type="success" @click="markSelectedRead" :disabled="selected.length === 0">
            批量标记已读
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 列表 -->
      <el-table
        :data="filteredRows"
        border
        @selection-change="selected = $event"
        row-key="id"
      >
        <el-table-column type="selection" width="50" />

        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="接收人" width="240">
          <template #default="{ row }">
            {{ userLabel(row.toUser) }}
          </template>
        </el-table-column>

        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="content" label="内容" min-width="260" />

        <el-table-column label="已读" width="90">
          <template #default="{ row }">
            <el-tag :type="row.readFlag === 1 ? 'success' : 'info'">
              {{ row.readFlag === 1 ? "已读" : "未读" }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="时间" width="180">
          <template #default="{ row }">{{ fmt(row.createTime) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="310">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
            <el-divider direction="vertical" />
            <el-button
              size="small"
              type="primary"
              :disabled="row.readFlag === 1"
              @click="markRead(row)"
            >
              标记已读
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-card>

  <!-- 发布/编辑弹窗 -->
  <el-dialog v-model="dlg" :title="editingId ? '编辑消息' : '发布消息'" width="560px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="接收人">
        <el-select v-model="form.toUserId" filterable placeholder="请选择接收用户" style="width: 100%">
          <el-option
            v-for="u in users"
            :key="u.id"
            :label="userLabel(u)"
            :value="u.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="请输入标题" />
      </el-form-item>

      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入内容" />
      </el-form-item>

      <el-form-item label="已读">
        <el-select v-model="form.readFlag" style="width: 160px">
          <el-option label="未读" :value="0" />
          <el-option label="已读" :value="1" />
        </el-select>
        <el-text type="info" style="margin-left: 10px;">（新发布建议为未读）</el-text>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dlg=false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">
        保存
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useAuthStore } from "@/store/auth";
import { UserApi, MessageApi } from "@/api/Api";

const auth = useAuthStore();
const role = computed(() => auth.role);

const loading = ref(false);
const saving = ref(false);

const users = ref([]);
const rows = ref([]);

const selected = ref([]);

const filters = reactive({
  toUserId: null,
  readFlag: null,
  keyword: "",
});

function fmt(v) {
  if (!v) return "";
  return String(v).replace("T", " ");
}

function userLabel(u) {
  if (!u) return "-";
  const roleText =
    u.role === "ADMIN" ? "管理员" : u.role === "MERCHANT" ? "商家" : "求职者";
  return `#${u.id} ${u.username}（${roleText}）`;
}

async function reload() {
  if (role.value !== "ADMIN") return;

  loading.value = true;
  try {
    // 先拉用户列表，用于下拉选择与表格展示
    users.value = await UserApi.list();

    // 拉所有消息（管理员可见全部）
    rows.value = await MessageApi.list();
    // 新消息靠前
    rows.value.sort((a, b) => (b.id || 0) - (a.id || 0));
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "加载失败");
  } finally {
    loading.value = false;
  }
}

const filteredRows = computed(() => {
  const kw = filters.keyword.trim().toLowerCase();

  return rows.value
    .filter((m) => (filters.toUserId ? m.toUser?.id === filters.toUserId : true))
    .filter((m) => (filters.readFlag === null || filters.readFlag === undefined ? true : m.readFlag === filters.readFlag))
    .filter((m) => !kw || (m.title || "").toLowerCase().includes(kw) || (m.content || "").toLowerCase().includes(kw));
});

// ============ 新增/编辑 ============
const dlg = ref(false);
const editingId = ref(null);

const form = reactive({
  toUserId: null,
  title: "",
  content: "",
  readFlag: 0,
});

function resetForm() {
  form.toUserId = null;
  form.title = "";
  form.content = "";
  form.readFlag = 0;
}

function openCreate() {
  editingId.value = null;
  resetForm();
  dlg.value = true;
}

function openEdit(row) {
  editingId.value = row.id;
  form.toUserId = row.toUser?.id || null;
  form.title = row.title || "";
  form.content = row.content || "";
  form.readFlag = row.readFlag ?? 0;
  dlg.value = true;
}

async function save() {
  if (!form.toUserId) return ElMessage.warning("请选择接收人");
  if (!form.title.trim()) return ElMessage.warning("请输入标题");
  if (!form.content.trim()) return ElMessage.warning("请输入内容");

  saving.value = true;
  try {
    const payload = {
      toUser: { id: form.toUserId },
      title: form.title,
      content: form.content,
      readFlag: form.readFlag,
    };

    if (!editingId.value) {
      await MessageApi.create(payload);
      ElMessage.success("消息已发布");
    } else {
      await MessageApi.update(editingId.value, payload);
      ElMessage.success("消息已更新");
    }

    dlg.value = false;
    await reload();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "保存失败");
  } finally {
    saving.value = false;
  }
}

// ============ 删除 ============
async function remove(row) {
  try {
    await ElMessageBox.confirm("确认删除该消息？", "提示", { type: "warning" });
    await MessageApi.remove(row.id);
    ElMessage.success("删除成功");
    await reload();
  } catch {}
}

// ============ 标记已读 ============
async function markRead(row) {
  try {
    if (row.readFlag === 1) return;

    await MessageApi.update(row.id, {
      toUser: { id: row.toUser?.id },
      title: row.title,
      content: row.content,
      readFlag: 1,
    });

    ElMessage.success("已标记为已读");
    await reload();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "操作失败");
  }
}

async function markSelectedRead() {
  try {
    const targets = selected.value.filter((r) => r.readFlag !== 1);
    if (targets.length === 0) return ElMessage.info("选中的消息都已读");

    for (const row of targets) {
      await MessageApi.update(row.id, {
        toUser: { id: row.toUser?.id },
        title: row.title,
        content: row.content,
        readFlag: 1,
      });
    }

    ElMessage.success("批量标记已读完成");
    await reload();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "批量操作失败");
  }
}

onMounted(reload);
</script>

<template>
  <div>
    <div style="display:flex; justify-content:space-between; margin-bottom: 12px;">
      <div style="font-weight:700">{{ title }}</div>

      <div style="display:flex; gap:8px;">
        <slot name="toolbar-left"></slot>
        <el-button v-if="showCreate" type="primary" @click="openCreate">新增</el-button>
        <el-button v-if="showRefresh" @click="load">刷新</el-button>
        <slot name="toolbar-right"></slot>
      </div>
    </div>

    <el-table :data="rows" border style="width: 100%">
      <el-table-column
        v-for="c in columns"
        :key="c.prop"
        :prop="c.prop"
        :label="c.label"
        :width="c.width"
      >
        <template v-if="c.slot" #default="{ row }">
          <slot :name="c.slot" :row="row"></slot>
        </template>
      </el-table-column>

      <el-table-column v-if="hasActions" label="操作" :width="actionWidth">
        <template #default="{ row }">
          <!-- 默认：编辑/删除 -->
          <template v-if="defaultActions">
            <el-button size="small" v-if="showEdit" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" v-if="showDelete" type="danger" @click="onDelete(row)">删除</el-button>
          </template>

          <!-- 额外：业务按钮 -->
          <template v-for="(act, idx) in rowActions" :key="idx">
            <el-button
              size="small"
              :type="act.type || 'default'"
              :disabled="act.disabled ? act.disabled(row) : false"
              @click="act.onClick(row)"
            >
              {{ act.label }}
            </el-button>
          </template>

          <slot name="row-actions" :row="row"></slot>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dlg" :title="editingId ? '编辑' : '新增'" width="520px">
      <el-form :model="form" label-width="120px">
        <el-form-item v-for="f in formFields" :key="f.prop" :label="f.label">
          <el-input v-if="f.type === 'text'" v-model="form[f.prop]" :placeholder="f.placeholder || ''" />
          <el-input v-else-if="f.type === 'number'" v-model.number="form[f.prop]" type="number" />
          <el-input v-else-if="f.type === 'textarea'" v-model="form[f.prop]" type="textarea" :rows="3" />
          <el-select v-else-if="f.type === 'select'" v-model="form[f.prop]" style="width:100%">
            <el-option v-for="op in f.options" :key="op.value" :label="op.label" :value="op.value" />
          </el-select>
          <el-input v-else v-model="form[f.prop]" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dlg=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

const props = defineProps({
  title: { type: String, required: true },
  api: { type: Object, required: true }, // createCrud() 返回的对象
  columns: { type: Array, required: true },
  formFields: { type: Array, default: () => [] },

  // 原有：转换
  transformOut: { type: Function, default: (x) => x },
  transformIn: { type: Function, default: (x) => ({ ...x }) },

  // 新增：控制
  showCreate: { type: Boolean, default: true },
  showEdit: { type: Boolean, default: true },
  showDelete: { type: Boolean, default: true },
  showRefresh: { type: Boolean, default: true },

  // 新增：自定义加载（例如只加载当前用户数据）
  loadFn: { type: Function, default: null },

  // 新增：业务按钮
  rowActions: { type: Array, default: () => [] },

  // 操作列
  defaultActions: { type: Boolean, default: true },
  actionWidth: { type: Number, default: 220 },
});

const rows = ref([]);
const dlg = ref(false);
const saving = ref(false);
const editingId = ref(null);
const form = reactive({});

const hasActions = computed(() => props.defaultActions || (props.rowActions && props.rowActions.length > 0));

async function load() {
  console.log("加载数据...");
  try {
    if (props.loadFn) {
       console.log("使用自定义加载...");
      rows.value = await props.loadFn();
    } else {
      rows.value = await props.api.list();
    }
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "加载失败");
  }
}

function resetForm(obj = {}) {
  for (const k of Object.keys(form)) delete form[k];
  Object.assign(form, obj);
}

function openCreate() {
  editingId.value = null;
  resetForm({});
  dlg.value = true;
}

function openEdit(row) {
  editingId.value = row.id;
  resetForm(props.transformIn(row));
  dlg.value = true;
}

async function onSave() {
  saving.value = true;
  try {
    const payload = props.transformOut({ ...form });
    if (!editingId.value) await props.api.create(payload);
    else await props.api.update(editingId.value, payload);
    ElMessage.success("保存成功");
    dlg.value = false;
    await load();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "保存失败");
  } finally {
    saving.value = false;
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm("确认删除该记录？", "提示", { type: "warning" });
    await props.api.remove(row.id);

    ElMessage.success("删除成功");
    await load();
  } catch {
    ElMessage.success("该用户已被关联（例如有申请记录等其他数据），无法直接删除");
  }
}

defineExpose({ load });

onMounted(load);
</script>

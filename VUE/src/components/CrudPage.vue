<template>
  <div>
    <div style="display:flex; justify-content:space-between; margin-bottom: 12px;">
      <div style="font-weight:700">{{ title }}</div>
      <el-button type="primary" @click="openCreate">新增</el-button>
    </div>

    <el-table :data="rows" border style="width: 100%">
      <el-table-column v-for="c in columns" :key="c.prop" :prop="c.prop" :label="c.label" :width="c.width" />

      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

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
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

const props = defineProps({
  title: { type: String, required: true },
  api: { type: Object, required: true }, // createCrud() 返回的对象
  columns: { type: Array, required: true },
  formFields: { type: Array, required: true },
  // 转换：表单->提交 payload
  transformOut: { type: Function, default: (x) => x },
  // 转换：row->表单
  transformIn: { type: Function, default: (x) => ({ ...x }) },
});

const rows = ref([]);
const dlg = ref(false);
const saving = ref(false);
const editingId = ref(null);
const form = reactive({});

async function load() {
  rows.value = await props.api.list();
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
  } catch {}
}

onMounted(load);
</script>

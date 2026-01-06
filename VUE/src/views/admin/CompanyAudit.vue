<template>
  <el-card>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
      <div style="font-weight:700">企业审核（管理员）</div>
      <el-button @click="reload">刷新</el-button>
    </div>

    <el-form :inline="true" :model="filters" style="margin-bottom: 10px">
      <el-form-item label="状态">
        <el-select v-model="filters.status" style="width: 160px">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="filters.keyword" placeholder="企业名称/联系人" clearable />
      </el-form-item>
    </el-form>

    <el-table :data="filteredCompanies" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="企业名称" min-width="200" />
      <el-table-column prop="licenseNo" label="执照号" width="140" />
      <el-table-column prop="licenseImg" label="执照图片" min-width="200" />
      <el-table-column prop="contactName" label="联系人" width="120" />
      <el-table-column prop="contactPhone" label="电话" width="140" />
      <el-table-column prop="verifyStatus" label="状态" width="120" />
      <el-table-column prop="rejectReason" label="驳回原因" min-width="200" />

      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button
            size="small"
            type="success"
            :disabled="row.verifyStatus !== 'PENDING'"
            @click="approve(row)"
          >
            通过
          </el-button>
          <el-button
            size="small"
            type="danger"
            :disabled="row.verifyStatus !== 'PENDING'"
            @click="reject(row)"
          >
            驳回
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { CompanyApi } from "@/api/Api";

const companies = ref([]);

const filters = reactive({
  status: "PENDING",
  keyword: "",
});

async function reload() {
  companies.value = await CompanyApi.list();
}

const filteredCompanies = computed(() => {
  const kw = filters.keyword.trim().toLowerCase();
  return companies.value
    .filter(c => !filters.status || c.verifyStatus === filters.status)
    .filter(c => !kw || (c.name || "").toLowerCase().includes(kw) || (c.contactName || "").toLowerCase().includes(kw));
});

async function approve(row) {
  try {
    await CompanyApi.update(row.id, {
      merchant: { id: row.merchant?.id },
      name: row.name,
      licenseNo: row.licenseNo,
      licenseImg: row.licenseImg,
      address: row.address,
      contactName: row.contactName,
      contactPhone: row.contactPhone,
      verifyStatus: "APPROVED",
      rejectReason: null,
    });
    ElMessage.success("已通过");
    await reload();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "操作失败");
  }
}

async function reject(row) {
  try {
    const { value } = await ElMessageBox.prompt("请输入驳回原因", "驳回企业", { confirmButtonText: "确定", cancelButtonText: "取消" });
    await CompanyApi.update(row.id, {
      merchant: { id: row.merchant?.id },
      name: row.name,
      licenseNo: row.licenseNo,
      licenseImg: row.licenseImg,
      address: row.address,
      contactName: row.contactName,
      contactPhone: row.contactPhone,
      verifyStatus: "REJECTED",
      rejectReason: value || "资料不完整",
    });
    ElMessage.success("已驳回");
    await reload();
  } catch (e) {
    if (e?.message === "cancel") return;
    ElMessage.error(e?.response?.data || e?.message || "操作失败");
  }
}

onMounted(reload);
</script>

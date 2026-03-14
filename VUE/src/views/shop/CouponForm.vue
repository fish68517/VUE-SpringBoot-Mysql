<template>
  <div class="coupon-form">
    <div class="page-header">
      <h1>{{ isEdit ? "编辑优惠券" : "创建优惠券" }}</h1>
      <router-link to="/shop/coupons" class="btn-back">返回列表</router-link>
    </div>

    <div class="form-container">
      <form @submit.prevent="handleSubmit" class="coupon-form-content">
        <div class="form-section">
          <h2>基本信息</h2>

          <div class="form-group">
            <label for="name">优惠券名称 *</label>
            <input id="name" v-model="formData.name" type="text" placeholder="例如：新年优惠券" required />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="discountAmount">优惠金额 *</label>
              <input
                id="discountAmount"
                v-model.number="formData.discountAmount"
                type="number"
                step="0.01"
                min="0"
                placeholder="请输入优惠金额"
                required
              />
            </div>

            <div class="form-group">
              <label for="minAmount">最低消费金额</label>
              <input id="minAmount" v-model.number="formData.minAmount" type="number" step="0.01" min="0" placeholder="不填表示无限制" />
            </div>
          </div>
        </div>

        <div class="form-section">
          <h2>发放设置</h2>

          <div class="form-row">
            <div class="form-group">
              <label for="totalCount">发放总数</label>
              <input id="totalCount" v-model.number="formData.totalCount" type="number" min="0" placeholder="不填表示无限制" />
            </div>

            <div class="form-group">
              <label for="exchangePoints">兑换所需积分</label>
              <input id="exchangePoints" v-model.number="formData.exchangePoints" type="number" min="0" placeholder="0 表示免费领取" />
            </div>

            <div class="form-group">
              <label for="usedCount">已使用数量</label>
              <input id="usedCount" v-model.number="formData.usedCount" type="number" disabled />
            </div>
          </div>
        </div>

        <div class="form-section">
          <h2>有效期</h2>

          <div class="form-row">
            <div class="form-group">
              <label for="startTime">开始时间 *</label>
              <input id="startTime" v-model="formData.startTime" type="datetime-local" required />
            </div>

            <div class="form-group">
              <label for="endTime">结束时间 *</label>
              <input id="endTime" v-model="formData.endTime" type="datetime-local" required />
            </div>
          </div>
        </div>

        <div class="form-section">
          <h2>状态</h2>

          <div class="form-group">
            <label for="status">优惠券状态</label>
            <select id="status" v-model.number="formData.status">
              <option :value="0">禁用</option>
              <option :value="1">启用</option>
            </select>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="submitting">
            {{ submitting ? "保存中..." : "保存优惠券" }}
          </button>
          <router-link to="/shop/coupons" class="btn-secondary">取消</router-link>
        </div>
      </form>

      <div class="preview-section">
        <h2>优惠券预览</h2>
        <div class="coupon-preview">
          <div class="coupon-header">
            <div class="coupon-name">{{ formData.name || "优惠券名称" }}</div>
            <span class="status-badge" :class="getStatusClass(formData.status)">
              {{ getStatusText(formData.status) }}
            </span>
          </div>

          <div class="coupon-content">
            <div class="discount-info">
              <div class="discount-amount">¥{{ formData.discountAmount || 0 }}</div>
              <div class="discount-label">优惠金额</div>
            </div>

            <div class="coupon-details">
              <div class="detail-item">
                <span class="label">最低消费：</span>
                <span class="value">¥{{ formData.minAmount || 0 }}</span>
              </div>
              <div class="detail-item">
                <span class="label">发放总数：</span>
                <span class="value">{{ formData.totalCount || "无限" }}</span>
              </div>
              <div class="detail-item">
                <span class="label">兑换积分：</span>
                <span class="value">{{ formData.exchangePoints || 0 }}</span>
              </div>
              <div class="detail-item">
                <span class="label">有效期：</span>
                <span class="value">{{ formatDate(formData.startTime) }} ~ {{ formatDate(formData.endTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { createCoupon, getCouponDetail, updateCoupon } from "@/api/coupon";
import { useUserStore } from "@/store/userStore";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const isEdit = ref(!!route.params.id);
const submitting = ref(false);
const formData = ref({
  name: "",
  discountAmount: 0,
  minAmount: 0,
  exchangePoints: 0,
  totalCount: null,
  usedCount: 0,
  startTime: "",
  endTime: "",
  status: 1
});

const getStatusText = (status) => {
  const map = { 0: "禁用", 1: "启用" };
  return map[status] || "未知";
};

const getStatusClass = (status) => {
  const map = { 0: "status-disabled", 1: "status-active" };
  return map[status] || "";
};

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  return new Date(dateStr).toLocaleDateString("zh-CN");
};

const formatToInput = (dateStr) => {
  if (!dateStr) return "";
  const date = new Date(dateStr);
  const pad = (n) => (n < 10 ? `0${n}` : `${n}`);
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
};

const loadData = async () => {
  if (!isEdit.value) {
    const now = new Date();
    const start = new Date(now.getTime() + 24 * 60 * 60 * 1000);
    const end = new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000);
    formData.value.startTime = formatToInput(start);
    formData.value.endTime = formatToInput(end);
    return;
  }

  try {
    const data = await getCouponDetail(route.params.id);
    formData.value = {
      name: data.name,
      discountAmount: data.discountAmount,
      minAmount: data.minAmount,
      exchangePoints: data.exchangePoints ?? 0,
      totalCount: data.totalCount,
      usedCount: data.usedCount,
      startTime: formatToInput(data.startTime),
      endTime: formatToInput(data.endTime),
      status: data.status
    };
  } catch (error) {
    console.error("加载优惠券详情失败:", error);
    ElMessage.error("加载优惠券详情失败");
  }
};

const handleSubmit = async () => {
  if (!formData.value.name || !formData.value.discountAmount || !formData.value.startTime || !formData.value.endTime) {
    ElMessage.error("请填写必填项");
    return;
  }

  if (new Date(formData.value.startTime) >= new Date(formData.value.endTime)) {
    ElMessage.error("结束时间必须晚于开始时间");
    return;
  }

  if ((formData.value.exchangePoints ?? 0) < 0) {
    ElMessage.error("兑换所需积分不能小于0");
    return;
  }

  submitting.value = true;
  try {
    const data = {
      shopId: userStore.userInfo.shopId,
      name: formData.value.name,
      discountAmount: formData.value.discountAmount,
      minAmount: formData.value.minAmount || 0,
      exchangePoints: formData.value.exchangePoints || 0,
      totalCount: formData.value.totalCount,
      startTime: formData.value.startTime,
      endTime: formData.value.endTime,
      status: formData.value.status
    };

    if (isEdit.value) {
      await updateCoupon(route.params.id, data);
      ElMessage.success("优惠券更新成功");
    } else {
      await createCoupon(data);
      ElMessage.success("优惠券创建成功");
    }

    router.push("/shop/coupons");
  } catch (error) {
    console.error("保存失败:", error);
    ElMessage.error(error.message || "保存失败");
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.coupon-form { max-width: 1000px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.page-header h1 { margin: 0; font-size: 28px; color: #333; }
.btn-back { padding: 10px 20px; background: #f0f0f0; color: #333; border-radius: 4px; text-decoration: none; font-size: 14px; }
.form-container { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; }
.coupon-form-content { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,.1); padding: 30px; display: flex; flex-direction: column; gap: 30px; }
.form-section { border-bottom: 1px solid #eee; padding-bottom: 20px; }
.form-section:last-of-type { border-bottom: none; }
.form-section h2 { margin: 0 0 20px; font-size: 18px; color: #333; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; color: #333; font-weight: 500; font-size: 14px; }
.form-group input, .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }
.form-group input:disabled { background: #f9f9f9; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.form-actions { display: flex; gap: 10px; margin-top: 20px; }
.btn-primary, .btn-secondary { padding: 12px 24px; border: none; border-radius: 4px; font-size: 14px; cursor: pointer; text-decoration: none; text-align: center; }
.btn-primary { background: #667eea; color: #fff; }
.btn-secondary { background: #f5f5f5; color: #666; }
.preview-section { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,.1); padding: 30px; height: fit-content; }
.coupon-preview { border: 2px dashed #667eea; border-radius: 8px; overflow: hidden; }
.coupon-header { display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff; }
.coupon-name { font-size: 18px; font-weight: 600; }
.status-badge { padding: 4px 12px; border-radius: 12px; font-size: 12px; }
.status-active { background: rgba(46, 204, 113, 0.2); color: #2ecc71; }
.status-disabled { background: rgba(231, 76, 60, 0.2); color: #e74c3c; }
.coupon-content { padding: 20px; display: flex; gap: 20px; }
.discount-info { text-align: center; min-width: 100px; }
.discount-amount { font-size: 32px; font-weight: 700; color: #e74c3c; }
.discount-label { font-size: 14px; color: #666; }
.coupon-details { flex: 1; }
.detail-item { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 14px; }
.detail-item .label { color: #666; }
.detail-item .value { color: #333; font-weight: 500; }
@media (max-width: 768px) {
  .form-container { grid-template-columns: 1fr; }
  .form-row { grid-template-columns: 1fr; }
}
</style>

<template>
  <div class="coupon-exchange-page">
    <div class="page-header">
      <h1>优惠券兑换</h1>
      <div class="point-box">当前积分: {{ userPoints }}</div>
    </div>

    <div v-if="loading" class="state-block">加载中...</div>
    <div v-else-if="couponList.length === 0" class="state-block">暂无可兑换优惠券</div>

    <div v-else class="coupon-grid">
      <div v-for="coupon in couponList" :key="coupon.id" class="coupon-card">
        <div class="coupon-main">
          <div class="coupon-name">{{ coupon.name }}</div>
          <div class="coupon-shop">{{ coupon.shopName || `商家ID: ${coupon.shopId}` }}</div>
          <div class="coupon-discount">减 {{ coupon.discountAmount }} 元</div>
          <div class="coupon-threshold">满 {{ coupon.minAmount || 0 }} 元可用</div>
          <div class="coupon-points">兑换需 {{ coupon.exchangePoints || 0 }} 积分</div>
        </div>

        <button
          class="exchange-btn"
          :disabled="isExchanged(coupon.id) || !canExchange(coupon) || exchangingId === coupon.id"
          @click="handleExchange(coupon)"
        >
          {{
            isExchanged(coupon.id)
              ? "已兑换"
              : exchangingId === coupon.id
              ? "兑换中..."
              : canExchange(coupon)
              ? "立即兑换"
              : "积分不足"
          }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/userStore";
import { exchangeCoupon, getExchangeableCoupons, getUserCoupons } from "@/api/coupon";
import { getUserProfile } from "@/api/user";

const userStore = useUserStore();
const loading = ref(false);
const exchangingId = ref(null);
const couponList = ref([]);
const ownedCouponIds = ref(new Set());

const userId = computed(() => userStore.userInfo?.id);
const userPoints = computed(() => Number(userStore.userInfo?.point || 0));

const isExchanged = (couponId) => ownedCouponIds.value.has(Number(couponId));

const canExchange = (coupon) => {
  const need = Number(coupon.exchangePoints || 0);
  return userPoints.value >= need;
};

const refreshUserPoints = async () => {
  if (!userId.value) return;
  const latest = await getUserProfile(userId.value);
  userStore.setUserInfo({
    ...userStore.userInfo,
    ...latest
  });
};

const loadData = async () => {
  if (!userId.value) return;
  loading.value = true;
  try {
    await refreshUserPoints();
    const [coupons, userCoupons] = await Promise.all([
      getExchangeableCoupons(),
      getUserCoupons(userId.value)
    ]);
    couponList.value = Array.isArray(coupons) ? coupons : [];
    ownedCouponIds.value = new Set((userCoupons || []).map((item) => Number(item.couponId)));
  } catch (error) {
    console.error("加载兑换中心失败:", error);
    ElMessage.error("加载兑换中心失败");
  } finally {
    loading.value = false;
  }
};

const handleExchange = async (coupon) => {
  if (!userId.value) return;
  if (isExchanged(coupon.id)) {
    ElMessage.warning("该优惠券已兑换");
    return;
  }
  if (!canExchange(coupon)) {
    ElMessage.warning("积分不足");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确认使用 ${coupon.exchangePoints || 0} 积分兑换「${coupon.name}」吗？`,
      "兑换确认",
      {
        confirmButtonText: "确认",
        cancelButtonText: "取消",
        type: "warning"
      }
    );

    exchangingId.value = coupon.id;
    const result = await exchangeCoupon(coupon.id, userId.value);

    userStore.setUserInfo({
      ...userStore.userInfo,
      point: Number(result?.currentPoints ?? userPoints.value)
    });
    ownedCouponIds.value.add(Number(coupon.id));

    // 再次走 user 接口，确保积分与后端一致
    await refreshUserPoints();

    ElMessage.success(`兑换成功，已扣减 ${result?.costPoints ?? coupon.exchangePoints ?? 0} 积分`);
  } catch (error) {
    if (error !== "cancel") {
      console.error("兑换失败:", error);
      ElMessage.error(error.message || "兑换失败");
    }
  } finally {
    exchangingId.value = null;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.coupon-exchange-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  color: #1f2937;
}

.point-box {
  padding: 8px 14px;
  border-radius: 8px;
  background: #ecfeff;
  color: #0f766e;
  font-weight: 600;
}

.state-block {
  padding: 28px;
  text-align: center;
  color: #6b7280;
  background: #ffffff;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

.coupon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}

.coupon-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #ffffff;
  padding: 14px;
  min-height: 210px;
}

.coupon-main {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.coupon-name {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
}

.coupon-shop,
.coupon-threshold,
.coupon-points {
  font-size: 13px;
  color: #4b5563;
}

.coupon-discount {
  font-size: 20px;
  color: #dc2626;
  font-weight: 700;
}

.exchange-btn {
  margin-top: 14px;
  height: 36px;
  border: none;
  border-radius: 8px;
  background: #2563eb;
  color: #fff;
  cursor: pointer;
  font-weight: 600;
}

.exchange-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}
</style>

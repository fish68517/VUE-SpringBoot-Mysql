<template>
  <div class="cart-page">
    <div class="container">
      <h1 class="page-title">购物车</h1>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="cartStore.cartItems.length === 0" class="empty-cart">
        <p>购物车为空</p>
        <router-link to="/products" class="btn-continue-shopping">继续购物</router-link>
      </div>
      <div v-else class="cart-content">
        <!-- 购物车表格 -->
        <div class="cart-table">
          <div class="table-header">
            <div class="col-checkbox">
              <input
                type="checkbox"
                v-model="selectAll"
                @change="toggleSelectAll"
                title="全选"
              />
            </div>
            <div class="col-product">商品</div>
            <div class="col-price">单价</div>
            <div class="col-quantity">数量</div>
            <div class="col-subtotal">小计</div>
            <div class="col-action">操作</div>
          </div>

          <div class="table-body">
            <div v-for="item in cartStore.cartItems" :key="item.id" class="table-row">
              <div class="col-checkbox">
                <input
                  type="checkbox"
                  :value="item.id"
                  v-model="selectedItems"
                  @change="updateSelectAll"
                />
              </div>

              <div class="col-product">
                <div class="product-info">
                  <img :src="item.productImage" :alt="item.productName" class="product-image" />
                  <div class="product-details">
                    <p class="product-name">{{ item.productName }}</p>
                    <p v-if="item.productStatus === 0" class="product-status">已下架</p>
                    <p v-else-if="item.productStock === 0" class="product-status">缺货</p>
                  </div>
                </div>
              </div>

              <div class="col-price">
                <span class="price">¥{{ item.productPrice }}</span>
              </div>

              <div class="col-quantity">
                <div class="quantity-control">
                  <button
                    @click="decreaseQuantity(item)"
                    :disabled="item.quantity <= 1 || item.productStock === 0"
                    class="btn-qty"
                  >
                    -
                  </button>
                  <input
                    v-model.number="item.quantity"
                    type="number"
                    min="1"
                    :max="item.productStock"
                    @change="updateQuantity(item)"
                    class="qty-input"
                  />
                  <button
                    @click="increaseQuantity(item)"
                    :disabled="item.quantity >= item.productStock || item.productStock === 0"
                    class="btn-qty"
                  >
                    +
                  </button>
                </div>
              </div>

              <div class="col-subtotal">
                <span class="subtotal">¥{{ item.subtotal }}</span>
              </div>

              <div class="col-action">
                <button @click="removeItem(item.id)" class="btn-delete">删除</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 购物车摘要 -->
        <div class="cart-summary">
          <div class="summary-left">
            <label class="select-all-label">
              <input
                type="checkbox"
                v-model="selectAll"
                @change="toggleSelectAll"
              />
              全选
            </label>
            <button @click="deleteSelected" :disabled="selectedItems.length === 0" class="btn-delete-selected">
              删除选中
            </button>
          </div>

          <div class="summary-right">
            <div class="summary-item">
              <span>已选择 {{ selectedItems.length }} 件商品</span>
            </div>
            <div class="summary-item">
              <span>合计：</span>
              <span class="total-amount">¥{{ selectedTotal }}</span>
            </div>
            <button
              @click="checkout"
              :disabled="selectedItems.length === 0"
              class="btn-checkout"
            >
              结算
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/userStore";
import { useCartStore } from "@/store/cartStore";

const router = useRouter();
const userStore = useUserStore();
const cartStore = useCartStore();

const loading = ref(false);
const selectedItems = ref([]);
const selectAll = ref(false);

const selectedTotal = computed(() => {
  return cartStore.calculateSelectedTotal(selectedItems.value).toFixed(2);
});

const loadCart = async () => {
  if (!userStore.isLogin) {
    router.push("/login");
    return;
  }

  loading.value = true;
  try {
    await cartStore.fetchCart(userStore.userInfo.id);
  } catch (error) {
    console.error("加载购物车失败:", error);
    ElMessage.error("加载购物车失败");
  } finally {
    loading.value = false;
  }
};

const increaseQuantity = async (item) => {
  if (item.quantity < item.productStock) {
    item.quantity++;
    await updateQuantity(item);
  }
};

const decreaseQuantity = async (item) => {
  if (item.quantity > 1) {
    item.quantity--;
    await updateQuantity(item);
  }
};

const updateQuantity = async (item) => {
  try {
    // 验证数量
    if (item.quantity < 1) {
      item.quantity = 1;
    }
    if (item.quantity > item.productStock) {
      item.quantity = item.productStock;
      ElMessage.warning("数量不能超过库存");
    }

    await cartStore.updateQuantity(item.id, item.quantity);
  } catch (error) {
    console.error("更新购物车失败:", error);
    ElMessage.error("更新购物车失败");
  }
};

const removeItem = async (cartId) => {
  try {
    await cartStore.removeItem(cartId);
    selectedItems.value = selectedItems.value.filter(id => id !== cartId);
    ElMessage.success("已删除");
  } catch (error) {
    console.error("删除购物车商品失败:", error);
    ElMessage.error("删除失败");
  }
};

const deleteSelected = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning("请先选择要删除的商品");
    return;
  }

  try {
    for (const cartId of selectedItems.value) {
      await cartStore.removeItem(cartId);
    }
    selectedItems.value = [];
    selectAll.value = false;
    ElMessage.success("已删除选中商品");
  } catch (error) {
    console.error("删除失败:", error);
    ElMessage.error("删除失败");
  }
};

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedItems.value = cartStore.cartItems.map(item => item.id);
  } else {
    selectedItems.value = [];
  }
};

const updateSelectAll = () => {
  selectAll.value = selectedItems.value.length === cartStore.cartItems.length;
};

const checkout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning("请先选择要购买的商品");
    return;
  }

  // 将选中的商品信息传递到订单确认页
  const selectedCartItems = cartStore.getSelectedItems(selectedItems.value);
  // 打印选中的商品信息
  console.log("需要结算的商品：" +JSON.stringify(selectedCartItems));
  console.log("准备结算，商品ID:", selectedItems.value);
  // ★★★ 核心修改：路径改为 /user/order-confirm ★★★
  router.push({
    path: "/user/order-confirm", 
    query: {
      cartIds: selectedItems.value.join(",")
    }
  });
};

onMounted(() => {
  loadCart();
});
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  margin: 0 0 30px 0;
  font-size: 28px;
  color: #333;
  font-weight: 600;
}

.loading,
.empty-cart {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  color: #999;
  font-size: 16px;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.btn-continue-shopping {
  display: inline-block;
  padding: 10px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.btn-continue-shopping:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.cart-content {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.cart-table {
  border-collapse: collapse;
}

.table-header {
  display: grid;
  grid-template-columns: 50px 1fr 100px 150px 100px 80px;
  gap: 15px;
  padding: 15px 20px;
  background: #f9f9f9;
  border-bottom: 1px solid #eee;
  font-weight: 600;
  color: #333;
  font-size: 14px;
  align-items: center;
}

.table-body {
  display: flex;
  flex-direction: column;
}

.table-row {
  display: grid;
  grid-template-columns: 50px 1fr 100px 150px 100px 80px;
  gap: 15px;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  align-items: center;
  transition: background 0.3s ease;
}

.table-row:hover {
  background: #f9f9f9;
}

.col-checkbox {
  display: flex;
  justify-content: center;
}

.col-checkbox input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.col-product {
  display: flex;
  align-items: center;
}

.product-info {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
  background: #f5f5f5;
}

.product-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-name {
  margin: 0;
  font-size: 14px;
  color: #333;
  font-weight: 500;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-status {
  margin: 0;
  font-size: 12px;
  color: #ff6b6b;
}

.col-price {
  text-align: center;
}

.price {
  font-size: 14px;
  color: #ff6b6b;
  font-weight: 600;
}

.col-quantity {
  display: flex;
  justify-content: center;
}

.quantity-control {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.btn-qty {
  width: 32px;
  height: 32px;
  border: none;
  background: white;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
}

.btn-qty:hover:not(:disabled) {
  background: #f5f5f5;
}

.btn-qty:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.qty-input {
  width: 50px;
  border: none;
  text-align: center;
  font-size: 14px;
  padding: 0 5px;
}

.qty-input:focus {
  outline: none;
}

.col-subtotal {
  text-align: center;
}

.subtotal {
  font-size: 14px;
  color: #ff6b6b;
  font-weight: 600;
}

.col-action {
  display: flex;
  justify-content: center;
}

.btn-delete {
  padding: 6px 12px;
  background: white;
  color: #ff6b6b;
  border: 1px solid #ff6b6b;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.btn-delete:hover {
  background: #ff6b6b;
  color: white;
}

.cart-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f9f9f9;
  border-top: 1px solid #eee;
}

.summary-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.select-all-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
}

.select-all-label input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.btn-delete-selected {
  padding: 8px 16px;
  background: white;
  color: #ff6b6b;
  border: 1px solid #ff6b6b;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-delete-selected:hover:not(:disabled) {
  background: #ff6b6b;
  color: white;
}

.btn-delete-selected:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.summary-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.summary-item {
  font-size: 14px;
  color: #666;
}

.total-amount {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: 600;
}

.btn-checkout {
  padding: 12px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-checkout:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-checkout:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .table-header,
  .table-row {
    grid-template-columns: 40px 1fr 60px 60px;
    gap: 10px;
  }

  .col-price,
  .col-action {
    display: none;
  }

  .cart-summary {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .summary-right {
    width: 100%;
    flex-direction: column;
    align-items: flex-end;
  }

  .btn-checkout {
    width: 100%;
  }
}
</style>

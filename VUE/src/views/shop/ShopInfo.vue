<template>
  <div class="shop-info">
    <div class="page-header">
      <h1>店铺信息管理</h1>
      <p>管理您的店铺基本信息</p>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else class="info-container">
      <form @submit.prevent="handleSubmit" class="info-form">
        <div class="form-group">
          <label for="name">店铺名称 *</label>
          <input
            id="name"
            v-model="formData.name"
            type="text"
            placeholder="请输入店铺名称"
            required
          />
        </div>

        <div class="form-group">
          <label for="description">店铺描述</label>
          <textarea
            id="description"
            v-model="formData.description"
            placeholder="请输入店铺描述"
            rows="4"
          ></textarea>
        </div>

        <div class="form-group">
          <label for="contact">联系方式</label>
          <input
            id="contact"
            v-model="formData.contact"
            type="text"
            placeholder="请输入联系方式"
          />
        </div>

        <div class="form-group">
          <label for="logo">店铺Logo</label>
          <div class="logo-upload">
            <img v-if="formData.logo" :src="formData.logo" alt="店铺Logo" class="logo-preview" />
            <input
              id="logo"
              type="file"
              accept="image/*"
              @change="handleLogoUpload"
              class="file-input"
            />
            <label for="logo" class="upload-btn">选择图片</label>
          </div>
        </div>

        <div class="form-group">
          <label>店铺状态</label>
          <div class="status-display">
            <span class="status-badge" :class="getStatusClass(shopInfo.status)">
              {{ getStatusText(shopInfo.status) }}
            </span>
            <p class="status-hint">
              <span v-if="shopInfo.status === 0">您的店铺正在审核中，请耐心等待</span>
              <span v-else-if="shopInfo.status === 1">您的店铺已通过审核，可以正常营业</span>
              <span v-else-if="shopInfo.status === 2">您的店铺已被禁用，请联系管理员</span>
            </p>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="submitting">
            {{ submitting ? "保存中..." : "保存信息" }}
          </button>
          <button type="button" class="btn-secondary" @click="handleReset">重置</button>
        </div>
      </form>

      <!-- 店铺统计信息 -->
      <div class="stats-section">
        <h2>店铺统计</h2>
        <div class="stats-grid">
          <div class="stat-item">
            <span class="stat-label">创建时间</span>
            <span class="stat-value">{{ formatDate(shopInfo.createTime) }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">更新时间</span>
            <span class="stat-value">{{ formatDate(shopInfo.updateTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getShopInfo, updateShopInfo } from "@/api/shop";
import { useUserStore } from "@/store/userStore";

const userStore = useUserStore();
const loading = ref(false);
const submitting = ref(false);
const shopInfo = ref({});
const formData = ref({
  name: "",
  description: "",
  contact: "",
  logo: ""
});

const getStatusText = (status) => {
  const statusMap = {
    0: "待审核",
    1: "正常",
    2: "禁用"
  };
  return statusMap[status] || "未知";
};

const getStatusClass = (status) => {
  const classMap = {
    0: "status-pending",
    1: "status-active",
    2: "status-disabled"
  };
  return classMap[status] || "";
};

const formatDate = (date) => {
  if (!date) return "-";
  return new Date(date).toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  });
};

const handleLogoUpload = (event) => {
  const file = event.target.files?.[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      formData.value.logo = e.target?.result;
    };
    reader.readAsDataURL(file);
  }
};

const handleSubmit = async () => {
  submitting.value = true;
  try {
    await updateShopInfo(userStore.userInfo?.shopId, formData.value);
    ElMessage.success("店铺信息更新成功");
    await loadShopInfo();
  } catch (error) {
    ElMessage.error(error.message || "更新失败");
  } finally {
    submitting.value = false;
  }
};

const handleReset = () => {
  formData.value = {
    name: shopInfo.value.name || "",
    description: shopInfo.value.description || "",
    contact: shopInfo.value.contact || "",
    logo: shopInfo.value.logo || ""
  };
};

const loadShopInfo = async () => {
  loading.value = true;
  try {
    const data = await getShopInfo(userStore.userInfo?.shopId);
    shopInfo.value = data || {};
    formData.value = {
      name: data?.name || "",
      description: data?.description || "",
      contact: data?.contact || "",
      logo: data?.logo || ""
    };
  } catch (error) {
    ElMessage.error("加载店铺信息失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadShopInfo();
});
</script>

<style scoped>
.shop-info {
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.info-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.info-form {
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.logo-upload {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.file-input {
  display: none;
}

.upload-btn {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  background: #5568d3;
}

.status-display {
  display: flex;
  align-items: center;
  gap: 15px;
}

.status-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  color: white;
}

.status-pending {
  background: #f39c12;
}

.status-active {
  background: #27ae60;
}

.status-disabled {
  background: #e74c3c;
}

.status-hint {
  margin: 0;
  color: #999;
  font-size: 12px;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.btn-primary,
.btn-secondary {
  padding: 10px 24px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #5568d3;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f0f0f0;
  color: #333;
}

.btn-secondary:hover {
  background: #e0e0e0;
}

.stats-section {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}

.stats-section h2 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.stat-label {
  color: #999;
  font-size: 12px;
}

.stat-value {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .info-container {
    padding: 20px;
  }

  .logo-upload {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

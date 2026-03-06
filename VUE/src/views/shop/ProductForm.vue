<template>
  <div class="product-form">
    <div class="page-header">
      <h1>{{ isEdit ? "编辑商品" : "添加商品" }}</h1>
      <router-link to="/shop/products" class="btn-back">← 返回列表</router-link>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else class="form-container">
      <form @submit.prevent="handleSubmit" class="product-form-content">
        <!-- 基本信息 -->
        <div class="form-section">
          <h2>基本信息</h2>

          <div class="form-group">
            <label for="name">商品名称 *</label>
            <input
              id="name"
              v-model="formData.name"
              type="text"
              placeholder="请输入商品名称"
              required
            />
          </div>

          <div class="form-group">
            <label for="category">商品分类 *</label>
            <select id="category" v-model="formData.categoryId" required>
              <option value="">请选择分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="price">价格 *</label>
              <input
                id="price"
                v-model.number="formData.price"
                type="number"
                step="0.01"
                placeholder="请输入价格"
                required
              />
            </div>

            <div class="form-group">
              <label for="stock">库存 *</label>
              <input
                id="stock"
                v-model.number="formData.stock"
                type="number"
                placeholder="请输入库存数量"
                required
              />
            </div>
          </div>
        </div>

        <!-- 商品描述 -->
        <div class="form-section">
          <h2>商品描述</h2>

          <div class="form-group">
            <label for="description">商品描述</label>
            <textarea
              id="description"
              v-model="formData.description"
              placeholder="请输入商品描述"
              rows="6"
            ></textarea>
          </div>
        </div>

        <!-- 商品图片 -->
        <div class="form-section">
          <h2>商品图片</h2>

          <div class="form-group">
            <label>主图 *</label>
            <div class="image-upload">
              <img v-if="formData.image" :src="formData.image" alt="主图" class="image-preview" />
              <input
                type="file"
                accept="image/*"
                @change="handleMainImageUpload"
                class="file-input"
                id="main-image"
              />
              <label for="main-image" class="upload-btn">选择主图</label>
            </div>
          </div>

          <div class="form-group">
            <label>商品图片</label>
            <div class="images-upload">
              <div v-for="(img, idx) in formData.images" :key="idx" class="image-item">
                <img :src="img" :alt="`图片${idx + 1}`" class="image-preview" />
                <button type="button" @click="removeImage(idx)" class="btn-remove">✕</button>
              </div>
              <input
                type="file"
                accept="image/*"
                @change="handleImagesUpload"
                class="file-input"
                id="images"
                multiple
              />
              <label for="images" class="upload-btn">添加图片</label>
            </div>
          </div>
        </div>

        <!-- 库存管理 -->
        <div class="form-section">
          <h2>库存管理</h2>

          <div class="form-group">
            <label for="stock-input">调整库存</label>
            <div class="stock-control">
              <button type="button" @click="decreaseStock" class="btn-stock">-</button>
              <input
                id="stock-input"
                v-model.number="formData.stock"
                type="number"
                class="stock-input"
              />
              <button type="button" @click="increaseStock" class="btn-stock">+</button>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="submitting">
            {{ submitting ? "保存中..." : "保存商品" }}
          </button>
          <router-link to="/shop/products" class="btn-secondary">取消</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { createProduct, updateProduct, getShopProductList } from "@/api/shop";
import { getCategories } from "@/api/product";

import { useUserStore } from "@/store/userStore";
const userStore = useUserStore();

const route = useRoute();
const router = useRouter();
const isEdit = ref(!!route.params.id);
const loading = ref(false);
const submitting = ref(false);
const categories = ref([]);
const formData = ref({
  name: "",
  categoryId: "",
  price: 0,
  stock: 0,
  description: "",
  image: "",
  images: []
});

const handleMainImageUpload = (event) => {
  const file = event.target.files?.[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      formData.value.image = e.target?.result;
    };
    reader.readAsDataURL(file);
  }
};

const handleImagesUpload = (event) => {
  const files = event.target.files;
  if (files) {
    Array.from(files).forEach((file) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        formData.value.images.push(e.target?.result);
      };
      reader.readAsDataURL(file);
    });
  }
};

const removeImage = (index) => {
  formData.value.images.splice(index, 1);
};

const increaseStock = () => {
  formData.value.stock++;
};

const decreaseStock = () => {
  if (formData.value.stock > 0) {
    formData.value.stock--;
  }
};

const handleSubmit = async () => {
  if (!formData.value.name || !formData.value.categoryId || !formData.value.price ) {
    ElMessage.error("请填写必填项");
    return;
  }

  submitting.value = true;
  try {
    const data = {
      shopId: userStore.userInfo.shopId, // 从用户信息中获取店铺ID
      name: formData.value.name,
      categoryId: formData.value.categoryId,
      price: formData.value.price,
      stock: formData.value.stock,
      description: formData.value.description,
      image: formData.value.image,
      images: formData.value.images
    };

    if (isEdit.value) {
      await updateProduct(route.params.id, data);
      ElMessage.success("商品更新成功");
    } else {
      await createProduct(data);
      ElMessage.success("商品创建成功");
    }

    router.push("/shop/products");
  } catch (error) {
    ElMessage.error(error.message || "保存失败");
  } finally {
    submitting.value = false;
  }
};

const loadCategories = async () => {
  try {
    const data = await getCategories();
    categories.value = Array.isArray(data) ? data : [];
  } catch (error) {
    ElMessage.error("加载分类失败");
  }
};

const loadProductData = async () => {
  if (!isEdit.value) return;

  loading.value = true;
  try {
    const data = await getShopProductList({ page: 1, pageSize: 1 });
    const product = data?.content?.find((p) => p.id === parseInt(route.params.id));
    if (product) {
      formData.value = {
        name: product.name,
        categoryId: product.categoryId,
        price: product.price,
        stock: product.stock,
        description: product.description,
        image: product.image,
        images: product.images || []
      };
    }
  } catch (error) {
    ElMessage.error("加载商品信息失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadCategories();
  loadProductData();
});
</script>

<style scoped>
.product-form {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
}

.btn-back {
  padding: 10px 20px;
  background: #f0f0f0;
  color: #333;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: #e0e0e0;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.form-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.product-form-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.form-section {
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

.form-section:last-of-type {
  border-bottom: none;
}

.form-section h2 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
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
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.image-upload,
.images-upload {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.image-preview {
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

.image-item {
  position: relative;
  display: inline-block;
}

.btn-remove {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px;
  height: 24px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.btn-remove:hover {
  background: #c0392b;
}

.stock-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-stock {
  width: 40px;
  height: 40px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.3s ease;
}

.btn-stock:hover {
  background: #5568d3;
}

.stock-input {
  width: 80px;
  text-align: center;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.btn-primary,
.btn-secondary {
  padding: 12px 24px;
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
  text-decoration: none;
  display: inline-block;
}

.btn-secondary:hover {
  background: #e0e0e0;
}

@media (max-width: 768px) {
  .form-container {
    padding: 20px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
}
</style>

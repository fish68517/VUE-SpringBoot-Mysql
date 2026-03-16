<template>
  <div class="review-form-container">
    <div class="container">
      <div class="form-header">
        <h1>商品评价</h1>
        <p v-if="product" class="product-info">{{ product.name }}</p>
      </div>

      <form @submit.prevent="submitReview" class="review-form">
        <!-- 评分 -->
        <div class="form-group">
          <label class="form-label">评分 <span class="required">*</span></label>
          <div class="rating-selector">
            <button
              v-for="i in 5"
              :key="i"
              type="button"
              class="star-btn"
              :class="{ active: rating >= i }"
              @click="rating = i"
            >
              ★
            </button>
            <span class="rating-display">{{ rating }}分</span>
          </div>
        </div>

        <!-- 评价内容 -->
        <div class="form-group">
          <label class="form-label">评价内容 <span class="required">*</span></label>
          <textarea
            v-model="contentText"
            class="form-textarea"
            placeholder="请输入您的评价内容（至少10个字符）"
            rows="6"
          ></textarea>
          <p class="char-count">{{ contentText.length }}/500</p>
        </div>

        <!-- 图片上传 -->
        <div class="form-group">
          <label class="form-label">上传图片（可选）</label>
          <div class="image-upload">
            <div class="upload-area" @click="triggerFileInput">
              <input
                ref="fileInput"
                type="file"
                multiple
                accept="image/*"
                @change="handleImageSelect"
                style="display: none"
              />
              <div class="upload-icon">📷</div>
              <p>点击上传图片或拖拽图片到此</p>
              <p class="upload-hint">最多上传5张图片，单张不超过2MB</p>
            </div>

            <!-- 图片预览 -->
            <div v-if="uploadedImages.length > 0" class="image-preview">
              <div v-for="(img, index) in uploadedImages" :key="index" class="preview-item">
                <img :src="img.preview" :alt="`预览${index + 1}`" />
                <button type="button" class="delete-btn" @click="removeImage(index)">✕</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="goBack">取消</button>
          <button type="submit" class="btn-submit" :disabled="submitting || uploading">
            {{ submitting || uploading ? "提交中..." : "提交评价" }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { createReview } from "@/api/review";
import { uploadImage } from "@/api/upload";
import { getProductDetail } from "@/api/product";
import { useUserStore } from "@/store/userStore";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const fileInput = ref(null);
const product = ref(null);
const rating = ref(5);
const contentText = ref("");
const uploadedImages = ref([]);
const submitting = ref(false);
const uploading = ref(false);

const triggerFileInput = () => {
  fileInput.value?.click();
};

const getServerImageUrl = (imageName) => `http://localhost:8080/images/${imageName}`;

const extractImageName = (uploadedResult) => {
  if (!uploadedResult) return "";
  const cleanPath = String(uploadedResult).split("?")[0];
  const index = cleanPath.lastIndexOf("/");
  return index >= 0 ? cleanPath.substring(index + 1) : cleanPath;
};

const handleImageSelect = async (event) => {
  const files = Array.from(event.target.files || []);

  if (uploadedImages.value.length + files.length > 5) {
    ElMessage.error("最多只能上传 5 张图片");
    event.target.value = "";
    return;
  }

  uploading.value = true;
  let successCount = 0;

  try {
    for (const file of files) {
      if (file.size > 2 * 1024 * 1024) {
        ElMessage.error(`${file.name} 超过2MB限制`);
        continue;
      }

      if (!file.type.startsWith("image/")) {
        ElMessage.error(`${file.name} 不是有效的图片文件`);
        continue;
      }

      try {
        const uploadedResult = await uploadImage(file);
        const imageName = extractImageName(uploadedResult);
        if (!imageName) {
          throw new Error("invalid upload response");
        }

        uploadedImages.value.push({
          imageName,
          preview: getServerImageUrl(imageName)
        });
        successCount++;
      } catch (error) {
        ElMessage.error(`${file.name} 上传失败`);
      }
    }

    if (successCount > 0) {
      ElMessage.success(`已成功上传 ${successCount} 张图片`);
    }
  } finally {
    uploading.value = false;
    event.target.value = "";
  }
};

const removeImage = (index) => {
  uploadedImages.value.splice(index, 1);
};

const submitReview = async () => {
  if (rating.value < 1 || rating.value > 5) {
    ElMessage.error("请选择评分");
    return;
  }

  if (!contentText.value || contentText.value.length < 10) {
    ElMessage.error("评价内容至少需要10个字符");
    return;
  }

  if (contentText.value.length > 500) {
    ElMessage.error("评价内容不能超过500个字符");
    return;
  }

  submitting.value = true;

  try {
    const imagesJson =
      uploadedImages.value.length > 0
        ? JSON.stringify(uploadedImages.value.map((img) => img.imageName))
        : null;

    const queryOrderId = route.query.orderId ? Number(route.query.orderId) : 1;
    const paramOrderId = route.params.orderId ? Number(route.params.orderId) : 1;

    const reviewData = {
      userId: userStore.userInfo.id,
      productId: Number(route.params.productId),
      orderId: paramOrderId || queryOrderId,
      rating: rating.value,
      content: contentText.value,
      images: imagesJson
    };

    await createReview(reviewData);
    ElMessage.success("评价提交成功");

    router.push(`/user/product/${route.params.productId}`);
  } catch (error) {
    console.error("提交评价失败:", error);
    ElMessage.error("提交评价失败，请重试");
  } finally {
    submitting.value = false;
  }
};

const goBack = () => {
  router.back();
};

const loadProduct = async () => {
  try {
    const data = await getProductDetail(route.params.productId);
    product.value = data;
  } catch (error) {
    console.error("加载商品信息失败:", error);
  }
};

onMounted(() => {
  if (!userStore.isLogin) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  loadProduct();
});
</script>

<style scoped>
.review-form-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.form-header {
  background: white;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.form-header h1 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #333;
}

.product-info {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.review-form {
  background: white;
  padding: 30px;
  border-radius: 8px;
}

.form-group {
  margin-bottom: 30px;
}

.form-label {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.required {
  color: #ff6b6b;
}

.rating-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.star-btn {
  width: 40px;
  height: 40px;
  border: 2px solid #ddd;
  background: white;
  border-radius: 4px;
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
  transition: all 0.3s ease;
}

.star-btn:hover {
  border-color: #ffc107;
  color: #ffc107;
}

.star-btn.active {
  border-color: #ffc107;
  color: #ffc107;
  background: #fffbf0;
}

.rating-display {
  margin-left: 10px;
  font-size: 14px;
  color: #666;
}

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  transition: all 0.3s ease;
}

.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.char-count {
  margin: 8px 0 0 0;
  text-align: right;
  font-size: 12px;
  color: #999;
}

.image-upload {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 4px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.upload-area:hover {
  border-color: #667eea;
  background: #f0f4ff;
}

.upload-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.upload-area p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.upload-hint {
  color: #999;
  font-size: 12px;
}

.image-preview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 10px;
}

.preview-item {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f5f5;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.delete-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  border: none;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  background: rgba(0, 0, 0, 0.8);
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.btn-cancel,
.btn-submit {
  padding: 12px 30px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: #f5f5f5;
  color: #333;
}

.btn-cancel:hover {
  background: #eee;
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .form-header,
  .review-form {
    padding: 20px;
  }

  .form-header h1 {
    font-size: 20px;
  }

  .image-preview {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .btn-cancel,
  .btn-submit {
    width: 100%;
  }
}
</style>


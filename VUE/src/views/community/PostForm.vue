<template>
  <div class="post-form-page">
    <div class="container">
      <router-link to="/community" class="btn-back">← 返回社区</router-link>

      <div class="form-card">
        <h1 class="form-title">发布帖子</h1>

        <form @submit.prevent="submitForm" class="post-form">
          <!-- 标题输入 -->
          <div class="form-group">
            <label for="title" class="form-label">标题</label>
            <input
              id="title"
              v-model="form.title"
              type="text"
              class="form-input"
              placeholder="请输入帖子标题"
              maxlength="100"
              required
            />
            <p class="char-count">{{ form.title.length }}/100</p>
          </div>

          <!-- 内容输入 -->
          <div class="form-group">
            <label for="content" class="form-label">内容</label>
            <textarea
              id="content"
              v-model="form.content"
              class="form-textarea"
              placeholder="请输入帖子内容"
              rows="8"
              required
            ></textarea>
          </div>

          <!-- 图片上传 -->
          <div class="form-group">
            <label class="form-label">图片（可选）</label>
            <div class="image-upload">
              <input
                type="file"
                ref="fileInput"
                multiple
                accept="image/*"
                @change="handleImageSelect"
                class="file-input"
              />
              <button
                type="button"
                @click="$refs.fileInput.click()"
                class="btn-upload"
              >
                选择图片
              </button>
              <p class="upload-hint">支持多张图片，单张不超过5MB</p>
            </div>

            <!-- 图片预览 -->
            <div v-if="form.images.length > 0" class="image-preview">
              <div
                v-for="(image, index) in form.images"
                :key="index"
                class="preview-item"
              >
                <img :src="image.preview" :alt="image.name" class="preview-image" />
                <button
                  type="button"
                  @click="removeImage(index)"
                  class="btn-remove"
                >
                  ✕
                </button>
              </div>
            </div>
          </div>

          <!-- 提交按钮 -->
          <div class="form-actions">
            <button
              type="submit"
              class="btn-submit"
              :disabled="submitting"
            >
              {{ submitting ? "发布中..." : "发布帖子" }}
            </button>
            <router-link to="/community" class="btn-cancel">取消</router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/userStore";
import { createPost } from "@/api/community";

const router = useRouter();
const userStore = useUserStore();

const fileInput = ref(null);
const submitting = ref(false);

const form = reactive({
  title: "",
  content: "",
  images: []
});

const handleImageSelect = (event) => {
  const files = Array.from(event.target.files || []);

  for (const file of files) {
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.error(`${file.name} 超过5MB限制`);
      continue;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      form.images.push({
        name: file.name,
        file: file,
        preview: e.target.result
      });
    };
    reader.readAsDataURL(file);
  }

  // 重置文件输入
  event.target.value = "";
};

const removeImage = (index) => {
  form.images.splice(index, 1);
};

const submitForm = async () => {
  if (!form.title.trim()) {
    ElMessage.warning("请输入标题");
    return;
  }

  if (!form.content.trim()) {
    ElMessage.warning("请输入内容");
    return;
  }

  if (!userStore.isLogin) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  submitting.value = true;

  try {
    // 构建FormData用于上传文件
    const formData = new FormData();
    formData.append("title", form.title);
    formData.append("content", form.content);

    // 添加图片文件
    form.images.forEach((image, index) => {
      formData.append(`images`, image.file);
    });

    await createPost(formData);
    ElMessage.success("帖子发布成功");
    router.push("/community");
  } catch (error) {
    console.error("发布帖子失败:", error);
    ElMessage.error("发布帖子失败");
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.post-form-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 20px;
}

.btn-back {
  display: inline-block;
  margin-bottom: 20px;
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-back:hover {
  color: #764ba2;
}

.form-card {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-title {
  margin: 0 0 30px 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
  text-align: center;
}

.post-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.form-input,
.form-textarea {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.3s ease;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 150px;
}

.char-count {
  margin: 0;
  font-size: 12px;
  color: #999;
  text-align: right;
}

.image-upload {
  padding: 15px;
  background: #f9f9f9;
  border: 2px dashed #ddd;
  border-radius: 4px;
  text-align: center;
}

.file-input {
  display: none;
}

.btn-upload {
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-upload:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.upload-hint {
  margin: 8px 0 0 0;
  font-size: 12px;
  color: #999;
}

.image-preview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
  margin-top: 15px;
}

.preview-item {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border-radius: 4px;
  overflow: hidden;
  background: #f0f0f0;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.btn-remove {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  padding: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.btn-remove:hover {
  background: rgba(0, 0, 0, 0.8);
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.btn-submit,
.btn-cancel {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
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

.btn-cancel {
  background: white;
  color: #666;
  border: 1px solid #ddd;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-cancel:hover {
  border-color: #667eea;
  color: #667eea;
}

@media (max-width: 768px) {
  .form-card {
    padding: 20px;
  }

  .form-title {
    font-size: 20px;
  }

  .image-preview {
    grid-template-columns: repeat(3, 1fr);
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>

<template>
  <div class="admin-settings">
    
    <div class="admin-container">
      
      <div class="settings-content">
        <h1>参数设置</h1>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-indicator">
          <div class="spinner"></div>
          <p>加载中...</p>
        </div>

        <!-- 设置表单 -->
        <div v-else class="settings-form-section">
          <!-- 轮播图设置 -->
          <div class="settings-group">
            <h2>轮播图设置</h2>
            <div class="form-group">
              <label for="carousel-interval">轮播图显示时间（毫秒）</label>
              <input
                id="carousel-interval"
                v-model.number="formData.carouselInterval"
                type="number"
                min="1000"
                step="1000"
                placeholder="输入轮播图显示时间，单位毫秒"
              />
              <small>默认值：5000毫秒（5秒）</small>
            </div>

            <div class="form-group">
              <label for="carousel-effect">轮播图切换效果</label>
              <select id="carousel-effect" v-model="formData.carouselEffect">
                <option value="fade">淡入淡出</option>
                <option value="slide">滑动</option>
                <option value="zoom">缩放</option>
              </select>
              <small>选择轮播图的切换效果</small>
            </div>

            <div class="form-group">
              <label for="carousel-auto-play">自动播放</label>
              <div class="checkbox-group">
                <input
                  id="carousel-auto-play"
                  v-model="formData.carouselAutoPlay"
                  type="checkbox"
                />
                <label for="carousel-auto-play" class="checkbox-label">启用自动播放</label>
              </div>
              <small>勾选后轮播图将自动循环播放</small>
            </div>

            <div class="form-group">
              <label for="carousel-show-indicators">显示指示器</label>
              <div class="checkbox-group">
                <input
                  id="carousel-show-indicators"
                  v-model="formData.carouselShowIndicators"
                  type="checkbox"
                />
                <label for="carousel-show-indicators" class="checkbox-label">显示轮播图指示器</label>
              </div>
              <small>勾选后将显示轮播图的页码指示器</small>
            </div>
          </div>

          <!-- 平台基础设置 -->
          <div class="settings-group">
            <h2>平台基础设置</h2>
            <div class="form-group">
              <label for="platform-name">平台名称</label>
              <input
                id="platform-name"
                v-model="formData.platformName"
                type="text"
                placeholder="输入平台名称"
              />
              <small>显示在页面标题和导航栏中</small>
            </div>

            <div class="form-group">
              <label for="platform-description">平台描述</label>
              <textarea
                id="platform-description"
                v-model="formData.platformDescription"
                placeholder="输入平台描述"
                rows="4"
              ></textarea>
              <small>显示在首页底部的平台简介</small>
            </div>

            <div class="form-group">
              <label for="items-per-page">每页显示条数</label>
              <input
                id="items-per-page"
                v-model.number="formData.itemsPerPage"
                type="number"
                min="5"
                max="100"
                step="5"
                placeholder="输入每页显示的条数"
              />
              <small>用于列表分页显示</small>
            </div>

            <div class="form-group">
              <label for="max-upload-size">最大上传文件大小（MB）</label>
              <input
                id="max-upload-size"
                v-model.number="formData.maxUploadSize"
                type="number"
                min="1"
                max="500"
                step="1"
                placeholder="输入最大上传文件大小"
              />
              <small>限制用户上传文件的大小</small>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="form-actions">
            <button @click="handleSave" class="btn btn-primary" :disabled="saving">
              {{ saving ? '保存中...' : '保存设置' }}
            </button>
            <button @click="handleReset" class="btn btn-secondary">重置</button>
          </div>

          <!-- 成功提示 -->
          <div v-if="saveSuccess" class="success-message">
            <span class="success-icon">✓</span>
            <span>设置已成功保存</span>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminAPI } from '../services/api'

import { ElMessage } from 'element-plus'

// State
const loading = ref(false)
const saving = ref(false)
const saveSuccess = ref(false)
const originalFormData = ref({})
const formData = ref({
  carouselInterval: 5000,
  carouselEffect: 'fade',
  carouselAutoPlay: true,
  carouselShowIndicators: true,
  platformName: '纳西族纹样数字化展示平台',
  platformDescription: '',
  itemsPerPage: 10,
  maxUploadSize: 50
})

// Load settings
const loadSettings = async () => {
  loading.value = true
  try {
    const response = await adminAPI.getSettings()
    if (response.code === 200) {
      const settings = response.data
      
      // Map API response to form data
      if (Array.isArray(settings)) {
        settings.forEach(setting => {
          const key = setting.settingKey
          if (key === 'carousel_interval') {
            formData.value.carouselInterval = parseInt(setting.settingValue) || 5000
          } else if (key === 'carousel_effect') {
            formData.value.carouselEffect = setting.settingValue || 'fade'
          } else if (key === 'carousel_auto_play') {
            formData.value.carouselAutoPlay = setting.settingValue === 'true'
          } else if (key === 'carousel_show_indicators') {
            formData.value.carouselShowIndicators = setting.settingValue === 'true'
          } else if (key === 'platform_name') {
            formData.value.platformName = setting.settingValue || '纳西族纹样数字化展示平台'
          } else if (key === 'platform_description') {
            formData.value.platformDescription = setting.settingValue || ''
          } else if (key === 'items_per_page') {
            formData.value.itemsPerPage = parseInt(setting.settingValue) || 10
          } else if (key === 'max_upload_size') {
            formData.value.maxUploadSize = parseInt(setting.settingValue) || 50
          }
        })
      }
      
      // Save original data for reset
      originalFormData.value = JSON.parse(JSON.stringify(formData.value))
    } else {
      ElMessage.error(response.message || '获取设置失败')
    }
  } catch (error) {
    ElMessage.error('获取设置失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// Save settings
const handleSave = async () => {
  saving.value = true
  saveSuccess.value = false
  
  try {
    // Prepare settings data
    const settingsData = [
      {
        settingKey: 'carousel_interval',
        settingValue: String(formData.value.carouselInterval),
        description: '轮播图显示时间（毫秒）'
      },
      {
        settingKey: 'carousel_effect',
        settingValue: formData.value.carouselEffect,
        description: '轮播图切换效果'
      },
      {
        settingKey: 'carousel_auto_play',
        settingValue: String(formData.value.carouselAutoPlay),
        description: '轮播图自动播放'
      },
      {
        settingKey: 'carousel_show_indicators',
        settingValue: String(formData.value.carouselShowIndicators),
        description: '轮播图显示指示器'
      },
      {
        settingKey: 'platform_name',
        settingValue: formData.value.platformName,
        description: '平台名称'
      },
      {
        settingKey: 'platform_description',
        settingValue: formData.value.platformDescription,
        description: '平台描述'
      },
      {
        settingKey: 'items_per_page',
        settingValue: String(formData.value.itemsPerPage),
        description: '每页显示条数'
      },
      {
        settingKey: 'max_upload_size',
        settingValue: String(formData.value.maxUploadSize),
        description: '最大上传文件大小（MB）'
      }
    ]

    const response = await adminAPI.updateSettings(settingsData)
    if (response.code === 200) {
      saveSuccess.value = true
      originalFormData.value = JSON.parse(JSON.stringify(formData.value))
      ElMessage.success('设置已成功保存')
      
      // Hide success message after 3 seconds
      setTimeout(() => {
        saveSuccess.value = false
      }, 3000)
    } else {
      ElMessage.error(response.message || '保存设置失败')
    }
  } catch (error) {
    ElMessage.error('保存设置失败: ' + error.message)
  } finally {
    saving.value = false
  }
}

// Reset form
const handleReset = () => {
  formData.value = JSON.parse(JSON.stringify(originalFormData.value))
  saveSuccess.value = false
  ElMessage.info('已重置为上次保存的设置')
}

// Lifecycle
onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.admin-settings {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-container {
  display: flex;
  flex: 1;
  max-width: 14000px;
  margin: 0 auto;
  width: 100%;
  gap: 2rem;
  padding: 2rem;
}

.settings-content {
  flex: 1;
}

.settings-content h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

/* Loading */
.loading-indicator {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  gap: 1rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Settings Form */
.settings-form-section {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.settings-group {
  margin-bottom: 2.5rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #eee;
}

.settings-group:last-of-type {
  border-bottom: none;
  margin-bottom: 2rem;
  padding-bottom: 0;
}

.settings-group h2 {
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
  color: #333;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.settings-group h2::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 1.25rem;
  background-color: #667eea;
  border-radius: 2px;
}

/* Form Groups */
.form-group {
  margin-bottom: 1.5rem;
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
}

.form-group input[type="text"],
.form-group input[type="number"],
.form-group select,
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
  transition: border-color 0.3s;
}

.form-group input[type="text"]:focus,
.form-group input[type="number"]:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-group small {
  color: #999;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

/* Checkbox Group */
.checkbox-group {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.checkbox-group input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #667eea;
}

.checkbox-label {
  margin: 0;
  cursor: pointer;
  color: #333;
  font-weight: 500;
}

/* Form Actions */
.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #eee;
}

/* Buttons */
.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #5568d3;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #e0e0e0;
}

/* Success Message */
.success-message {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-top: 1.5rem;
  padding: 1rem;
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
  color: #155724;
  animation: slideIn 0.3s ease-out;
}

.success-icon {
  font-size: 1.25rem;
  font-weight: bold;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .admin-container {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .settings-form-section {
    padding: 1.5rem;
  }

  .settings-content h1 {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
  }

  .settings-group h2 {
    font-size: 1.1rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .admin-container {
    padding: 0.5rem;
  }

  .settings-form-section {
    padding: 1rem;
  }

  .settings-content h1 {
    font-size: 1.25rem;
    margin-bottom: 1rem;
  }

  .settings-group {
    margin-bottom: 1.5rem;
    padding-bottom: 1.5rem;
  }

  .form-group {
    margin-bottom: 1rem;
  }

  .form-actions {
    gap: 0.5rem;
  }
}
</style>

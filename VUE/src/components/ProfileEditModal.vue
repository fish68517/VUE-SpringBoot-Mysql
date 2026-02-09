<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-content">
      <h3>完善考研画像</h3>
      <p class="modal-desc">为了查看精准的竞争对手分析，请先完善您的基本信息（仅用于匿名统计）。</p>
      
      <form @submit.prevent="handleSubmit" class="profile-form">
        <div class="form-group">
          <label>本科院校层次 <span class="required">*</span></label>
          <select v-model="form.undergradTier" required>
            <option value="" disabled>请选择</option>
            <option value="985">985 院校</option>
            <option value="211">211 院校</option>
            <option value="DOUBLE_NON">双非一本</option>
            <option value="OTHER">二本及其他</option>
          </select>
        </div>

        <div class="form-group">
          <label>英语四级分数 <span class="required">*</span></label>
          <input 
            type="number" 
            v-model.number="form.cet4Score" 
            placeholder="例如: 520" 
            min="0" 
            max="710"
            required 
          />
        </div>

        <div class="modal-actions">
          <button type="button" @click="$emit('close')" class="btn-cancel">取消</button>
          <button type="submit" class="btn-save" :disabled="loading">
            {{ loading ? '保存中...' : '保存并继续' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import userService from '../services/userService'

export default {
  props: {
    visible: Boolean,
    initialData: Object
  },
  data() {
    return {
      loading: false,
      form: {
        undergradTier: '',
        cet4Score: null
      }
    }
  },
  watch: {
    // 当弹窗打开时，回填已有数据（如果有的话）
    visible(newVal) {
      if (newVal && this.initialData) {
        this.form.undergradTier = this.initialData.undergradTier || ''
        this.form.cet4Score = this.initialData.cet4Score || null
      }
    }
  },
  methods: {
    async handleSubmit() {
      this.loading = true
      try {
        // 调用更新接口（注意：这里假设你后端 UserProfileDTO 接收这些字段）
        // 如果需要更新更多字段，请确保这里的对象结构与后端 StatusUpdateRequest 或 UserProfileDTO 匹配
        // 这里为了简单，我们构造一个符合更新逻辑的对象
        const updateData = {
          ...this.initialData, // 保留原有的其他字段
          undergradTier: this.form.undergradTier,
          cet4Score: this.form.cet4Score
        }
        
        await userService.updateProfile(updateData)
        this.$emit('saved') // 通知父组件保存成功
        this.$emit('close')
      } catch (error) {
        alert('保存失败，请重试')
        console.error(error)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 450px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

h3 { margin-bottom: 10px; color: #333; }
.modal-desc { font-size: 14px; color: #666; margin-bottom: 20px; line-height: 1.5; }

.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; font-size: 14px;}
.required { color: red; }
.form-group select, .form-group input {
  width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;
}

.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 25px; }
.btn-cancel { background: #f5f5f5; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
.btn-save { background: #1890ff; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
.btn-save:disabled { opacity: 0.7; cursor: not-allowed; }
</style>
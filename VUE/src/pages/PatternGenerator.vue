<template>
  <section class="generator-page">
    <div class="card control-card">
      <h2>纹样生成器</h2>
      <p class="tip">登录后可根据公式图生成对应平面纹样，结果会由 Python 服务保存并返回到页面渲染。</p>

      <div class="control-row">
        <label for="formula-select">选择公式图</label>
        <el-select
          id="formula-select"
          v-model="selectedFormula"
          class="formula-select"
          placeholder="请选择公式"
          :disabled="loading || formulas.length === 0"
        >
          <el-option v-for="item in formulas" :key="item.key" :label="item.key" :value="item.key" />
        </el-select>
        <el-button type="primary" :loading="loading" :disabled="!selectedFormula" @click="generatePattern">
          生成图案
        </el-button>
      </div>

      <div v-if="currentFormula" class="formula-preview">
        <img :src="formulaImageUrl" :alt="`${currentFormula.key} 公式图`" />
      </div>
    </div>

    <div class="card result-card">
      <h3>生成结果</h3>
      <div v-if="resultImageUrl" class="result-content">
        <img :src="resultImageUrl" :alt="`${selectedFormula} 纹样图`" />
        <p class="feature-text">此公式生成的纹样图特点：{{ resultFeature }}</p>
      </div>
      <p v-else class="empty-text">请选择公式并点击“生成图案”。</p>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { wallpaperAPI, resolveWallpaperUrl } from '../services/wallpaper'

const formulas = ref([])
const selectedFormula = ref('')
const loading = ref(false)
const resultImageUrl = ref('')
const resultFeature = ref('')

const currentFormula = computed(() => formulas.value.find((x) => x.key === selectedFormula.value) || null)
const formulaImageUrl = computed(() => {
  if (!currentFormula.value) return ''
  return resolveWallpaperUrl(currentFormula.value.formula_image_url)
})

const loadFormulas = async () => {
  try {
    const data = await wallpaperAPI.listFormulas()
    formulas.value = data.items || []
    if (formulas.value.length > 0) {
      selectedFormula.value = formulas.value[0].key
      resultFeature.value = formulas.value[0].feature || ''
    }
  } catch (error) {
    ElMessage.error('加载公式列表失败，请确认 Python 接口服务是否已启动')
    console.error(error)
  }
}

const generatePattern = async () => {
  if (!selectedFormula.value) {
    ElMessage.warning('请先选择公式')
    return
  }

  loading.value = true
  try {
    const data = await wallpaperAPI.generate(selectedFormula.value)
    const ts = Date.now()
    resultImageUrl.value = `${resolveWallpaperUrl(data.image_url)}?t=${ts}`
    resultFeature.value = data.feature || ''
    ElMessage.success(`${data.formula_key} 生成成功`)
  } catch (error) {
    ElMessage.error('生成失败，请稍后重试')
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadFormulas()
})
</script>

<style scoped>
.generator-page {
  max-width: 1180px;
  margin: 24px auto;
  padding: 0 16px;
}

.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  padding: 20px;
  margin-bottom: 18px;
}

.control-card h2,
.result-card h3 {
  margin: 0 0 12px;
  color: #24364b;
}

.tip {
  margin: 0 0 16px;
  color: #50637a;
  line-height: 1.6;
}

.control-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

label {
  color: #2e4460;
  font-weight: 600;
}

.formula-select {
  width: 220px;
}

.formula-preview {
  margin-top: 16px;
  border: 1px solid #e8edf3;
  border-radius: 8px;
  padding: 10px;
  background: #fafcff;
  display: inline-block;
}

.formula-preview img {
  max-width: 100%;
  height: auto;
  display: block;
}

.result-content img {
  max-width: 100%;
  border-radius: 8px;
  border: 1px solid #e8edf3;
}

.feature-text {
  margin-top: 12px;
  color: #2b3f58;
  line-height: 1.7;
  background: #f6f9ff;
  border-left: 3px solid #4f8df8;
  padding: 10px 12px;
  border-radius: 6px;
}

.empty-text {
  color: #6d7d90;
}

@media (max-width: 768px) {
  .control-row {
    flex-direction: column;
    align-items: stretch;
  }

  .formula-select {
    width: 100%;
  }
}
</style>


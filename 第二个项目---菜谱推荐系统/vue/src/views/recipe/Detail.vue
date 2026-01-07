<template>
  <div class="recipe-detail">
    <el-card v-if="recipe">
      <div class="recipe-header">
        <el-image 
          :src="getImageUrl(recipe.image)"
          class="recipe-image"
          fit="cover"
        >
          <template #error>
            <div class="image-error">
              <el-icon><PictureIcon /></el-icon>
            </div>
          </template>
        </el-image>
        
        <div class="recipe-info">
          <h1>{{ recipe.name }}</h1>
          <p class="description">{{ recipe.description }}</p>
          <div class="meta">
            <el-tag 
              :type="getDifficultyType(recipe.difficulty)"
              size="small"
            >
              {{ recipe.difficulty || '简单' }}
            </el-tag>
            <span class="cooking-time">烹饪时间：{{ recipe.cookingTime }}分钟</span>
          </div>
        </div>
      </div>

      <el-divider>食材</el-divider>
      <div class="ingredients">
        <p>{{ recipe.ingredients }}</p>
      </div>

      <el-divider>烹饪步骤</el-divider>
      <div class="steps">
        <p>{{ recipe.steps }}</p>
      </div>

      <div class="actions">
        <el-button type="primary" @click="addToCart(recipe)">
          加入购物车
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture as PictureIcon } from '@element-plus/icons-vue'
import { recipeApi, cartApi } from '@/api/networkApi'
import { getImageUrl } from '@/utils/image'

export default {
  components: {
    PictureIcon
  },

  setup() {
    const route = useRoute()
    const router = useRouter()
    const recipe = ref(null)

    const getRecipe = async () => {
      try {
        const data = await recipeApi.getRecipeById(route.params.id)
        recipe.value = data
      } catch (error) {
        ElMessage.error('获取菜品详情失败')
      }
    }

    const getDifficultyType = (difficulty) => {
      const types = {
        '简单': 'success',
        '中等': 'warning',
        '困难': 'danger'
      }
      return types[difficulty] || 'info'
    }

    const addToCart = async (recipe) => {
      try {
        await cartApi.addToCart({
          recipeId: recipe.id,
          quantity: 1
        })
        ElMessage.success('已加入购物车')
      } catch (error) {
        ElMessage.error('添加失败')
      }
    }

    onMounted(() => {
      getRecipe()
    })

    return {
      recipe,
      getImageUrl,
      getDifficultyType,
      addToCart
    }
  }
}
</script>

<style scoped>
.recipe-detail {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}
.recipe-header {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
}
.recipe-image {
  width: 400px;
  height: 300px;
  border-radius: 8px;
}
.recipe-info {
  flex: 1;
}
.recipe-info h1 {
  margin: 0 0 20px 0;
  font-size: 24px;
  color: #303133;
}
.description {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}
.meta {
  display: flex;
  align-items: center;
  gap: 20px;
}
.cooking-time {
  color: #909399;
}
.ingredients, .steps {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.8;
}
.actions {
  margin-top: 30px;
  text-align: center;
}
.image-error {
  height: 300px;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 30px;
}
</style> 
<template>
  <div class="recommend">
    <el-card>
      <template #header>
        <div class="header">
          <span class="title">为您推荐</span>
          <el-button @click="refreshRecommendations" :loading="loading">
            <el-icon><Refresh /></el-icon>
            换一批
          </el-button>
        </div>
      </template>

      <div class="filter-container">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="口味偏好">
            <el-select v-model="filterForm.taste" placeholder="口味" clearable style="width: 110px" @change="applyFilters">
              <el-option label="清淡" value="清淡" />
              <el-option label="麻辣" value="麻辣" />
              <el-option label="酸甜" value="酸甜" />
              <el-option label="咸鲜" value="咸鲜" />
            </el-select>
          </el-form-item>

          <el-form-item label="菜系">
            <el-select v-model="filterForm.cuisineType" placeholder="菜系" clearable style="width: 110px" @change="applyFilters">
              <el-option label="川菜" value="川菜" />
              <el-option label="粤菜" value="粤菜" />
              <el-option label="湘菜" value="湘菜" />
              <el-option label="鲁菜" value="鲁菜" />
            </el-select>
          </el-form-item>

          <el-form-item label="烹饪方式">
            <el-select v-model="filterForm.cookingMethod" placeholder="方式" clearable style="width: 110px" @change="applyFilters">
              <el-option label="炒" value="炒" />
              <el-option label="煮" value="煮" />
              <el-option label="炖" value="炖" />
              <el-option label="蒸" value="蒸" />
            </el-select>
          </el-form-item>

          <el-form-item label="食物分类">
            <el-select v-model="filterForm.foodCategory" placeholder="分类" clearable style="width: 110px" @change="applyFilters">
              <el-option label="主食" value="主食" />
              <el-option label="甜点" value="甜点" />
              <el-option label="小吃" value="小吃" />
              <el-option label="汤品" value="汤品" />
              <el-option label="饮品" value="饮品" />
            </el-select>
          </el-form-item>

          <el-form-item label="烹饪难度">
            <el-select v-model="filterForm.difficulty" placeholder="难度" clearable style="width: 110px" @change="applyFilters">
              <el-option label="简单" value="简单" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="resetFilters">
              <el-icon><RefreshLeft /></el-icon>
              重置筛选
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <el-empty v-else-if="recommendedRecipes.length === 0" description="暂无推荐菜品" />

      <div v-else>
        <div class="recommendation-info">
          <el-alert type="info" show-icon :closable="false">
            <template #title>
              <span>根据您的历史订单、收藏和购物车记录，为您推荐以下菜品</span>
            </template>
          </el-alert>
        </div>

        <el-row :gutter="20">
          <el-col
            v-for="recipe in recommendedRecipes"
            :key="recipe.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <el-card class="recipe-card" :body-style="{ padding: '0px' }">
              <el-image :src="getImageUrl(recipe.image)" class="recipe-image" fit="cover">
                <template #error>
                  <div class="image-error">
                    <el-icon><PictureIcon /></el-icon>
                  </div>
                </template>
              </el-image>

              <div class="recipe-info">
                <h3>{{ recipe.name }}</h3>
                <p class="description">{{ recipe.description }}</p>
                <div class="bottom">
                  <div class="left">
                    <span class="price">￥{{ formatPrice(recipe.price) }}</span>
                  </div>
                  <div class="right">
                    <el-button :type="recipe.isFavorite ? 'danger' : 'default'" circle @click="toggleFavorite(recipe)">
                      <el-icon>
                        <component :is="recipe.isFavorite ? Star : StarFilled" />
                      </el-icon>
                    </el-button>
                    <el-button type="primary" link @click="viewRecipe(recipe)">查看详情</el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { recommendApi, favoriteApi } from '@/api/networkApi'
import { Picture as PictureIcon, Star, StarFilled, Refresh, RefreshLeft } from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/image'

export default {
  components: {
    PictureIcon,
    Star,
    StarFilled,
    Refresh,
    RefreshLeft
  },
  setup() {
    const router = useRouter()
    const allRecommendedRecipes = ref([])
    const recommendedRecipes = ref([])
    const loading = ref(false)

    const filterForm = reactive({
      taste: '',
      cuisineType: '',
      cookingMethod: '',
      foodCategory: '',
      difficulty: ''
    })

    const readField = (recipe, ...keys) => {
      for (const key of keys) {
        if (recipe?.[key] !== undefined && recipe?.[key] !== null && recipe?.[key] !== '') {
          return recipe[key]
        }
      }
      return ''
    }

    const formatPrice = (price) => {
      const value = Number(price || 0)
      return Number.isNaN(value) ? '0.00' : value.toFixed(2)
    }

    const getFavoriteStatus = async (recipes) => {
      try {
        const promises = recipes.map((recipe) =>
          favoriteApi.checkFavorite(recipe.id).then((isFavorite) => {
            recipe.isFavorite = isFavorite
          })
        )
        await Promise.all(promises)
      } catch (error) {
        // ignore favorite state errors
      }
    }

    const getRecommendations = async () => {
      loading.value = true
      try {
        const data = await recommendApi.getRecommendedRecipes()
        allRecommendedRecipes.value = Array.isArray(data) ? data : []
        recommendedRecipes.value = [...allRecommendedRecipes.value]
        await getFavoriteStatus(allRecommendedRecipes.value)
      } catch (error) {
        ElMessage.error('获取推荐菜品失败')
      } finally {
        loading.value = false
      }
    }

    const applyFilters = () => {
      let list = [...allRecommendedRecipes.value]

      if (filterForm.taste) {
        list = list.filter((recipe) => readField(recipe, 'taste').includes(filterForm.taste))
      }
      if (filterForm.cuisineType) {
        list = list.filter((recipe) => readField(recipe, 'cuisineType', 'cuisine_type') === filterForm.cuisineType)
      }
      if (filterForm.cookingMethod) {
        list = list.filter((recipe) => readField(recipe, 'cookingMethod', 'cooking_method') === filterForm.cookingMethod)
      }
      if (filterForm.foodCategory) {
        list = list.filter((recipe) => readField(recipe, 'foodCategory', 'food_category') === filterForm.foodCategory)
      }
      if (filterForm.difficulty) {
        list = list.filter((recipe) => readField(recipe, 'difficulty') === filterForm.difficulty)
      }

      recommendedRecipes.value = list
      if (list.length === 0) {
        ElMessage.info('没有找到符合条件的菜品')
      }
    }

    const resetFilters = () => {
      Object.keys(filterForm).forEach((key) => {
        filterForm[key] = ''
      })
      recommendedRecipes.value = [...allRecommendedRecipes.value]
    }

    const refreshRecommendations = () => {
      getRecommendations()
      resetFilters()
    }

    const toggleFavorite = async (recipe) => {
      try {
        if (recipe.isFavorite) {
          await favoriteApi.removeFavorite(recipe.id)
          recipe.isFavorite = false
          ElMessage.success('已取消收藏')
        } else {
          await favoriteApi.addFavorite(recipe.id)
          recipe.isFavorite = true
          ElMessage.success('收藏成功')
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const viewRecipe = (recipe) => {
      router.push(`/user/recipe/${recipe.id}`)
    }

    onMounted(() => {
      getRecommendations()
    })

    return {
      recommendedRecipes,
      loading,
      filterForm,
      getImageUrl,
      formatPrice,
      toggleFavorite,
      viewRecipe,
      refreshRecommendations,
      applyFilters,
      resetFilters,
      Star,
      StarFilled
    }
  }
}
</script>

<style scoped>
.recommend { padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 18px; font-weight: bold; }
.recommendation-info { margin-bottom: 20px; }
.recipe-card { margin-bottom: 20px; transition: all 0.3s; height: 100%; }
.recipe-card:hover { transform: translateY(-5px); box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); }
.recipe-image { width: 100%; height: 200px; }
.image-error { height: 200px; background-color: #f5f7fa; display: flex; justify-content: center; align-items: center; color: #909399; font-size: 30px; }
.recipe-info { padding: 14px; }
.recipe-info h3 { margin: 0; font-size: 16px; color: #303133; }
.description { font-size: 13px; color: #909399; line-height: 1.4; margin: 8px 0; height: 36px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.price { color: #e67e22; font-size: 16px; font-weight: 700; }
.right { display: flex; align-items: center; gap: 10px; }
.loading-container { padding: 20px; }
.filter-container { margin-bottom: 20px; padding: 15px; background-color: #f8f9fa; border-radius: 4px; }
.filter-form { display: flex; flex-wrap: wrap; }
@media (max-width: 768px) {
  .el-form-item { margin-right: 0; width: 100%; }
}
</style>

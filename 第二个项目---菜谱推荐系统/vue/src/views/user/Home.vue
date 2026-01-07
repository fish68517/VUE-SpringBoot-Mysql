<template>
  <div class="home">
    <div class="banner">
      <h2>智能菜谱推荐</h2>
      <div class="search">
        <el-input v-model="searchKey" placeholder="搜索菜品..." clearable>
          <template #append>
            <el-button icon="Search" @click="searchRecipes"/>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 分类导航 -->
    <div class="categories">
      <el-scrollbar>
        <div class="category-list">
          <el-button 
            v-for="cat in categories" 
            :key="cat.id"
            :type="selectedCategory === cat.id ? 'primary' : 'default'"
            @click="selectCategory(cat.id)">
            {{ cat.name }}
          </el-button>
        </div>
      </el-scrollbar>
    </div>

    <!-- 菜品列表 -->
    <div class="recipe-list">
      <el-row :gutter="20">
        <el-col v-for="recipe in recipes" :key="recipe.id" :span="6">
          <el-card class="recipe-card" :body-style="{ padding: '0px' }">
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
              <h3>{{ recipe.name }}</h3>
              <p class="description">{{ recipe.description }}</p>
              <div class="bottom">
                <div class="left">
                  <el-tag 
                    :type="getDifficultyType(recipe.difficulty)"
                    size="small"
                    class="difficulty-tag"
                  >
                    {{ recipe.difficulty || '简单' }}
                  </el-tag>
                </div>
                <div class="right">
                  <el-button 
                    :type="recipe.isFavorite ? 'danger' : 'default'"
                    circle
                    @click="toggleFavorite(recipe)"
                  >
                    <el-icon>
                      <component :is="recipe.isFavorite ? Star : StarFilled" />
                    </el-icon>
                  </el-button>
                  <el-button type="primary" link @click="viewRecipe(recipe)">
                    查看详情
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { recipeApi, favoriteApi } from '@/api/networkApi'
import { useRouter } from 'vue-router'
import { Picture as PictureIcon, Star, StarFilled } from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/image'

export default {
  components: {
    PictureIcon,
    Star,
    StarFilled
  },

  setup() {
    const router = useRouter()
    const searchKey = ref('')
    const categories = ref([])
    const recipes = ref([])
    const selectedCategory = ref(null)

    const getCategories = async () => {
      try {
        const data = await recipeApi.getCategories()
        categories.value = data
      } catch (error) {
        ElMessage.error('获取分类失败')
      }
    }

    const getRecipes = async (categoryId = null) => {
      try {
        let data
        if (categoryId) {
          data = await recipeApi.getRecipesByCategory(categoryId)
        } else {
          data = await recipeApi.getRecipeList()
        }
        recipes.value = data
        await getFavoriteStatus(recipes.value)
      } catch (error) {
        ElMessage.error('获取菜品失败')
      }
    }

    const searchRecipes = async () => {
      if (!searchKey.value.trim()) {
        getRecipes()
        return
      }
      try {
        const data = await recipeApi.searchRecipes(searchKey.value)
        recipes.value = data
      } catch (error) {
        ElMessage.error('搜索失败')
      }
    }

    const selectCategory = (categoryId) => {
      selectedCategory.value = categoryId
      getRecipes(categoryId)
    }

    const addToCart = async (recipe) => {
      try {
        await recipeApi.addToCart({
          recipeId: recipe.id,
          quantity: 1
        })
        ElMessage.success('已加入购物车')
      } catch (error) {
        ElMessage.error('添加失败')
      }
    }

    const viewRecipe = (recipe) => {
      router.push({
        path: `/user/recipe/${recipe.id}`,
        state: { recipe }
      })
    }

    const getDifficultyType = (difficulty) => {
      const types = {
        '简单': 'success',
        '中等': 'warning',
        '困难': 'danger'
      }
      return types[difficulty] || 'info'
    }

    // 切换收藏状态
    const toggleFavorite = async (recipe) => {
      try {
        if (recipe.isFavorite) {
          await favoriteApi.removeFavorite(recipe.id)
          ElMessage.success('已取消收藏')
        } else {
          await favoriteApi.addFavorite(recipe.id)
          ElMessage.success('收藏成功')
        }
        recipe.isFavorite = !recipe.isFavorite
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    // 获取收藏状态
    const getFavoriteStatus = async (recipes) => {
      try {
        const promises = recipes.map(recipe => 
          favoriteApi.checkFavorite(recipe.id)
            .then(isFavorite => recipe.isFavorite = isFavorite)
        )
        await Promise.all(promises)
      } catch (error) {
        console.error('获取收藏状态失败:', error)
      }
    }

    onMounted(() => {
      getCategories()
      getRecipes()
      selectCategory(1)
    })

    return {
      searchKey,
      categories,
      recipes,
      selectedCategory,
      selectCategory,
      addToCart,
      searchRecipes,
      router,
      getImageUrl,
      viewRecipe,
      getDifficultyType,
      toggleFavorite,
      Star,
      StarFilled
    }
  }
}
</script>

<style scoped>
.home {
  padding: 20px;
}
.banner {
  text-align: center;
  margin-bottom: 30px;
}
.search {
  max-width: 500px;
  margin: 0 auto;
}
.categories {
  margin-bottom: 20px;
}
.category-list {
  display: flex;
  gap: 10px;
  padding: 10px 0;
}
.recipe-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}
.recipe-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}
.recipe-image {
  width: 100%;
  height: 200px;
}
.image-error {
  height: 200px;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 30px;
}
.recipe-info {
  padding: 14px;
}
.recipe-info h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}
.description {
  font-size: 13px;
  color: #909399;
  line-height: 1.4;
  margin: 8px 0;
  height: 36px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.difficulty-tag {
  font-size: 12px;
}
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding: 0 14px 14px;
}
.right {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style> 
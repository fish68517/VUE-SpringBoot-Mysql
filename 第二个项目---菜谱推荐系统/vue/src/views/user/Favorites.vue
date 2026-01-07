<template>
  <div class="favorites">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的收藏</span>
          <el-input
            v-model="searchKey"
            placeholder="搜索收藏的菜品..."
            class="search-input"
            clearable
            @input="filterRecipes"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filters">
        <el-select
          v-model="difficultyFilter"
          placeholder="难度筛选"
          clearable
          @change="filterRecipes"
        >
          <el-option label="简单" value="简单" />
          <el-option label="中等" value="中等" />
          <el-option label="困难" value="困难" />
        </el-select>
      </div>

      <!-- 收藏列表 -->
      <el-row :gutter="20">
        <el-col 
          v-for="favorite in filteredFavorites" 
          :key="favorite.id" 
          :span="6"
        >
          <el-card class="recipe-card" :body-style="{ padding: '0px' }">
            <el-image 
              :src="getImageUrl(favorite.recipe.image)"
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
              <h3>{{ favorite.recipe.name }}</h3>
              <p class="description">{{ favorite.recipe.description }}</p>
              <div class="bottom">
                <div class="left">
                  <el-tag 
                    :type="getDifficultyType(favorite.recipe.difficulty)"
                    size="small"
                  >
                    {{ favorite.recipe.difficulty || '简单' }}
                  </el-tag>
                </div>
                <div class="right">
                  <el-button 
                    type="danger"
                    circle
                    @click="removeFavorite(favorite)"
                  >
                    <el-icon><Star /></el-icon>
                  </el-button>
                  <el-button 
                    type="primary" 
                    link 
                    @click="viewRecipe(favorite.recipe)"
                    v-show="false"
                  >
                    查看详情
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty
        v-if="filteredFavorites.length === 0"
        description="暂无收藏的菜品"
      />
    </el-card>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { favoriteApi } from '@/api/networkApi'
import { Picture as PictureIcon, Search, Star } from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/image'

export default {
  components: {
    PictureIcon,
    Search,
    Star
  },

  setup() {
    const router = useRouter()
    const favorites = ref([])
    const searchKey = ref('')
    const difficultyFilter = ref('')

    // 获取收藏列表
    const getFavorites = async () => {
      try {
        const data = await favoriteApi.getFavorites()
        favorites.value = data
      } catch (error) {
        ElMessage.error('获取收藏列表失败')
      }
    }

    // 筛选后的收藏列表
    const filteredFavorites = computed(() => {
      return favorites.value.filter(favorite => {
        const recipe = favorite.recipe
        const matchesSearch = !searchKey.value || 
          recipe.name.toLowerCase().includes(searchKey.value.toLowerCase()) ||
          recipe.description.toLowerCase().includes(searchKey.value.toLowerCase())
        
        const matchesDifficulty = !difficultyFilter.value || 
          recipe.difficulty === difficultyFilter.value

        return matchesSearch && matchesDifficulty
      })
    })

    // 移除收藏
    const removeFavorite = async (favorite) => {
      try {
        await favoriteApi.removeFavorite(favorite.recipeId)
        ElMessage.success('已取消收藏')
        // 从列表中移除
        favorites.value = favorites.value.filter(f => f.id !== favorite.id)
      } catch (error) {
        ElMessage.error('取消收藏失败')
      }
    }

    // 查看菜品详情
    const viewRecipe = (recipe) => {
      router.push(`/user/recipe/${recipe.id}`)
    }

    // 获取难度对应的类型
    const getDifficultyType = (difficulty) => {
      const types = {
        '简单': 'success',
        '中等': 'warning',
        '困难': 'danger'
      }
      return types[difficulty] || 'info'
    }

    onMounted(() => {
      getFavorites()
    })

    return {
      favorites,
      searchKey,
      difficultyFilter,
      filteredFavorites,
      removeFavorite,
      viewRecipe,
      getImageUrl,
      getDifficultyType
    }
  }
}
</script>

<style scoped>
.favorites {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-input {
  width: 300px;
}

.filters {
  margin-bottom: 20px;
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

.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.right {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style> 
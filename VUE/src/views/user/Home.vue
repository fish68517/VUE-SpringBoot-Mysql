<template>
  <div class="home">
    <div class="banner">
      <h2>智能菜品推荐</h2>
      <div class="search">
        <el-input v-model="searchKey" placeholder="搜索菜品..." clearable>
          <template #append>
            <el-button icon="Search" @click="searchRecipes" />
          </template>
        </el-input>
      </div>
    </div>

    <el-card class="hot-carousel">
      <template #header>
        <span>热门菜品（销量前四）</span>
      </template>
      <el-carousel height="240px" indicator-position="outside" v-if="hotRecipes.length">
        <el-carousel-item v-for="item in hotRecipes" :key="item.id">
          <div class="hot-item" @click="viewRecipe(item)">
            <el-image :src="getImageUrl(item.image)" fit="cover" class="hot-image" />
            <div class="hot-info" v-if="false">
              <h3>{{ item.name }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      <el-empty v-else description="暂无热门菜品" />
    </el-card>

    <div class="categories">
      <el-scrollbar>
        <div class="category-list">
          <el-button
            v-for="cat in categories"
            :key="cat.id"
            :type="selectedCategory === cat.id ? 'primary' : 'default'"
            @click="selectCategory(cat.id)"
          >
            {{ cat.name }}
          </el-button>
        </div>
      </el-scrollbar>
    </div>

    <div class="recipe-list">
      <el-row :gutter="20">
        <el-col v-for="recipe in recipes" :key="recipe.id" :span="6">
          <el-card class="recipe-card" :body-style="{ padding: '0px' }">
            <el-image :src="getImageUrl(recipe.image)" class="recipe-image" fit="cover" />
            <div class="recipe-info">
              <h3>{{ recipe.name }}</h3>
              <p class="description">{{ recipe.description }}</p>
              <div class="bottom">
                <el-tag :type="getDifficultyType(recipe.difficulty)" size="small">
                  {{ recipe.difficulty || '简单' }}
                </el-tag>
                <el-button type="primary" link @click="viewRecipe(recipe)">查看详情</el-button>
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
import { recipeApi, recommendApi } from '@/api/networkApi'
import { useRouter } from 'vue-router'
import { getImageUrl } from '@/utils/image'

export default {
  setup() {
    const router = useRouter()
    const searchKey = ref('')
    const categories = ref([])
    const recipes = ref([])
    const hotRecipes = ref([])
    const selectedCategory = ref(null)

    const getCategories = async () => {
      try {
        categories.value = await recipeApi.getCategories()
      } catch (error) {
        ElMessage.error('获取分类失败')
      }
    }

    const getRecipes = async (categoryId = null) => {
      try {
        if (categoryId) {
          recipes.value = await recipeApi.getRecipesByCategory(categoryId)
        } else {
          const resp = await recipeApi.getRecipeList({ page: 1, pageSize: 100 })
          recipes.value = Array.isArray(resp) ? resp : (resp?.data || [])
        }
      } catch (error) {
        ElMessage.error('获取菜品失败')
      }
    }

    const getHotRecipes = async () => {
      try {
        hotRecipes.value = await recommendApi.getHotRecipes()
      } catch (error) {
        ElMessage.error('获取热门菜品失败')
      }
    }

    const searchRecipes = async () => {
      if (!searchKey.value.trim()) {
        getRecipes()
        return
      }
      try {
        recipes.value = await recipeApi.searchRecipes(searchKey.value.trim())
      } catch (error) {
        ElMessage.error('搜索失败')
      }
    }

    const selectCategory = (categoryId) => {
      selectedCategory.value = categoryId
      getRecipes(categoryId)
    }

    const viewRecipe = (recipe) => {
      router.push(`/user/recipe/${recipe.id}`)
    }

    const getDifficultyType = (difficulty) => {
      const types = { 简单: 'success', 中等: 'warning', 困难: 'danger' }
      return types[difficulty] || 'info'
    }

    onMounted(() => {
      getCategories()
      getRecipes()
      getHotRecipes()
    })

    return {
      searchKey,
      categories,
      recipes,
      hotRecipes,
      selectedCategory,
      selectCategory,
      searchRecipes,
      viewRecipe,
      getImageUrl,
      getDifficultyType
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
  margin-bottom: 20px;
}
.search {
  max-width: 500px;
  margin: 0 auto;
}
.hot-carousel {
  margin-bottom: 20px;
}
.hot-item {
  display: flex;
  gap: 16px;
  height: 240px;
  cursor: pointer;
}
.hot-image {
  width: 3600px;
  height: 240px;
  border-radius: 8px;
}
.hot-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
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
}
.recipe-image {
  width: 100%;
  height: 200px;
}
.recipe-info {
  padding: 14px;
}
.description {
  color: #909399;
  font-size: 13px;
  margin: 8px 0;
  min-height: 34px;
}
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

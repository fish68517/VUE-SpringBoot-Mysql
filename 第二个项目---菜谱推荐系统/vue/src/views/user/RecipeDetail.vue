<template>
  <div class="recipe-detail">
    <el-card v-if="loading">
      <el-skeleton :rows="10" animated />
    </el-card>

    <div v-else>
      <!-- 菜谱基本信息 -->
      <el-card class="recipe-card">
        <div class="recipe-header">
          <h1 class="recipe-title">{{ recipe.name }}</h1>
          <div class="recipe-actions">
            <el-button 
              :type="isFavorite ? 'danger' : 'default'" 
              @click="toggleFavorite"
            >
              <el-icon><Star /></el-icon>
              {{ isFavorite ? '已收藏' : '收藏' }}
            </el-button>
            <el-button 
              type="primary" 
              @click="addToCart"
            >
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
          </div>
        </div>

        <!-- 菜谱图片和基本信息 -->
        <div class="recipe-info-container">
          <div class="recipe-image-container">
            <el-image 
              :src="getImageUrl(recipe.image)" 
              class="recipe-image" 
              fit="cover"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>

          <div class="recipe-info">
            <div class="info-item">
              <span class="label">烹饪时间：</span>
              <span>{{ recipe.cookingTime || '未知' }} 分钟</span>
            </div>
            <div class="info-item">
              <span class="label">难度：</span>
              <el-tag :type="getDifficultyType(recipe.difficulty)">
                {{ recipe.difficulty || '简单' }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">菜系：</span>
              <span>{{ recipe.cuisine_type || '未分类' }}</span>
            </div>
            <div class="info-item">
              <span class="label">烹饪方式：</span>
              <span>{{ recipe.cooking_method || '未指定' }}</span>
            </div>
            <div class="info-item">
              <span class="label">分类：</span>
              <span>{{ recipe.food_category || '未分类' }}</span>
            </div>
            <div class="info-item">
              <span class="label">口味：</span>
              <span>{{ recipe.taste || '未指定' }}</span>
            </div>
            <div class="description">
              <p>{{ recipe.description }}</p>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 菜谱详情 -->
      <el-card class="recipe-detail-card">
        <template #header>
          <div class="card-header">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="食材清单" name="ingredients"></el-tab-pane>
              <el-tab-pane label="烹饪步骤" name="steps"></el-tab-pane>
              <el-tab-pane label="营养成分" name="nutrition"></el-tab-pane>
            </el-tabs>
          </div>
        </template>

        <!-- 食材清单 -->
        <div v-if="activeTab === 'ingredients'">
          <el-table :data="ingredients" style="width: 100%">
            <el-table-column prop="name" label="食材" />
            <el-table-column prop="amount" label="用量" />
          </el-table>
        </div>

        <!-- 烹饪步骤 -->
        <div v-if="activeTab === 'steps'" class="steps-container">
          <el-timeline>
            <el-timeline-item
              v-for="(step, index) in steps"
              :key="index"
              :timestamp="`步骤 ${index + 1}`"
              placement="top"
            >
              <el-card>
                <p>{{ step.description }}</p>
                <el-image
                  v-if="step.image"
                  :src="getImageUrl(step.image)"
                  :preview-src-list="[getImageUrl(step.image)]"
                  class="step-image"
                ></el-image>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>

        <!-- 营养成分 -->
        <div v-if="activeTab === 'nutrition'" class="nutrition-container">
          <el-descriptions border>
            <el-descriptions-item label="热量">{{ recipe.calories || '未知' }} 千卡</el-descriptions-item>
            <el-descriptions-item label="蛋白质">{{ recipe.protein || '未知' }} 克</el-descriptions-item>
            <el-descriptions-item label="脂肪">{{ recipe.fat || '未知' }} 克</el-descriptions-item>
            <el-descriptions-item label="碳水化合物">{{ recipe.carbs || '未知' }} 克</el-descriptions-item>
            <el-descriptions-item label="纤维素">{{ recipe.fiber || '未知' }} 克</el-descriptions-item>
            <el-descriptions-item label="糖">{{ recipe.sugar || '未知' }} 克</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>

      <!-- 评论区 -->
      <el-card class="comment-card">
        <template #header>
          <div class="card-header">
            <h3>用户评论 ({{ comments.length }})</h3>
          </div>
        </template>

        <!-- 发表评论 -->
        <div class="comment-form">
          <h4>发表评论</h4>
          <el-form :model="commentForm" :rules="commentRules" ref="commentFormRef">
            <el-form-item prop="content">
              <el-input
                v-model="commentForm.content"
                type="textarea"
                :rows="3"
                placeholder="分享您对这道菜的看法..."
              ></el-input>
            </el-form-item>
            <el-form-item label="评分" prop="rating">
              <el-rate
                v-model="commentForm.rating"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
              ></el-rate>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitComment" :loading="submitting">发表评论</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list">
          <el-empty 
            v-if="comments.length === 0" 
            description="暂无评论，快来发表第一条评论吧！"
          ></el-empty>

          <el-timeline v-else>
            <el-timeline-item
              v-for="comment in comments"
              :key="comment.id"
              :timestamp="formatTime(comment.created_at)"
              placement="top"
            >
              <el-card>
                <div class="comment-header">
                  <div class="user-info">
                    <el-avatar :size="40" :src="getImageUrl(comment.userAvatar)">
                      {{ comment.nickname ? comment.nickname[0] : 'U' }}
                    </el-avatar>
                    <span class="nickname">{{ comment.nickname || '用户' }}</span>
                  </div>
                  <div class="rating">
                    <el-rate
                      v-model="comment.rating"
                      disabled
                      text-color="#ff9900"
                    ></el-rate>
                  </div>
                </div>
                <div class="comment-content">
                  {{ comment.content }}
                </div>
                <div class="comment-footer">
                  <el-button 
                    text 
                    :type="comment.liked ? 'danger' : 'default'"
                    @click="toggleLike(comment)"
                  >
                    <el-icon><Thumb /></el-icon>
                    {{ comment.likes }} 赞
                  </el-button>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  recipeApi, 
  cartApi, 
  favoriteApi,
  commentApi 
} from '@/api/networkApi'
import { 
  Star, 
  ShoppingCart, 
  Picture, 
  Thumb 
} from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/image'

export default {
  components: {
    Star,
    ShoppingCart,
    Picture,
    Thumb
  },

  setup() {
    const route = useRoute()
    const router = useRouter()
    const recipeId = parseInt(route.params.id)
    
    const recipe = ref({})
    const ingredients = ref([])
    const steps = ref([])
    const comments = ref([])
    const loading = ref(true)
    const isFavorite = ref(false)
    const activeTab = ref('ingredients')
    const submitting = ref(false)
    const commentFormRef = ref(null)
    
    const commentForm = reactive({
      content: '',
      rating: 5
    })
    
    const commentRules = {
      content: [
        { required: true, message: '请输入评论内容', trigger: 'blur' },
        { min: 5, message: '评论内容至少 5 个字符', trigger: 'blur' }
      ],
      rating: [
        { required: true, message: '请给出评分', trigger: 'change' }
      ]
    }

    // 获取菜谱详情
    const getRecipeDetail = async () => {
      loading.value = true
      try {
        const data = await recipeApi.getRecipeById(recipeId)
        recipe.value = data
        
        // 处理食材和步骤
        parseIngredientsAndSteps()
        
        // 检查收藏状态
        checkFavoriteStatus()
        
        // 获取评论
        getComments()
      } catch (error) {
        ElMessage.error('获取菜谱详情失败')
      } finally {
        loading.value = false
      }
    }

    // 解析食材和步骤
    const parseIngredientsAndSteps = () => {
      // 解析食材
      if (recipe.value.ingredients) {
        try {
          ingredients.value = JSON.parse(recipe.value.ingredients)
        } catch (e) {
          ingredients.value = []
          console.error('解析食材数据失败:', e)
        }
      }
      
      // 解析步骤
      if (recipe.value.steps) {
        try {
          steps.value = JSON.parse(recipe.value.steps)
        } catch (e) {
          steps.value = []
          console.error('解析步骤数据失败:', e)
        }
      }
    }

    // 检查收藏状态
    const checkFavoriteStatus = async () => {
      try {
        isFavorite.value = await favoriteApi.checkFavorite(recipeId)
      } catch (error) {
        console.error('检查收藏状态失败:', error)
      }
    }

    // 切换收藏状态
    const toggleFavorite = async () => {
      try {
        if (isFavorite.value) {
          await favoriteApi.removeFavorite(recipeId)
          isFavorite.value = false
          ElMessage.success('已取消收藏')
        } else {
          await favoriteApi.addFavorite(recipeId)
          isFavorite.value = true
          ElMessage.success('已添加到收藏')
        }
      } catch (error) {
        ElMessage.error(isFavorite.value ? '取消收藏失败' : '收藏失败')
      }
    }

    // 加入购物车
    const addToCart = async () => {
      try {
        await cartApi.addToCart(recipeId)
        ElMessage.success('已加入购物车')
      } catch (error) {
        ElMessage.error('加入购物车失败')
      }
    }

    // 获取评论列表
    const getComments = async () => {
      try {
        comments.value = await commentApi.getCommentsByRecipe(recipeId)
        // 获取每条评论的点赞状态
        getCommentsLikeStatus()
      } catch (error) {
        console.error('获取评论失败:', error)
      }
    }
    
    // 获取评论点赞状态
    const getCommentsLikeStatus = async () => {
      try {
        const promises = comments.value.map(comment => 
          commentApi.checkCommentLike(comment.id)
            .then(liked => comment.liked = liked)
        )
        await Promise.all(promises)
      } catch (error) {
        console.error('获取评论点赞状态失败:', error)
      }
    }
    
    // 提交评论
    const submitComment = async () => {
      commentFormRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true
          try {
            await commentApi.addComment({
              recipeId,
              content: commentForm.content,
              rating: commentForm.rating
            })
            
            // 清空表单
            commentForm.content = ''
            commentForm.rating = 5
            
            // 刷新评论列表
            await getComments()
            
            ElMessage.success('评论发表成功')
          } catch (error) {
            ElMessage.error('评论发表失败')
          } finally {
            submitting.value = false
          }
        }
      })
    }
    
    // 点赞/取消点赞评论
    const toggleLike = async (comment) => {
      try {
        if (comment.liked) {
          await commentApi.unlikeComment(comment.id)
          comment.liked = false
          comment.likes--
        } else {
          await commentApi.likeComment(comment.id)
          comment.liked = true
          comment.likes++
        }
      } catch (error) {
        ElMessage.error(comment.liked ? '取消点赞失败' : '点赞失败')
      }
    }
    
    // 格式化时间
    const formatTime = (timestamp) => {
      if (!timestamp) return '未知时间'
      
      const date = new Date(timestamp)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
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
      getRecipeDetail()
    })

    return {
      recipe,
      ingredients,
      steps,
      comments,
      loading,
      isFavorite,
      activeTab,
      commentForm,
      commentRules,
      commentFormRef,
      submitting,
      getImageUrl,
      toggleFavorite,
      addToCart,
      getDifficultyType,
      submitComment,
      toggleLike,
      formatTime
    }
  }
}
</script>

<style scoped>
.recipe-detail {
  padding: 20px;
}

.recipe-card,
.recipe-detail-card,
.comment-card {
  margin-bottom: 20px;
}

.recipe-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.recipe-title {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.recipe-actions {
  display: flex;
  gap: 10px;
}

.recipe-info-container {
  display: flex;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.recipe-image-container {
  flex: 0 0 300px;
  margin-right: 20px;
  margin-bottom: 20px;
}

.recipe-image {
  width: 100%;
  height: 300px;
  border-radius: 4px;
}

.image-error {
  width: 100%;
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 30px;
  border-radius: 4px;
}

.recipe-info {
  flex: 1;
  min-width: 300px;
}

.info-item {
  margin-bottom: 10px;
}

.label {
  font-weight: bold;
  color: #606266;
}

.description {
  margin-top: 20px;
  color: #606266;
  line-height: 1.6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.steps-container {
  padding: 10px;
}

.step-image {
  max-width: 100%;
  margin-top: 10px;
  max-height: 300px;
}

.nutrition-container {
  padding: 10px;
}

.comment-card {
  margin-top: 30px;
}

.comment-form {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.comment-list {
  margin-top: 20px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.user-info {
  display: flex;
  align-items: center;
}

.nickname {
  margin-left: 10px;
  font-weight: bold;
}

.comment-content {
  color: #303133;
  line-height: 1.6;
  margin-bottom: 10px;
}

.comment-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

@media (max-width: 768px) {
  .recipe-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .recipe-actions {
    margin-top: 10px;
  }
  
  .recipe-image-container {
    flex: 0 0 100%;
    margin-right: 0;
  }
}
</style> 
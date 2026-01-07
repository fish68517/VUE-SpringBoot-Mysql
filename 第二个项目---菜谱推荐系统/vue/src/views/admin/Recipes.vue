<template>
  <div class="recipes">
    <el-card>
      <template #header>
        <div class="header">
          <div class="left">
            <span class="title">菜品管理</span>

          </div>
          <div class="right">
            <el-button type="primary" @click="handleAdd">添加菜品</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索菜品名称"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <!-- 菜品列表 -->
      <el-table :data="recipes" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="120">
          <template #default="{ row }">
            <el-image
                :src="getImageUrl(row.image)"
                fit="cover"
                class="item-image"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><PictureIcon /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag>{{ getCategoryName(row.categoryId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="getDifficultyType(row.difficulty)">
              {{ row.difficulty }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cookingTime" label="烹饪时间" width="120">
          <template #default="{ row }">
            {{ row.cookingTime }}分钟
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 菜品表单对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '添加菜品' : '编辑菜品'"
      v-model="dialogVisible"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-upload
            class="recipe-uploader"
            action="/api/upload/image"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <img v-if="form.image" :src="form.image" class="uploaded-image">
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="食材" prop="ingredients">
          <el-input v-model="form.ingredients" type="textarea" :rows="3" placeholder="每种食材一行，包含用量" />
        </el-form-item>
        <el-form-item label="步骤" prop="steps">
          <el-input v-model="form.steps" type="textarea" :rows="4" placeholder="每个步骤一行" />
        </el-form-item>
        <el-form-item label="烹饪时间" prop="cookingTime">
          <el-input-number v-model="form.cookingTime" :min="1" :max="180" />
          <span class="unit">分钟</span>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="form.difficulty">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>



  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { recipeApi } from '@/api/networkApi'
import { getImageUrl } from '@/utils/image'

export default {
  components: {
    Plus
  },
  
  setup() {
    const loading = ref(false)
    const recipes = ref([])
    const categories = ref([])
    const searchQuery = ref('')
    const selectedCategory = ref(null)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    
    // 菜品表单相关
    const dialogVisible = ref(false)
    const dialogType = ref('add')
    const formRef = ref(null)
    const form = ref({
      name: '',
      categoryId: null,
      image: '',
      description: '',
      ingredients: '',
      steps: '',
      cookingTime: 30,
      difficulty: '中等'
    })

    const rules = {
      name: [
        { required: true, message: '请输入菜品名称', trigger: 'blur' }
      ],
      categoryId: [
        { required: true, message: '请选择分类', trigger: 'change' }
      ],
      image: [
        { required: true, message: '请上传图片', trigger: 'change' }
      ],
      ingredients: [
        { required: true, message: '请输入食材', trigger: 'blur' }
      ],
      steps: [
        { required: true, message: '请输入烹饪步骤', trigger: 'blur' }
      ]
    }

    // 分类管理相关
    const categoryDialogVisible = ref(false)
    const categoryFormRef = ref(null)
    const categoryForm = ref({
      name: '',
      description: '',
      sortOrder: 0
    })

    const categoryRules = {
      name: [
        { required: true, message: '请输入分类名称', trigger: 'blur' }
      ]
    }

    // 获取菜品列表
    const getRecipes = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          pageSize: pageSize.value,
          query: searchQuery.value,
          categoryId: selectedCategory.value
        }
        const { data, total: totalCount } = await recipeApi.getRecipeList(params)
        recipes.value = data
        total.value = totalCount
      } catch (error) {
        ElMessage.error('获取菜品列表失败')
      } finally {
        loading.value = false
      }
    }

    // 获取分类列表
    const getCategories = async () => {
      try {
        const data = await recipeApi.getCategories()
        categories.value = data
      } catch (error) {
        ElMessage.error('获取分类失败')
      }
    }

    // 工具方法
    const getCategoryName = (categoryId) => {
      const category = categories.value.find(c => c.id === categoryId)
      return category ? category.name : '-'
    }

    const getDifficultyType = (difficulty) => {
      const types = {
        '简单': 'success',
        '中等': 'warning',
        '困难': 'danger'
      }
      return types[difficulty] || 'info'
    }

    // 事件处理方法
    const handleSearch = () => {
      currentPage.value = 1
      getRecipes()
    }

    const handleCategoryChange = () => {
      currentPage.value = 1
      getRecipes()
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      getRecipes()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      getRecipes()
    }

    // 菜品表单相关方法
    const handleAdd = () => {
      dialogType.value = 'add'
      form.value = {
        name: '',
        categoryId: null,
        image: '',
        description: '',
        ingredients: '',
        steps: '',
        cookingTime: 30,
        difficulty: '中等'
      }
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      dialogType.value = 'edit'
      form.value = { ...row }
      dialogVisible.value = true
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该菜品吗？')
        await recipeApi.deleteRecipe(row.id)
        ElMessage.success('删除成功')
        getRecipes()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const handleImageSuccess = (response) => {
      form.value.image = response.url
    }

    const beforeImageUpload = (file) => {
      const isImage = /^image\//.test(file.type)
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
      }
      if (!isLt2M) {
        ElMessage.error('图片大小不能超过 2MB!')
      }
      return isImage && isLt2M
    }

    const handleSubmit = async () => {
      if (!formRef.value) return
      
      await formRef.value.validate(async (valid) => {
        if (valid) {
          try {
            if (dialogType.value === 'add') {
              await recipeApi.createRecipe(form.value)
              ElMessage.success('添加成功')
            } else {
              await recipeApi.updateRecipe(form.value)
              ElMessage.success('更新成功')
            }
            dialogVisible.value = false
            getRecipes()
          } catch (error) {
            ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
          }
        }
      })
    }



    onMounted(() => {
      getCategories()
      getRecipes()
    })

    return {
      loading,
      recipes,
      categories,
      searchQuery,
      selectedCategory,
      currentPage,
      pageSize,
      total,
      dialogVisible,
      dialogType,
      formRef,
      form,
      rules,
      categoryDialogVisible,
      categoryFormRef,
      categoryForm,
      categoryRules,
      getCategoryName,
      getDifficultyType,
      handleSearch,
      handleCategoryChange,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleDelete,
      handleImageSuccess,
      beforeImageUpload,
      handleSubmit,
      getImageUrl
    }
  }
}
</script>

<style scoped>
.recipes {
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.left {
  display: flex;
  align-items: center;
  gap: 20px;
}
.title {
  font-size: 16px;
  font-weight: bold;
}
.search-bar {
  margin: 20px 0;
  width: 300px;
}
.recipe-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.recipe-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.recipe-uploader:hover {
  border-color: #409EFF;
}
.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-icon {
  font-size: 28px;
  color: #8c939d;
}
.unit {
  margin-left: 10px;
  color: #666;
}
.dialog-footer {
  margin-top: 20px;
  text-align: right;
}
.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
}
</style> 
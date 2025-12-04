<template>
  <div class="product-management-container">
    <div class="page-header">
      <h2>周边商品管理</h2>
      <div class="header-actions">
        <el-button @click="openCategoryDialog">分类管理</el-button>
        <el-button type="primary" @click="openProductDialog()">
          <el-icon><Plus /></el-icon> 发布新商品
        </el-button>
      </div>
    </div>

    <el-alert
      v-if="lowStockCount > 0"
      :title="`库存预警：当前有 ${lowStockCount} 个商品库存不足 10 件，请及时补货。`"
      type="warning"
      show-icon
      class="mb-20"
    />

    <div class="filter-bar mb-20">
      <el-select 
        v-model="filter.categoryId" 
        placeholder="全部分类" 
        clearable 
        style="width: 150px; margin-right: 10px;"
        @change="handleFilterChange"
      >
        <el-option 
          v-for="cat in categories" 
          :key="cat.id" 
          :label="cat.name" 
          :value="cat.id" 
        />
      </el-select>
      <el-input 
        v-model="filter.keyword" 
        placeholder="搜索商品名称" 
        style="width: 200px;" 
        clearable 
        @clear="handleFilterChange"
        @keyup.enter="handleFilterChange"
      >
        <template #append>
          <el-button @click="handleFilterChange"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="products" border style="width: 100%" v-loading="loading">
      <el-table-column label="图片" width="100">
        <template #default="{ row }">
          <el-image 
            :src="row.images?.[0]" 
            :preview-src-list="row.images" 
            fit="cover" 
            class="table-image"
          >
            <template #error>
              <div class="image-slot"><el-icon><Picture /></el-icon></div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      
      <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
      
      <el-table-column label="分类" width="120">
        <template #default="{ row }">
          {{ getCategoryName(row.categoryId) }}
        </template>
      </el-table-column>

      <el-table-column label="价格" width="150">
        <template #default="{ row }">
          <div class="price-info">
            <span class="current-price">¥{{ row.currentPrice }}</span>
            <span v-if="row.originalPrice > row.currentPrice" class="original-price">¥{{ row.originalPrice }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="库存" width="120">
        <template #default="{ row }">
          <span :class="{ 'low-stock': row.stock < 10 }">
            {{ row.stock }}
            <el-tag v-if="row.stock < 10" type="danger" size="small" effect="plain">不足</el-tag>
          </span>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.isActive"
            :loading="row.statusLoading"
            @change="(val) => handleStatusChange(row, val as boolean)"
          />
        </template>
      </el-table-column>

      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openProductDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="total, prev, pager, next"
        @current-change="loadProducts"
      />
    </div>

    <el-dialog 
      v-model="dialog.product" 
      :title="isEdit ? '编辑商品' : '发布新商品'" 
      width="650px"
      @close="resetProductForm"
    >
      <el-form :model="productForm" label-width="100px" ref="productFormRef">
        <el-form-item label="商品名称" required>
          <el-input v-model="productForm.name" />
        </el-form-item>
        
        <el-form-item label="所属分类" required>
          <el-select v-model="productForm.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="商品图片">
          <div class="image-input-list">
             <div v-for="(img, idx) in productForm.images" :key="idx" class="image-row">
               <el-input v-model="productForm.images[idx]" placeholder="图片URL">
                 <template #prepend>图{{ idx + 1 }}</template>
                 <template #append>
                   <el-button @click="removeImageRow(idx)"><el-icon><Delete /></el-icon></el-button>
                 </template>
               </el-input>
             </div>
             <el-button size="small" type="primary" link @click="addImageRow">+ 添加图片地址</el-button>
          </div>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="现价" required>
              <el-input-number v-model="productForm.currentPrice" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价">
              <el-input-number v-model="productForm.originalPrice" :precision="2" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="库存数量" required>
          <el-input-number v-model="productForm.stock" :min="0" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="商品描述">
          <el-input v-model="productForm.description" type="textarea" :rows="3" />
        </el-form-item>
        
        <el-form-item label="规格配置">
          <el-alert title="格式：JSON对象，如 {'颜色': ['红','蓝'], '尺码': ['M','L']}" type="info" :closable="false" style="line-height: 1.2; margin-bottom: 5px;" />
          <el-input 
            v-model="productForm.specsJson" 
            type="textarea" 
            :rows="3" 
            placeholder='{"颜色": ["红色", "蓝色"], "尺码": ["S", "M"]}'
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.product = false">取消</el-button>
        <el-button type="primary" @click="submitProduct" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialog.category" title="分类管理" width="500px">
      <div class="category-add-row mb-20">
        <el-input v-model="newCategoryName" placeholder="新分类名称" style="margin-right: 10px;" />
        <el-button type="primary" @click="addCategory" :disabled="!newCategoryName">添加</el-button>
      </div>
      <el-table :data="categories" border height="300px">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="danger" link @click="deleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Picture, Delete } from '@element-plus/icons-vue'
import { adminProductApi } from '@/api/adminProduct'
import { Product, ProductCategory } from '@/api/product'

// --- 状态定义 ---
const loading = ref(false)
const submitting = ref(false)

const products = ref<(Product & { statusLoading?: boolean })[]>([])
const categories = ref<ProductCategory[]>([])

const filter = reactive({
  categoryId: undefined as number | undefined,
  keyword: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialog = reactive({
  product: false,
  category: false
})

const isEdit = ref(false)
const newCategoryName = ref('')

// 表单对象
const productForm = reactive({
  id: undefined as number | undefined,
  name: '',
  categoryId: undefined as number | undefined,
  images: [''] as string[],
  currentPrice: 0,
  originalPrice: 0,
  stock: 0,
  description: '',
  specsJson: '', // 临时存储 JSON 字符串
  isActive: true
})

// --- 计算属性 ---
const lowStockCount = computed(() => {
  // 注意：这里仅统计当前页，如果要统计所有需要后端接口支持返回统计数据
  // 此处演示前端统计当前页的逻辑，或者假设后端返回了 extra stat
  return products.value.filter(p => p.stock < 10).length
})

// --- 初始化 ---
onMounted(() => {
  loadCategories()
  loadProducts()
})

// --- 核心方法 ---

// 1. 加载分类
const loadCategories = async () => {
  try {
    const res: any = await adminProductApi.getCategories()
    categories.value = res.data || res
  } catch (e) {
    console.error('加载分类失败')
  }
}

const getCategoryName = (id: number) => {
  return categories.value.find(c => c.id === id)?.name || '未知分类'
}

// 2. 加载商品
const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      categoryId: filter.categoryId,
      keyword: filter.keyword || undefined
    }
    const res: any = await adminProductApi.getProducts(params)
    const data = res.data || res
    
    if (data.content) {
      products.value = data.content
      pagination.total = data.totalElements
    } else {
      products.value = Array.isArray(data) ? data : []
    }
  } catch (e) {
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  pagination.page = 1
  loadProducts()
}

// 3. 商品增删改
const openProductDialog = (row?: Product) => {
  if (row) {
    isEdit.value = true
    productForm.id = row.id
    productForm.name = row.name
    productForm.categoryId = row.categoryId
    productForm.images = row.images && row.images.length > 0 ? [...row.images] : ['']
    productForm.currentPrice = row.currentPrice
    productForm.originalPrice = row.originalPrice
    productForm.stock = row.stock
    productForm.description = row.description
    productForm.specsJson = JSON.stringify(row.specs || {})
    productForm.isActive = row.isActive
  } else {
    isEdit.value = false
    resetProductForm()
  }
  dialog.product = true
}

const resetProductForm = () => {
  productForm.id = undefined
  productForm.name = ''
  productForm.categoryId = undefined
  productForm.images = ['']
  productForm.currentPrice = 0
  productForm.originalPrice = 0
  productForm.stock = 0
  productForm.description = ''
  productForm.specsJson = ''
  productForm.isActive = true
}

const submitProduct = async () => {
  if (!productForm.name || !productForm.categoryId) {
    ElMessage.warning('请填写必填项')
    return
  }

  // 验证 JSON
  let specsObj = {}
  if (productForm.specsJson) {
    try {
      specsObj = JSON.parse(productForm.specsJson)
    } catch (e) {
      ElMessage.error('规格配置 JSON 格式错误')
      return
    }
  }

  submitting.value = true
  try {
    const payload: Partial<Product> = {
      name: productForm.name,
      categoryId: productForm.categoryId,
      images: productForm.images.filter(i => !!i), // 过滤空串
      currentPrice: productForm.currentPrice,
      originalPrice: productForm.originalPrice,
      stock: productForm.stock,
      description: productForm.description,
      specs: specsObj,
      isActive: productForm.isActive
    }

    if (isEdit.value && productForm.id) {
      await adminProductApi.updateProduct(productForm.id, payload)
      ElMessage.success('更新成功')
    } else {
      await adminProductApi.createProduct(payload)
      ElMessage.success('发布成功')
    }
    dialog.product = false
    loadProducts()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (row: Product) => {
  ElMessageBox.confirm(`确定删除商品 "${row.name}" 吗？`, '警告', { type: 'warning' })
    .then(async () => {
      await adminProductApi.deleteProduct(row.id)
      ElMessage.success('已删除')
      loadProducts()
    })
    .catch(() => {})
}

// 4. 上下架切换
const handleStatusChange = async (row: Product & { statusLoading?: boolean }, isActive: boolean) => {
  row.statusLoading = true
  try {
    await adminProductApi.updateStatus(row.id, isActive)
    ElMessage.success(isActive ? '已上架' : '已下架')
  } catch (e) {
    row.isActive = !isActive // 恢复状态
    ElMessage.error('状态更新失败')
  } finally {
    row.statusLoading = false
  }
}

// 5. 图片行操作
const addImageRow = () => {
  productForm.images.push('')
}
const removeImageRow = (idx: number) => {
  productForm.images.splice(idx, 1)
}

// 6. 分类管理逻辑
const openCategoryDialog = () => {
  dialog.category = true
}

const addCategory = async () => {
  if (!newCategoryName.value) return
  try {
    await adminProductApi.createCategory({ name: newCategoryName.value })
    ElMessage.success('分类添加成功')
    newCategoryName.value = ''
    loadCategories()
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const deleteCategory = (row: ProductCategory) => {
  ElMessageBox.confirm(`确定删除分类 "${row.name}" 吗？`, '警告', { type: 'warning' })
    .then(async () => {
      await adminProductApi.deleteCategory(row.id)
      ElMessage.success('分类已删除')
      loadCategories()
    })
    .catch(() => {})
}
</script>

<style scoped>
.product-management-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  min-height: 600px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  align-items: center;
}

.table-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  display: block;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.price-info {
  display: flex;
  flex-direction: column;
}

.current-price {
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 12px;
}

.low-stock {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 图片输入列表样式 */
.image-input-list .image-row {
  margin-bottom: 8px;
}

.category-add-row {
  display: flex;
}
</style>
<template>
  <div class="content-management-container">
    <div class="page-header">
      <h2>内容管理 (CMS)</h2>
    </div>

    <el-tabs v-model="activeTab" type="border-card" class="content-tabs" @tab-change="handleTabChange">
      
      <el-tab-pane label="轮播图管理" name="carousel">
        <div class="tab-action-bar">
          <el-button type="primary" @click="openCarouselDialog()">
            <el-icon><Plus /></el-icon> 新增轮播图
          </el-button>
        </div>

        <el-table :data="carousels" border v-loading="loading.carousel">
          <el-table-column label="图片预览" width="180">
            <template #default="{ row }">
              <el-image 
                :src="row.imageUrl" 
                :preview-src-list="[row.imageUrl]" 
                fit="cover" 
                class="table-image"
              />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="linkUrl" label="跳转链接" />
          <el-table-column prop="sortOrder" label="排序权重" width="100" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="openCarouselDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteCarousel(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="新闻公告" name="news">
        <div class="tab-action-bar">
          <el-button type="primary" @click="openNewsDialog()">
            <el-icon><Plus /></el-icon> 发布公告
          </el-button>
          <div class="search-box">
             <el-input v-model="search.newsKeyword" placeholder="搜索标题" clearable @clear="loadNews" @keyup.enter="loadNews">
               <template #append><el-button @click="loadNews"><el-icon><Search /></el-icon></el-button></template>
             </el-input>
          </div>
        </div>

        <el-table :data="newsList" border v-loading="loading.news">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="publishTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="openNewsDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteNews(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="艺人阵容" name="artist">
        <div class="tab-action-bar">
          <el-button type="primary" @click="openArtistDialog()">
            <el-icon><Plus /></el-icon> 添加艺人
          </el-button>
          <el-input v-model="search.artistKeyword" placeholder="搜索艺人" style="width: 200px; margin-left: 10px;" @keyup.enter="loadArtists" />
        </div>

        <el-table :data="artists" border v-loading="loading.artist">
          <el-table-column label="头像" width="100">
            <template #default="{ row }">
              <el-avatar :size="50" :src="row.imageUrl" />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="艺人名称" width="150" />
          <el-table-column label="标签" width="150">
            <template #default="{ row }">
              <el-tag v-if="row.isLocalBand" type="success" effect="dark">沈阳本土</el-tag>
              <el-tag v-for="tag in row.tags" :key="tag" style="margin-left: 5px;">{{ tag }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="简介" show-overflow-tooltip />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="openArtistDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteArtist(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="演出日程" name="schedule">
        <el-alert title="提示" type="info" :closable="false" show-icon class="mb-20">
          此处维护演出时间轴数据，用于前端“演出时刻表”展示。
        </el-alert>
        
        <div class="schedule-editor">
          <el-input
            v-model="scheduleJson"
            type="textarea"
            :rows="15"
            placeholder="请输入日程配置 JSON 数据..."
          />
          <div class="schedule-actions">
            <el-button type="primary" @click="saveSchedule">保存日程配置</el-button>
            <el-button @click="resetSchedule">重置</el-button>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="静态页面" name="static">
        <div class="static-page-editor">
          <el-form label-width="100px">
            <el-form-item label="选择页面">
              <el-radio-group v-model="staticPageType">
                <el-radio-button label="transport">交通指南</el-radio-button>
                <el-radio-button label="entrance">入场须知</el-radio-button>
                <el-radio-button label="faq">常见问题</el-radio-button>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="页面内容">
              <el-input
                v-model="staticPageContent"
                type="textarea"
                :rows="15"
                placeholder="支持 HTML 或 Markdown 内容..."
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveStaticPage">发布页面</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

    </el-tabs>

    <el-dialog v-model="dialog.carousel" :title="isEdit ? '编辑轮播图' : '新增轮播图'" width="500px">
      <el-form :model="carouselForm" label-width="80px">
        <el-form-item label="图片地址">
          <el-input v-model="carouselForm.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="carouselForm.title" placeholder="可选" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="carouselForm.linkUrl" placeholder="点击后跳转的地址" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="carouselForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.carousel = false">取消</el-button>
        <el-button type="primary" @click="submitCarousel">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialog.news" :title="isEdit ? '编辑公告' : '发布公告'" width="800px">
      <el-form :model="newsForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="newsForm.title" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="newsForm.coverImage" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input 
            v-model="newsForm.content" 
            type="textarea" 
            :rows="10" 
            placeholder="请输入公告内容（支持HTML）..." 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.news = false">取消</el-button>
        <el-button type="primary" @click="submitNews">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialog.artist" :title="isEdit ? '编辑艺人' : '添加艺人'" width="600px">
      <el-form :model="artistForm" label-width="100px">
        <el-form-item label="艺人名称">
          <el-input v-model="artistForm.name" />
        </el-form-item>
        <el-form-item label="照片">
          <el-input v-model="artistForm.imageUrl" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="本土乐队">
          <el-switch v-model="artistForm.isLocalBand" active-text="是沈阳本土乐队" />
        </el-form-item>
        <el-form-item label="标签">
          <el-select
            v-model="artistForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="输入标签后回车"
          />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="artistForm.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.artist = false">取消</el-button>
        <el-button type="primary" @click="submitArtist">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { contentApi, Carousel, News, Artist } from '@/api/content'

// --- 状态定义 ---
const activeTab = ref('carousel')
const isEdit = ref(false)

const loading = reactive({
  carousel: false,
  news: false,
  artist: false
})

const dialog = reactive({
  carousel: false,
  news: false,
  artist: false
})

const search = reactive({
  newsKeyword: '',
  artistKeyword: ''
})

// 数据列表
const carousels = ref<Carousel[]>([])
const newsList = ref<News[]>([])
const artists = ref<Artist[]>([])

// 表单数据模型
const carouselForm = reactive<Carousel>({
  imageUrl: '',
  title: '',
  linkUrl: '',
  sortOrder: 0
})

const newsForm = reactive<News>({
  title: '',
  content: '',
  coverImage: ''
})

const artistForm = reactive<Artist>({
  name: '',
  description: '',
  imageUrl: '',
  isLocalBand: false,
  tags: []
})

// 日程与静态页
const scheduleJson = ref('')
const staticPageType = ref('transport')
const staticPageContent = ref('')

// --- 初始化与加载 ---
onMounted(() => {
  loadCarousels()
})

const handleTabChange = (tabName: string) => {
  if (tabName === 'carousel') loadCarousels()
  if (tabName === 'news') loadNews()
  if (tabName === 'artist') loadArtists()
  // schedule 和 static 可以在切换时按需加载，这里暂略
}

// ---------------- 1. 轮播图逻辑 ----------------
const loadCarousels = async () => {
  loading.carousel = true
  try {
    const res = await contentApi.getCarousels()
    carousels.value = (res as any).data || res
  } catch (err) {
    ElMessage.error('加载轮播图失败')
  } finally {
    loading.carousel = false
  }
}

const openCarouselDialog = (row?: Carousel) => {
  if (row) {
    isEdit.value = true
    Object.assign(carouselForm, row)
  } else {
    isEdit.value = false
    carouselForm.id = undefined
    carouselForm.imageUrl = ''
    carouselForm.title = ''
    carouselForm.linkUrl = ''
    carouselForm.sortOrder = 0
  }
  dialog.carousel = true
}

const submitCarousel = async () => {
  try {
    await contentApi.saveCarousel(carouselForm)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialog.carousel = false
    loadCarousels()
  } catch (err) {
    ElMessage.error('保存失败')
  }
}

const handleDeleteCarousel = (row: Carousel) => {
  ElMessageBox.confirm('确定删除该轮播图吗?', '提示', { type: 'warning' })
    .then(async () => {
      await contentApi.deleteCarousel(row.id!)
      ElMessage.success('已删除')
      loadCarousels()
    })
}

// ---------------- 2. 新闻公告逻辑 ----------------
const loadNews = async () => {
  loading.news = true
  try {
    const res = await contentApi.getNewsList({ keyword: search.newsKeyword, page: 0, size: 20 })
    // 假设后端返回分页结构，这里简单取 content
    const data = (res as any).data || res
    newsList.value = Array.isArray(data) ? data : data.content || []
  } catch (err) {
    ElMessage.error('加载新闻失败')
  } finally {
    loading.news = false
  }
}

const openNewsDialog = (row?: News) => {
  if (row) {
    isEdit.value = true
    Object.assign(newsForm, row)
  } else {
    isEdit.value = false
    newsForm.id = undefined
    newsForm.title = ''
    newsForm.content = ''
    newsForm.coverImage = ''
  }
  dialog.news = true
}

const submitNews = async () => {
  try {
    await contentApi.saveNews(newsForm)
    ElMessage.success('发布成功')
    dialog.news = false
    loadNews()
  } catch (err) {
    ElMessage.error('发布失败')
  }
}

const handleDeleteNews = (row: News) => {
  ElMessageBox.confirm('确定删除该公告吗?', '提示', { type: 'warning' })
    .then(async () => {
      await contentApi.deleteNews(row.id!)
      ElMessage.success('已删除')
      loadNews()
    })
}

// ---------------- 3. 艺人管理逻辑 ----------------
const loadArtists = async () => {
  loading.artist = true
  try {
    const res = await contentApi.getArtists({ keyword: search.artistKeyword, page: 0, size: 20 })
    const data = (res as any).data || res
    artists.value = Array.isArray(data) ? data : data.content || []
  } catch (err) {
    ElMessage.error('加载艺人列表失败')
  } finally {
    loading.artist = false
  }
}

const openArtistDialog = (row?: Artist) => {
  if (row) {
    isEdit.value = true
    Object.assign(artistForm, row)
    // 确保 tags 是数组
    if (!Array.isArray(artistForm.tags)) {
      artistForm.tags = []
    }
  } else {
    isEdit.value = false
    artistForm.id = undefined
    artistForm.name = ''
    artistForm.description = ''
    artistForm.imageUrl = ''
    artistForm.isLocalBand = false
    artistForm.tags = []
  }
  dialog.artist = true
}

const submitArtist = async () => {
  try {
    await contentApi.saveArtist(artistForm)
    ElMessage.success('保存成功')
    dialog.artist = false
    loadArtists()
  } catch (err) {
    ElMessage.error('保存失败')
  }
}

const handleDeleteArtist = (row: Artist) => {
  ElMessageBox.confirm(`确定删除艺人 "${row.name}" 吗?`, '警告', { type: 'warning' })
    .then(async () => {
      await contentApi.deleteArtist(row.id!)
      ElMessage.success('已删除')
      loadArtists()
    })
}

// ---------------- 4. 日程逻辑 ----------------
const saveSchedule = async () => {
  try {
    const data = JSON.parse(scheduleJson.value)
    await contentApi.saveSchedule(data)
    ElMessage.success('日程配置已更新')
  } catch (e) {
    ElMessage.error('JSON 格式错误或保存失败')
  }
}
const resetSchedule = () => {
  scheduleJson.value = ''
}

// ---------------- 5. 静态页逻辑 ----------------
const saveStaticPage = async () => {
  if (!staticPageContent.value) return ElMessage.warning('内容不能为空')
  try {
    await contentApi.saveStaticPage(staticPageType.value, staticPageContent.value)
    ElMessage.success('页面发布成功')
  } catch (err) {
    ElMessage.error('发布失败')
  }
}
</script>

<style scoped>
.content-management-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  min-height: 600px;
}

.page-header {
  margin-bottom: 20px;
}

.tab-action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-image {
  width: 100px;
  height: 60px;
  border-radius: 4px;
}

.mb-20 {
  margin-bottom: 20px;
}

.schedule-editor, .static-page-editor {
  max-width: 800px;
}

.schedule-actions {
  margin-top: 20px;
}
</style>
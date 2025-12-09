<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>资源内容配置 - {{ currentResourceName }}</span>
        <el-button @click="$router.back()">返回列表</el-button>
      </div>
    </template>

    <el-form :model="form" label-width="100px" style="max-width: 800px">
      
      <el-form-item label="内容形式">
        <el-radio-group v-model="form.contentType">
          <el-radio label="article">纯文章</el-radio>
          <el-radio label="video">视频</el-radio>
          <el-radio label="image">图片集</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="详情标题">
        <el-input v-model="form.title" placeholder="请输入详情页的大标题" />
      </el-form-item>

      <el-form-item label="文章内容" v-if="form.contentType === 'article'">
        <el-input 
          v-model="form.contentText" 
          type="textarea" 
          :rows="15" 
          placeholder="在此输入文章正文..." 
        />
      </el-form-item>

      <el-form-item label="上传视频" v-if="form.contentType === 'video'">
        <el-upload
          class="upload-demo"
          action="#"
          :http-request="(option) => handleUpload(option, 'video')"
          :show-file-list="false"
        >
          <el-button type="primary">点击上传视频</el-button>
        </el-upload>
        <div v-if="form.mediaFileNames" class="video-preview">
          <p>当前视频文件: {{ form.mediaFileNames }}</p>
          <el-tag type="success">上传成功</el-tag>
        </div>
      </el-form-item>

      <el-form-item label="上传图片" v-if="form.contentType === 'image'">
        <el-upload
          action="#"
          list-type="picture-card"
          :http-request="(option) => handleUpload(option, 'image')"
          :on-remove="handleRemoveImage"
          :file-list="fileList"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSave">保存配置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import api from '../../api/NetWorkApi.js';

const route = useRoute();
const router = useRouter();
const resourceId = route.query.resourceId; // 从路由参数获取资源ID
const currentResourceName = ref(route.query.resourceName || '未知资源');

const form = reactive({
  detailId: null,
  learnResourceId: resourceId,
  contentType: 'article',
  title: '',
  contentText: '',
  mediaFileNames: '', // 存储文件名字符串
});

const fileList = ref([]); // 用于图片回显

// 初始化加载
onMounted(async () => {
  if (!resourceId) {
    ElMessage.error('未指定资源ID');
    return;
  }
  try {
    const res = await api.resourceDetailApi.getByResourceId(resourceId);
    if (res.data) {
      Object.assign(form, res.data);
      // 如果是图片，处理回显列表
      if (form.contentType === 'image' && form.mediaFileNames) {
        fileList.value = form.mediaFileNames.split(',').map(name => ({
          name: name,
          url: 'http://localhost:8081/upload/image/' + name // 假定静态资源路径
        }));
      }
    }
  } catch (e) {
    console.error(e);
  }
});

// 自定义上传逻辑
const handleUpload = async (option, type) => {
  try {
    const res = await api.resourceDetailApi.uploadFile(option.file, type);
    const fileName = res.data; // 后端返回文件名
    
    if (type === 'video') {
      form.mediaFileNames = fileName;
      ElMessage.success('视频上传成功');
    } else {
      // 图片追加
      const currentImages = form.mediaFileNames ? form.mediaFileNames.split(',') : [];
      currentImages.push(fileName);
      form.mediaFileNames = currentImages.join(',');
      
      // 更新回显列表
      fileList.value.push({
        name: fileName,
        url: URL.createObjectURL(option.file) // 临时预览
      });
    }
  } catch (e) {
    ElMessage.error('上传失败');
  }
};

const handleRemoveImage = (file) => {
  // 移除图片逻辑
  const currentImages = form.mediaFileNames.split(',');
  const index = currentImages.indexOf(file.name);
  if (index > -1) {
    currentImages.splice(index, 1);
    form.mediaFileNames = currentImages.join(',');
  }
};

const handleSave = async () => {
  try {
    form.learnResourceId = resourceId;
    await api.resourceDetailApi.saveOrUpdate(form);
    ElMessage.success('保存成功');
    router.back();
  } catch (e) {
    ElMessage.error('保存失败');
  }
};
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.video-preview { margin-top: 10px; padding: 10px; background: #f0f2f5; border-radius: 4px; }
</style>
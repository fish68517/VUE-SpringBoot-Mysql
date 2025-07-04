<template>
  <n-card class="upload-box">
    <n-upload
      v-model:file-list="uploadFileListRef"
      :show-file-list="false"
      :customRequest="customRequest"
      :onBeforeUpload="beforeUploadHandle"
    >
      <n-upload-dragger>
        <n-spin :show="loading">
          <img v-if="type === 'image' && fileUrl" class="upload-show" :src="fileUrl" alt="背景" />
          <video :src="fileUrl" v-if="type === 'video' && fileUrl" class="upload-show" />
          <img :src="audioUrl" v-if="type === 'audio' && fileUrl" class="upload-show" />
          <div class="upload-img" v-show="!fileUrl">
            <n-text class="upload-desc" depth="3" v-if="type === 'image'"> 将图片拖入该区域 </n-text>
            <n-text class="upload-desc" depth="3" v-if="type === 'video'"> 将视频拖入该区域 </n-text>
            <n-text class="upload-desc" depth="3" v-if="type === 'audio'"> 将mp3格式音频拖入该区域 </n-text>
            <div class="btn">选择文件</div>
          </div>
        </n-spin>
      </n-upload-dragger>
    </n-upload>
  </n-card>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { UploadCustomRequestOptions, pProps } from 'naive-ui'
import { fetchRouteParamsLocation } from '@/utils'

const props = defineProps({
  uploadUrl: {
    type: String,
    default: 'http://sjjsc.htgl.devops.com/api/projectManage/pageDataManage/upload'
  },
  token: {
    type: String,
    default: ''
  },
  fileUrl: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'image'
  }
})

const uploadFileListRef = ref()
const emit = defineEmits<{
  (e: 'update:fileUrl', val: string): void
}>()
const audioUrl =
  'data:image/png;base64,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'
const loading = ref(false)
// 自定义上传操作
const baseUrl = window.location.origin.includes('http://localhost')
  ? 'http://58.220.24.195:9098/api'
  : window.location.origin.includes('https://szzx-test.jszx.gov.cn:9001') ||
    window.location.origin.includes('http://172.26.192.19:23343')
  ? '/yituapi'
  : '/api'


const customRequest = (options: UploadCustomRequestOptions) => {
  const { file } = options
  nextTick(async () => {
    if (file.file) {
      loading.value = true
      // 修改名称
      const newNameFile = new File(
        [file.file],
        `${fetchRouteParamsLocation()}_index_${props.type}.${file.name.split('.').pop()}`,
        {
          type: file.file.type
        }
      )
      let uploadParams = new FormData()
      uploadParams.append('object', newNameFile)
      try {

        // const uploadRes = await uploadImage(uploadParams)
        const uploadRes: any = await fetch(props.uploadUrl, {
          method: 'POST',
          headers: {
            Authorization: props.token
          },
          body: uploadParams
        })
        let result = await uploadRes.json()
        
        // console.log(result)
        if (result && result.code === 200) {
          emit('update:fileUrl', baseUrl + '/api/download/' + result.data.fileUrl)
          loading.value = false
          return
        } else {
          props.type === 'image'
            ? window['$message'].error('添加图片错误,请将图片转换为 jpg 格式上传！')
            : props.type === 'video'
            ? window['$message'].error('添加视频失败，请稍后重试！')
            : window['$message'].error('添加音频失败，请稍后重试！')
          loading.value = false
        }
      } catch (err) {
        console.log(err)
        props.type === 'image'
          ? window['$message'].error('添加图片失败，请稍后重试！')
          : props.type === 'video'
          ? window['$message'].error('添加视频失败，请稍后重试！')
          : window['$message'].error('添加音频失败，请稍后重试！')
        loading.value = false
      }
    } else {
      console.log('props.type', props.type)
      props.type === 'image'
        ? window['$message'].error('添加图片失败，请稍后重试！')
        : props.type === 'video'
        ? window['$message'].error('添加视频失败，请稍后重试！')
        : window['$message'].error('添加音频失败，请稍后重试！')
      loading.value = false
    }
  })
}
// 上传图片前置处理
//@ts-ignore
const beforeUploadHandle = async ({ file }) => {
  uploadFileListRef.value = []
  const type = file.file.type
  const size = file.file.size
  // if (size > 1024 * 1024 * backgroundImageSize) {
  //   window['$message'].warning(`图片超出 ${backgroundImageSize}M 限制，请重新上传！`)
  //   return false
  // }
  console.log(type)
  const imageType = ['image/png', 'image/jpg', 'image/jpeg', 'image/gif', 'image/bmp', 'image/svg+xml']
  const videoType = ['video/mp4', 'video/webm', 'video/quicktime']
  const audioType = ['audio/mpeg', 'video/ogg']
  if (props.type === 'image') {
    if (!imageType.includes(type)) {
      window['$message'].warning('文件格式不符合，请重新上传！')
      return false
    }
    if (size > 1024 * 1024 * 10) {
      window['$message'].warning(`图片超出 10M 限制，请重新上传！`)
      return false
    }
  } else if (props.type === 'video') {
    if (!videoType.includes(type) && type !== 'application/x-mpegurl' && !file.file.name.endsWith('mov')) {
      window['$message'].warning('文件格式不符合，请重新上传！')
      return false
    }
  } else {
    if (!audioType.includes(type)) {
      window['$message'].warning('文件格式不符合，请重新上传！')
      return false
    }
  }
  return true
}


const uploadImage = (data: FormData) => {
  return new Promise<string>((resolve, reject) => {
    const xhr = new XMLHttpRequest()
    const url = props.uploadUrl
    xhr.open("POST", url, true)
    xhr.setRequestHeader("Authorization", props.token)
    xhr.onreadystatechange = function () {
      if (xhr.readyState === 4) {
        if (xhr.status === 200) {
          resolve(xhr.responseText)
        } else {
          reject(xhr.statusText)
        }        
      }
    }
    xhr.onerror = function () {
      reject(xhr.statusText)
    }
    xhr.send(data)
  })
}
</script>
<style scoped lang="scss">
@import '@/styles/pages/form.scss';
.upload-box {
  cursor: pointer;
  margin-bottom: 12px;

  :deep(.n-card__content) {
    padding: 0;
    overflow: hidden;
  }
  :deep(.n-upload-dragger) {
    padding: 5px;
    width: 100%;
  }

  .upload-show {
    max-width: 100%;
    width: -webkit-fill-available;
    height: -webkit-fill-available;
    border-radius: 5px;
  }
  .upload-img {
    display: flex;
    flex-direction: column;
    align-items: center;
    img {
      height: 150px;
    }
    .upload-desc {
      padding: 10px 0;
    }
    .btn {
      width: 100px;
      height: 30px;
      line-height: 30px;
      background: $primary-color;
      border-radius: 4px;
      font-size: 14px;
      font-family: PingFangSC-Semibold, PingFang SC;
      font-weight: 600;
      color: #ffffff;
    }
  }
}
/* 拖拽上传 */
:deep(.n-spin-content),
.n-upload-dragger {
  width: 250px;
  height: 100px;
  // background: #10151f;
  // border: 1px solid #222831;
}
</style>

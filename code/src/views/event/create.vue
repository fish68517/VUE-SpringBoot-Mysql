<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { ElMessage } from 'element-plus'
import { ArrowLeft, DocumentAdd, UploadFilled } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import PageHeader from '@/components/PageHeader/index.vue'
import { createResource } from '@/api/resource'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const files = ref<UploadFile[]>([])
const form = reactive({
  id: 0,
  title: '',
  category: '',
  priority: '普通',
  source: '平台录入',
  deptName: '',
  assignee: '',
  deadline: '',
  description: '',
  contactName: '',
  contactPhone: '',
})
const rules: FormRules = {
  title: [{ required: true, message: '请输入事件标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择事件类型', trigger: 'change' }],
  deptName: [{ required: true, message: '请选择承办部门', trigger: 'change' }],
  description: [{ required: true, message: '请输入事件描述', trigger: 'blur' }],
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    const result = await createResource('event', 'event', {
      ...form,
      eventNo: `SJ${dayjs().format('YYYYMMDD')}${String(Date.now()).slice(-4)}`,
      initiator: '系统管理员',
      processStatus: '待受理',
      status: 1,
    })
    ElMessage.success('事件发起成功')
    await router.replace(`/event/detail/${result.data}`)
  } finally { submitting.value = false }
}
</script>

<template>
  <section class="event-create">
    <PageHeader title="新建事件" description="按标准业务流程登记事件并提交承办部门处理。">
      <el-button :icon="ArrowLeft" @click="router.back()">返回</el-button>
    </PageHeader>

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <div class="form-section page-card">
        <div class="section-title"><span><el-icon><DocumentAdd /></el-icon></span><div><h3>基本信息</h3><p>填写事件的分类、优先级和来源信息</p></div></div>
        <div class="form-grid">
          <el-form-item label="事件标题" prop="title" class="wide"><el-input v-model="form.title" maxlength="100" show-word-limit placeholder="请简要概括事件内容" /></el-form-item>
          <el-form-item label="事件类型" prop="category"><el-select v-model="form.category" placeholder="请选择事件类型"><el-option label="行政执法监督" value="行政执法监督" /><el-option label="法治督察" value="法治督察" /><el-option label="行政复议" value="行政复议" /><el-option label="公共法律服务" value="公共法律服务" /></el-select></el-form-item>
          <el-form-item label="优先级"><el-radio-group v-model="form.priority"><el-radio-button value="普通">普通</el-radio-button><el-radio-button value="高">高</el-radio-button><el-radio-button value="紧急">紧急</el-radio-button></el-radio-group></el-form-item>
          <el-form-item label="事件来源"><el-select v-model="form.source"><el-option label="平台录入" value="平台录入" /><el-option label="渝快政" value="渝快政" /><el-option label="渝快办" value="渝快办" /><el-option label="Open API" value="Open API" /></el-select></el-form-item>
          <el-form-item label="期望完成时间"><el-date-picker v-model="form.deadline" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择完成时间" /></el-form-item>
        </div>
      </div>

      <div class="form-section page-card">
        <div class="section-title"><span>办</span><div><h3>办理信息</h3><p>指定承办部门、经办人和详细说明</p></div></div>
        <div class="form-grid">
          <el-form-item label="承办部门" prop="deptName"><el-select v-model="form.deptName" placeholder="请选择承办部门"><el-option label="法治建设处" value="法治建设处" /><el-option label="执法监督处" value="执法监督处" /><el-option label="行政复议处" value="行政复议处" /><el-option label="公共法律服务处" value="公共法律服务处" /></el-select></el-form-item>
          <el-form-item label="指定经办人"><el-select v-model="form.assignee" clearable placeholder="可不指定，按规则分配"><el-option label="王海峰" value="王海峰" /><el-option label="刘明宇" value="刘明宇" /><el-option label="陈思远" value="陈思远" /></el-select></el-form-item>
          <el-form-item label="事件描述" prop="description" class="wide"><el-input v-model="form.description" type="textarea" :rows="5" maxlength="1000" show-word-limit placeholder="请描述事件背景、问题现状和办理要求" /></el-form-item>
          <el-form-item label="联系人"><el-input v-model="form.contactName" placeholder="请输入联系人姓名" /></el-form-item>
          <el-form-item label="联系电话"><el-input v-model="form.contactPhone" placeholder="请输入联系电话" /></el-form-item>
          <el-form-item label="附件材料" class="wide">
            <el-upload v-model:file-list="files" drag action="#" :auto-upload="false" multiple>
              <el-icon class="el-icon--upload"><UploadFilled /></el-icon><div class="el-upload__text">将文件拖到此处，或 <em>点击选择</em></div><template #tip><div class="el-upload__tip">Mock 上传：支持文档和图片，单个文件不超过 20 MB</div></template>
            </el-upload>
          </el-form-item>
        </div>
      </div>

      <div class="submit-bar page-card">
        <span>提交后将自动生成事件编号并进入“待受理”流程节点。</span>
        <div><el-button @click="router.push('/event/list')">取消</el-button><el-button type="primary" :loading="submitting" @click="submit">提交事件</el-button></div>
      </div>
    </el-form>
  </section>
</template>

<style scoped lang="scss">
.event-create { max-width: 1180px; margin: 0 auto; }
.form-section { padding: 22px 26px 5px; margin-bottom: 14px; }
.section-title { display: flex; align-items: center; gap: 12px; margin-bottom: 21px; padding-bottom: 16px; border-bottom: 1px solid #edf0f3; }
.section-title > span { width: 37px; height: 37px; display: grid; place-items: center; border-radius: 8px; background: #fff0e9; color: var(--brand); font-size: 17px; font-weight: 700; }
.section-title h3 { margin: 0; font-size: 15px; }
.section-title p { margin: 4px 0 0; color: #9aa1ad; font-size: 11px; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); column-gap: 25px; }
.wide { grid-column: 1 / -1; }
:deep(.el-select), :deep(.el-date-editor) { width: 100%; }
:deep(.el-upload), :deep(.el-upload-dragger) { width: 100%; }
.submit-bar { display: flex; align-items: center; justify-content: space-between; padding: 17px 24px; color: #8b93a0; font-size: 12px; }
</style>

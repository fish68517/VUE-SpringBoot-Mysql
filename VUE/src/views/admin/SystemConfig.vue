<template>
  <div class="system-config">
    <h1>系统配置</h1>

    <!-- Tabs for Config and Templates -->
    <el-tabs v-model="activeTab" style="margin-top: 20px">
      <!-- System Configuration Tab -->
      <el-tab-pane label="系统配置" name="config">
        <div class="toolbar">
          <el-button type="primary" @click="handleAddConfig">新增配置</el-button>
          <el-button @click="loadConfigs" :loading="configLoading">刷新</el-button>
        </div>

        <el-table :data="configs" stripe style="margin-top: 20px">
          <el-table-column prop="id" label="配置ID" width="100" />
          <el-table-column prop="configKey" label="配置键" width="150" />
          <el-table-column prop="configValue" label="配置值" show-overflow-tooltip />
          <el-table-column prop="description" label="描述" show-overflow-tooltip />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditConfig(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteConfig(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- Notification Templates Tab -->
      <el-tab-pane label="通知模板" name="templates">
        <div class="toolbar">
          <el-button type="primary" @click="handleAddTemplate">新增模板</el-button>
          <el-button @click="loadTemplates" :loading="templateLoading">刷新</el-button>
        </div>

        <el-table :data="templates" stripe style="margin-top: 20px">
          <el-table-column prop="id" label="模板ID" width="100" />
          <el-table-column prop="templateName" label="模板名称" width="150" />
          <el-table-column prop="templateType" label="模板类型" width="120" />
          <el-table-column prop="templateContent" label="模板内容" show-overflow-tooltip />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditTemplate(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteTemplate(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- Add/Edit Config Dialog -->
    <el-dialog v-model="configDialogVisible" :title="isEditConfig ? '编辑系统配置' : '新增系统配置'" width="600px">
      <el-form :model="configForm" label-width="120px" :rules="configRules" ref="configFormRef">
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="configForm.configKey" placeholder="请输入配置键" :disabled="isEditConfig" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="configForm.configValue" type="textarea" rows="4" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="configForm.description" type="textarea" rows="2" placeholder="请输入配置描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveConfig" :loading="configSaveLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- Add/Edit Template Dialog -->
    <el-dialog v-model="templateDialogVisible" :title="isEditTemplate ? '编辑通知模板' : '新增通知模板'" width="700px">
      <el-form :model="templateForm" label-width="120px" :rules="templateRules" ref="templateFormRef">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="templateForm.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板类型" prop="templateType">
          <el-select v-model="templateForm.templateType" placeholder="请选择模板类型">
            <el-option label="投稿通知" value="SUBMISSION" />
            <el-option label="审稿通知" value="REVIEW" />
            <el-option label="录用通知" value="ACCEPTANCE" />
            <el-option label="驳回通知" value="REJECTION" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板内容" prop="templateContent">
          <el-input v-model="templateForm.templateContent" type="textarea" rows="6" placeholder="请输入模板内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="templateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTemplate" :loading="templateSaveLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { systemConfigService } from '../../services/systemConfigService'

const activeTab = ref('config')

// Config state
const configLoading = ref(false)
const configSaveLoading = ref(false)
const configDialogVisible = ref(false)
const isEditConfig = ref(false)
const configs = ref([])

const configForm = ref({
  id: null,
  configKey: '',
  configValue: '',
  description: ''
})

const configFormRef = ref(null)

const configRules = {
  configKey: [
    { required: true, message: '配置键不能为空', trigger: 'blur' }
  ],
  configValue: [
    { required: true, message: '配置值不能为空', trigger: 'blur' }
  ]
}

// Template state
const templateLoading = ref(false)
const templateSaveLoading = ref(false)
const templateDialogVisible = ref(false)
const isEditTemplate = ref(false)
const templates = ref([])

const templateForm = ref({
  id: null,
  templateName: '',
  templateContent: '',
  templateType: ''
})

const templateFormRef = ref(null)

const templateRules = {
  templateName: [
    { required: true, message: '模板名称不能为空', trigger: 'blur' }
  ],
  templateType: [
    { required: true, message: '模板类型不能为空', trigger: 'change' }
  ],
  templateContent: [
    { required: true, message: '模板内容不能为空', trigger: 'blur' }
  ]
}

// Load data on component mount
onMounted(async () => {
  await loadConfigs()
  await loadTemplates()
})

// Load all system configurations
const loadConfigs = async () => {
  configLoading.value = true
  try {
    const response = await systemConfigService.getAllConfigs()
    if (response.data.code === 200) {
      configs.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '加载系统配置失败')
    }
  } catch (error) {
    ElMessage.error('加载系统配置失败')
    console.error(error)
  } finally {
    configLoading.value = false
  }
}

// Load all notification templates
const loadTemplates = async () => {
  templateLoading.value = true
  try {
    const response = await systemConfigService.getAllTemplates()
    if (response.data.code === 200) {
      templates.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '加载通知模板失败')
    }
  } catch (error) {
    ElMessage.error('加载通知模板失败')
    console.error(error)
  } finally {
    templateLoading.value = false
  }
}

// Add new config
const handleAddConfig = () => {
  isEditConfig.value = false
  configForm.value = {
    id: null,
    configKey: '',
    configValue: '',
    description: ''
  }
  configDialogVisible.value = true
}

// Edit config
const handleEditConfig = (config) => {
  isEditConfig.value = true
  configForm.value = {
    id: config.id,
    configKey: config.configKey,
    configValue: config.configValue || '',
    description: config.description || ''
  }
  configDialogVisible.value = true
}

// Save config
const handleSaveConfig = async () => {
  if (!configFormRef.value) return

  await configFormRef.value.validate(async (valid) => {
    if (!valid) return

    configSaveLoading.value = true
    try {
      let response
      if (isEditConfig.value) {
        response = await systemConfigService.updateConfig(configForm.value.id, configForm.value)
      } else {
        response = await systemConfigService.createConfig(configForm.value)
      }

      if (response.data.code === 200 || response.data.code === 201) {
        ElMessage.success(isEditConfig.value ? '配置已更新' : '配置已创建')
        configDialogVisible.value = false
        await loadConfigs()
      } else {
        ElMessage.error(response.data.message || '保存失败')
      }
    } catch (error) {
      ElMessage.error('保存失败')
      console.error(error)
    } finally {
      configSaveLoading.value = false
    }
  })
}

// Delete config
const handleDeleteConfig = (configId) => {
  ElMessageBox.confirm('确定要删除该配置吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await systemConfigService.deleteConfig(configId)
      if (response.data.code === 200) {
        ElMessage.success('配置已删除')
        await loadConfigs()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {
    // User cancelled the action
  })
}

// Add new template
const handleAddTemplate = () => {
  isEditTemplate.value = false
  templateForm.value = {
    id: null,
    templateName: '',
    templateContent: '',
    templateType: ''
  }
  templateDialogVisible.value = true
}

// Edit template
const handleEditTemplate = (template) => {
  isEditTemplate.value = true
  templateForm.value = {
    id: template.id,
    templateName: template.templateName,
    templateContent: template.templateContent || '',
    templateType: template.templateType || ''
  }
  templateDialogVisible.value = true
}

// Save template
const handleSaveTemplate = async () => {
  if (!templateFormRef.value) return

  await templateFormRef.value.validate(async (valid) => {
    if (!valid) return

    templateSaveLoading.value = true
    try {
      let response
      if (isEditTemplate.value) {
        response = await systemConfigService.updateTemplate(templateForm.value.id, templateForm.value)
      } else {
        response = await systemConfigService.createTemplate(templateForm.value)
      }

      if (response.data.code === 200 || response.data.code === 201) {
        ElMessage.success(isEditTemplate.value ? '模板已更新' : '模板已创建')
        templateDialogVisible.value = false
        await loadTemplates()
      } else {
        ElMessage.error(response.data.message || '保存失败')
      }
    } catch (error) {
      ElMessage.error('保存失败')
      console.error(error)
    } finally {
      templateSaveLoading.value = false
    }
  })
}

// Delete template
const handleDeleteTemplate = (templateId) => {
  ElMessageBox.confirm('确定要删除该模板吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await systemConfigService.deleteTemplate(templateId)
      if (response.data.code === 200) {
        ElMessage.success('模板已删除')
        await loadTemplates()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {
    // User cancelled the action
  })
}
</script>

<style scoped>
.system-config {
  padding: 20px;
}

.system-config h1 {
  margin-bottom: 20px;
  color: #333;
}

.toolbar {
  margin-bottom: 20px;
}
</style>

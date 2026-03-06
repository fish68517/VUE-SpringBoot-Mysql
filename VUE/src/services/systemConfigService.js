import api from './api'

export const systemConfigService = {
  // Get all system configurations
  getAllConfigs() {
    return api.get('/api/config')
  },

  // Get system configuration by key
  getConfigByKey(configKey) {
    return api.get(`/api/config/key/${configKey}`)
  },

  // Get system configuration by id
  getConfigById(id) {
    return api.get(`/api/config/${id}`)
  },

  // Create a new system configuration
  createConfig(configData) {
    return api.post('/api/config', configData)
  },

  // Update system configuration by id
  updateConfig(id, configData) {
    return api.put(`/api/config/${id}`, configData)
  },

  // Update system configuration by key
  updateConfigByKey(configKey, configData) {
    return api.put(`/api/config/key/${configKey}`, configData)
  },

  // Delete system configuration by id
  deleteConfig(id) {
    return api.delete(`/api/config/${id}`)
  },

  // Get all notification templates
  getAllTemplates() {
    return api.get('/api/config/templates')
  },

  // Get notification templates by type
  getTemplatesByType(templateType) {
    return api.get(`/api/config/templates/type/${templateType}`)
  },

  // Get notification template by id
  getTemplateById(id) {
    return api.get(`/api/config/templates/${id}`)
  },

  // Create a new notification template
  createTemplate(templateData) {
    return api.post('/api/config/templates', templateData)
  },

  // Update notification template
  updateTemplate(id, templateData) {
    return api.put(`/api/config/templates/${id}`, templateData)
  },

  // Delete notification template
  deleteTemplate(id) {
    return api.delete(`/api/config/templates/${id}`)
  }
}

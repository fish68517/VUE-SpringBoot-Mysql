import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import HealthDataInput from '../HealthDataInput.vue'

// Mock modules
vi.mock('../../../services/api', () => ({
  healthDataAPI: {
    submitData: vi.fn()
  }
}))

vi.mock('../../../services/auth', () => ({
  authService: {
    getUser: vi.fn(),
    getToken: vi.fn(),
    getUserRole: vi.fn()
  }
}))

vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn()
  }
}))

describe('HealthDataInput 组件', () => {
  let wrapper

  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('应该正确渲染健康数据采集组件', async () => {
    wrapper = mount(HealthDataInput, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-date-picker': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.health-data-input-container').exists()).toBe(true)
  })

  it('应该初始化表单数据对象', async () => {
    wrapper = mount(HealthDataInput, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-date-picker': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.formData.height).toBeNull()
    expect(vm.formData.weight).toBeNull()
    expect(vm.formData.bloodPressure).toBe('')
    expect(vm.formData.heartRate).toBeNull()
    expect(vm.formData.dietRecord).toBe('')
    expect(vm.formData.exerciseRecord).toBe('')
  })

  it('应该有表单验证规则', async () => {
    wrapper = mount(HealthDataInput, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-date-picker': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.formRules.height).toBeDefined()
    expect(vm.formRules.weight).toBeDefined()
    expect(vm.formRules.heartRate).toBeDefined()
  })

  it('应该有提交表单的方法', async () => {
    wrapper = mount(HealthDataInput, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-date-picker': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(typeof vm.handleSubmit).toBe('function')
  })

  it('应该有重置表单的方法', async () => {
    wrapper = mount(HealthDataInput, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-date-picker': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(typeof vm.resetForm).toBe('function')
  })

  it('应该初始化加载和提交状态', async () => {
    wrapper = mount(HealthDataInput, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-date-picker': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.isLoading).toBe(false)
    expect(vm.isSubmitting).toBe(false)
    expect(vm.errorMessage).toBe('')
    expect(vm.successMessage).toBe('')
  })
})

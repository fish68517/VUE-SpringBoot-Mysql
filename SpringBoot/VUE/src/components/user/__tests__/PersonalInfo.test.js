import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import PersonalInfo from '../PersonalInfo.vue'

// Mock modules
vi.mock('../../../services/api', () => ({
  userAPI: {
    getProfile: vi.fn(),
    updateProfile: vi.fn()
  }
}))

vi.mock('../../../services/auth', () => ({
  authService: {
    getUser: vi.fn(),
    getToken: vi.fn(),
    getUserRole: vi.fn(),
    setAuth: vi.fn()
  }
}))

vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn()
  }
}))

describe('PersonalInfo 组件', () => {
  let wrapper

  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('应该正确渲染个人信息组件', async () => {
    wrapper = mount(PersonalInfo, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.personal-info-container').exists()).toBe(true)
  })

  it('应该初始化用户信息对象', async () => {
    wrapper = mount(PersonalInfo, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.userInfo.username).toBe('')
    expect(vm.userInfo.email).toBe('')
    expect(vm.userInfo.name).toBe('')
    expect(vm.userInfo.age).toBeNull()
    expect(vm.userInfo.gender).toBe('')
    expect(vm.userInfo.phone).toBe('')
  })

  it('应该初始化编辑表单对象', async () => {
    wrapper = mount(PersonalInfo, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.editForm.name).toBe('')
    expect(vm.editForm.email).toBe('')
    expect(vm.editForm.age).toBeNull()
    expect(vm.editForm.gender).toBe('')
    expect(vm.editForm.phone).toBe('')
  })

  it('应该有表单验证规则', async () => {
    wrapper = mount(PersonalInfo, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.formRules.name).toBeDefined()
    expect(vm.formRules.email).toBeDefined()
    expect(vm.formRules.age).toBeDefined()
    expect(vm.formRules.gender).toBeDefined()
    expect(vm.formRules.phone).toBeDefined()
  })

  it('应该有开始编辑的方法', async () => {
    wrapper = mount(PersonalInfo, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(typeof vm.startEdit).toBe('function')
  })

  it('应该有取消编辑的方法', async () => {
    wrapper = mount(PersonalInfo, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(typeof vm.cancelEdit).toBe('function')
  })

  it('应该有提交表单的方法', async () => {
    wrapper = mount(PersonalInfo, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(typeof vm.handleSubmit).toBe('function')
  })

  it('应该有格式化性别的方法', async () => {
    wrapper = mount(PersonalInfo, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.formatGender('MALE')).toBe('男')
    expect(vm.formatGender('FEMALE')).toBe('女')
    expect(vm.formatGender('OTHER')).toBe('其他')
    expect(vm.formatGender('UNKNOWN')).toBe('-')
  })

  it('应该初始化加载状态', async () => {
    wrapper = mount(PersonalInfo, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.isLoading).toBe(true)
    expect(vm.isSubmitting).toBe(false)
    expect(vm.isEditing).toBe(false)
    expect(vm.errorMessage).toBe('')
    expect(vm.successMessage).toBe('')
  })
})

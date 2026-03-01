import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import RegisterPage from '../RegisterPage.vue'

// Mock modules
vi.mock('../../services/api', () => ({
  authAPI: {
    register: vi.fn(),
    doctorRegister: vi.fn()
  }
}))

vi.mock('../../services/auth', () => ({
  authService: {
    setAuth: vi.fn()
  }
}))

vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn()
  }
}))

describe('RegisterPage 组件', () => {
  let wrapper
  let router

  beforeEach(() => {
    localStorage.clear()
    router = createRouter({
      history: createMemoryHistory(),
      routes: [
        { path: '/register', component: RegisterPage },
        { path: '/login', component: { template: '<div>Login</div>' } },
        { path: '/user/profile', component: { template: '<div>User Profile</div>' } },
        { path: '/doctor/patients', component: { template: '<div>Doctor Patients</div>' } }
      ]
    })
  })

  it('应该正确渲染注册页面', async () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-radio': true,
          'el-radio-group': true,
          'el-link': true,
          'el-alert': true
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.register-container').exists()).toBe(true)
  })

  it('应该有两个注册类型选项', async () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-radio': true,
          'el-radio-group': true,
          'el-link': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.registerType).toBe('USER')
  })

  it('应该初始化注册表单', async () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-radio': true,
          'el-radio-group': true,
          'el-link': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.registerForm.username).toBe('')
    expect(vm.registerForm.password).toBe('')
    expect(vm.registerForm.confirmPassword).toBe('')
    expect(vm.registerForm.email).toBe('')
  })

  it('应该有注册表单验证规则', async () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-radio': true,
          'el-radio-group': true,
          'el-link': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.registerRules.username).toBeDefined()
    expect(vm.registerRules.password).toBeDefined()
    expect(vm.registerRules.confirmPassword).toBeDefined()
    expect(vm.registerRules.email).toBeDefined()
  })

  it('应该有处理注册的方法', async () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-radio': true,
          'el-radio-group': true,
          'el-link': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(typeof vm.handleRegister).toBe('function')
  })

  it('应该有导航到登录页面的方法', async () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-radio': true,
          'el-radio-group': true,
          'el-link': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(typeof vm.goToLogin).toBe('function')
  })

  it('应该有重置表单的方法', async () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-radio': true,
          'el-radio-group': true,
          'el-link': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(typeof vm.resetForm).toBe('function')
  })

  it('应该初始化加载和错误状态', async () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-radio': true,
          'el-radio-group': true,
          'el-link': true,
          'el-alert': true
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.isLoading).toBe(false)
    expect(vm.errorMessage).toBe('')
  })
})

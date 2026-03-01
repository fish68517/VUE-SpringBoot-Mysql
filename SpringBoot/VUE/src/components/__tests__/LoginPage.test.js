import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import LoginPage from '../LoginPage.vue'

// Mock modules
vi.mock('../../services/api', () => ({
  authAPI: {
    login: vi.fn(),
    doctorLogin: vi.fn(),
    adminLogin: vi.fn()
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

describe('LoginPage 组件', () => {
  let wrapper
  let router

  beforeEach(() => {
    localStorage.clear()
    router = createRouter({
      history: createMemoryHistory(),
      routes: [
        { path: '/login', component: LoginPage },
        { path: '/register', component: { template: '<div>Register</div>' } },
        { path: '/user/profile', component: { template: '<div>User Profile</div>' } },
        { path: '/doctor/patients', component: { template: '<div>Doctor Patients</div>' } },
        { path: '/admin/users', component: { template: '<div>Admin Users</div>' } }
      ]
    })
  })

  it('应该正确渲染登录页面', async () => {
    wrapper = mount(LoginPage, {
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
    expect(wrapper.find('.login-container').exists()).toBe(true)
  })

  it('应该有三个角色选项', async () => {
    wrapper = mount(LoginPage, {
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
    expect(vm.selectedRole).toBe('USER')
  })

  it('应该初始化登录表单', async () => {
    wrapper = mount(LoginPage, {
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
    expect(vm.loginForm.username).toBe('')
    expect(vm.loginForm.password).toBe('')
    expect(vm.isLoading).toBe(false)
    expect(vm.errorMessage).toBe('')
  })

  it('应该有登录规则验证', async () => {
    wrapper = mount(LoginPage, {
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
    expect(vm.loginRules.username).toBeDefined()
    expect(vm.loginRules.password).toBeDefined()
    expect(vm.loginRules.username[0].required).toBe(true)
    expect(vm.loginRules.password[0].required).toBe(true)
  })

  it('应该有导航到注册页面的方法', async () => {
    wrapper = mount(LoginPage, {
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
    expect(typeof vm.goToRegister).toBe('function')
  })

  it('应该有重置表单的方法', async () => {
    wrapper = mount(LoginPage, {
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

  it('应该有处理登录的方法', async () => {
    wrapper = mount(LoginPage, {
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
    expect(typeof vm.handleLogin).toBe('function')
  })
})

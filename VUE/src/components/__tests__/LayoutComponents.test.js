import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import { createPinia } from 'pinia'
import Header from '../Header.vue'
import Sidebar from '../Sidebar.vue'
import Footer from '../Footer.vue'

describe('Layout Components', () => {
  let router

  beforeEach(() => {
    router = createRouter({
      history: createMemoryHistory(),
      routes: [
        { path: '/dashboard', name: 'Dashboard', component: { template: '<div>Dashboard</div>' } },
        { path: '/records', name: 'RecordList', component: { template: '<div>Records</div>' } },
        { path: '/plans', name: 'PlanList', component: { template: '<div>Plans</div>' } },
        { path: '/social', name: 'SocialFeed', component: { template: '<div>Social</div>' } },
        { path: '/profile', name: 'Profile', component: { template: '<div>Profile</div>' } }
      ]
    })
  })

  describe('Header Component', () => {
    it('should render header with logo', () => {
      const wrapper = mount(Header, {
        global: {
          plugins: [router, createPinia()],
          stubs: {
            'el-icon': true,
            'el-button': true,
            'el-dropdown': true,
            'el-dropdown-menu': true,
            'el-dropdown-item': true,
            'router-link': true
          }
        }
      })

      expect(wrapper.find('.header').exists()).toBe(true)
      expect(wrapper.text()).toContain('Travel Memory')
    })

    it('should render navigation links', () => {
      const wrapper = mount(Header, {
        global: {
          plugins: [router, createPinia()],
          stubs: {
            'el-icon': true,
            'el-button': true,
            'el-dropdown': true,
            'el-dropdown-menu': true,
            'el-dropdown-item': true,
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      const navLinks = wrapper.findAll('.nav-link')
      expect(navLinks.length).toBeGreaterThan(0)
    })

    it('should have user section', () => {
      const wrapper = mount(Header, {
        global: {
          plugins: [router, createPinia()],
          stubs: {
            'el-icon': true,
            'el-button': true,
            'el-dropdown': true,
            'el-dropdown-menu': true,
            'el-dropdown-item': true,
            'router-link': true
          }
        }
      })

      expect(wrapper.find('.user-section').exists()).toBe(true)
    })

    it('should have responsive header class', () => {
      const wrapper = mount(Header, {
        global: {
          plugins: [router, createPinia()],
          stubs: {
            'el-icon': true,
            'el-button': true,
            'el-dropdown': true,
            'el-dropdown-menu': true,
            'el-dropdown-item': true,
            'router-link': true
          }
        }
      })

      const header = wrapper.find('.header')
      expect(header.exists()).toBe(true)
    })
  })

  describe('Sidebar Component', () => {
    it('should render sidebar', () => {
      const wrapper = mount(Sidebar, {
        global: {
          plugins: [router],
          stubs: {
            'el-icon': true,
            'el-button': true,
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      expect(wrapper.find('.sidebar').exists()).toBe(true)
    })

    it('should render menu sections', () => {
      const wrapper = mount(Sidebar, {
        global: {
          plugins: [router],
          stubs: {
            'el-icon': true,
            'el-button': true,
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      const menuSections = wrapper.findAll('.menu-section')
      expect(menuSections.length).toBeGreaterThan(0)
    })

    it('should render menu items', () => {
      const wrapper = mount(Sidebar, {
        global: {
          plugins: [router],
          stubs: {
            'el-icon': true,
            'el-button': true,
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      const menuItems = wrapper.findAll('.menu-item')
      expect(menuItems.length).toBeGreaterThan(0)
    })

    it('should toggle collapse state', async () => {
      const wrapper = mount(Sidebar, {
        global: {
          plugins: [router],
          stubs: {
            'el-icon': true,
            'el-button': true,
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      const collapseBtn = wrapper.find('.collapse-btn')
      expect(collapseBtn.exists()).toBe(true)

      await collapseBtn.trigger('click')
      expect(wrapper.vm.isCollapsed).toBe(true)

      await collapseBtn.trigger('click')
      expect(wrapper.vm.isCollapsed).toBe(false)
    })

    it('should have collapsed class when collapsed', async () => {
      const wrapper = mount(Sidebar, {
        global: {
          plugins: [router],
          stubs: {
            'el-icon': true,
            'el-button': true,
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      const sidebar = wrapper.find('.sidebar')
      expect(sidebar.classes()).not.toContain('collapsed')

      await wrapper.vm.$nextTick()
      wrapper.vm.isCollapsed = true
      await wrapper.vm.$nextTick()

      expect(sidebar.classes()).toContain('collapsed')
    })
  })

  describe('Footer Component', () => {
    it('should render footer', () => {
      const wrapper = mount(Footer, {
        global: {
          plugins: [router],
          stubs: {
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      expect(wrapper.find('.footer').exists()).toBe(true)
    })

    it('should render footer sections', () => {
      const wrapper = mount(Footer, {
        global: {
          plugins: [router],
          stubs: {
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      const sections = wrapper.findAll('.footer-section')
      expect(sections.length).toBeGreaterThan(0)
    })

    it('should render footer links', () => {
      const wrapper = mount(Footer, {
        global: {
          plugins: [router],
          stubs: {
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      const links = wrapper.findAll('a')
      expect(links.length).toBeGreaterThan(0)
    })

    it('should render social links', () => {
      const wrapper = mount(Footer, {
        global: {
          plugins: [router],
          stubs: {
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      const socialLinks = wrapper.findAll('.social-link')
      expect(socialLinks.length).toBeGreaterThan(0)
    })

    it('should render footer bottom with copyright', () => {
      const wrapper = mount(Footer, {
        global: {
          plugins: [router],
          stubs: {
            'router-link': { template: '<a><slot /></a>' }
          }
        }
      })

      expect(wrapper.find('.footer-bottom').exists()).toBe(true)
      expect(wrapper.text()).toContain('2024')
    })
  })
})

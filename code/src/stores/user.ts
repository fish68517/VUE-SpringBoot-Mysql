import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getUserInfoApi, loginApi, logoutApi } from '@/api/auth'
import { getToken, removeToken, setToken } from '@/utils/auth'
import type { LoginForm, LoginUser } from '@/types/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const user = ref<LoginUser | null>(null)
  const permissions = ref<string[]>([])
  const loaded = ref(false)

  const displayName = computed(() => user.value?.realName || user.value?.username || '管理员')

  async function login(form: LoginForm) {
    const { data } = await loginApi(form)
    token.value = data.accessToken
    user.value = data.user
    permissions.value = data.permissions
    loaded.value = true
    setToken(data.accessToken)
  }

  async function loadUser() {
    if (!token.value) return
    const { data } = await getUserInfoApi()
    user.value = data.user
    permissions.value = data.permissions
    loaded.value = true
  }

  function hasPermission(code?: string) {
    if (!code) return true
    return permissions.value.includes('*:*:*') || permissions.value.includes(code)
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      reset()
    }
  }

  function reset() {
    token.value = ''
    user.value = null
    permissions.value = []
    loaded.value = false
    removeToken()
  }

  return { token, user, permissions, loaded, displayName, login, loadUser, logout, reset, hasPermission }
})

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useMessageStore = defineStore('message', () => {
  const messages = ref([])
  const unreadCount = computed(() => messages.value.filter(m => !m.is_read).length)

  const setMessages = (data) => {
    messages.value = data
  }

  const addMessage = (message) => {
    messages.value.unshift(message)
  }

  const markAsRead = (messageId) => {
    const message = messages.value.find(m => m.id === messageId)
    if (message) {
      message.is_read = true
    }
  }

  return {
    messages,
    unreadCount,
    setMessages,
    addMessage,
    markAsRead
  }
})

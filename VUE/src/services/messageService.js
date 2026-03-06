import api from './api'

export const messageService = {
  // Get all messages for the current user
  getMessages() {
    return api.get('/api/messages')
  },

  // Mark a message as read
  markMessageAsRead(messageId) {
    return api.put(`/api/messages/${messageId}/read`)
  },

  // Get unread message count
  getUnreadCount() {
    return api.get('/api/messages/unread-count')
  },

  // Send a message
  sendMessage(data) {
    return api.post('/api/messages', data)
  },

  // Get messages with a specific author (for editor)
  getMessagesWithAuthor(authorId) {
    return api.get(`/api/messages/with-author/${authorId}`)
  },

  // Get messages with a specific reviewer (for editor)
  getMessagesWithReviewer(reviewerId) {
    return api.get(`/api/messages/with-reviewer/${reviewerId}`)
  }
}

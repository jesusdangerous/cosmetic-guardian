import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

export default {
  async startRegistration(telegramId, email) {
    return api.post('/auth/start', { telegramId, email })
  },

  async verifyCode(telegramId, code) {
    return api.post('/auth/verify', { telegramId, code })
  },

  async checkRegistration(telegramId) {
    return api.get('/auth/check', { params: { telegramId } })
  }
}

import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const pendingEmail = ref(null)
  const isAuthenticated = ref(false)

  const setUser = (userData) => {
    user.value = userData
    isAuthenticated.value = true
  }

  const setPendingEmail = (email) => {
    pendingEmail.value = email
  }

  const logout = () => {
    user.value = null
    isAuthenticated.value = false
  }

  return { user, pendingEmail, isAuthenticated, setUser, setPendingEmail, logout }
})

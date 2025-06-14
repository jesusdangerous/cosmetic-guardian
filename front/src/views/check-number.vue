<template>
  <div class="page-wrapper">
    <nav>
      <div>
        <IconButton @click="goBack"><img src='../assets/images/arrow-back.svg'></IconButton>
        <NavButton text="Назад" @click="goBack"></NavButton>
      </div>
      <NavButton text="Пропустить" @click="skipRegistration"></NavButton>
    </nav>
    <div class="check-number">
      <header>
        <h1>Еще немного!</h1>
        <h2>Введите код из письма</h2>
        <p class="email-hint">Код отправлен на {{ pendingEmail }}</p>
      </header>
      <label>
        <Input v-model="code" placeholder="_ _ _ _" maxlength="6"></Input>
      </label>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <div class="buttons">
        <Button
          text="Завершить регистрацию"
          @click="completeRegistration"
          :disabled="isLoading || code.length < 4"
        >
          <span v-if="isLoading">Проверка...</span>
          <span v-else>Завершить регистрацию</span>
        </Button>
        <Button
          text="Отправить код повторно"
          @click="resendCode"
          :disabled="isResending"
        >
          <span v-if="isResending">Отправка...</span>
          <span v-else>Отправить код повторно</span>
        </Button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import IconButton from '@/components/UI/IconButton.vue'
import NavButton from '@/components/UI/NavButton.vue'
import Input from '@/components/UI/Input.vue'
import Button from '@/components/UI/Button.vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import { API_URL } from '@/config'

const router = useRouter()
const authStore = useAuthStore()

const code = ref('')
const isLoading = ref(false)
const isResending = ref(false)
const errorMessage = ref('')

const pendingEmail = computed(() => authStore.pendingEmail)

const goBack = () => {
  router.go(-1)
}

const skipRegistration = () => {
  router.push('/main-page')
}

const completeRegistration = async () => {
  if (code.value.length < 4) {
    errorMessage.value = 'Введите полный код'
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await axios.post(`${API_URL}/api/auth/verify`, {
      email: pendingEmail.value,
      code: code.value
    })

    if (response.data === "Регистрация подтверждена") {
      authStore.setUser({ email: pendingEmail.value })
      router.push('/main-page')
    }
  } catch (error) {
    if (error.response) {
      errorMessage.value = error.response.data.message || error.response.data
    } else {
      errorMessage.value = 'Ошибка сети. Пожалуйста, попробуйте позже.'
    }
  } finally {
    isLoading.value = false
  }
}

const resendCode = async () => {
  isResending.value = true
  errorMessage.value = ''

  try {
    await axios.post(`${API_URL}/api/auth/start`, {
      email: pendingEmail.value
    })
    errorMessage.value = 'Новый код отправлен на вашу почту'
  } catch (error) {
    if (error.response) {
      errorMessage.value = error.response.data.message || error.response.data
    } else {
      errorMessage.value = 'Ошибка сети. Пожалуйста, попробуйте позже.'
    }
  } finally {
    isResending.value = false
  }
}
</script>

<style scoped>
  .email-hint {
    color: #666;
    font-size: 14px;
    margin-top: 5px;
  }

  .error-message {
    color: red;
    margin: 10px 0;
  }
  .page-wrapper {
    width: 100%;
    min-height: 486px;
    display: flex;
    align-items: center;
    flex-direction: column;
    padding-top: 32px;
    justify-content: space-between;
  }

  nav {
    width: 100%;
    display: flex;
    justify-content: space-between;
  }

  nav div {
    display: flex;
    gap: 8px;
    align-items: center;
  }

  .check-number {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 32px;
  }

  header {
    display: flex;
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .buttons {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }


</style>

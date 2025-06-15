<template>
  <div class="page-wrapper">
    <NavButton text="Пропустить" @click="skipRegistration"></NavButton>
    <div class="main">
      <header>
        <h1>Добро пожаловать!</h1>
        <h2>Введите свои данные для регистрации</h2>
      </header>
      <div class="data-user">
        <form ref="userForm" @submit.prevent="startRegistration">
          <label>
            <Input v-model="name" placeholder="Имя"></Input>
          </label>
          <label>
            <Input v-model="email" type="email" placeholder="Email" required></Input>
          </label>
          <div class="checkbox">
            <div>
              <input v-model="consent" type="checkbox" id="true-data" required>
              <label for="true-data">Даю согласие на обработку данных</label>
            </div>
            <Button type="submit" text="Получить код" :disabled="isLoading">
              <span v-if="isLoading">Отправка...</span>
              <span v-else>Получить код</span>
            </Button>
          </div>
        </form>
      </div>
    </div>
    <footer>
      <p>Или войти через</p>
      <div>
        <IconButton><img src="../assets/images/icon-google.svg"></IconButton>
        <IconButton><img src="../assets/images/icon-apple.svg"></IconButton>
        <IconButton><img src="../assets/images/icon-wk.svg"></IconButton>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Input from '@/components/UI/Input.vue'
import Button from '@/components/UI/Button.vue'
import IconButton from '@/components/UI/IconButton.vue'
import NavButton from '@/components/UI/NavButton.vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import { API_URL } from '@/config'

const router = useRouter()
const authStore = useAuthStore()

const name = ref('')
const email = ref('')
const consent = ref(false)
const isLoading = ref(false)
const errorMessage = ref('')

const skipRegistration = () => {
  router.push('/main-page')
}

const startRegistration = async () => {
  if (!consent.value) {
    errorMessage.value = 'Необходимо дать согласие на обработку данных'
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await axios.post(`${API_URL}/api/auth/start`, {
      email: email.value
    })

    if (response.data === "Пользователь уже зарегистрирован") {
      errorMessage.value = response.data
      return
    }

    // Сохраняем email в хранилище для использования на следующем шаге
    authStore.setPendingEmail(email.value)
    router.push('/check-number')
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
</script>

<style scoped>
  .error-message {
    color: red;
    margin-top: 10px;
  }
  .page-wrapper {
    min-height: 600px;
    display: flex;
    align-items: center;
    flex-direction: column;
    padding-top: 32px;
    justify-content: space-between;
  }

  .skip-button {
    border: none;
    background-color: transparent;
  }

  .main {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  button {
    margin-left: auto;
  }

  header {
    display: flex;
    flex-direction: column;
    color: rgba(19, 19, 19, 1);
    text-align: center;
    gap: 12px;
  }

  h1 {
    margin: 0;
    font-size: 32px;
  }

  h2 {
    font-size: 16px;
    margin: 0;
  }

  form {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  .data-user {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .checkbox {
    display: flex;
    flex-direction: column;
    gap: 32px;
  }

  footer {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  footer p {
    margin: 0;
    text-align: center;
  }

  footer div {
    display: flex;
    gap: 16px;
  }


</style>


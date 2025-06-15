<template>
  <div class="page-wrapper">
    <header>
      <IconButton @click="goBack"><img src='../assets/images/arrow-back.svg'></IconButton>
      <h1>Анализ по фото</h1>
    </header>

    <main>
      <!-- Режим камеры -->
      <div v-if="cameraActive" class="camera-mode">
        <video ref="videoElement" autoplay playsinline class="camera-view"></video>
        <div class="camera-controls">
          <button @click="capturePhoto" class="capture-btn">
            <img src="../assets/images/camera-icon.svg" alt="Сделать фото">
          </button>
          <button @click="closeCamera" class="close-btn">×</button>
        </div>
      </div>

      <!-- Основной режим -->
      <div v-else>
        <div class="instructions">
          <h2>Для качественного анализа с помощью фото необходимо:</h2>
          <ul class="instruction-list">
            <li>Выбрать хорошо освещенное место</li>
            <li>Убедиться, что этикетка в фокусе</li>
            <li>Избегать бликов и отражений</li>
            <li>Использовать нейтральный фон</li>
          </ul>
        </div>

        <!-- Предпросмотр фото -->
        <div v-if="photo" class="photo-preview-container">
          <div class="photo-preview">
            <img :src="photoPreview" alt="Предпросмотр фото" class="preview-image">
            <button @click="removePhoto" class="remove-btn">×</button>
          </div>
          <p class="photo-hint">Проверьте, что весь состав четко виден</p>
        </div>

        <!-- Кнопки выбора изображения -->
        <div class="action-buttons">
          <button @click="openGallery" class="action-btn gallery-btn">
            <img src="../assets/images/gallery-icon.svg" alt="Галерея">
            <span>Из галереи</span>
          </button>
          <button @click="openCamera" class="action-btn camera-btn">
            <img src="../assets/images/camera-icon.svg" alt="Камера">
            <span>Сделать фото</span>
          </button>
        </div>

        <!-- Кнопка анализа -->
        <button
          @click="analyzePhoto"
          :disabled="!photo || isAnalyzing"
          class="analyze-btn"
        >
          <span v-if="!isAnalyzing">Анализ состава</span>
          <div v-else class="loading-spinner"></div>
        </button>
      </div>
    </main>

    <Footer></Footer>

    <!-- Скрытый input для выбора файла -->
    <input
      type="file"
      ref="fileInput"
      accept="image/*"
      @change="handleFileUpload"
      style="display: none;"
    >

    <!-- Модальное окно ошибки -->
    <div v-if="errorMessage" class="error-modal">
      <div class="error-content">
        <h3>Ошибка</h3>
        <p>{{ errorMessage }}</p>
        <button @click="errorMessage = ''" class="close-error-btn">Понятно</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import IconButton from '@/components/UI/IconButton.vue';
import Footer from '@/components/Footer.vue';

const router = useRouter();
const fileInput = ref(null);
const videoElement = ref(null);

// Состояния компонента
const photo = ref(null);
const photoPreview = ref('');
const cameraActive = ref(false);
const cameraStream = ref(null);
const isAnalyzing = ref(false);
const errorMessage = ref('');

// Очистка при размонтировании
onBeforeUnmount(() => {
  stopCameraStream();
});

// Навигация назад
const goBack = () => {
  router.go(-1);
};

// Открытие галереи
const openGallery = () => {
  fileInput.value.click();
};

// Обработка выбора файла
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file && file.type.startsWith('image/')) {
    photo.value = file;
    photoPreview.value = URL.createObjectURL(file);
  } else {
    showError('Пожалуйста, выберите файл изображения');
  }
};

// Удаление фото
const removePhoto = () => {
  photo.value = null;
  photoPreview.value = '';
};

// Активация камеры
const openCamera = async () => {
  try {
    cameraActive.value = true;
    await nextTick(); // Ждем отрисовки video элемента

    cameraStream.value = await navigator.mediaDevices.getUserMedia({
      video: {
        facingMode: 'environment',
        width: { ideal: 1920 },
        height: { ideal: 1080 }
      },
      audio: false
    });

    if (videoElement.value) {
      videoElement.value.srcObject = cameraStream.value;
    }
  } catch (err) {
    showError('Не удалось получить доступ к камере');
    cameraActive.value = false;
    console.error('Ошибка камеры:', err);
  }
};

// Снимок с камеры
const capturePhoto = () => {
  if (!videoElement.value || !cameraStream.value) return;

  const canvas = document.createElement('canvas');
  canvas.width = videoElement.value.videoWidth;
  canvas.height = videoElement.value.videoHeight;

  const ctx = canvas.getContext('2d');
  ctx.drawImage(videoElement.value, 0, 0, canvas.width, canvas.height);

  canvas.toBlob(blob => {
    if (blob) {
      photo.value = new File([blob], 'capture.jpg', { type: 'image/jpeg' });
      photoPreview.value = URL.createObjectURL(blob);
    }
    closeCamera();
  }, 'image/jpeg', 0.9);
};

// Закрытие камеры
const closeCamera = () => {
  stopCameraStream();
  cameraActive.value = false;
};

// Остановка потока камеры
const stopCameraStream = () => {
  if (cameraStream.value) {
    cameraStream.value.getTracks().forEach(track => track.stop());
    cameraStream.value = null;
  }
};

// Анализ фото
const analyzePhoto = async () => {
  if (!photo.value || isAnalyzing.value) return;

  isAnalyzing.value = true;
  errorMessage.value = '';

  try {
    const formData = new FormData();
    formData.append('image', photo.value);

    const response = await fetch('/api/analysis/photo', {
      method: 'POST',
      body: formData
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Ошибка при анализе фото');
    }

    const result = await response.json();
    localStorage.setItem('analysisResult', JSON.stringify(result));
    router.push('/analysis-result');
  } catch (err) {
    showError(err.message || 'Не удалось проанализировать фото. Проверьте качество изображения.');
    console.error('Ошибка анализа:', err);
  } finally {
    isAnalyzing.value = false;
  }
};

// Показать ошибку
const showError = (message) => {
  errorMessage.value = message;
};
</script>

<style scoped>
.page-wrapper {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  box-sizing: border-box;
}

header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

h1 {
  flex-grow: 1;
  text-align: center;
  font-size: 20px;
  font-weight: 600;
}

/* Стили для камеры */
.camera-mode {
  position: relative;
  width: 100%;
  height: 70vh;
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
  background: #000;
}

.camera-view {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.camera-controls {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
}

.capture-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.capture-btn img {
  width: 30px;
  height: 30px;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  background: rgba(0,0,0,0.5);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

/* Инструкции */
.instructions {
  margin-bottom: 24px;
}

h2 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
}

.instruction-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.instruction-list li {
  position: relative;
  padding-left: 24px;
  margin-bottom: 8px;
  color: #545454;
}

.instruction-list li:before {
  content: "•";
  position: absolute;
  left: 8px;
  color: #131313;
}

/* Предпросмотр фото */
.photo-preview-container {
  margin-bottom: 20px;
}

.photo-preview {
  position: relative;
  width: 100%;
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.remove-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background: rgba(0,0,0,0.5);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.photo-hint {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 8px;
}

/* Кнопки действий */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.action-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 12px;
  background: white;
  cursor: pointer;
}

.action-btn img {
  width: 24px;
  height: 24px;
  margin-bottom: 8px;
}

.action-btn span {
  font-size: 14px;
}

/* Кнопка анализа */
.analyze-btn {
  width: 100%;
  padding: 16px;
  background: #131313;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.analyze-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Модальное окно ошибки */
.error-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.error-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  max-width: 80%;
  text-align: center;
}

.error-content h3 {
  color: #d32f2f;
  margin-top: 0;
}

.close-error-btn {
  margin-top: 16px;
  padding: 8px 16px;
  background: #131313;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>

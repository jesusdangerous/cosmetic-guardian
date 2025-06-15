<template>
  <div class="page-wrapper">
    <header>
      <IconButton @click="goBack"><img src='../assets/images/arrow-back.svg'></IconButton>
      <h1>Анализ по фото</h1>
    </header>
    <main>
      <div>
        <h2>Для качественного анализа с помощью фото необходимо:</h2>
        <div class="instruction">
          <p>1. Выбрать хорошо освещенное место;</p>
          <p>2. Убедиться, что этикетка хорошо видна и находится в фокусе;</p>
          <p>3. На фото отсутствуют блики и отражения;</p>
          <p>4. Фон нейтральный.</p>
        </div>
      </div>

      <div class="photo-preview" v-if="photo">
        <img :src="photoPreview" alt="Предпросмотр фото">
        <button @click="removePhoto" class="remove-btn">×</button>
      </div>

      <div class="buttons">
        <button @click="openGallery">
          <p>Из галереи</p>
          <IconButton><img src="../assets/images/icon-clip.svg"></IconButton>
        </button>
        <button @click="openCamera">
          <p>Открыть камеру</p>
          <IconButton><img src="../assets/images/icon-scan-black.svg"></IconButton>
        </button>
      </div>

      <div>
        <button @click="analyzePhoto" :disabled="!photo" class="analyze-btn">
          <p>Анализ состава</p>
        </button>
      </div>
    </main>
    <Footer></Footer>

    <input type="file" ref="fileInput" accept="image/*" @change="handleFileUpload" style="display: none;">
  </div>
</template>


<script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import IconButton from '@/components/UI/IconButton.vue';
  import Footer from '@/components/Footer.vue';

  const router = useRouter();
  const fileInput = ref(null);
  const photo = ref(null);
  const photoPreview = ref('');

  const goBack = () => {
    router.go(-1);
  };

  const openGallery = () => {
    fileInput.value.click();
  };

  const openCamera = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      // Здесь можно реализовать логику для съемки фото с камеры
      // Например, использовать <video> элемент и canvas для захвата кадра
      alert('Режим камеры будет реализован в следующей версии');
    } catch (error) {
      console.error('Ошибка доступа к камере:', error);
      alert('Не удалось получить доступ к камере');
    }
  };

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      photo.value = file;
      photoPreview.value = URL.createObjectURL(file);
    }
  };

  const removePhoto = () => {
    photo.value = null;
    photoPreview.value = '';
  };

  const analyzePhoto = async () => {
    if (!photo.value) return;

    try {
      const formData = new FormData();
      formData.append('image', photo.value);

      const response = await fetch('/api/analysis/photo', {
        method: 'POST',
        body: formData
      });

      if (!response.ok) throw new Error('Ошибка анализа фото');

      const result = await response.json();
      localStorage.setItem('analysisResult', JSON.stringify(result));
      router.push('/analysis-result');
    } catch (error) {
      console.error('Ошибка при анализе фото:', error);
      alert('Не удалось проанализировать фото');
    }
  };
</script>

<style scoped>
  .page-wrapper {
    width: 92%;
    padding: 20px 0;
    display: flex;
    flex-direction: column;
    gap: 32px;
  }

  .photo-preview {
    position: relative;
    width: 100%;
    max-height: 300px;
    border: 1px dashed #ccc;
    border-radius: 12px;
    overflow: hidden;
  }

  .photo-preview img {
    width: 100%;
    height: auto;
    object-fit: contain;
  }

  .remove-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    width: 30px;
    height: 30px;
    background: rgba(0,0,0,0.5);
    color: white;
    border: none;
    border-radius: 50%;
    font-size: 20px;
    cursor: pointer;
  }

  .analyze-btn {
    width: 100%;
    background-color: #131313;
    color: #FBFBFB;
    border: none;
    border-radius: 12px;
    padding: 16px 0;
    text-align: center;
    cursor: pointer;
  }

  .analyze-btn:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  .page-wrapper {
    width: 92%;
    padding: 20px 0;
    display: flex;
    flex-direction: column;
    gap: 32px;
  }

  header {
    display: flex;
    flex-direction: row;
  }

  h1 {
    width: 87%;
    text-align: center;
  }

  main {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  textarea {
    width: 90%;
    resize: none;
    min-height: 15vh;
    border: none;
    border-radius: 12px;
    padding: 12px 16px;
  }

  h2 {
    font-size: 20px;
  }

  div {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  div div {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .instruction p {
    color: #545454;
  }

  div a {
    display: flex;
    background-color: #131313;
    justify-content: space-between;
    padding: 16px 12px;
    border-radius: 12px;
  }

  div a p {
    width: 100%;
    color: #FBFBFB;
    text-align: center;
  }

  .buttons {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
  }

  .buttons button {
    width: 47%;
    display: flex;
    padding: 8px 12px;
    justify-content: space-between;
    background-color: #FBFBFB;
    border: 1px solid #131313;
    border-radius: 12px;
    background: transparent;
  }
</style>

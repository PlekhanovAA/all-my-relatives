// gallery.js

// galleryOwner и список файлов подставляются Thymeleaf'ом в window.galleryData
const galleryOwner = window.galleryData?.galleryOwner || '';
const photoList = Array.isArray(window.galleryData?.photos) ? window.galleryData.photos : [];
const noPhotosMsg = window.galleryData?.noPhotosMsg || 'Нет фотографий';

// Полные ссылки на изображения строим через galleryOwner
const photoUrls = photoList.map(p => `/uploads/${encodeURIComponent(galleryOwner)}/gallery/${encodeURIComponent(p)}`);

let currentIndex = 0;
const mainPhoto = document.getElementById('mainPhoto');

// Вспомогательно: убрать все рамки-теги (на случай смены фото или пустой галереи)
function removeAllTags() {
  document.querySelectorAll('.tag-box').forEach(el => el.remove());
}

// Отобразить фото по индексу
function showPhoto(index) {
  if (!mainPhoto) return;

  if (photoUrls.length === 0) {
    // Пустая галерея
    mainPhoto.src = '';
    mainPhoto.alt = noPhotosMsg;
    removeAllTags(); // чтобы не оставались рамки от предыдущих фото
    return;
  }

  currentIndex = (index + photoUrls.length) % photoUrls.length;

  const url = photoUrls[currentIndex];
  const filename = photoList[currentIndex];

  // Перед сменой изображения — очистим старые теги
  removeAllTags();

  // Поменяем фото
  mainPhoto.src = url;
  mainPhoto.alt = filename;

  // Подгрузка тегов только если есть функция (скрипт подключён) — т.е. для ADMIN
  if (typeof window.loadTags === 'function') {
    window.loadTags(filename);
  }
}

// Следующее фото
function showNext() {
  showPhoto(currentIndex + 1);
}

// Предыдущее фото
function showPrevious() {
  showPhoto(currentIndex - 1);
}

// При загрузке страницы показываем первую фотку (если есть)
document.addEventListener('DOMContentLoaded', () => {
  showPhoto(0);
});

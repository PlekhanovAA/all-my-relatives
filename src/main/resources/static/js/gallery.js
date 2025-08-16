// gallery.js

// username и список файлов будут подставлены Thymeleaf'ом в window.galleryData
const username = window.galleryData?.username || '';
const photoList = window.galleryData?.photos || [];

// Полные ссылки на изображения
const photoUrls = photoList.map(p => `/uploads/${username}/gallery/${p}`);

let currentIndex = 0;
const mainPhoto = document.getElementById("mainPhoto");

// Отобразить фото по индексу
function showPhoto(index) {
    if (photoUrls.length === 0) {
        if (mainPhoto) {
            mainPhoto.src = "";
            mainPhoto.alt = "Нет фотографий";
        }
        return;
    }
    currentIndex = (index + photoUrls.length) % photoUrls.length;
    if (mainPhoto) {
        mainPhoto.src = photoUrls[currentIndex];
        mainPhoto.alt = photoList[currentIndex];
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
document.addEventListener("DOMContentLoaded", () => {
    showPhoto(0);
});

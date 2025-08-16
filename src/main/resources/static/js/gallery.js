// gallery.js

// galleryOwner и список файлов подставляются Thymeleaf'ом в window.galleryData
const galleryOwner = window.galleryData?.galleryOwner || '';
const photoList = window.galleryData?.photos || [];

// Полные ссылки на изображения строим через galleryOwner
const photoUrls = photoList.map(p => `/uploads/${galleryOwner}/gallery/${p}`);

let currentIndex = 0;
const mainPhoto = document.getElementById("mainPhoto");

// Отобразить фото по индексу
function showPhoto(index) {
    if (photoUrls.length === 0) {
        if (mainPhoto) {
            mainPhoto.src = "";
            mainPhoto.alt = window.galleryData?.noPhotosMsg || "Нет фотографий";
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

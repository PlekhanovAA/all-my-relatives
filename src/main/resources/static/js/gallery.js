// gallery.js

const photoList = Array.isArray(window.galleryData?.photos)
  ? window.galleryData.photos
  : [];

const photoUrls = photoList.map(p => ({
  id: p.id,
  url: `/uploads/${encodeURIComponent(window.galleryData.galleryOwner)}/gallery/${encodeURIComponent(p.filename)}`
}));

let currentIndex = 0;
const mainPhoto = document.getElementById('mainPhoto');

function removeAllTags() {
  document.querySelectorAll('.tag-box').forEach(el => el.remove());
  const list = document.getElementById("relativesList");
  if (list) list.innerHTML = "";
}

function showPhoto(index) {
  if (!mainPhoto) return;

  if (photoList.length === 0) {
    mainPhoto.src = '';
    mainPhoto.alt = window.galleryData?.noPhotosMsg || 'Нет фотографий';
    removeAllTags();
    return;
  }

  currentIndex = (index + photoList.length) % photoList.length;
  const photo = photoList[currentIndex];
  const url = `/uploads/${encodeURIComponent(window.galleryData.galleryOwner)}/gallery/${encodeURIComponent(photo.filename)}`;

  mainPhoto.src = url;
  mainPhoto.alt = photo.filename;
  window.currentPhotoId = photo.id; // 👈 сохраняем id

  if (typeof window.loadTags === 'function') {
    window.loadTags(photo.id); // 👈 грузим теги по id
  }
}

function showNext() {
  showPhoto(currentIndex + 1);
}

function showPrevious() {
  showPhoto(currentIndex - 1);
}

document.addEventListener('DOMContentLoaded', () => {
  showPhoto(0);
});

const toggleBtn = document.getElementById("toggleTagsBtn");
if (toggleBtn) {
  toggleBtn.onclick = () => {
    if (typeof window.toggleTagVisibility === "function") {
      window.toggleTagVisibility();
    }
  };
}

document.addEventListener("DOMContentLoaded", () => {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(el => new bootstrap.Tooltip(el));
});

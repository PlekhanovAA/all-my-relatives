let currentIndex = 0;

function showPhoto(index) {
    if (!photoList || photoList.length === 0) return;

    if (index < 0) index = photoList.length - 1;
    if (index >= photoList.length) index = 0;

    currentIndex = index;
    const filename = photoList[currentIndex];
    const mainPhoto = document.getElementById('mainPhoto');
    mainPhoto.src = `/uploads/${filename}`;
}

function showNext() {
    showPhoto(currentIndex + 1);
}

function showPrevious() {
    showPhoto(currentIndex - 1);
}

document.addEventListener("DOMContentLoaded", () => {
    showPhoto(0);
});

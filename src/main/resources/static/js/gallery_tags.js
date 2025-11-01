// ========== ИНИЦИАЛИЗАЦИЯ ==========
const photoCanvas = document.getElementById("photoCanvas");
let editMode = false;
let selectionBox = null;
let startX, startY;
let tagsVisible = true;

// Глобально доступные переменные
window.currentPhotoId = null;
window.currentRect = null;

// ========== CSRF ==========
function getCsrfHeaders() {
    const csrfHeader = document.querySelector("meta[name='_csrf_header']");
    const csrfToken = document.querySelector("meta[name='_csrf']");

    if (!csrfHeader || !csrfToken) {
        console.warn("CSRF meta not found — request sent without token");
        return {};
    }

    return {
        [csrfHeader.content]: csrfToken.content
    };
}

// ========== МОДАЛ ОТКРЫТИЕ ==========
function openTagModal(photoId, rect) {
    window.currentPhotoId = photoId;
    window.currentRect = rect;
    new bootstrap.Modal(document.getElementById("relativeTagModal")).show();
}
window.openTagModal = openTagModal;

// ========== СОХРАНЕНИЕ ТЕГА ==========
function saveTag() {
    const relativeSelect = document.getElementById("relativeSelect");
    if (!relativeSelect) return;

    const tagDto = {
        relativeId: relativeSelect.value,
        photoId: window.currentPhotoId,
        ...window.currentRect
    };

    fetch("/gallery/tags/save", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            ...getCsrfHeaders()
        },
        body: JSON.stringify(tagDto)
    })
        .then(res => res.json())
        .then(tag => {
            renderTag(tag);
            bootstrap.Modal.getInstance(document.getElementById("relativeTagModal")).hide();
            selectionBox?.remove();
            selectionBox = null;
        })
        .catch(err => console.error("Ошибка сохранения метки:", err));
}
window.saveTag = saveTag;

// ========== РЕНДЕР ТЕГА ==========
function renderTag(tag) {
    const box = document.createElement("div");
    box.classList.add("tag-box");
    box.style.left = tag.x + "px";
    box.style.top = tag.y + "px";
    box.style.width = tag.width + "px";
    box.style.height = tag.height + "px";
    box.title = tag.relativeName;
    box.style.display = tagsVisible ? "block" : "none";
    photoCanvas.appendChild(box);

    const list = document.getElementById("relativesList");
    if (list) {
        const li = document.createElement("li");
        li.textContent = tag.relativeName;
        list.appendChild(li);
    }
}

// ========== ЗАГРУЗКА ТЕГОВ ==========
function loadTags(photoId) {
    if (!photoId) return;

    document.querySelectorAll(".tag-box").forEach(el => el.remove());
    const list = document.getElementById("relativesList");
    if (list) list.innerHTML = "";

    fetch(`/gallery/tags/${photoId}`)
        .then(res => res.json())
        .then(tags => {
            if (!Array.isArray(tags)) return;
            tags.forEach(t => renderTag(t));
        })
        .catch(err => console.error("Ошибка загрузки тегов:", err));
}
window.loadTags = loadTags;

// ========== ПЕРЕКЛЮЧЕНИЕ ВИДИМОСТИ ==========
window.toggleTagVisibility = function() {
    tagsVisible = !tagsVisible;
    document.querySelectorAll(".tag-box").forEach(box => {
        box.style.display = tagsVisible ? "block" : "none";
    });
};

// ========== РЕЖИМ РЕДАКТИРОВАНИЯ ==========
// gallery_tags.js

document.addEventListener("DOMContentLoaded", () => {
    const editBtn = document.getElementById("editTagsBtn");

    if (editBtn) {
        editBtn.onclick = () => {
            editMode = !editMode;

            // 🔥 Активное состояние кнопки (подсветка)
            editBtn.classList.toggle("btn-active", editMode);

            // ✅ Включаем показ тегов, если был выключен
            if (editMode && !tagsVisible) {
                tagsVisible = true;

                const toggleCheckbox = document.getElementById("toggleShowTags");
                if (toggleCheckbox) toggleCheckbox.checked = true;

                document.querySelectorAll(".tag-box").forEach(b => b.style.display = "block");
            }

            // ♻️ При включении режима — перезагружаем текущие теги
            if (editMode && window.currentPhotoId) {
                loadTags(window.currentPhotoId);
            }

            // 🎯 Включаем/выключаем режим выделения рамки
            if (editMode) {
                enableTagDrawing();
            } else {
                disableTagDrawing();
            }
        };
    }

    // ✅ ESC отключает режим и убирает текущий прямоугольник
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && editMode) {
            editMode = false;

            if (editBtn) editBtn.classList.remove("btn-active");

            disableTagDrawing();
            removePreviewBox?.();
        }
    });

    // ✅ Инициализация Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(el => new bootstrap.Tooltip(el));
});


// ========== ВЫДЕЛЕНИЕ НА ФОТО ==========
if (document.body.getAttribute("data-is-admin") === "true" && photoCanvas) {
    photoCanvas.addEventListener("mousedown", (e) => {
        const mainPhoto = document.getElementById("mainPhoto");
        if (!editMode || e.target !== mainPhoto) return;

        const rect = photoCanvas.getBoundingClientRect();
        startX = e.clientX - rect.left;
        startY = e.clientY - rect.top;

        selectionBox = document.createElement("div");
        selectionBox.classList.add("tag-box");
        selectionBox.style.left = `${startX}px`;
        selectionBox.style.top = `${startY}px`;
        photoCanvas.appendChild(selectionBox);

        function onMouseMove(ev) {
            const currentX = ev.offsetX;
            const currentY = ev.offsetY;

            const width = Math.max(0, currentX - startX);
            const height = Math.max(0, currentY - startY);

            selectionBox.style.width = width + "px";
            selectionBox.style.height = height + "px";
        }


        function onMouseUp() {
            document.removeEventListener("mousemove", onMouseMove);
            document.removeEventListener("mouseup", onMouseUp);

            const boxRect = selectionBox.getBoundingClientRect();
            const canvasRect = photoCanvas.getBoundingClientRect();

            openTagModal(window.currentPhotoId, {
                x: parseInt(selectionBox.style.left),
                y: parseInt(selectionBox.style.top),
                width: parseInt(selectionBox.style.width),
                height: parseInt(selectionBox.style.height)
            });
        }

        document.addEventListener("mousemove", onMouseMove);
        document.addEventListener("mouseup", onMouseUp);
    });
}

function cancelTagSelection() {
    if (selectionBox) {
        selectionBox.remove();
        selectionBox = null;
    }
    window.currentRect = null;
}
window.cancelTagSelection = cancelTagSelection;

if (!tagsVisible) {
    window.toggleTagVisibility(); // показать существующие
}
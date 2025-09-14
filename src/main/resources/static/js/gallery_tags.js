// gallery_tags.js

let startX, startY, selectionBox = null;
let tagLayer = null;

const photoCanvas = document.getElementById("photoCanvas");

// режим редактирования
let editMode = false;

// кнопка переключения
const toggleEditBtn = document.getElementById("toggleEditBtn");
if (toggleEditBtn) {
    toggleEditBtn.addEventListener("click", () => {
        editMode = !editMode;

        const span = toggleEditBtn.querySelector("span");
        if (editMode) {
            span.textContent = window.i18n?.galleryEditOn || "Режим редактирования: вкл";
            toggleEditBtn.classList.add("active");
        } else {
            span.textContent = window.i18n?.galleryEditOff || "Режим редактирования: выкл";
            toggleEditBtn.classList.remove("active");
        }
    });
}

// Событие мыши для выделения (только если включён editMode)
if (photoCanvas) {
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
        selectionBox.style.width = "0px";
        selectionBox.style.height = "0px";
        photoCanvas.appendChild(selectionBox);

        function onMouseMove(ev) {
            const currentX = ev.clientX - rect.left;
            const currentY = ev.clientY - rect.top;
            selectionBox.style.width = `${currentX - startX}px`;
            selectionBox.style.height = `${currentY - startY}px`;
        }

        function onMouseUp() {
            document.removeEventListener("mousemove", onMouseMove);
            document.removeEventListener("mouseup", onMouseUp);

            console.log("📌 Выделена область:", selectionBox.getBoundingClientRect());
        }

        document.addEventListener("mousemove", onMouseMove);
        document.addEventListener("mouseup", onMouseUp);
    });
}

// загрузка тегов
function loadTags(filename) {
    if (!filename) return;

    if (tagLayer) tagLayer.remove();
    tagLayer = document.createElement("div");
    tagLayer.style.position = "absolute";
    tagLayer.style.top = "0";
    tagLayer.style.left = "0";
    tagLayer.style.width = "100%";
    tagLayer.style.height = "100%";
    tagLayer.style.pointerEvents = "none";
    tagLayer.id = "tagLayer";

    photoCanvas.appendChild(tagLayer);

    const galleryOwner = window.galleryData?.galleryOwner || '';

    fetch(`/gallery/tags/${filename}?ownerName=${encodeURIComponent(galleryOwner)}`)
        .then(res => res.json())
        .then(tags => {
            if (Array.isArray(tags)) {
                tags.forEach(tag => renderTag(tag));
            }
        })
        .catch(err => console.error("Ошибка загрузки тегов:", err));
}

function renderTag(tag) {
    const box = document.createElement("div");
    box.classList.add("tag-box");
    box.style.left = tag.x + "px";
    box.style.top = tag.y + "px";
    box.style.width = tag.width + "px";
    box.style.height = tag.height + "px";
    box.title = tag.relativeName;
    tagLayer.appendChild(box);
}

window.loadTags = loadTags;

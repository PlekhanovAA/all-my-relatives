// gallery_tags.js

let startX, startY, selectionBox = null;
const photoCanvas = document.getElementById("photoCanvas");

// Только для админа
if (document.body.getAttribute("data-is-admin") === "true") {
    photoCanvas.addEventListener("mousedown", (e) => {
        if (e.target.id !== "mainPhoto") return;

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

            // 🔥 здесь должен быть вызов модалки выбора родственника
            console.log("Выделена область:", selectionBox.getBoundingClientRect());
        }

        document.addEventListener("mousemove", onMouseMove);
        document.addEventListener("mouseup", onMouseUp);
    });
}


let tagLayer = null;
const isAdmin = window.galleryData?.isAdmin || false;

function loadTags(filename) {
    if (!filename) return;

    // Очищаем старые метки
    if (tagLayer) tagLayer.remove();
    tagLayer = document.createElement("div");
    tagLayer.style.position = "absolute";
    tagLayer.style.top = "0";
    tagLayer.style.left = "0";
    tagLayer.style.width = "100%";
    tagLayer.style.height = "100%";
    tagLayer.style.pointerEvents = "none"; // клики проходят сквозь
    tagLayer.id = "tagLayer";

    const canvas = document.getElementById("photoCanvas");
    canvas.appendChild(tagLayer);

    const galleryOwner = window.galleryData?.galleryOwner || '';

    // Загружаем метки с сервера
    fetch(`/gallery/tags/${filename}?ownerName=${encodeURIComponent(galleryOwner)}`)
        .then(res => {
            if (!res.ok) throw new Error("Ошибка сети");
            return res.json();
        })
        .then(tags => {
            if (!Array.isArray(tags)) {
                console.warn("Ответ не массив:", tags);
                return;
            }

            tags.forEach(tag => renderTag(tag));
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
    box.style.position = "absolute";
    box.style.border = "2px solid red";
    box.style.background = "rgba(255,0,0,0.2)";
    box.style.cursor = "pointer";
    box.title = tag.relativeName;

    tagLayer.appendChild(box);
}

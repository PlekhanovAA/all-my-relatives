let editMode = false;
let selectionBox = null;
let startX, startY;
const photoCanvas = document.getElementById("photoCanvas");

function openTagModal(photoId, rect) {
    window.currentPhotoId = photoId;
    window.currentRect = rect;
    const modal = new bootstrap.Modal(document.getElementById("relativeTagModal"));
    modal.show();
}

function saveTag() {
    const relativeSelect = document.getElementById("relativeSelect");
    if (!relativeSelect) return;

    const relativeId = relativeSelect.value;
    const photoId = window.currentPhotoId; // 👈 используем id, а не filename
    const rect = window.currentRect;

    const tagDto = { relativeId, photoId, ...rect };

    fetch("/gallery/tags/save", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [document.querySelector("meta[name='_csrf_header']").content]:
                document.querySelector("meta[name='_csrf']").content
        },
        body: JSON.stringify(tagDto)
    })
        .then(res => res.json())
        .then(tag => {
            renderTag(tag);
            bootstrap.Modal.getInstance(document.getElementById("relativeTagModal")).hide();
        })
        .catch(err => console.error("Ошибка сохранения метки:", err));
}

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
            const currentX = ev.clientX - rect.left;
            const currentY = ev.clientY - rect.top;
            selectionBox.style.width = `${currentX - startX}px`;
            selectionBox.style.height = `${currentY - startY}px`;
        }

        function onMouseUp() {
            document.removeEventListener("mousemove", onMouseMove);
            document.removeEventListener("mouseup", onMouseUp);

            const boxRect = selectionBox.getBoundingClientRect();
            const canvasRect = photoCanvas.getBoundingClientRect();
            const photoId = window.currentPhotoId;

            openTagModal(photoId, {
                left: boxRect.left - canvasRect.left,
                top: boxRect.top - canvasRect.top,
                width: boxRect.width,
                height: boxRect.height
            });
        }

        document.addEventListener("mousemove", onMouseMove);
        document.addEventListener("mouseup", onMouseUp);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    const toggleBtn = document.getElementById("toggleEditMode");
    if (toggleBtn) {
        toggleBtn.addEventListener("click", () => {
            editMode = !editMode;
            toggleBtn.classList.toggle("btn-danger", editMode);
            toggleBtn.classList.toggle("btn-warning", !editMode);
            toggleBtn.innerHTML = editMode
                ? "✅ Режим редактирования"
                : "✏️ Режим редактирования";
        });
    }
});

function renderTag(tag) {
    const box = document.createElement("div");
    box.classList.add("tag-box");
    box.style.left = tag.x + "px";
    box.style.top = tag.y + "px";
    box.style.width = tag.width + "px";
    box.style.height = tag.height + "px";
    box.title = tag.relativeName;
    photoCanvas.appendChild(box);

    const list = document.getElementById("relativesList");
    if (list) {
        const li = document.createElement("li");
        li.textContent = tag.relativeName;
        list.appendChild(li);
    }
}

function loadTags(photoId) {
    if (!photoId) return;
    console.log("➡️ loadTags: запрашиваю /gallery/tags/" + photoId);
    fetch(`/gallery/tags/${photoId}`)
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

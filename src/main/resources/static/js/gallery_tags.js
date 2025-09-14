let editMode = false; // 🔥 по умолчанию режим просмотра
let selectionBox = null;
let startX, startY;
const photoCanvas = document.getElementById("photoCanvas");

// 📌 показать модалку после выделения
function openTagModal(filename, rect) {
    // сохраняем текущий filename, чтобы потом использовать
    window.currentFilename = filename;

    // сохраняем координаты выделенной области (относительно контейнера)
    window.currentRect = rect;

    const modal = new bootstrap.Modal(document.getElementById("relativeTagModal"));
    modal.show();
}

// 📌 сохранить метку
function saveTag() {
    const relativeSelect = document.getElementById("relativeSelect");
    if (!relativeSelect) {
        console.error("⚠️ Не найден select с родственниками!");
        return;
    }

    const relativeId = relativeSelect.value;
    const filename = window.currentFilename;
    const rect = window.currentRect;

    const tagDto = {
        relativeId,
        filename,
        x: rect.left,
        y: rect.top,
        width: rect.width,
        height: rect.height
    };

    const token = document.querySelector("meta[name='_csrf']").content;
    const header = document.querySelector("meta[name='_csrf_header']").content;

    fetch("/gallery/tags/save", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [header]: token
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

// 📌 выделение области (если админ и editMode включён)
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
            const filename = window.galleryData.photos[window.currentIndex];

            openTagModal(filename, {
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

// 📌 переключатель режима редактирования
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

// 📌 отрисовка метки (для уже сохранённых)
function renderTag(tag) {
    const box = document.createElement("div");
    box.classList.add("tag-box");
    box.style.left = tag.x + "px";
    box.style.top = tag.y + "px";
    box.style.width = tag.width + "px";
    box.style.height = tag.height + "px";
    box.title = tag.relativeName;

    document.getElementById("photoCanvas").appendChild(box);
}

let deleteTargetRow = null;
let deleteTargetFilename = null;

function confirmDeleteFromButton(buttonElement) {
    deleteTargetRow = buttonElement.closest("tr");
    deleteTargetFilename = buttonElement.getAttribute('data-filename');

    const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
    modal.show();
}

function confirmDelete(filename) {
    // Установим имя файла в скрытое поле
    const input = document.getElementById('modalFilename');
    if (input) input.value = filename;

    // Отправим AJAX-запрос вручную
    sendDeleteRequest(filename);
}

function sendDeleteRequest(filename) {
    const tokenElement = document.querySelector("#deleteForm input[name='_csrf']");
    const csrfToken = tokenElement?.value;
    const url = "/gallery/delete";

    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "X-CSRF-TOKEN": csrfToken
        },
        body: new URLSearchParams({ filename })
    })
        .then(res => {
            if (!res.ok) throw new Error("Ошибка удаления");
            // Удалим строку из таблицы
            if (deleteTargetRow) {
                deleteTargetRow.remove();
            }
            bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
            showToast("Фото удалено");
        })
        .catch(err => {
            console.error(err);
            showToast("Ошибка при удалении");
        });
}

function showToast(message) {
    const toastContainer = document.getElementById('toastContainer');
    toastContainer.querySelector('.toast-body').textContent = message;
    const toast = new bootstrap.Toast(toastContainer);
    toast.show();
}

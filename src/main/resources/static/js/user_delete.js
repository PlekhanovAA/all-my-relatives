let deleteTargetUsername = null;

function confirmDeleteUser(button) {
    deleteTargetUsername = button.getAttribute("data-username");
    document.getElementById("modalUsername").value = deleteTargetUsername;
    const deleteModal = new bootstrap.Modal(document.getElementById("deleteUserModal"));
    deleteModal.show();
}

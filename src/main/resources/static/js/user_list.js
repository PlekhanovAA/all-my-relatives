function confirmUserDelete(button) {
    const username = button.getAttribute("data-username");
    document.getElementById("modalUsername").value = username;
    const modal = new bootstrap.Modal(document.getElementById("deleteUserModal"));
    modal.show();
}

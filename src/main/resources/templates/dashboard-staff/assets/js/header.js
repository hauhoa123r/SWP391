document.addEventListener('DOMContentLoaded', function() {
    const avatarDropdown = document.getElementById('avatarDropdown');
    const dropdownMenu = document.getElementById('dropdownMenu');

    avatarDropdown.addEventListener('click', function(e) {
        e.stopPropagation();
        dropdownMenu.classList.toggle('show');
    });

    document.addEventListener('click', function(e) {
        if (!avatarDropdown.contains(e.target)) {
            dropdownMenu.classList.remove('show');
        }
    });

    const logoutBtn = document.getElementById('logoutBtn');
    logoutBtn.addEventListener('click', function() {
        // Thực hiện đăng xuất
        window.location.href = '/logout';
    });
});
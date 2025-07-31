document.addEventListener('DOMContentLoaded', function() {
    // Highlight active menu item
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            navLinks.forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });

    // Responsive sidebar toggle for mobile
    const sidebar = document.getElementById('sidebar');
    if (window.innerWidth < 992) {
        const mobileMenu = document.querySelector('.mobile-menu');
        if (mobileMenu) {
            mobileMenu.addEventListener('click', function() {
                sidebar.classList.toggle('active');
            });
        }
    }
});
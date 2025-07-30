document.addEventListener('DOMContentLoaded', function() {
    // Handle wishlist button clicks
    const wishlistButtons = document.querySelectorAll('.wishlist-btn');
    
    wishlistButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Get product ID from data attribute
            const productId = this.getAttribute('data-id');
            if (!productId) {
                console.error('No product ID found on wishlist button');
                return;
            }
            
            // Set up the form data for the AJAX request
            const formData = new FormData();
            formData.append('productId', productId);
            
            // Send AJAX request to add to wishlist
            fetch('/wishlist/add', {
                method: 'POST',
                body: formData,
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'same-origin'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                // Show success message
                Swal.fire({
                    title: 'Thành công!',
                    text: 'Đã thêm sản phẩm vào danh sách yêu thích',
                    icon: 'success',
                    confirmButtonText: 'Tiếp tục mua sắm'
                });
                
                // Visual feedback - change button appearance
                button.classList.add('active');
            })
            .catch(error => {
                console.error('Error adding to wishlist:', error);
                Swal.fire({
                    title: 'Lỗi!',
                    text: 'Không thể thêm vào danh sách yêu thích. Vui lòng thử lại.',
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            });
        });
    });
    
    // Handle wishlist removal if we're on the wishlist page
    const removeButtons = document.querySelectorAll('.remove-from-wishlist');
    
    removeButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Get product ID from data attribute
            const productId = this.getAttribute('data-id');
            if (!productId) {
                console.error('No product ID found on remove button');
                return;
            }
            
            // Set up the form data for the AJAX request
            const formData = new FormData();
            formData.append('productId', productId);
            
            // Send AJAX request to remove from wishlist
            fetch('/wishlist/remove', {
                method: 'POST',
                body: formData,
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'same-origin'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                // Remove the item from the DOM if we're on the wishlist page
                const itemElement = button.closest('.wishlist-item');
                if (itemElement) {
                    itemElement.remove();
                }
                
                // Show success message
                Swal.fire({
                    title: 'Thành công!',
                    text: 'Đã xóa sản phẩm khỏi danh sách yêu thích',
                    icon: 'success',
                    confirmButtonText: 'OK'
                });
            })
            .catch(error => {
                console.error('Error removing from wishlist:', error);
                Swal.fire({
                    title: 'Lỗi!',
                    text: 'Không thể xóa khỏi danh sách yêu thích. Vui lòng thử lại.',
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            });
        });
    });
}); 
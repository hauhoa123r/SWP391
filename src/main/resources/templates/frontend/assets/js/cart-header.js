$(document).ready(function() {
    console.log('Cart header JavaScript loaded');
    
    // Add CSS for disabled cart dropdown
    if (!$('#cart-header-styles').length) {
        $('<style id="cart-header-styles">')
            .text(`
                #cart-drop.disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                    pointer-events: none;
                }
                .iq-button.disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                    pointer-events: none;
                }
            `)
            .appendTo('head');
    }
    
    // Initialize cart total on page load
    initializeCartTotal();
    
    // Handle cart item deletion from header
    $(document).on("click", ".delete-btn", function(e) {
        e.preventDefault();
        
        const __this = $(this);
        const productId = __this.data('product-id');
        const form = __this.closest('.delete-cart-form');
        
        console.log('Delete button clicked for product ID:', productId);
        
        Swal.fire({
            title: 'Bạn có chắc chắn?',
            text: "Bạn muốn xóa sản phẩm này khỏi giỏ hàng?",
            icon: 'warning',
            showCancelButton: true,
            backdrop: `rgba(60,60,60,0.8)`,
            confirmButtonText: 'Có, xóa nó!',
            cancelButtonText: 'Hủy',
            confirmButtonColor: "#c03221"
        }).then((result) => {
            if (result.isConfirmed) {
                // Show loading state
                __this.prop('disabled', true);
                
                // Make AJAX call to delete item
                $.ajax({
                    url: form.attr('action'),
                    method: 'POST',
                    data: {
                        productId: productId
                    },
                    success: function(response) {
                        console.log('AJAX response:', response);
                        
                        if (response.success) {
                            // Remove the cart item from view
                            __this.closest('[data-item="list"]').remove();
                            
                            // Update cart total in header
                            updateCartTotal(response.newTotal);
                            
                            // Update cart item count
                            updateCartItemCount(response.cartItemCount);
                            
                            // Show success message
                            Swal.fire(
                                'Đã xóa!',
                                'Sản phẩm đã được xóa khỏi giỏ hàng.',
                                'success'
                            );
                        } else {
                            Swal.fire(
                                'Lỗi!',
                                response.message || 'Có lỗi xảy ra khi xóa sản phẩm.',
                                'error'
                            );
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error('Error deleting cart item:', error);
                        Swal.fire(
                            'Lỗi!',
                            'Có lỗi xảy ra khi xóa sản phẩm.',
                            'error'
                        );
                    },
                    complete: function() {
                        // Re-enable button
                        __this.prop('disabled', false);
                    }
                });
            }
        });
    });
    
    // Function to initialize cart total on page load
    function initializeCartTotal() {
        const cartTotalSpan = $('#header-cart-total');
        if (cartTotalSpan.length > 0) {
            // Get the initial value from Thymeleaf
            const initialValue = cartTotalSpan.text();
            console.log('Initial cart total from Thymeleaf:', initialValue);
            
            // If the value is not properly formatted, we can fetch it from server
            if (!initialValue.includes('₫') || initialValue.includes('0 ₫')) {
                // Optionally fetch the current cart total from server
                fetchCartTotalFromServer();
            }
        }
    }
    
    // Function to fetch cart total from server
    function fetchCartTotalFromServer() {
        $.ajax({
            url: '/cart/total-ajax',
            method: 'GET',
            success: function(response) {
                if (response.success) {
                    updateCartTotal(response.total);
                    updateCartItemCount(response.itemCount);
                }
            },
            error: function(xhr, status, error) {
                console.error('Error fetching cart total:', error);
            }
        });
    }
    
    // Function to update cart total in header
    function updateCartTotal(newTotal) {
        console.log('Updating cart total to:', newTotal);
        
        // Format the total with Vietnamese currency format
        const formattedTotal = new Intl.NumberFormat('vi-VN').format(newTotal) + ' ₫';
        
        // Update the cart total using the specific ID
        const cartTotalSpan = $('#header-cart-total');
        if (cartTotalSpan.length > 0) {
            cartTotalSpan.text(formattedTotal);
            console.log('Successfully updated cart total to:', formattedTotal);
        } else {
            console.error('Could not find header-cart-total element');
            
            // Fallback: Try multiple selectors to find the subtotal element
            let subtotalSpan = null;
            
            // Method 1: Look in the cart dropdown
            subtotalSpan = $('.sub-drop .card-footer .d-flex.align-items-center.justify-content-between span:last');
            
            // Method 2: If not found, try broader selector
            if (subtotalSpan.length === 0) {
                subtotalSpan = $('.card-footer .d-flex.align-items-center.justify-content-between span:last');
            }
            
            // Method 3: If still not found, try finding by text content
            if (subtotalSpan.length === 0) {
                subtotalSpan = $('span').filter(function() {
                    return $(this).text().includes('₫');
                }).last();
            }
            
            if (subtotalSpan.length > 0) {
                subtotalSpan.text(formattedTotal);
                console.log('Successfully updated cart total using fallback to:', formattedTotal);
            } else {
                console.error('Could not find subtotal span element. Available spans:', $('span').length);
                // Log all spans for debugging
                $('span').each(function(index) {
                    if ($(this).text().includes('₫')) {
                        console.log('Found span with ₫ at index', index, ':', $(this).text());
                    }
                });
            }
        }
        
        // Handle empty cart state
        if (newTotal === 0) {
            const cartBody = $('.sub-drop .card-body');
            if (cartBody.length > 0 && cartBody.find('[data-item="list"]').length === 0) {
                cartBody.html('<div class="text-center p-3"><p class="text-muted">Giỏ hàng trống</p></div>');
            }
            
            // Disable checkout buttons when cart is empty
            const checkoutBtn = $('.sub-drop .iq-button[href*="checkout"]');
            const viewCartBtn = $('.sub-drop .iq-button[href*="cart"]');
            
            if (checkoutBtn.length > 0) {
                checkoutBtn.addClass('disabled').attr('href', 'javascript:void(0)');
                checkoutBtn.find('.iq-btn-text-holder').text('Giỏ hàng trống');
            }
            
            if (viewCartBtn.length > 0) {
                viewCartBtn.addClass('disabled').attr('href', 'javascript:void(0)');
                viewCartBtn.find('.iq-btn-text-holder').text('Giỏ hàng trống');
            }
        } else {
            // Re-enable checkout buttons when cart has items
            const checkoutBtn = $('.sub-drop .iq-button[href*="checkout"]');
            const viewCartBtn = $('.sub-drop .iq-button[href*="cart"]');
            
            if (checkoutBtn.length > 0) {
                checkoutBtn.removeClass('disabled').attr('href', '/checkout');
                checkoutBtn.find('.iq-btn-text-holder').text('Thanh toán');
                // Remove any price text that might be added
                checkoutBtn.find('span').not('.iq-btn-text-holder, .iq-btn-icon-holder').remove();
            }
            
            if (viewCartBtn.length > 0) {
                viewCartBtn.removeClass('disabled').attr('href', '/cart');
                viewCartBtn.find('.iq-btn-text-holder').text('Xem giỏ hàng');
            }
        }
    }
    
    // Function to update cart item count in header
    function updateCartItemCount(count) {
        console.log('Updating cart item count to:', count);
        
        // Update the badge count
        const badge = $('.badge-notification');
        if (badge.length > 0) {
            badge.text(count);
            
            // If count is 0, hide the badge and disable cart dropdown
            if (count === 0) {
                badge.hide();
                
                // Disable cart dropdown when empty
                const cartDropdown = $('#cart-drop');
                if (cartDropdown.length > 0) {
                    cartDropdown.addClass('disabled');
                    cartDropdown.attr('data-bs-toggle', '');
                    cartDropdown.attr('title', 'Giỏ hàng trống');
                }
            } else {
                badge.show();
                
                // Re-enable cart dropdown when has items
                const cartDropdown = $('#cart-drop');
                if (cartDropdown.length > 0) {
                    cartDropdown.removeClass('disabled');
                    cartDropdown.attr('data-bs-toggle', 'dropdown');
                    cartDropdown.removeAttr('title');
                }
            }
            console.log('Successfully updated cart item count to:', count);
        } else {
            console.error('Could not find badge notification element');
        }
    }
}); 
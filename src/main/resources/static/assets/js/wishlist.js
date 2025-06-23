document.addEventListener('DOMContentLoaded', () => {
  const tokenMeta = document.querySelector('meta[name="_csrf"]');
  const headerMeta = document.querySelector('meta[name="_csrf_header"]');
  const csrfToken = tokenMeta ? tokenMeta.getAttribute('content') : null;
  const csrfHeader = headerMeta ? headerMeta.getAttribute('content') : 'X-CSRF-TOKEN';

  function sendPost(url, body) {
    return fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        [csrfHeader]: csrfToken || ''
      },
      body: new URLSearchParams(body)
    }).then(r => {
      if (!r.ok) throw new Error('Network response was not ok');
      return r.text();
    });
  }

  document.querySelectorAll('.wishlist-btn').forEach(btn => {
    const productId = btn.dataset.id;
    if (!productId) return;

    btn.addEventListener('click', (e) => {
      e.preventDefault();
      sendPost('/wishlist/add', { productId })
        .then(() => {
          btn.classList.toggle('active');
        })
        .catch(err => console.error('Wishlist error', err));
    });
  });
});

package org.project.admin.service.restore;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.admin.entity.Product;
import org.project.admin.repository.ProductRepository;
import org.project.admin.util.RestoreService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductRestoreService implements RestoreService<Product> {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void restoreById(Long id) {
        Product product = productRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Product"));
        product.setDeleted(false);
        productRepository.save(product);
    }
}


package org.project.service;

import java.util.List;

import org.project.entity.ProductEntity;
import org.project.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<ProductEntity> getAllProducts() {
		return productRepository.findAll();
	}

	public ProductEntity findById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
	}

	public ProductEntity getProduct(Long id) {
		return productRepository.findById(id).orElseThrow();
	}

	public void sellProduct(Long id, int quantity) {
		ProductEntity product = getProduct(id);
		if (product.getStockQuantities() < quantity) {
			throw new IllegalArgumentException("Insufficient stock");
		}
		product.setStockQuantities(product.getStockQuantities() - quantity);
		productRepository.save(product);
	}

	public void placeOrder(Long id, int quantity) {
		ProductEntity product = getProduct(id);
		product.setStockQuantities(product.getStockQuantities() + quantity);
		productRepository.save(product);
	}
}

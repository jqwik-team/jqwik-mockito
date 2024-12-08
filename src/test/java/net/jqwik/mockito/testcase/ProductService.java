/*
 * Copyright (c) 2024 Atlassian US, Inc
 */
package net.jqwik.mockito.testcase;

public class ProductService {
	private final ProductRepository productRepository;
	private final LoggingService loggingService;

	public ProductService(ProductRepository productRepository, LoggingService loggingService) {
		this.productRepository = productRepository;
		this.loggingService = loggingService;
	}

	public Product getProductById(final int productId) {
		final Product product = productRepository.getById(productId);
		loggingService.log(String.format("Retrieved product for ID %s: %s", productId, product));
		return product;
	}
}

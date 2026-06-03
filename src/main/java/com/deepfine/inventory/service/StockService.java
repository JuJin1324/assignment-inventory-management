package com.deepfine.inventory.service;

import com.deepfine.inventory.domain.ProductRepository;
import com.deepfine.inventory.service.dto.StockResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StockService {

	private final ProductRepository productRepository;

	public StockResult query(String productId) {
		return productRepository.findByProductId(productId)
				.map(p -> new StockResult(p.getQuantity()))
				.orElseThrow(() -> new ProductNotFoundException(productId));
	}
}

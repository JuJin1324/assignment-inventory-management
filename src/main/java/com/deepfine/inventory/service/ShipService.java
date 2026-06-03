package com.deepfine.inventory.service;

import com.deepfine.inventory.domain.ShipmentResult;
import com.deepfine.inventory.domain.Product;
import com.deepfine.inventory.domain.ProductRepository;
import com.deepfine.inventory.service.dto.ShipCommand;
import com.deepfine.inventory.service.dto.ShipResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 출고 오케스트레이션 — productId로 재고를 조회해 도메인 {@code ship}을 태우고,
 * 성공이면 저장한다. 조회부터 저장까지가 한 트랜잭션 안에서 돈다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ShipService {

	private final ProductRepository productRepository;

	public ShipResult ship(ShipCommand command) {
		Product product = productRepository.findByProductId(command.productId())
				.orElseThrow(() -> new ProductNotFoundException(command.productId()));

		ShipmentResult outcome = product.ship(command.quantity());
		if (outcome == ShipmentResult.SUCCESS) {
			productRepository.save(product);
		}
		return new ShipResult(outcome, product.getQuantity());
	}
}

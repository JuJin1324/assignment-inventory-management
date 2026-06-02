package com.deepfine.inventory.service;

import com.deepfine.inventory.domain.ShipmentResult;
import com.deepfine.inventory.domain.Stock;
import com.deepfine.inventory.domain.StockRepository;
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

	private final StockRepository stockRepository;

	public ShipResult ship(ShipCommand command) {
		Stock stock = stockRepository.findByProductId(command.productId())
				.orElseThrow(() -> new StockNotFoundException(command.productId()));

		ShipmentResult outcome = stock.ship(command.quantity());
		if (outcome == ShipmentResult.SUCCESS) {
			stockRepository.save(stock);
		}
		return new ShipResult(outcome, stock.getQuantity());
	}
}

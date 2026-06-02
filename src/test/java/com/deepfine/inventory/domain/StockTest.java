package com.deepfine.inventory.domain;

import static com.deepfine.inventory.domain.StockFixture.aStock;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StockTest {

	@Test
	@DisplayName("재고가 충분하면 출고 수량만큼 차감되고 남은 재고가 확인된다")
	void ship_decrements_stock_when_sufficient() {
		// given
		Stock stock = aStock().quantity(100).build();

		// when
		stock.ship(30);

		// then
		assertThat(stock.getQuantity()).isEqualTo(70);
	}
}

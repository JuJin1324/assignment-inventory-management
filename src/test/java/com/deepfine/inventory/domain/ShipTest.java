package com.deepfine.inventory.domain;

import static com.deepfine.inventory.domain.ShipmentResult.INSUFFICIENT;
import static com.deepfine.inventory.domain.ShipmentResult.SUCCESS;
import static com.deepfine.inventory.domain.StockFixture.aStock;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShipTest {

	@Test
	@DisplayName("재고가 충분하면 출고 수량만큼 차감되고 SUCCESS를 돌려준다")
	void ship_decrements_stock_when_sufficient() {
		// given
		Stock stock = aStock().quantity(100).build();

		// when
		ShipmentResult result = stock.ship(30);

		// then
		assertThat(result).isEqualTo(SUCCESS);
		assertThat(stock.getQuantity()).isEqualTo(70);
	}

	@Test
	@DisplayName("재고가 부족하면 출고를 거부하고 재고를 건드리지 않는다")
	void ship_rejects_and_keeps_stock_when_insufficient() {
		// given
		Stock stock = aStock().quantity(100).build();

		// when
		ShipmentResult result = stock.ship(120);

		// then
		assertThat(result).isEqualTo(INSUFFICIENT);
		assertThat(stock.getQuantity()).isEqualTo(100);
	}
}

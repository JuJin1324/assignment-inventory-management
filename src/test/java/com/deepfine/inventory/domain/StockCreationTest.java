package com.deepfine.inventory.domain;

import static com.deepfine.inventory.StockFixtures.aStock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StockCreationTest {

	@Test
	@DisplayName("음수 수량 → 생성 거부")
	void rejects_negative_quantity() {
		// when / then
		assertThatThrownBy(() -> aStock().negativeQuantity().build())
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("0 수량 → 생성 허용")
	void allows_zero_quantity() {
		// when
		Stock stock = aStock().zeroQuantity().build();

		// then
		assertThat(stock.getQuantity()).isZero();
	}
}

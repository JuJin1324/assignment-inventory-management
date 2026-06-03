package com.deepfine.inventory.domain;

import static com.deepfine.inventory.ProductFixtures.aProduct;
import static com.deepfine.inventory.domain.ShipmentResult.INSUFFICIENT;
import static com.deepfine.inventory.domain.ShipmentResult.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShipTest {

	@Test
	@DisplayName("재고 충분 → 차감 후 SUCCESS")
	void ship_decrements_stock_when_sufficient() {
		// given
		Product product = aProduct().quantity(100).build();

		// when
		ShipmentResult result = product.ship(30);

		// then
		assertThat(result).isEqualTo(SUCCESS);
		assertThat(product.getQuantity()).isEqualTo(70);
	}

	@Test
	@DisplayName("재고 부족 → 거부하고 재고 유지")
	void ship_rejects_and_keeps_stock_when_insufficient() {
		// given
		Product product = aProduct().quantity(100).build();

		// when
		ShipmentResult result = product.ship(120);

		// then
		assertThat(result).isEqualTo(INSUFFICIENT);
		assertThat(product.getQuantity()).isEqualTo(100);
	}

	@Test
	@DisplayName("같은 수량 출고 → 0까지 차감")
	void ship_decrements_to_zero_when_equal_to_stock() {
		// given
		Product product = aProduct().quantity(100).build();

		// when
		ShipmentResult result = product.ship(100);

		// then
		assertThat(result).isEqualTo(SUCCESS);
		assertThat(product.getQuantity()).isZero();
	}

	@Test
	@DisplayName("출고 수량 0·음수 → 예외")
	void ship_rejects_non_positive_quantity() {
		// given
		Product product = aProduct().quantity(100).build();

		// when / then
		assertThatThrownBy(() -> product.ship(0)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> product.ship(-1)).isInstanceOf(IllegalArgumentException.class);
		assertThat(product.getQuantity()).isEqualTo(100);
	}
}

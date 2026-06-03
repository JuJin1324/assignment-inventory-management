package com.deepfine.inventory.domain;

import static com.deepfine.inventory.ProductFixtures.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductCreationTest {

	@Test
	@DisplayName("음수 수량 → 생성 거부")
	void rejects_negative_quantity() {
		// when / then
		assertThatThrownBy(() -> aProduct().negativeQuantity().build())
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("0 수량 → 생성 허용")
	void allows_zero_quantity() {
		// when
		Product product = aProduct().zeroQuantity().build();

		// then
		assertThat(product.getQuantity()).isZero();
	}
}

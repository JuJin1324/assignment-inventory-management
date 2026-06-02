package com.deepfine.inventory.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 재고 테스트 데이터를 공급한다 (ADR-003 — Test Data Builder). 테스트는 관심 있는
 * 필드만 지정하고 나머지는 기본값으로 둔다 — 예: {@code aStock().quantity(100).build()}.
 *
 * <p>생성은 {@code aStock()}으로만 — 기본 생성자를 private으로 막아 진입점을 하나로 둔다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class StockFixture {

	private String productId = "PROD-001";
	private int quantity = 100;

	static StockFixture aStock() {
		return new StockFixture();
	}

	StockFixture productId(String productId) {
		this.productId = productId;
		return this;
	}

	StockFixture quantity(int quantity) {
		this.quantity = quantity;
		return this;
	}

	Stock build() {
		return new Stock(productId, quantity);
	}
}

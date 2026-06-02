package com.deepfine.inventory;

import com.deepfine.inventory.domain.Stock;
import com.deepfine.inventory.service.ShipCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 재고·출고 테스트 데이터 공급 (ADR-003 — Test Data Builder). 타입별 빌더를 내장으로 묶고,
 * 두 빌더가 공유하는 기본값은 바깥 상수로 올려 한 곳에서 관리한다. 같은 productId를 공유해야
 * {@code aShipCommand()}가 {@code aStock()}이 만든 재고를 가리킨다.
 *
 * <p>예: {@code aStock().quantity(100).build()}, {@code aShipCommand().quantity(120).build()}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockFixtures {

	public static final String DEFAULT_PRODUCT_ID = "PROD-001";
	private static final int DEFAULT_STOCK_QUANTITY = 100;
	private static final int DEFAULT_SHIP_QUANTITY = 30;

	public static StockTestBuilder aStock() {
		return new StockTestBuilder();
	}

	public static ShipCommandTestBuilder aShipCommand() {
		return new ShipCommandTestBuilder();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class StockTestBuilder {

		private String productId = DEFAULT_PRODUCT_ID;
		private int quantity = DEFAULT_STOCK_QUANTITY;

		public StockTestBuilder quantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public StockTestBuilder negativeQuantity() {
			this.quantity = -1;
			return this;
		}

		public StockTestBuilder zeroQuantity() {
			this.quantity = 0;
			return this;
		}

		public Stock build() {
			return new Stock(productId, quantity);
		}
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ShipCommandTestBuilder {

		private String productId = DEFAULT_PRODUCT_ID;
		private int quantity = DEFAULT_SHIP_QUANTITY;

		public ShipCommandTestBuilder notFoundProductId() {
			this.productId = "NO-SUCH-PRODUCT";
			return this;
		}

		public ShipCommandTestBuilder quantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public ShipCommand build() {
			return new ShipCommand(productId, quantity);
		}
	}
}

package com.deepfine.inventory;

import com.deepfine.inventory.domain.Product;
import com.deepfine.inventory.service.dto.ReceiveCommand;
import com.deepfine.inventory.service.dto.ShipCommand;
import com.deepfine.inventory.web.dto.ReceiveRequest;
import com.deepfine.inventory.web.dto.ShipmentRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 재고·출고 테스트 데이터 공급 (ADR-003 — Test Data Builder). 타입별 빌더를 내장으로 묶고,
 * 두 빌더가 공유하는 기본값은 바깥 상수로 올려 한 곳에서 관리한다. 같은 productId를 공유해야
 * {@code aShipCommand()}가 {@code aProduct()}이 만든 재고를 가리킨다.
 *
 * <p>예: {@code aProduct().quantity(100).build()}, {@code aShipCommand().quantity(120).build()}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductFixtures {

	public static final String DEFAULT_PRODUCT_ID = "PROD-001";
	public static final String NOT_FOUND_PRODUCT_ID = "NO-SUCH-PRODUCT";
	private static final String DEFAULT_PRODUCT_NAME = "상품 A";
	private static final int DEFAULT_PRODUCT_QUANTITY = 100;
	private static final int DEFAULT_SHIP_QUANTITY = 30;

	public static ProductTestBuilder aProduct() {
		return new ProductTestBuilder();
	}

	public static ShipCommandTestBuilder aShipCommand() {
		return new ShipCommandTestBuilder();
	}

	public static ReceiveCommandTestBuilder aReceiveCommand() {
		return new ReceiveCommandTestBuilder();
	}

	public static ReceiveRequestTestBuilder aReceiveRequest() {
		return new ReceiveRequestTestBuilder();
	}

	public static ShipmentRequestTestBuilder aShipmentRequest() {
		return new ShipmentRequestTestBuilder();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ProductTestBuilder {

		private String productId = DEFAULT_PRODUCT_ID;
		private String name = DEFAULT_PRODUCT_NAME;
		private int quantity = DEFAULT_PRODUCT_QUANTITY;

		public ProductTestBuilder quantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public ProductTestBuilder name(String name) {
			this.name = name;
			return this;
		}

		public ProductTestBuilder negativeQuantity() {
			this.quantity = -1;
			return this;
		}

		public ProductTestBuilder zeroQuantity() {
			this.quantity = 0;
			return this;
		}

		public Product build() {
			return new Product(productId, name, quantity);
		}
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ReceiveCommandTestBuilder {

		private String productId = DEFAULT_PRODUCT_ID;
		private String name = DEFAULT_PRODUCT_NAME;
		private int quantity = 30;

		public ReceiveCommandTestBuilder newProductId() {
			this.productId = "NEW-PROD-001";
			return this;
		}

		public ReceiveCommandTestBuilder quantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public ReceiveCommand build() {
			return new ReceiveCommand(productId, name, quantity);
		}
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ShipCommandTestBuilder {

		private String productId = DEFAULT_PRODUCT_ID;
		private int quantity = DEFAULT_SHIP_QUANTITY;

		public ShipCommandTestBuilder notFoundProductId() {
			this.productId = NOT_FOUND_PRODUCT_ID;
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

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ReceiveRequestTestBuilder {

		private String productId = DEFAULT_PRODUCT_ID;
		private String name = DEFAULT_PRODUCT_NAME;
		private int quantity = 30;

		public ReceiveRequestTestBuilder newProductId() {
			this.productId = "NEW-PROD-001";
			return this;
		}

		public ReceiveRequestTestBuilder quantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public ReceiveRequestTestBuilder zeroQuantity() {
			this.quantity = 0;
			return this;
		}

		public ReceiveRequestTestBuilder blankName() {
			this.name = "";
			return this;
		}

		public ReceiveRequestTestBuilder blankProductId() {
			this.productId = "";
			return this;
		}

		public ReceiveRequest build() {
			return new ReceiveRequest(productId, name, quantity);
		}
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ShipmentRequestTestBuilder {

		private String productId = DEFAULT_PRODUCT_ID;
		private int quantity = DEFAULT_SHIP_QUANTITY;

		public ShipmentRequestTestBuilder quantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public ShipmentRequestTestBuilder notFoundProductId() {
			this.productId = NOT_FOUND_PRODUCT_ID;
			return this;
		}

		public ShipmentRequestTestBuilder zeroQuantity() {
			this.quantity = 0;
			return this;
		}

		public ShipmentRequestTestBuilder blankProductId() {
			this.productId = "";
			return this;
		}

		public ShipmentRequest build() {
			return new ShipmentRequest(productId, quantity);
		}
	}
}

package com.deepfine.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 어떤 상품의 재고. 현재 수량을 들고, 출고되면 그만큼 차감한다.
 *
 * <p>재고가 충분하면 출고 수량만큼 차감하고, 부족하면 거부한다. 부족은 정상 분기라
 * 예외가 아니라 {@link ShipmentResult}로 돌려준다. 재고 수량은 0 미만이 될 수 없고
 * (불변식), 잘못된 출고 수량(0·음수)은 부족과 구분되는 방어로 예외를 던진다.
 * 단순 수량 값으로 두고 ADT는 도입하지 않는다 (ADR-002).
 *
 * <p>도메인에 JPA를 직접 매핑한다 (ADR-004 — 하나로). 그 대가로 {@code productId}는
 * JPA가 채울 수 있게 {@code final}을 뗀다. 기본 생성자는 JPA 전용이라 protected로 닫아
 * 앱 코드는 검증 생성자만 쓰게 한다.
 */
@Entity
@Table(name = "stock", indexes = @Index(name = "idx_stock_product_id", columnList = "product_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String productId;

	@Column(nullable = false)
	private int quantity;

	public Stock(String productId, int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("재고 수량은 0 미만일 수 없습니다: " + quantity);
		}
		this.productId = productId;
		this.quantity = quantity;
	}

	/**
	 * 출고를 시도한다. 재고가 충분하면 출고 수량만큼 차감하고 {@link ShipmentResult#SUCCESS},
	 * 부족하면 재고를 건드리지 않고 {@link ShipmentResult#INSUFFICIENT}을 돌려준다.
	 * 남은 재고는 {@code getQuantity()}로 확인한다.
	 *
	 * <p>출고 수량은 양수여야 한다 — 0·음수는 부족(정상 분기)과 구분되는 잘못된 요청이라
	 * {@link IllegalArgumentException}을 던진다.
	 */
	public ShipmentResult ship(int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("출고 수량은 양수여야 합니다: " + quantity);
		}
		if (quantity > this.quantity) {
			return ShipmentResult.INSUFFICIENT;
		}
		this.quantity -= quantity;
		return ShipmentResult.SUCCESS;
	}
}

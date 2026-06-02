package com.deepfine.inventory.domain;

import lombok.Getter;

/**
 * 어떤 상품의 재고. 현재 수량을 들고, 출고되면 그만큼 차감한다.
 *
 * <p>재고가 충분한 정상 출고만 다룬다 — 부족 거부는 Task 3, 경계·불변식·입력 검증은
 * Task 4에서 붙는다. 단순 수량 값으로 두고 ADT는 도입하지 않는다 (ADR-002).
 */
@Getter
public class Stock {

    private final String productId;
    private int quantity;

    public Stock(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * 출고 수량만큼 재고를 차감한다. 남은 재고는 {@code getQuantity()}로 확인한다.
     */
    public void ship(int quantity) {
        this.quantity -= quantity;
    }
}

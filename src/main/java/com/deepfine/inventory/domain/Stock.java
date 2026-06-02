package com.deepfine.inventory.domain;

import lombok.Getter;

/**
 * 어떤 상품의 재고. 현재 수량을 들고, 출고되면 그만큼 차감한다.
 *
 * <p>재고가 충분하면 출고 수량만큼 차감하고, 부족하면 거부한다. 부족은 정상 분기라
 * 예외가 아니라 {@link ShipmentResult}로 돌려준다. 재고 수량은 0 미만이 될 수 없고
 * (불변식), 잘못된 출고 수량(0·음수)은 부족과 구분되는 방어로 예외를 던진다.
 * 단순 수량 값으로 두고 ADT는 도입하지 않는다 (ADR-002).
 */
@Getter
public class Stock {

    private final String productId;
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

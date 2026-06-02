package com.deepfine.inventory.domain;

import lombok.Getter;

/**
 * 어떤 상품의 재고. 현재 수량을 들고, 출고되면 그만큼 차감한다.
 *
 * <p>재고가 충분하면 출고 수량만큼 차감하고, 부족하면 거부한다. 부족은 정상 분기라
 * 예외가 아니라 {@link ShipmentResult}로 돌려준다. 경계·불변식·입력 검증은 Task 4에서
 * 붙는다. 단순 수량 값으로 두고 ADT는 도입하지 않는다 (ADR-002).
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
     * 출고를 시도한다. 재고가 충분하면 출고 수량만큼 차감하고 {@link ShipmentResult#SUCCESS},
     * 부족하면 재고를 건드리지 않고 {@link ShipmentResult#INSUFFICIENT}을 돌려준다.
     * 남은 재고는 {@code getQuantity()}로 확인한다.
     */
    public ShipmentResult ship(int quantity) {
        if (quantity > this.quantity) {
            return ShipmentResult.INSUFFICIENT;
        }
        this.quantity -= quantity;
        return ShipmentResult.SUCCESS;
    }
}

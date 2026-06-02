package com.deepfine.inventory.service;

import lombok.Getter;

/**
 * productId로 재고를 찾지 못했을 때 던진다. 입력 오류(0·음수)와 구분되는 "대상 없음"이라,
 * Story 4에서 별도 HTTP 상태(404)로 매핑된다.
 */

@Getter
public class StockNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "재고를 찾을 수 없습니다: productId=%s";

    private final String productId;

    public StockNotFoundException(String productId) {
        super(MESSAGE_TEMPLATE.formatted(productId));
        this.productId = productId;
    }
}

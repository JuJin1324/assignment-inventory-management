package com.deepfine.inventory.web.dto;

/**
 * 출고 응답 본문. 출고 후 남은(부족이면 현재) 재고를 싣는다. 성공/부족은 HTTP 상태로 전한다.
 */
public record ShipmentResponse(int remainingQuantity) {
}

package com.deepfine.inventory.service;

import com.deepfine.inventory.domain.ShipmentResult;

/**
 * 출고 서비스 결과 — 도메인 결과(성공/부족)와 남은 재고를 함께 돌려준다.
 * 성공이면 차감 후 남은 재고, 부족이면 그대로인 현재 재고.
 */
public record ShipResult(ShipmentResult outcome, int remainingQuantity) {
}

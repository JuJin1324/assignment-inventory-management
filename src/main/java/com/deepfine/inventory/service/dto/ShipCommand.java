package com.deepfine.inventory.service.dto;

/**
 * 출고 요청 입력 — 어느 상품(productId)을 얼마나(quantity) 출고할지. 단순 운반체로,
 * 검증은 도메인이 한다. 입력이 늘어도 서비스 시그니처가 흔들리지 않게 묶어 둔다.
 */
public record ShipCommand(String productId, int quantity) {
}

package com.deepfine.inventory.domain;

/**
 * 출고 시도의 결과. 재고 부족은 예외가 아니라 예상되는 정상 분기이므로,
 * 통제 흐름을 끊는 예외 대신 일급 결과 값으로 돌려준다 (ADR-002 — 단순 값, ADT 없음).
 */
public enum ShipmentResult {

	/** 재고가 충분해 출고 수량만큼 차감됨. */
	SUCCESS,

	/** 재고가 부족해 출고를 거부하고 재고를 건드리지 않음. */
	INSUFFICIENT
}

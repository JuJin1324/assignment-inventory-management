package com.deepfine.inventory.web;

import com.deepfine.inventory.service.ShipCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 출고 요청 본문. 웹 DTO를 서비스 입력과 분리하고, {@code toCommand()}로 변환한다.
 *
 * <p>잘못된 입력(빈 productId·0·음수 수량)은 부족(정상 분기)과 구분되는 잘못된 요청이라
 * 경계에서 bean validation으로 거부한다 — 서비스에 들어가기 전 400으로 끊는다. 도메인
 * {@code Stock.ship}의 수량 방어는 불변식 보호로 그대로 두되, API 경로에선 여기서 먼저 걸린다.
 */
public record ShipmentRequest(@NotBlank String productId, @Min(1) int quantity) {

	public ShipCommand toCommand() {
		return new ShipCommand(productId, quantity);
	}
}

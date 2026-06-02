package com.deepfine.inventory.web;

import com.deepfine.inventory.service.ShipCommand;

/**
 * 출고 요청 본문. 웹 DTO를 서비스 입력과 분리하고, {@code toCommand()}로 변환한다.
 */
public record ShipmentRequest(String productId, int quantity) {

	public ShipCommand toCommand() {
		return new ShipCommand(productId, quantity);
	}
}

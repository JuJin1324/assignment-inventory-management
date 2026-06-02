package com.deepfine.inventory.web;

import com.deepfine.inventory.domain.ShipmentResult;
import com.deepfine.inventory.service.ShipResult;
import com.deepfine.inventory.service.ShipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 출고 REST 엔드포인트. 출고 요청을 받아 {@link ShipService}를 태우고, 성공이면 200,
 * 부족이면 409로 남은(현재) 재고를 돌려준다. 부족은 예외가 아닌 정상 분기라 여기서 인라인 처리.
 *
 * <p>출고는 독립 리소스가 아니라 재고에 귀속된 행위라 {@code POST /api/stock/shipment}로 둔다.
 */
@RestController
@RequiredArgsConstructor
public class ShipmentController {

	private final ShipService shipService;

	@PostMapping("/api/stock/shipment")
	public ResponseEntity<ShipmentResponse> ship(@Valid @RequestBody ShipmentRequest request) {
		ShipResult result = shipService.ship(request.toCommand());
		ShipmentResponse body = new ShipmentResponse(result.remainingQuantity());

		if (result.outcome() == ShipmentResult.SUCCESS) {
			return ResponseEntity.ok(body);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}
}

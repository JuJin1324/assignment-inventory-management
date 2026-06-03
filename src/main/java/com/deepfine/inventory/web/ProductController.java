package com.deepfine.inventory.web;

import com.deepfine.inventory.domain.ShipmentResult;
import com.deepfine.inventory.service.ReceiveResult;
import com.deepfine.inventory.service.ReceiveService;
import com.deepfine.inventory.service.ShipResult;
import com.deepfine.inventory.service.ShipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ShipService shipService;
	private final ReceiveService receiveService;

	@PostMapping("/shipments")
	public ResponseEntity<ShipmentResponse> ship(@Valid @RequestBody ShipmentRequest request) {
		ShipResult result = shipService.ship(request.toCommand());
		var body = new ShipmentResponse(result.remainingQuantity());

		if (result.outcome() == ShipmentResult.SUCCESS) {
			return ResponseEntity.ok(body);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	@PostMapping("/receipts")
	public ResponseEntity<ReceiveResponse> receive(@Valid @RequestBody ReceiveRequest request) {
		ReceiveResult result = receiveService.receive(request.toCommand());
		return ResponseEntity.ok(new ReceiveResponse(result.quantity()));
	}
}

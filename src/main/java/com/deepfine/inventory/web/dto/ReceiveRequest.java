package com.deepfine.inventory.web.dto;

import com.deepfine.inventory.service.dto.ReceiveCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReceiveRequest(@NotBlank String productId, @NotBlank String name, @Min(1) int quantity) {

	public ReceiveCommand toCommand() {
		return new ReceiveCommand(productId, name, quantity);
	}
}

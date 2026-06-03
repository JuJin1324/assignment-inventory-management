package com.deepfine.inventory.service.dto;

public record ReceiveCommand(String productId, String name, int quantity) {
}

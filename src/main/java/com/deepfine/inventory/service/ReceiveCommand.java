package com.deepfine.inventory.service;

public record ReceiveCommand(String productId, String name, int quantity) {
}

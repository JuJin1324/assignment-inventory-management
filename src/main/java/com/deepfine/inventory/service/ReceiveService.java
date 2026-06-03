package com.deepfine.inventory.service;

import com.deepfine.inventory.domain.Product;
import com.deepfine.inventory.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReceiveService {

    private final ProductRepository productRepository;

    public ReceiveResult receive(ReceiveCommand command) {
        Product product = productRepository.findByProductId(command.productId())
                .orElseGet(() -> new Product(command.productId(), command.name(), 0));

        product.receive(command.quantity());
        productRepository.save(product);

        return new ReceiveResult(product.getQuantity());
    }
}

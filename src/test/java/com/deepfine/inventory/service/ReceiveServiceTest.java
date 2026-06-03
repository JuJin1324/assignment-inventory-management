package com.deepfine.inventory.service;

import static com.deepfine.inventory.ProductFixtures.DEFAULT_PRODUCT_ID;
import static com.deepfine.inventory.ProductFixtures.aProduct;
import static com.deepfine.inventory.ProductFixtures.aReceiveCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deepfine.inventory.domain.Product;
import com.deepfine.inventory.domain.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiveServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReceiveService receiveService;

    @Test
    @DisplayName("기존 상품 입고 → 수량 증가·저장 후 현재 수량 반환")
    void receive_existing_product() {
        // given
        Product product = aProduct().quantity(100).build();
        when(productRepository.findByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));

        // when
        ReceiveResult result = receiveService.receive(aReceiveCommand().quantity(30).build());

        // then
        assertThat(result.quantity()).isEqualTo(130);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("미등록 상품 입고 → name으로 신규 등록·저장 후 수량 반환")
    void receive_new_product() {
        // given
        ReceiveCommand command = aReceiveCommand().newProductId().quantity(50).build();
        when(productRepository.findByProductId(command.productId())).thenReturn(Optional.empty());

        // when
        ReceiveResult result = receiveService.receive(command);

        // then
        assertThat(result.quantity()).isEqualTo(50);
        verify(productRepository).save(any(Product.class));
    }
}

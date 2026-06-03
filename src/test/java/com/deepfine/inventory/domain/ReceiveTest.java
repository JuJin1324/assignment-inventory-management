package com.deepfine.inventory.domain;

import static com.deepfine.inventory.ProductFixtures.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReceiveTest {

    @Test
    @DisplayName("입고 → 수량 증가")
    void receive_increments_quantity() {
        Product product = aProduct().quantity(100).build();

        product.receive(30);

        assertThat(product.getQuantity()).isEqualTo(130);
    }

    @Test
    @DisplayName("수량 0인 상품에 입고 → 수량 증가")
    void receive_from_zero() {
        Product product = aProduct().quantity(0).build();

        product.receive(50);

        assertThat(product.getQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("입고 수량 0·음수 → 예외, 수량 불변")
    void receive_rejects_non_positive_quantity() {
        Product product = aProduct().quantity(100).build();

        assertThatThrownBy(() -> product.receive(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> product.receive(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(product.getQuantity()).isEqualTo(100);
    }
}

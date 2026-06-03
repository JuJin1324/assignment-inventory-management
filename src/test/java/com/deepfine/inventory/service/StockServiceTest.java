package com.deepfine.inventory.service;

import static com.deepfine.inventory.ProductFixtures.DEFAULT_PRODUCT_ID;
import static com.deepfine.inventory.ProductFixtures.NOT_FOUND_PRODUCT_ID;
import static com.deepfine.inventory.ProductFixtures.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.deepfine.inventory.domain.ProductRepository;
import com.deepfine.inventory.service.dto.StockResult;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private StockService stockService;

	@Test
	@DisplayName("등록된 상품 조회 → 현재 수량 반환")
	void query_existing_product() {
		when(productRepository.findByProductId(DEFAULT_PRODUCT_ID))
				.thenReturn(Optional.of(aProduct().quantity(50).build()));

		StockResult result = stockService.query(DEFAULT_PRODUCT_ID);

		assertThat(result.quantity()).isEqualTo(50);
	}

	@Test
	@DisplayName("미등록 상품 조회 → ProductNotFoundException")
	void query_product_not_found() {
		when(productRepository.findByProductId(NOT_FOUND_PRODUCT_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> stockService.query(NOT_FOUND_PRODUCT_ID))
				.isInstanceOf(ProductNotFoundException.class);
	}
}

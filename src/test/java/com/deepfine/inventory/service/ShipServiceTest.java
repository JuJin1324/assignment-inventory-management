package com.deepfine.inventory.service;

import static com.deepfine.inventory.ProductFixtures.DEFAULT_PRODUCT_ID;
import static com.deepfine.inventory.ProductFixtures.aShipCommand;
import static com.deepfine.inventory.ProductFixtures.aProduct;
import static com.deepfine.inventory.domain.ShipmentResult.INSUFFICIENT;
import static com.deepfine.inventory.domain.ShipmentResult.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deepfine.inventory.domain.Product;
import com.deepfine.inventory.domain.ProductRepository;
import com.deepfine.inventory.service.dto.ShipCommand;
import com.deepfine.inventory.service.dto.ShipResult;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShipServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ShipService shipService;

	@Test
	@DisplayName("재고 충분 → 차감·저장 후 남은 재고 반환")
	void ship_success() {
		// given
		Product product = aProduct().quantity(100).build();
		when(productRepository.findByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));

		// when
		ShipResult result = shipService.ship(aShipCommand().quantity(30).build());

		// then
		assertThat(result.outcome()).isEqualTo(SUCCESS);
		assertThat(result.remainingQuantity()).isEqualTo(70);
		verify(productRepository).save(product);
	}

	@Test
	@DisplayName("재고 부족 → 저장 안 함, 부족 반환")
	void ship_insufficient() {
		// given
		Product product = aProduct().quantity(100).build();
		when(productRepository.findByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));

		// when
		ShipResult result = shipService.ship(aShipCommand().quantity(120).build());

		// then
		assertThat(result.outcome()).isEqualTo(INSUFFICIENT);
		assertThat(result.remainingQuantity()).isEqualTo(100);
		verify(productRepository, never()).save(any());
	}

	@Test
	@DisplayName("상품 재고 없음 → ProductNotFoundException")
	void ship_product_not_found() {
		// given
		ShipCommand command = aShipCommand().notFoundProductId().build();
		when(productRepository.findByProductId(command.productId())).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> shipService.ship(command))
				.isInstanceOf(ProductNotFoundException.class);
	}
}

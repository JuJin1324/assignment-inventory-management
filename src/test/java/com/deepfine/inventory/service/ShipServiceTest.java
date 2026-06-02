package com.deepfine.inventory.service;

import static com.deepfine.inventory.StockFixtures.DEFAULT_PRODUCT_ID;
import static com.deepfine.inventory.StockFixtures.aShipCommand;
import static com.deepfine.inventory.StockFixtures.aStock;
import static com.deepfine.inventory.domain.ShipmentResult.INSUFFICIENT;
import static com.deepfine.inventory.domain.ShipmentResult.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deepfine.inventory.domain.Stock;
import com.deepfine.inventory.domain.StockRepository;
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
	private StockRepository stockRepository;

	@InjectMocks
	private ShipService shipService;

	@Test
	@DisplayName("재고 충분 → 차감·저장 후 남은 재고 반환")
	void ship_success() {
		// given
		Stock stock = aStock().quantity(100).build();
		when(stockRepository.findByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(stock));

		// when
		ShipResult result = shipService.ship(aShipCommand().quantity(30).build());

		// then
		assertThat(result.outcome()).isEqualTo(SUCCESS);
		assertThat(result.remainingQuantity()).isEqualTo(70);
		verify(stockRepository).save(stock);
	}

	@Test
	@DisplayName("재고 부족 → 저장 안 함, 부족 반환")
	void ship_insufficient() {
		// given
		Stock stock = aStock().quantity(100).build();
		when(stockRepository.findByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(stock));

		// when
		ShipResult result = shipService.ship(aShipCommand().quantity(120).build());

		// then
		assertThat(result.outcome()).isEqualTo(INSUFFICIENT);
		assertThat(result.remainingQuantity()).isEqualTo(100);
		verify(stockRepository, never()).save(any());
	}

	@Test
	@DisplayName("상품 재고 없음 → StockNotFoundException")
	void ship_stock_not_found() {
		// given
		ShipCommand command = aShipCommand().notFoundProductId().build();
		when(stockRepository.findByProductId(command.productId())).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> shipService.ship(command))
				.isInstanceOf(StockNotFoundException.class);
	}
}

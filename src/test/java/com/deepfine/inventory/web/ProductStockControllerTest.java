package com.deepfine.inventory.web;

import static com.deepfine.inventory.ProductFixtures.DEFAULT_PRODUCT_ID;
import static com.deepfine.inventory.ProductFixtures.NOT_FOUND_PRODUCT_ID;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deepfine.inventory.service.ProductNotFoundException;
import com.deepfine.inventory.service.ReceiveService;
import com.deepfine.inventory.service.ShipService;
import com.deepfine.inventory.service.StockService;
import com.deepfine.inventory.service.dto.StockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductStockControllerTest {

	private static final String STOCK_URL = "/api/products/" + DEFAULT_PRODUCT_ID;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ShipService shipService;

	@MockitoBean
	private ReceiveService receiveService;

	@MockitoBean
	private StockService stockService;

	@Test
	@DisplayName("등록된 상품 조회 → 200 + 현재 수량")
	void stock_existing_product() throws Exception {
		when(stockService.query(DEFAULT_PRODUCT_ID)).thenReturn(new StockResult(100));

		mockMvc.perform(get(STOCK_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantity").value(100));
	}

	@Test
	@DisplayName("미등록 상품 조회 → 404")
	void stock_product_not_found() throws Exception {
		when(stockService.query(NOT_FOUND_PRODUCT_ID))
				.thenThrow(new ProductNotFoundException(NOT_FOUND_PRODUCT_ID));

		mockMvc.perform(get("/api/products/" + NOT_FOUND_PRODUCT_ID))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}
}

package com.deepfine.inventory.web;

import com.deepfine.inventory.service.ProductNotFoundException;
import com.deepfine.inventory.service.ReceiveService;
import com.deepfine.inventory.service.ShipService;
import com.deepfine.inventory.service.dto.ShipResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.deepfine.inventory.ProductFixtures.aShipmentRequest;
import static com.deepfine.inventory.domain.ShipmentResult.INSUFFICIENT;
import static com.deepfine.inventory.domain.ShipmentResult.SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductShipControllerTest {

    private static final String SHIP_URL = "/api/products/shipments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ShipService shipService;

    @MockitoBean
    private ReceiveService receiveService;

    @Test
    @DisplayName("출고 성공 → 200 + 남은 재고")
    void ship_success() throws Exception {
        when(shipService.ship(any())).thenReturn(new ShipResult(SUCCESS, 70));

        var request = aShipmentRequest().quantity(30).build();
        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingQuantity").value(70));
    }

    @Test
    @DisplayName("재고 부족 → 409 + 현재 재고")
    void ship_insufficient() throws Exception {
        when(shipService.ship(any())).thenReturn(new ShipResult(INSUFFICIENT, 100));

        var request = aShipmentRequest().quantity(120).build();
        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.remainingQuantity").value(100));
    }

    @Test
    @DisplayName("대상 상품 없음 → 404")
    void ship_productNotFound() throws Exception {
        var request = aShipmentRequest().notFoundProductId().build();
        when(shipService.ship(any())).thenThrow(new ProductNotFoundException(request.productId()));

        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("출고 수량 0 → 잘못된 요청 거부 (400)")
    void ship_invalidQuantity() throws Exception {
        var request = aShipmentRequest().zeroQuantity().build();
        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("빈 productId → 잘못된 요청 거부 (400)")
    void ship_blankProductId() throws Exception {
        var request = aShipmentRequest().blankProductId().build();
        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("동시 수정 충돌 → 409")
    void ship_optimisticLockConflict() throws Exception {
        when(shipService.ship(any())).thenThrow(new ObjectOptimisticLockingFailureException(Object.class, null));

        var request = aShipmentRequest().quantity(10).build();
        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}

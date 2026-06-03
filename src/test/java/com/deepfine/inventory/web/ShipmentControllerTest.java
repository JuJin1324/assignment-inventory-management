package com.deepfine.inventory.web;

import com.deepfine.inventory.service.ShipResult;
import com.deepfine.inventory.service.ShipService;
import com.deepfine.inventory.service.ProductNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
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

@WebMvcTest(ShipmentController.class)
class ShipmentControllerTest {

    private static final String SHIP_URL = "/api/stock/shipment";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ShipService shipService;

    @Test
    @DisplayName("출고 성공 → 200 + 남은 재고")
    void ship_success() throws Exception {
        when(shipService.ship(any())).thenReturn(new ShipResult(SUCCESS, 70));

        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(aShipmentRequest().quantity(30).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingQuantity").value(70));
    }

    @Test
    @DisplayName("재고 부족 → 409 + 현재 재고")
    void ship_insufficient() throws Exception {
        when(shipService.ship(any())).thenReturn(new ShipResult(INSUFFICIENT, 100));

        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(aShipmentRequest().quantity(120).build())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.remainingQuantity").value(100));
    }

    @Test
    @DisplayName("대상 재고 없음 → 404")
    void ship_productNotFound() throws Exception {
        ShipmentRequest request = aShipmentRequest().notFoundProductId().build();
        when(shipService.ship(any())).thenThrow(new ProductNotFoundException(request.productId()));

        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("출고 수량 0 → 400 (경계 검증, 서비스 미진입)")
    void ship_invalidQuantity() throws Exception {
        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(aShipmentRequest().zeroQuantity().build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("빈 productId → 400 (경계 검증, 서비스 미진입)")
    void ship_blankProductId() throws Exception {
        mockMvc.perform(post(SHIP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(aShipmentRequest().blankProductId().build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private String asJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}

package com.deepfine.inventory.web;

import static com.deepfine.inventory.ProductFixtures.aReceiveRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deepfine.inventory.service.dto.ReceiveResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ProductReceiveControllerTest extends ProductControllerTestBase {

    private static final String RECEIVE_URL = "/api/products/receipts";

    @Test
    @DisplayName("기존 상품 입고 → 200 + 증가된 수량")
    void receive_existing_product() throws Exception {
        when(receiveService.receive(any())).thenReturn(new ReceiveResult(130));

        var request = aReceiveRequest().quantity(30).build();
        mockMvc.perform(post(RECEIVE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(130));
    }

    @Test
    @DisplayName("신규 상품 입고 → 200 + 등록된 수량")
    void receive_new_product() throws Exception {
        when(receiveService.receive(any())).thenReturn(new ReceiveResult(50));

        var request = aReceiveRequest().newProductId().quantity(50).build();
        mockMvc.perform(post(RECEIVE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    @DisplayName("입고 수량 0 → 잘못된 요청 거부 (400)")
    void receive_zeroQuantity() throws Exception {
        var request = aReceiveRequest().zeroQuantity().build();
        mockMvc.perform(post(RECEIVE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("name 누락(blank) → 잘못된 요청 거부 (400)")
    void receive_blankName() throws Exception {
        var request = aReceiveRequest().blankName().build();
        mockMvc.perform(post(RECEIVE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("빈 productId → 잘못된 요청 거부 (400)")
    void receive_blankProductId() throws Exception {
        var request = aReceiveRequest().blankProductId().build();
        mockMvc.perform(post(RECEIVE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}

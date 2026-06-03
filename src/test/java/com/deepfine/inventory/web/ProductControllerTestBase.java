package com.deepfine.inventory.web;

import com.deepfine.inventory.service.ReceiveService;
import com.deepfine.inventory.service.ShipService;
import com.deepfine.inventory.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
abstract class ProductControllerTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected ShipService shipService;

    @MockitoBean
    protected ReceiveService receiveService;

    @MockitoBean
    protected StockService stockService;
}

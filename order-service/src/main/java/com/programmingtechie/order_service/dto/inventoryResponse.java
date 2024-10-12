package com.programmingtechie.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder

public class inventoryResponse {
    private String skuCode;
    private boolean isInStock;
}

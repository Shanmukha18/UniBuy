package com.ecommerce.server_side.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long userId;
    private Double amount;
    private String currency;
    private String receipt;
    private String notes;
}

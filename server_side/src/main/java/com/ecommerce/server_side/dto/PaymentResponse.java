package com.ecommerce.server_side.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String orderId;
    private String currency;
    private Double amount;
    private String key;
    private String name;
    private String description;
    private String prefill;
    private String notes;
    private String theme;
}

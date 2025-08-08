package com.ecommerce.server_side.service;

import com.ecommerce.server_side.dto.PaymentRequest;
import com.ecommerce.server_side.dto.PaymentResponse;
import com.ecommerce.server_side.dto.PaymentVerificationRequest;

public interface PaymentService {
    PaymentResponse createPaymentOrder(PaymentRequest paymentRequest);
    boolean verifyPayment(PaymentVerificationRequest verificationRequest);
}

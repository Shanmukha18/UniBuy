package com.ecommerce.server_side.controller;

import com.ecommerce.server_side.dto.PaymentRequest;
import com.ecommerce.server_side.dto.PaymentResponse;
import com.ecommerce.server_side.dto.PaymentVerificationRequest;
import com.ecommerce.server_side.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://unibuy-shanmukha-thadavarthis-projects.vercel.app",
    "https://unibuy-git-main-shanmukha-thadavarthis-projects.vercel.app",
    "https://unibuy-six.vercel.app"
})
@Slf4j
public class PaymentController {

    @Value("${razorpay.key.id:rzp_test_placeholder}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:placeholder_secret}")
    private String razorpayKeySecret;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createPaymentOrder(@RequestBody PaymentRequest paymentRequest) {
        try {
            log.info("Creating payment order for user: {}, amount: {}, currency: {}", 
                paymentRequest.getUserId(), paymentRequest.getAmount(), paymentRequest.getCurrency());
            
            // Validate payment request
            if (paymentRequest.getUserId() == null) {
                log.error("User ID is required");
                return ResponseEntity.badRequest().body("User ID is required");
            }
            
            if (paymentRequest.getAmount() == null || paymentRequest.getAmount() <= 0) {
                log.error("Invalid amount: {}", paymentRequest.getAmount());
                return ResponseEntity.badRequest().body("Invalid amount");
            }
            
            if (paymentRequest.getCurrency() == null || paymentRequest.getCurrency().isEmpty()) {
                log.error("Currency is required");
                return ResponseEntity.badRequest().body("Currency is required");
            }
            
            PaymentResponse response = paymentService.createPaymentOrder(paymentRequest);
            log.info("Payment order created successfully: {}", response.getOrderId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Runtime error creating payment order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error creating payment order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Failed to create payment order: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyPayment(@RequestBody PaymentVerificationRequest verificationRequest) {
        try {
            log.info("Verifying payment for order: {}", verificationRequest.getRazorpayOrderId());
            boolean isValid = paymentService.verifyPayment(verificationRequest);
            log.info("Payment verification result: {}", isValid);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.error("Error verifying payment: ", e);
            return ResponseEntity.badRequest().body(false);
        }
    }
}

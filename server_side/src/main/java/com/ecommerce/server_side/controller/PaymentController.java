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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
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

    @GetMapping("/debug-config")
    public ResponseEntity<?> debugConfig() {
        try {
            // This endpoint will help debug the Razorpay configuration
            Map<String, Object> config = new HashMap<>();
            
            // Check if keys are configured
            boolean isConfigured = !"rzp_test_placeholder".equals(razorpayKeyId) && 
                                 !"placeholder_secret".equals(razorpayKeySecret) &&
                                 !"rzp_test_YOUR_KEY_ID".equals(razorpayKeyId) &&
                                 !"YOUR_SECRET_KEY".equals(razorpayKeySecret) &&
                                 razorpayKeyId != null && razorpayKeySecret != null &&
                                 !razorpayKeyId.isEmpty() && !razorpayKeySecret.isEmpty();
            
            config.put("razorpayConfigured", isConfigured);
            config.put("keyId", razorpayKeyId != null ? razorpayKeyId : "NOT_SET");
            config.put("secretConfigured", razorpayKeySecret != null && !razorpayKeySecret.isEmpty() && 
                                         !"placeholder_secret".equals(razorpayKeySecret) &&
                                         !"YOUR_SECRET_KEY".equals(razorpayKeySecret));
            config.put("keyIdLength", razorpayKeyId != null ? razorpayKeyId.length() : 0);
            config.put("secretLength", razorpayKeySecret != null ? razorpayKeySecret.length() : 0);
            config.put("environment", System.getenv("SPRING_PROFILES_ACTIVE"));
            config.put("timestamp", new java.util.Date().toString());
            
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("Error in debug config: ", e);
            return ResponseEntity.badRequest().body("Error checking configuration: " + e.getMessage());
        }
    }
}

package com.ecommerce.server_side.service.implementation;

import com.ecommerce.server_side.dto.PaymentRequest;
import com.ecommerce.server_side.dto.PaymentResponse;
import com.ecommerce.server_side.dto.PaymentVerificationRequest;
import com.ecommerce.server_side.service.PaymentService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class PaymentServiceImplementation implements PaymentService {

    @Value("${razorpay.key.id:rzp_test_placeholder}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:placeholder_secret}")
    private String razorpayKeySecret;

    @Override
    public PaymentResponse createPaymentOrder(PaymentRequest paymentRequest) {
        log.info("Starting payment order creation for user: {}", paymentRequest.getUserId());
        
        // Validate input parameters
        if (paymentRequest.getUserId() == null) {
            log.error("User ID is null");
            throw new RuntimeException("User ID is required");
        }
        
        if (paymentRequest.getAmount() == null || paymentRequest.getAmount() <= 0) {
            log.error("Invalid amount: {}", paymentRequest.getAmount());
            throw new RuntimeException("Invalid amount");
        }
        
        if (paymentRequest.getCurrency() == null || paymentRequest.getCurrency().isEmpty()) {
            log.error("Currency is null or empty");
            throw new RuntimeException("Currency is required");
        }
        
        // Check if Razorpay is properly configured
        if ("rzp_test_placeholder".equals(razorpayKeyId) || "placeholder_secret".equals(razorpayKeySecret) || 
            razorpayKeyId == null || razorpayKeySecret == null || 
            razorpayKeyId.isEmpty() || razorpayKeySecret.isEmpty()) {
            log.error("Razorpay is not configured. Please set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
            log.error("Current razorpayKeyId: {}", razorpayKeyId);
            log.error("Current razorpayKeySecret: {}", razorpayKeySecret != null ? "***SET***" : "NOT SET");
            throw new RuntimeException("Razorpay is not configured. Please set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
        }
        
        log.info("Razorpay configuration found. Key ID: {}", razorpayKeyId);
        log.info("Razorpay key length: {}", razorpayKeyId.length());
        
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            log.info("Razorpay client created successfully");

            JSONObject orderRequest = new JSONObject();
            int amountInPaise = (int) (paymentRequest.getAmount() * 100); // Convert to paise
            
            // Validate amount conversion
            if (amountInPaise <= 0) {
                log.error("Invalid amount after conversion to paise: {}", amountInPaise);
                throw new RuntimeException("Invalid amount after conversion to paise");
            }
            
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", paymentRequest.getCurrency());
            orderRequest.put("receipt", paymentRequest.getReceipt());
            
            // Only add notes if they are not null or empty, and wrap them in a JSONObject
            if (paymentRequest.getNotes() != null && !paymentRequest.getNotes().isEmpty()) {
                JSONObject notesObject = new JSONObject();
                notesObject.put("description", paymentRequest.getNotes()); // Wrap the string note in a JSONObject
                orderRequest.put("notes", notesObject);
            }

            log.info("Creating Razorpay order with request: {}", orderRequest.toString());
            log.info("Creating Razorpay order with amount: {} paise, currency: {}, receipt: {}", 
                amountInPaise, paymentRequest.getCurrency(), paymentRequest.getReceipt());

            com.razorpay.Order order = razorpayClient.orders.create(orderRequest);
            log.info("Razorpay order created successfully: {}", (String) order.get("id"));

            PaymentResponse response = new PaymentResponse();
            response.setOrderId(order.get("id").toString());
            response.setCurrency(paymentRequest.getCurrency());
            response.setAmount(paymentRequest.getAmount());
            response.setKey(razorpayKeyId);
            response.setName("E-Commerce Store");
            response.setDescription("Payment for your order");
            response.setPrefill("email");
            response.setNotes(paymentRequest.getNotes());
            response.setTheme("#3399cc");

            log.info("Payment response created successfully: {}", response.getOrderId());
            return response;
        } catch (RazorpayException e) {
            log.error("RazorpayException occurred: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment order: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment order: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPayment(PaymentVerificationRequest verificationRequest) {
        log.info("Starting payment verification for order: {}", verificationRequest.getRazorpayOrderId());
        
        // Check if Razorpay is properly configured
        if (razorpayKeyId == null || razorpayKeySecret == null || 
            razorpayKeyId.isEmpty() || razorpayKeySecret.isEmpty() ||
            "rzp_test_placeholder".equals(razorpayKeyId) || "placeholder_secret".equals(razorpayKeySecret) ||
            "rzp_test_YOUR_KEY_ID".equals(razorpayKeyId) || "YOUR_SECRET_KEY".equals(razorpayKeySecret)) {
            log.error("Razorpay is not configured. Please set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
            log.error("Current razorpayKeyId: {}", razorpayKeyId);
            log.error("Current razorpayKeySecret: {}", razorpayKeySecret != null ? "***SET***" : "NOT SET");
            throw new RuntimeException("Razorpay is not configured. Please set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
        }
        
        try {
            // Validate input parameters
            if (verificationRequest.getRazorpayOrderId() == null || verificationRequest.getRazorpayOrderId().isEmpty()) {
                log.error("Razorpay order ID is required");
                return false;
            }
            
            if (verificationRequest.getRazorpayPaymentId() == null || verificationRequest.getRazorpayPaymentId().isEmpty()) {
                log.error("Razorpay payment ID is required");
                return false;
            }
            
            if (verificationRequest.getRazorpaySignature() == null || verificationRequest.getRazorpaySignature().isEmpty()) {
                log.error("Razorpay signature is required");
                return false;
            }
            
            // Create the data string in the format: razorpay_order_id|razorpay_payment_id
            String data = verificationRequest.getRazorpayOrderId() + "|" + verificationRequest.getRazorpayPaymentId();
            log.info("Verification data string: {}", data);
            log.info("Secret key length: {}", razorpayKeySecret.length());
            
            // Create HMAC SHA256 signature
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = java.util.Base64.getEncoder().encodeToString(hmacData);
            
            log.info("Calculated signature: {}", calculatedSignature);
            log.info("Received signature: {}", verificationRequest.getRazorpaySignature());
            
            // Compare signatures (case-sensitive)
            boolean isValid = calculatedSignature.equals(verificationRequest.getRazorpaySignature());
            log.info("Payment verification result: {}", isValid);
            
            if (!isValid) {
                log.error("Signature verification failed. Expected: {}, Received: {}", calculatedSignature, verificationRequest.getRazorpaySignature());
                log.error("Data string used for verification: {}", data);
                log.error("Secret key used (first 10 chars): {}", razorpayKeySecret.substring(0, Math.min(10, razorpayKeySecret.length())));
                
                // Additional debugging information
                log.error("Razorpay order ID: {}", verificationRequest.getRazorpayOrderId());
                log.error("Razorpay payment ID: {}", verificationRequest.getRazorpayPaymentId());
                log.error("User ID: {}", verificationRequest.getUserId());
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Error verifying payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to verify payment: " + e.getMessage());
        }
    }
}

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
        
        // Check if Razorpay is properly configured
        if ("rzp_test_placeholder".equals(razorpayKeyId) || "placeholder_secret".equals(razorpayKeySecret)) {
            log.error("Razorpay is not configured. Please set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
            throw new RuntimeException("Razorpay is not configured. Please set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
        }
        
        log.info("Razorpay configuration found. Key ID: {}", razorpayKeyId);
        
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            log.info("Razorpay client created successfully");

            JSONObject orderRequest = new JSONObject();
            int amountInPaise = (int) (paymentRequest.getAmount() * 100); // Convert to paise
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", paymentRequest.getCurrency());
            orderRequest.put("receipt", paymentRequest.getReceipt());
            orderRequest.put("notes", paymentRequest.getNotes());

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
        if ("rzp_test_placeholder".equals(razorpayKeyId) || "placeholder_secret".equals(razorpayKeySecret)) {
            log.error("Razorpay is not configured. Please set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
            throw new RuntimeException("Razorpay is not configured. Please set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
        }
        
        try {
            String data = verificationRequest.getRazorpayOrderId() + "|" + verificationRequest.getRazorpayPaymentId();
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = java.util.Base64.getEncoder().encodeToString(hmacData);

            boolean isValid = calculatedSignature.equals(verificationRequest.getRazorpaySignature());
            log.info("Payment verification result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Error verifying payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to verify payment: " + e.getMessage());
        }
    }
}

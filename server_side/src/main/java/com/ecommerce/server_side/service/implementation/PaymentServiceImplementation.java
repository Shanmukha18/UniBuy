package com.ecommerce.server_side.service.implementation;

import com.ecommerce.server_side.dto.PaymentRequest;
import com.ecommerce.server_side.dto.PaymentResponse;
import com.ecommerce.server_side.dto.PaymentVerificationRequest;
import com.ecommerce.server_side.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImplementation implements PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public PaymentResponse createPaymentOrder(PaymentRequest paymentRequest) {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (paymentRequest.getAmount() * 100)); // Convert to paise
            orderRequest.put("currency", paymentRequest.getCurrency());
            orderRequest.put("receipt", paymentRequest.getReceipt());
            orderRequest.put("notes", paymentRequest.getNotes());

            Order order = razorpayClient.orders.create(orderRequest);

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

            // Update order with razorpay order ID
            // Note: You might want to inject OrderService here to update the order
            // For now, we'll return the response and handle order update in the controller

            return response;
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create payment order: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPayment(PaymentVerificationRequest verificationRequest) {
        try {
            String data = verificationRequest.getRazorpayOrderId() + "|" + verificationRequest.getRazorpayPaymentId();
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = java.util.Base64.getEncoder().encodeToString(hmacData);

            return calculatedSignature.equals(verificationRequest.getRazorpaySignature());
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify payment: " + e.getMessage());
        }
    }
}

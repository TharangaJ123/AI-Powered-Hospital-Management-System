package com.sliit.payment_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;
import java.util.HashMap;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    // Helper method to convert bytes to hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    @PostMapping("/payhere/notify")
    public ResponseEntity<?> handlePayHereNotification(@RequestBody Map<String, String> payload) {
        System.out.println("PayHere notification received: " + payload);
        // Your existing notification handling code
        return ResponseEntity.ok("Notification received");
    }
    
    // Add hash generation endpoint
    @PostMapping("/generate-hash")
    public ResponseEntity<Map<String, String>> generatePayHereHash(@RequestBody Map<String, String> request) {
        try {
            String merchantId = request.get("merchant_id");
            String orderId = request.get("order_id");
            String amount = request.get("amount");
            String currency = request.get("currency");
            String merchantSecret = "pw";
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] secretHash = md.digest(merchantSecret.getBytes());
            String hashedSecret = bytesToHex(secretHash);
            
            String hashString = merchantId + orderId + amount + currency + hashedSecret;
            byte[] finalHash = md.digest(hashString.getBytes());
            
            Map<String, String> response = new HashMap<>();
            response.put("hash", bytesToHex(finalHash).toUpperCase());
            
            System.out.println("Generated PayHere hash: " + response.get("hash"));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error generating PayHere hash: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to generate hash");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

package com.laptopshop.controller;

import com.laptopshop.dto.PaymentResponse;
import com.laptopshop.entity.PaymentMethod;
import com.laptopshop.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/create-url/{orderId}")
    public ResponseEntity<PaymentResponse> createPaymentUrl(
            @PathVariable Long orderId,
            @RequestParam PaymentMethod method,
            HttpServletRequest request) throws Exception {
        
        String url = paymentService.createPaymentUrl(orderId, method, request);
        return ResponseEntity.ok(PaymentResponse.builder()
                .paymentUrl(url)
                .status("OK")
                .message("Success")
                .build());
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<Void> vnpayCallback(@RequestParam Map<String, String> params) {
        paymentService.processVnPayCallback(params);
        // Redirect to frontend result page
        return ResponseEntity.status(302)
                .header("Location", "http://localhost:3000/payment/result?status=" + params.get("vnp_ResponseCode"))
                .build();
    }
}

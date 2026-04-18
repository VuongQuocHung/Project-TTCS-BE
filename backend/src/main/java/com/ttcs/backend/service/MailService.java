package com.ttcs.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendResetPasswordEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Đặt lại mật khẩu - Laptop Shop");
            message.setText(buildResetPasswordEmailContent(token));

            mailSender.send(message);

            System.out.println("Reset password email sent successfully to: " + email);

        } catch (Exception e) {
            System.err.println("Error while sending reset password email: " + e.getMessage());
            throw new RuntimeException("Failed to send reset password email", e);
        }
    }

    private String buildResetPasswordEmailContent(String token) {
        return """
            Chào bạn,

            Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Laptop Shop.

            Token đặt lại mật khẩu của bạn là: %s

            Token này sẽ hết hạn sau 15 phút.

            Để đặt lại mật khẩu, hãy sử dụng token này trong ứng dụng hoặc truy cập:
            http://localhost:3000/reset-password?token=%s

            Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.

            Trân trọng,
            Đội ngũ Laptop Shop
            """.formatted(token, token);
    }
}

package com.laptopshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendResetPasswordEmail(String to, String token) {
        String subject = "Password Reset Request";
        String resetUrl = "http://localhost:3000/reset-password?token=" + token;
        String message = "To reset your password, click the link below:\n" + resetUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom("your-email@gmail.com");

        mailSender.send(email);
    }
}

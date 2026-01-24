package com.learn.ecommerce.services;

import com.learn.ecommerce.entity.VerificationToken;
import com.learn.ecommerce.exceptionhandler.EmailFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.url}")
    private String url;

    // constructor
    public EmailService(JavaMailSender mailSender, JavaMailSender javaMailSender) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
    }

    private SimpleMailMessage createSimpleMailMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        return message;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) {
        SimpleMailMessage message = createSimpleMailMessage();

        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Please verify your email to complete your registration");
        String body = "To verify your email, please click the following link:\n " +
                url + "/auth/verify?token=" + "[PROTECTED_TOKEN]";
        message.setText(body);

        try {
            javaMailSender.send(message);
            log.info("Verification email sent to {}", verificationToken.getUser().getEmail());
        } catch (MailException e) {
            log.error("Failed to send verification email to {}: {}", verificationToken.getUser().getEmail(), e.getMessage());
            throw new EmailFailureException("Email sending failed");
        }
    }

    public void sendPasswordResetEmail(VerificationToken verificationToken) {
        SimpleMailMessage message = createSimpleMailMessage();

        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Password Reset Request");
        String body = "To reset your password, please click the following link:\n " +
                url + "/auth/reset-password?token=" + "[PROTECTED_TOKEN]";
        message.setText(body);

        try {
            mailSender.send(message);
            log.info("Password reset email sent to {}", verificationToken.getUser().getEmail());
        } catch (MailException e) {
            log.error("Failed to send password reset email to {}: {}", verificationToken.getUser().getEmail(), e.getMessage());
            throw new EmailFailureException("Email sending failed");
        }
    }
}
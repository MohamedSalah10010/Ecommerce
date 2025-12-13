package com.learn.ecommerce.services;

import com.learn.ecommerce.api.model.VerificationToken;
import com.learn.ecommerce.exception.EmailFailureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.url}")
    private String url;


    // constructor
    public EmailService(JavaMailSender mailSender, JavaMailSender javaMailSender) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
    }

    private SimpleMailMessage createSimpleMailMessage()
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        return message;
    }

    public void sendVerficationEmail(VerificationToken verificationToken)
    {
        SimpleMailMessage message = createSimpleMailMessage();

        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Please verify your email to complete your registration");
        String body = "To verify your email, please click the following link:\n " +
                url +"/auth/verify?token=" + verificationToken.getToken();
        message.setText(body);

        try {
            javaMailSender.send(message);
        }catch (MailException e) {
            throw new EmailFailureException("Email sending failed");
        }

    }
}

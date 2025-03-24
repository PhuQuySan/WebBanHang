package com.asm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendActivationEmail(String to, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Kích hoạt tài khoản");
        message.setText("Nhấn vào liên kết sau để kích hoạt tài khoản của bạn: " + activationLink);
        mailSender.send(message);
    }
    
    public void sendForgotPasswordEmail(String to, String username, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Khôi phục tài khoản");
        message.setText("Nhấp vào liên kết sau để khôi phục tài khoản " 
                + username + " của bạn: " + resetLink);
        mailSender.send(message);
    }
    
}




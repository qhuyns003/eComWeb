package com.qhuyns.ecomweb.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class EmailService {
    JavaMailSender mailSender;
    // content don gian
    public void sendVerificationEmail2(String to, String token,String username) {
        String subject = "Xác minh tài khoản";
        String verificationUrl = "http://localhost:5173/verify?token=" + token+"&username="+username;
        String content = "Nhấn vào link để xác minh tài khoản: " + verificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
    // content phuc tap
    public void sendVerificationEmail(String to, String token, String username) throws Exception {
        String subject = "Xác minh tài khoản";
        String verificationUrl = "http://localhost:5173/verify?token=" + token + "&username=" + username;
        String content = "<b>Chúc mừng bạn đã đăng kí tài khoản thành công trên EASIER!</b><br><br>"
                + "Nhấn vào <a href=\"" + verificationUrl + "\">đây</a> để xác minh tài khoản.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // true để gửi HTML

        mailSender.send(message);
    }
}

package com.example.computerweb.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

    public boolean sendConfirmLink(String emailTo, String newPassword , String emailLogin) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending confirming link to user, email={}", emailTo);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();


        Map<String, Object> properties = new HashMap<>();
        properties.put("emailLogin" , emailLogin);
        properties.put("newPassword", newPassword);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Phòng giáo vụ");
        helper.setTo(emailTo);
        helper.setSubject("New account generate");
        String html = templateEngine.process("confirm-email.html", context);
        helper.setText(html, true);
        log.info("emailLogin ={} , newPassword = {}" ,emailLogin, newPassword);
        try {
            mailSender.send(message);

            log.info("Confirming link has sent to user, email={}, linkConfirm={}", emailTo, newPassword);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error sending mail: {}", ex.getMessage(), ex);
            return false;
        }

    }
    /**
     * Gửi email thông báo kết quả xử lý yêu cầu.
     * @param emailTo Email của người nhận (người tạo yêu cầu)
     * @param properties Một Map chứa các giá trị để điền vào template
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public boolean sendRequestResponseEmail(String emailTo, Map<String, Object> properties) {
        if (emailTo == null || emailTo.isEmpty()) {
            log.warn("Không thể gửi email: địa chỉ người nhận trống.");
            return false;
        }

        final String subject = "Phản hồi về Yêu cầu #" + properties.getOrDefault("ticketId", "");
        log.info("Đang chuẩn bị gửi email phản hồi tới: {}, chủ đề: {}", emailTo, subject);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(properties);

            helper.setFrom(emailFrom, "Hệ thống Quản lý Lịch PTIT");
            helper.setTo(emailTo);
            helper.setSubject(subject);

            String html = templateEngine.process("request-response-email_1.html", context);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Đã gửi email phản hồi thành công tới: {}", emailTo);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Lỗi khi gửi email phản hồi tới {}: {}", emailTo, e.getMessage());
            return false;
        }
    }




}

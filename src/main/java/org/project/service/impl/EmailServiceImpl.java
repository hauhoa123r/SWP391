package org.project.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.project.exception.ErrorResponse;
import org.project.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    private JavaMailSender javaMailSender;
    @Value("${MAIL_USERNAME}")
    private String mailUsername;

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        simpleMailMessage.setFrom(mailUsername);
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sendEmailHtml(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(mailUsername);
            javaMailSender.send(message);
        } catch (MessagingException ex) {
            throw new ErrorResponse("Gửi email thất bại");
        }
    }

    @Override
    public void sendResetPasswordEmail(String to, String newPassword) {
        String subject = "KiviCare - Thông báo đặt lại mật khẩu";

        String html = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 480px; margin: 0 auto; border-radius: 12px; background: #f4f8fb; border: 1px solid #e0e6ed; padding: 32px 28px; color: #263238; box-shadow: 0 6px 24px rgba(44,62,80,0.07);\">"
                + "<h2 style=\"color: #1976d2; text-align:center; margin-bottom: 8px;\">Đặt lại mật khẩu thành công</h2>"
                + "<p style=\"font-size:15px; line-height:1.7; text-align:center; color:#455a64;\">Kính gửi Quý khách,<br><br>"
                + "Yêu cầu đặt lại mật khẩu cho tài khoản của bạn trên hệ thống <b>KiviCare</b> đã được xử lý thành công.</p>"
                + "<div style=\"margin: 28px 0; background: #ffffff; border-radius: 8px; border:1px solid #e3e8ee; padding: 18px 0; text-align:center;\">"
                + "  <span style=\"font-size:16px; color:#1976d2; font-weight:bold;\">Mật khẩu mới của bạn:</span><br>"
                + "  <span style=\"display:inline-block; margin-top:8px; background:#e3f2fd; color:#0d47a1; font-size:18px; font-weight:600; letter-spacing:1px; padding:8px 30px; border-radius:6px;\">" + newPassword + "</span>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #607d8b;\">Vui lòng sử dụng mật khẩu này để đăng nhập. Để đảm bảo an toàn thông tin, bạn nên thay đổi lại mật khẩu sau lần đăng nhập đầu tiên và tuyệt đối không chia sẻ với bất kỳ ai.</p>"
                + "<div style=\"margin: 22px 0 18px 0; text-align: center;\">"
                + "<a href=\"http://localhost:8080/login\" style=\"background: #1976d2; color: white; text-decoration: none; font-size: 15px; padding: 10px 28px; border-radius: 5px; box-shadow: 0 2px 8px rgba(44,62,80,0.06); font-weight: bold;\">Đăng nhập hệ thống</a>"
                + "</div>"
                + "<p style=\"font-size: 13px; color: #90a4ae; margin-top: 32px; text-align:center;\">Nếu bạn cần hỗ trợ thêm, vui lòng liên hệ bộ phận chăm sóc khách hàng của <b>KiviCare</b>.</p>"
                + "<p style=\"font-size: 14px; color: #78909c; margin-top:20px; text-align:right;\">Trân trọng,<br><b>Đội ngũ KiviCare</b></p>"
                + "</div>";

        sendEmailHtml(to, subject, html);
    }

    @Override
    public void sendAccountCreatedEmail(String to, String password) {
        String subject = "KiviCare - Thông báo tài khoản đã được tạo";
        String html = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 480px; margin: 0 auto; border-radius: 12px; background: #f4f8fb; border: 1px solid #e0e6ed; padding: 32px 28px; color: #263238; box-shadow: 0 6px 24px rgba(44,62,80,0.07);\">"
                + "<h2 style=\"color: #1976d2; text-align:center; margin-bottom: 8px;\">Tài khoản đã được tạo thành công</h2>"
                + "<p style=\"font-size:15px; line-height:1.7; text-align:center; color:#455a64;\">Kính gửi Quý khách,<br><br>"
                + "Tài khoản của bạn trên hệ thống <b>KiviCare</b> đã được tạo thành công.</p>"
                + "<div style=\"margin: 28px 0; background: #ffffff; border-radius: 8px; border:1px solid #e3e8ee; padding: 18px 0; text-align:center;\">"
                + "  <span style=\"font-size:16px; color:#1976d2; font-weight:bold;\">Mật khẩu đăng nhập của bạn:</span><br>"
                + "  <span style=\"display:inline-block; margin-top:8px; background:#e3f2fd; color:#0d47a1; font-size:18px; font-weight:600; letter-spacing:1px; padding:8px 30px; border-radius:6px;\">" + password + "</span>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #607d8b;\">Vui lòng sử dụng tài khoản và mật khẩu này để đăng nhập. Để đảm bảo an toàn, bạn nên đổi mật khẩu sau khi đăng nhập lần đầu và tuyệt đối không chia sẻ cho bất kỳ ai.</p>"
                + "<div style=\"margin: 22px 0 18px 0; text-align: center;\">"
                + "<a href=\"http://localhost:8080/login\" style=\"background: #1976d2; color: white; text-decoration: none; font-size: 15px; padding: 10px 28px; border-radius: 5px; box-shadow: 0 2px 8px rgba(44,62,80,0.06); font-weight: bold;\">Đăng nhập hệ thống</a>"
                + "</div>"
                + "<p style=\"font-size: 13px; color: #90a4ae; margin-top: 32px; text-align:center;\">Nếu cần hỗ trợ, vui lòng liên hệ bộ phận chăm sóc khách hàng của <b>KiviCare</b>.</p>"
                + "<p style=\"font-size: 14px; color: #78909c; margin-top:20px; text-align:right;\">Trân trọng,<br><b>Đội ngũ KiviCare</b></p>"
                + "</div>";

        sendEmailHtml(to, subject, html);
    }
}

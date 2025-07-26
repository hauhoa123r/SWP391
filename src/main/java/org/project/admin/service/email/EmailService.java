package org.project.admin.service.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.project.admin.entity.Coupon;
import org.project.admin.enums.coupon.DiscountType;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Mã OTP của bạn");
        message.setText("Mã OTP của bạn là: " + otp);
        mailSender.send(message);
    }

    public void sendCouponNotificationEmail(String email, Coupon coupon) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("Thông báo mã giảm giá mới từ KIVICARE");

        String discountInfo = getDiscountInfo(coupon.getDiscountType(), coupon.getValue());
        String formattedDate = "N/A";

        // Xử lý ngày tháng an toàn
        try {
            if (coupon.getExpirationDate() != null) {
                formattedDate = coupon.getExpirationDate().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
                );
            }
        } catch (Exception e) {
            formattedDate = "N/A";
        }

        String description = coupon.getDescription() != null && !coupon.getDescription().trim().isEmpty()
            ? coupon.getDescription()
            : "Mã giảm giá đặc biệt từ KIVICARE";

        // Template HTML đơn giản hơn
        String emailContent = buildEmailTemplate(coupon.getCode(), description, formattedDate, discountInfo);

        helper.setText(emailContent, true);
        mailSender.send(message);
    }

    private String buildEmailTemplate(String couponCode, String description, String validDate, String discountValue) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='vi'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Mã giảm giá từ KIVICARE</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background-color: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }");
        html.append(".header { background: linear-gradient(135deg, #c560a0, #a75a69); color: white; padding: 30px 20px; text-align: center; }");
        html.append(".header h1 { margin: 0; font-size: 2.5em; font-weight: bold; }");
        html.append(".header p { margin: 10px 0 0 0; font-size: 1.1em; opacity: 0.9; }");
        html.append(".content { padding: 30px 20px; }");
        html.append(".coupon-info { background-color: #f9f9f9; border-radius: 8px; padding: 20px; margin: 20px 0; border-left: 4px solid #c560a0; }");
        html.append(".coupon-code { font-size: 2em; font-weight: bold; color: #c560a0; text-align: center; margin: 10px 0; letter-spacing: 2px; }");
        html.append(".discount-value { font-size: 1.8em; font-weight: bold; color: #a75a69; text-align: center; margin: 15px 0; }");
        html.append(".info-row { display: flex; justify-content: space-between; margin: 10px 0; padding: 8px 0; border-bottom: 1px solid #eee; }");
        html.append(".info-label { font-weight: bold; color: #555; }");
        html.append(".info-value { color: #333; }");
        html.append(".footer { background-color: #f8f8f8; padding: 20px; text-align: center; color: #666; }");
        html.append("@media (max-width: 600px) { .info-row { flex-direction: column; } .info-label, .info-value { margin: 2px 0; } }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        // Header
        html.append("<div class='header'>");
        html.append("<h1>KIVICARE</h1>");
        html.append("<p>Bệnh viện chăm sóc sức khỏe</p>");
        html.append("</div>");

        // Content
        html.append("<div class='content'>");
        html.append("<h2 style='color: #333; text-align: center; margin-bottom: 20px;'>🎉 Bạn vừa nhận được mã giảm giá! 🎉</h2>");
        html.append("<p style='text-align: center; color: #666; font-size: 1.1em;'>").append(description).append("</p>");

        // Coupon info
        html.append("<div class='coupon-info'>");
        html.append("<div class='coupon-code'>").append(couponCode).append("</div>");
        html.append("<div class='discount-value'>Giảm ").append(discountValue).append("</div>");

        html.append("<div class='info-row'>");
        html.append("<span class='info-label'>Mã giảm giá:</span>");
        html.append("<span class='info-value'>").append(couponCode).append("</span>");
        html.append("</div>");

        html.append("<div class='info-row'>");
        html.append("<span class='info-label'>Giá trị giảm:</span>");
        html.append("<span class='info-value'>").append(discountValue).append("</span>");
        html.append("</div>");

        html.append("<div class='info-row'>");
        html.append("<span class='info-label'>Ngày hết hạn:</span>");
        html.append("<span class='info-value'>").append(validDate).append("</span>");
        html.append("</div>");

        html.append("</div>");

        html.append("<p style='text-align: center; margin-top: 30px; color: #666;'>");
        html.append("Hãy sử dụng mã giảm giá ngay để được ưu đãi tốt nhất!<br>");
        html.append("Cảm ơn bạn đã tin tưởng KIVICARE.");
        html.append("</p>");

        html.append("</div>");

        // Footer
        html.append("<div class='footer'>");
        html.append("<p>© 2025 KIVICARE Hospital. Tất cả quyền được bảo lưu.</p>");
        html.append("<p>Email này được gửi tự động, vui lòng không trả lời.</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    private String getDiscountInfo(DiscountType discountType, BigDecimal value) {
        if (discountType == DiscountType.PERCENTAGE) {
            return value + "%";
        } else {
            return String.format("%,.0f VNĐ", value);
        }
    }
}
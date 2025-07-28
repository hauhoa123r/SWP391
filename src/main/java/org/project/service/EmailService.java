package org.project.service;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendEmailHtml(String to, String subject, String body);

    void sendResetPasswordEmail(String to, String newPassword);
}

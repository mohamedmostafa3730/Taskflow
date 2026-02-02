package com.example.taskflow.auth.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String toEmail, String verificationCode) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Taskflow – Verify Your Email");
        helper.setText(buildEmailHtml(verificationCode), true);

        javaMailSender.send(mimeMessage);
    }

    private String buildEmailHtml(String code) {
        return """
                <div style="font-family: 'Urbanist', Arial, sans-serif; background: #0b0f14; padding: 40px 20px; min-height: 100vh;">
                  <div style="max-width: 480px; margin: 0 auto; background: rgba(255,255,255,0.08); border: 1px solid rgba(255,255,255,0.15); border-radius: 22px; padding: 40px 32px; text-align: center;">
                    <h2 style="color: #e5e7eb; margin-top: 0; font-size: 1.6rem;">
                      <span style="background: linear-gradient(135deg, #6366f1, #22d3ee); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">Taskflow</span>
                    </h2>
                    <p style="color: #94a3b8; font-size: 0.95rem; margin: 0 0 24px;">Use the code below to verify your email address.</p>
                    <div style="background: rgba(255,255,255,0.1); border-radius: 16px; padding: 20px; margin: 0 auto 24px; display: inline-block;">
                      <span style="color: #fff; font-size: 2.2rem; font-weight: 700; letter-spacing: 12px;">%s</span>
                    </div>
                    <p style="color: #64748b; font-size: 0.82rem; margin: 0;">This code expires in 10 minutes.</p>
                  </div>
                </div>
                """.formatted(code);
    }
}
package org.example.messmate.notification.infrastructure;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class SmtpEmailProvider implements EmailProvider {

    private final JavaMailSender mailSender;
    public SmtpEmailProvider(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String recipient,
                     String subject,
                     String message
    ) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(recipient);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}

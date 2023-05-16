package com.example.service;

import com.example.model.UserActivation;
import com.example.service.interfaces.IMailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class MailService implements IMailService {

    String activationMessage =
            "<p>Pozdrav,</p>"
                    + "<p>Aktivaciju naloga možete obaviti klikom na sledeći link:</p>";

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendMail(String recipientEmail, String token) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("fabcar.project@gmail.com", "FAB CAR");
        helper.setTo(recipientEmail);

        String subject = "Token za reset lozinke";

        String content = "<p>Pozdrav,</p>"
                + "<p>Zatražili ste da resetujete Vašu lozinku.</p>"
                + "<p>Ovo je token koji Vam je potreban za reset lozinke:</p>"
                + token + "<br>"
                + "<p>Ignorišite ovaj email ako se sećate Vaše lozinke, "
                + "ili ako niste napravili ovaj zahtev.</p>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    @Override
    public void sendTwoFactorAuthMail(String recipientEmail, String token) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("fabcar.project@gmail.com", "FAB CAR");
        helper.setTo(recipientEmail);

        String subject = "2Factor token";

        String content = "<p>Pozdrav,</p>"
                + "<p>Onesite ovaj token za two factor autentifikaciju.</p>"
                + token + "<br>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    @Override
    public void sendActivationEmail(String recipientEmail, UserActivation activation) throws MessagingException,UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("fabcar.project@gmail.com", "FAB CAR");
        helper.setTo(recipientEmail);

        String subject = "Aktivacija naloga";

        String activationLink = "http://localhost:8084/api/user/activate/" + activation.getId();
        String body = this.activationMessage +
                "<a href='" + activationLink +"'>" + activationLink;

        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }
}
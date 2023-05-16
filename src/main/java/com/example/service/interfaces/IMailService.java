package com.example.service.interfaces;

import com.example.model.UserActivation;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface IMailService {
    void sendMail(String recipientEmail, String token) throws MessagingException, UnsupportedEncodingException;

    void sendTwoFactorAuthMail(String recipientEmail, String token) throws MessagingException, UnsupportedEncodingException;

    void sendActivationEmail(String recipientEmail, UserActivation activation) throws MessagingException,UnsupportedEncodingException;
}
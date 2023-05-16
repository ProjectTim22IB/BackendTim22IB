package com.example.service.interfaces;

import com.example.model.UserActivation;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface ITwilioService {

    void sendResetPasswordCode(String toPhoneNumber, String token);

    void sendActivationSMS(String toPhoneNumber, UserActivation activation) throws MessagingException, UnsupportedEncodingException;
}

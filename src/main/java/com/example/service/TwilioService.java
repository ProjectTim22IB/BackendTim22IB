package com.example.service;

import com.example.model.UserActivation;
import com.example.service.interfaces.ITwilioService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;

@Service
public class TwilioService implements ITwilioService {

    String activationMessage =
            "<p>Pozdrav,</p>"
                    + "<p>Aktivaciju naloga možete obaviti klikom na sledeći link:</p>";

    public static final String ACCOUNT_SID = "AC019f343b932c807b4384d3c978abb1f2";
    public static final String AUTH_TOKEN = "8a68a393cd36c7764fdfba1f060479ad";
    public static final String FROM_PHONE_NUMBER = "whatsapp:+14155238886";
    public static final String TO_PHONE_NUMBER = "whatsapp:+381665423903";

    public void sendResetPasswordCode(String toPhoneNumber, String token) {

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(TO_PHONE_NUMBER),
                new com.twilio.type.PhoneNumber(FROM_PHONE_NUMBER),
                token).create();
        System.out.println(message.getSid());
    }

    @Override
    public void sendActivationSMS(String toPhoneNumber, UserActivation activation) {

        String activationLink = "http://localhost:8084/api/user/activate/" + activation.getId();
//        String body = this.activationMessage +
//                "<a href='" + activationLink +"'>" + activationLink;

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(TO_PHONE_NUMBER),
                new com.twilio.type.PhoneNumber(FROM_PHONE_NUMBER),
                "http://").create();
        System.out.println(message.getSid());
    }
}
package com.example.service.interfaces;

import com.example.dto.LoginDTO;
import com.example.dto.RegistrationUserDTO;
import com.example.dto.ResetPasswordDTO;
import com.example.dto.TokensDTO;
import com.example.exceptions.EmailAlreadyExistException;
import com.example.exceptions.UserNotFoundException;
import com.example.model.User;
import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface IUserService extends UserDetailsService {

    Optional<User> getUser(String id);

    Optional<User> getByEmail(String email);

    Optional<User> getByPhoneNumber(String phoneNumber);

    X500Name generateX500Name(User user);
    User createUserByEmail(RegistrationUserDTO userDto) throws EmailAlreadyExistException, MessagingException, UnsupportedEncodingException;

    User createUserBySMS(RegistrationUserDTO userDto) throws EmailAlreadyExistException, MessagingException, UnsupportedEncodingException;

    void resetPasswordByEmail(String email) throws MessagingException, UnsupportedEncodingException, UserNotFoundException;

    void resetPasswordBySMS(String id) throws UserNotFoundException, MessagingException, UnsupportedEncodingException;

    TokensDTO loginUser(LoginDTO login) throws Exception;

    void changePasswordWithResetToken(String id, ResetPasswordDTO request) throws Exception;
}

package com.example.service;

import com.example.dto.LoginDTO;
import com.example.dto.RegistrationUserDTO;
import com.example.dto.TokensDTO;
import com.example.enums.Role;
import com.example.exceptions.EmailAlreadyExistException;
import com.example.mapper.RegistrationUserMapper;
import com.example.model.User;
import com.example.model.UserActivation;
import com.example.repository.UserActivationRepository;
import com.example.repository.UserRepository;
import com.example.security.TokenUtils;
import com.example.service.interfaces.IMailService;
import com.example.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService implements IUserService {

    private UserRepository userRepository;
    private UserActivationRepository userActivationRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    private TokenUtils tokenUtils;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Lazy
    @Autowired
    private IMailService mailService;

    @Autowired
    public UserService(UserRepository userRepository, UserActivationRepository userActivationRepository) {
        this.userRepository = userRepository;
        this.userActivationRepository = userActivationRepository;
    }

    @Override
    public Optional<User> getUser(String id) {
        return  this.userRepository.findById(Long.parseLong(id));
    }

    @Override
    public Optional<User> getByEmail(String email){
        return this.userRepository.findByEmail(email);
    }

    @Override
    public User createUser(RegistrationUserDTO userDto) throws EmailAlreadyExistException, MessagingException, UnsupportedEncodingException {

        if(userRepository.findByEmail(userDto.getEmail()).isPresent() == false){

            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User user = RegistrationUserMapper.MAPPER.mapToUser(userDto);

            user.setRole(Role.USER);
            User savedUser = userRepository.save(user);
            UserActivation userActivation = userActivationRepository.save(new UserActivation(savedUser));

            mailService.sendActivationEmail("filipvuksan.iphone@gmail.com", userActivation);

            return savedUser;

        }else {
            throw new EmailAlreadyExistException("User already exist");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).get();
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return user;
        }
    }

    @Override
    public TokensDTO loginUser(LoginDTO login) {
        User user = getByEmail(login.getEmail()).get();
        TokensDTO tokens = new TokensDTO();
        tokens.setAccessToken(this.tokenUtils.generateToken(user));
        tokens.setRefreshToken(this.tokenUtils.generateRefreshToken(user));
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokens;
    }

    @Override
    public void resetPasswordByEmail(String id) {
        try {
            User user = this.getUser(id).get();
            String token = String.valueOf(new Random().nextInt(900000) + 100000);
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiration(LocalDateTime.now().plusMinutes(10));

            mailService.sendMail("filipvuksan.iphone@gmail.com", token);
            this.userRepository.save(user);

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetPasswordBySMS(String id) {
        try {
            User user = this.getUser(id).get();
            String token = String.valueOf(new Random().nextInt(900000) + 100000);
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiration(LocalDateTime.now().plusMinutes(10));

            mailService.sendMail("filipvuksan.iphone@gmail.com", token);
            this.userRepository.save(user);

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

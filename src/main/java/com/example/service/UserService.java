package com.example.service;

import com.example.dto.LoginDTO;
import com.example.dto.RegistrationUserDTO;
import com.example.dto.ResetPasswordDTO;
import com.example.dto.TokensDTO;
import com.example.enums.Role;
import com.example.exceptions.EmailAlreadyExistException;
import com.example.exceptions.UserNotFoundException;
import com.example.mapper.RegistrationUserMapper;
import com.example.model.User;
import com.example.model.UserActivation;
import com.example.repository.UserActivationRepository;
import com.example.repository.UserRepository;
import com.example.security.TokenUtils;
import com.example.service.interfaces.IMailService;
import com.example.service.interfaces.ITwilioService;
import com.example.service.interfaces.IUserService;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Lazy
    @Autowired
    private ITwilioService twilioService;

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
    public Optional<User> getByPhoneNumber(String phoneNumber){
        return this.userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public User createUserByEmail(RegistrationUserDTO userDto) throws EmailAlreadyExistException, MessagingException, UnsupportedEncodingException {

        if(userRepository.findByEmail(userDto.getEmail()).isPresent() == false && userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent() == false){

            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User user = RegistrationUserMapper.MAPPER.mapToUser(userDto);

            user.setRole(Role.USER);
            User savedUser = userRepository.save(user);
            UserActivation userActivation = new UserActivation(savedUser);
            this.userActivationRepository.save(userActivation);

            mailService.sendActivationEmail("filipvuksan.iphone@gmail.com", userActivation);

            return savedUser;

        }else {
            throw new EmailAlreadyExistException("User already exist");
        }
    }

    @Override
    public User createUserBySMS(RegistrationUserDTO userDto) throws EmailAlreadyExistException, MessagingException, UnsupportedEncodingException {

        if(userRepository.findByEmail(userDto.getEmail()).isPresent() == false && userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent() == false){

            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User user = RegistrationUserMapper.MAPPER.mapToUser(userDto);

            user.setRole(Role.USER);
            User savedUser = userRepository.save(user);
            UserActivation userActivation = new UserActivation(savedUser);
            this.userActivationRepository.save(userActivation);

            twilioService.sendActivationSMS(userDto.getPhoneNumber(), userActivation);

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
    public TokensDTO loginUser(LoginDTO login) throws Exception {
        User user = getByEmail(login.getEmail()).get();
        if(user.isAutentificated() == false){
            throw new Exception();
        }
        TokensDTO tokens = new TokensDTO();
        tokens.setAccessToken(this.tokenUtils.generateToken(user));
        tokens.setRefreshToken(this.tokenUtils.generateRefreshToken(user));
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokens;
    }

    @Override
    public X500Name generateX500Name(User user) {
        X500NameBuilder builder = new X500NameBuilder(BCStrictStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, user.getName() + " " + user.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, user.getName());
        builder.addRDN(BCStyle.SURNAME, user.getSurname());
        builder.addRDN(BCStyle.UID, user.getId().toString());
        return builder.build();
    }

    public void resetPasswordByEmail(String email) throws UserNotFoundException, MessagingException, UnsupportedEncodingException {
            if(this.userRepository.findByEmail(email).isPresent() == false){
                throw new UserNotFoundException("User not found");
            }
            User user = this.getByEmail(email).get();
            String token = String.valueOf(new Random().nextInt(900000) + 100000);
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiration(LocalDateTime.now().plusMinutes(10));

            mailService.sendMail("filipvuksan.iphone@gmail.com", token);
            this.userRepository.save(user);
    }

    @Override
    public void resetPasswordBySMS(String toPhoneNumber) throws UserNotFoundException {
        if(this.userRepository.findByPhoneNumber(toPhoneNumber).isPresent() == false){
            throw new UserNotFoundException("User not found");
        }
        User user = this.getByPhoneNumber(toPhoneNumber).get();
        String token = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiration(LocalDateTime.now().plusMinutes(10));

        twilioService.sendResetPasswordCode(toPhoneNumber, token);
        this.userRepository.save(user);
    }

    public void changePasswordWithResetToken(String id, ResetPasswordDTO request) throws Exception {
        User user = this.getUser(id).get();

        if(!request.getNewPassword().equals(request.getRepeateNewPassword())){
            throw new Exception();
        }

        if (user.getResetPasswordToken() == null || user.getResetPasswordTokenExpiration().isBefore(LocalDateTime.now()) || !user.getResetPasswordToken().equals(request.getCode())) {
            throw new Exception();
        }

        user.getOldPasswords().add(user.getPassword());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiration(null);
        this.userRepository.save(user);
    }
}

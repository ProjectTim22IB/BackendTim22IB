package com.example.service;

import com.example.dto.*;
import com.example.enums.AutentificationType;
import com.example.enums.Role;
import com.example.exceptions.*;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService implements IUserService {

    private static final int MAX_PASSWORDS = 4;
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
    public String generateToken(){
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    @Override
    public List<User> getAll(){
        return this.userRepository.findAll();
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
    public User createUser(RegistrationUserDTO userDto, AutentificationType type) {

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = RegistrationUserMapper.MAPPER.mapToUser(userDto);

        user.setRole(Role.USER);
        user.setAutentificationType(type);
        user.setLastPasswordResetDate(LocalDateTime.now().plusYears(1));
        addPassword(user.getPassword(), user.getOldPasswords());
        User savedUser = userRepository.save(user);

        return savedUser;
    }

    public UserActivation createUserActivation(User user) {

        UserActivation userActivation = new UserActivation(user);
        this.userActivationRepository.save(userActivation);

        return userActivation;
    }

    @Override
    public User createUserByEmail(RegistrationUserDTO userDto) throws EmailAlreadyExistException, MessagingException, UnsupportedEncodingException {

        if(!userRepository.findByEmail(userDto.getEmail()).isPresent() && !userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent()){

            User user = createUser(userDto, AutentificationType.EMAIL);
            UserActivation userActivation = createUserActivation(user);
            mailService.sendActivationEmail("filipvuksan.iphone@gmail.com", userActivation);

            return user;
        }
        else {
            throw new EmailAlreadyExistException("User already exist");
        }
    }

    @Override
    public User createUserBySMS(RegistrationUserDTO userDto) throws EmailAlreadyExistException, MessagingException, UnsupportedEncodingException {

        if(!userRepository.findByEmail(userDto.getEmail()).isPresent() && !userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent()){

            User user = createUser(userDto, AutentificationType.NUMBER);
            UserActivation userActivation = createUserActivation(user);
            twilioService.sendActivationSMS(userDto.getPhoneNumber(), userActivation);

            return user;
        }
        else {
            throw new EmailAlreadyExistException("User already exist");
        }
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).get();
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        if(passwordExpired(user)){
            try {
                throw new PasswordExpiredException("Your password is expired. Please, renew it.");
            } catch (PasswordExpiredException e) {
                e.getMessage();
            }
        }
        return user;
    }

    @Override
    public User loadUserByEmail(String email) throws UsernameNotFoundException, PasswordExpiredException {

        if(!this.userRepository.findByEmail(email).isPresent()){
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
        }

        User user = this.userRepository.findByEmail(email).get();
        if (passwordExpired(user)) {
            throw new PasswordExpiredException("Your password is expired. Please, renew it.");
        }
        return user;
    }

    @Override
    public TokensDTO loginUser(LoginDTO login) throws NotAutentificatedException, PasswordExpiredException, UserNotFoundException, MessagingException, UnsupportedEncodingException {

        User user = loadUserByEmail(login.getEmail());
        System.out.println(user.getEmail());

        if(!user.isAutentificated()){
            throw new NotAutentificatedException("Not autentificated");
        }

        TokensDTO tokens = new TokensDTO();
        tokens.setAccessToken(this.tokenUtils.generateToken(user));
        tokens.setRefreshToken(this.tokenUtils.generateRefreshToken(user));
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(user.getAutentificationType() == AutentificationType.EMAIL){
            sendTwoFactorAuthToken(user.getEmail(), AutentificationType.EMAIL);
        }

        if(user.getAutentificationType() == AutentificationType.NUMBER){
            sendTwoFactorAuthToken(user.getPhoneNumber(), AutentificationType.NUMBER);
        }

        return tokens;
    }

    public void sendTwoFactorAuthToken(String emailOrNumber, AutentificationType type) throws UserNotFoundException, MessagingException, UnsupportedEncodingException {

        if(type == AutentificationType.EMAIL){
            sendTwoFactorAuthTokenEmail(emailOrNumber);
        }

        if(type == AutentificationType.NUMBER){
            sendTwoFactorAuthTokenNumber(emailOrNumber);
        }
    }

    public void sendTwoFactorAuthTokenEmail(String emailOrNumber) throws UserNotFoundException, MessagingException, UnsupportedEncodingException {

        if(!this.userRepository.findByEmail(emailOrNumber).isPresent()){
            throw new UserNotFoundException("User not found");
        }

        User user = this.getByEmail(emailOrNumber).get();
        String token = generateToken();
        user.setTwoFactorAuthToken(token);
        user.setTwoFactorAuthTokenExpiration(LocalDateTime.now().plusMinutes(1));
        this.userRepository.save(user);

        mailService.sendTwoFactorAuthMail("filipvuksan.iphone@gmail.com", token);
    }

    public void sendTwoFactorAuthTokenNumber(String emailOrNumber) throws UserNotFoundException, MessagingException, UnsupportedEncodingException {
        if(!this.userRepository.findByPhoneNumber(emailOrNumber).isPresent()){
            throw new UserNotFoundException("User not found");
        }
        User user = this.getByPhoneNumber(emailOrNumber).get();
        String token = generateToken();
        user.setTwoFactorAuthToken(token);
        user.setTwoFactorAuthTokenExpiration(LocalDateTime.now().plusMinutes(10));
        this.userRepository.save(user);

        twilioService.sendResetPasswordCode(emailOrNumber, token);
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

    @Override
    public void resetPasswordByEmail(String email) throws UserNotFoundException, MessagingException, UnsupportedEncodingException {

        if(!this.userRepository.findByEmail(email).isPresent()){
            throw new UserNotFoundException("User not found");
        }

        User user = this.getByEmail(email).get();
        String token = generateToken();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiration(LocalDateTime.now().plusMinutes(10));

        mailService.sendMail("filipvuksan.iphone@gmail.com", token);
        this.userRepository.save(user);
    }

    @Override
    public void resetPasswordBySMS(String toPhoneNumber) throws UserNotFoundException {

        if(!this.userRepository.findByPhoneNumber(toPhoneNumber).isPresent()){
            throw new UserNotFoundException("User not found");
        }

        User user = this.getByPhoneNumber(toPhoneNumber).get();
        String token = generateToken();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiration(LocalDateTime.now().plusMinutes(10));

        twilioService.sendResetPasswordCode(toPhoneNumber, token);
        this.userRepository.save(user);
    }

    public void changePasswordWithResetToken(String id, ResetPasswordDTO request) throws Exception {

        User user = this.getUser(id).get();

        if (!request.getNewPassword().equals(request.getRepeateNewPassword())) {
            throw new Exception();
        }

        if (user.getResetPasswordToken() == null || user.getResetPasswordTokenExpiration().isBefore(LocalDateTime.now()) || !user.getResetPasswordToken().equals(request.getCode())) {
            throw new Exception();
        }

        if(isPasswordInList(request.getNewPassword(), user.getOldPasswords()) == true) {
            throw new Exception();
        }

        String newPasswordEncoded = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(newPasswordEncoded);
        addPassword(newPasswordEncoded, user.getOldPasswords());
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiration(null);
        this.userRepository.save(user);
    }

    public boolean passwordExpired(User user) {
        LocalDateTime passwordExpirationDate = user.getLastPasswordResetDate();
        if (passwordExpirationDate != null) {
            LocalDateTime currentDate = LocalDateTime.now();
            return currentDate.isAfter(passwordExpirationDate);
        }
        return false;
    }

    public boolean isPasswordInList(String encodedPassword, List<String> passwordList) {
        for (String password : passwordList) {
            if (passwordEncoder.matches(encodedPassword, password)) {
                return true;
            }
        }
        return false;
    }

    public void addPassword(String encodedPassword, List<String> passwordList) {
        if (passwordList.size() == MAX_PASSWORDS) {
            passwordList.remove(0);
        }
        passwordList.add(encodedPassword);
    }

    @Override
    public void checkTwoFactorAuth(User user, String auth) throws InvalidTwoFactorAuthTokenException, TwoFactorAuthExpiredException {
        String userAuthToken = user.getTwoFactorAuthToken();
        String authtoken = auth;

        if (!userAuthToken.equals(authtoken)){
            throw new InvalidTwoFactorAuthTokenException("Invalid Auth Token");
        }

        if (user.getTwoFactorAuthTokenExpiration().isBefore(LocalDateTime.now())){
            throw new TwoFactorAuthExpiredException("Token expired");
        }
        else{
            user.setTwoFactorAuth(true);
            user.setTwoFactorAuthToken(null);
            user.setTwoFactorAuthTokenExpiration(null);
            user.setTwoFactorAuthTokenSession(LocalDateTime.now().plusMinutes(1));
            this.userRepository.save(user);
        }
    }
}

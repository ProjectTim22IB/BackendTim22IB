package com.example.service;

import com.example.dto.LoginDTO;
import com.example.dto.RegistrationUserDTO;
import com.example.dto.TokensDTO;
import com.example.enums.Role;
import com.example.exceptions.EmailAlreadyExistException;
import com.example.mapper.RegistrationUserMapper;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.TokenUtils;
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

import java.util.Optional;

@Service
public class UserService implements IUserService {

    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    private TokenUtils tokenUtils;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public User createUser(RegistrationUserDTO userDto) throws EmailAlreadyExistException {

        if(userRepository.findByEmail(userDto.getEmail()).isPresent() == false){

            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User user = RegistrationUserMapper.MAPPER.mapToUser(userDto);
            user.setRole(Role.USER);
            User savedUser = userRepository.save(user);

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
    public X500Name generateX500Name(User user) {
        X500NameBuilder builder = new X500NameBuilder(BCStrictStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, user.getName() + " " + user.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, user.getName());
        builder.addRDN(BCStyle.SURNAME, user.getSurname());
        builder.addRDN(BCStyle.UID, user.getId().toString());
        return builder.build();
    }
}

package com.example.controller;

import com.example.dto.LoginDTO;
import com.example.dto.RegistrationUserDTO;
import com.example.dto.TokensDTO;
import com.example.model.User;
import com.example.rest.Message;
import com.example.security.TokenUtils;
import com.example.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;
    private final TokenUtils tokenUtils;
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserController(IUserService userService, TokenUtils tokenUtils, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
//        try{
            User user = this.userService.getByEmail(login.getEmail()).get();
            System.out.println(user.getEmail());
            System.out.println(user.getPassword());
            TokensDTO tokens = new TokensDTO();
            tokens.setAccessToken(this.tokenUtils.generateToken(user));
            tokens.setRefreshToken(this.tokenUtils.generateRefreshToken(user));
            System.out.println(tokens.getAccessToken());
            System.out.println(tokens.getRefreshToken());
            Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
            System.out.println("ASD");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new ResponseEntity<>(tokens, HttpStatus.OK);
//        } catch (Exception e){
//            return new ResponseEntity<>(new Message("Wrong username or password!"), HttpStatus.BAD_REQUEST);
//        }

    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDTO request) throws Exception {
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.OK);
    }




}

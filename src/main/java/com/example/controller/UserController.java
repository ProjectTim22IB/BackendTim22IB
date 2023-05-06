package com.example.controller;

import com.example.dto.LoginDTO;
import com.example.dto.RegistrationUserDTO;
import com.example.exceptions.ActivationExpiredException;
import com.example.exceptions.EmailAlreadyExistException;
import com.example.exceptions.InvalidUserActivation;
import com.example.exceptions.UserAlreadyAutentificatedException;
import com.example.model.User;
import com.example.rest.Message;
import com.example.service.interfaces.IUserActivationService;
import com.example.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;
    private final IUserActivationService userActivationService;

    @Autowired
    public UserController(IUserService userService, IUserActivationService iUserActivationService) {
        this.userService = userService;
        this.userActivationService = iUserActivationService;
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDTO request) {
        try {
            return new ResponseEntity<>(userService.createUser(request), HttpStatus.OK);
        } catch(EmailAlreadyExistException e){
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (MessagingException e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (UnsupportedEncodingException e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/activate/{id}")
    public ResponseEntity<?> activate(@PathVariable("id") String id) {
        try{
            userActivationService.activate(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ActivationExpiredException e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (InvalidUserActivation e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (UserAlreadyAutentificatedException e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        try{
            return new ResponseEntity<>(this.userService.loginUser(login), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(new Message("Wrong username or password!"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}/resetPasswordByEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPasswordByEmail(@PathVariable("id") String id) {
        try{
            this.userService.resetPasswordByEmail(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException | UnsupportedEncodingException e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}/resetPasswordBySMS", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPasswordBySMS(@PathVariable("id") String id) {
        try{
            this.userService.resetPasswordByEmail(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException | UnsupportedEncodingException e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

//    @PutMapping (value = "/{id}/resetPasswordEmail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> changePasswordWithResetCode(@PathVariable("id") String id, @RequestBody RequestUserResetPasswordDTO requestUserResetPasswordDTO) {
//        User user = userService.getUser(id).get();
//        if (user.getResetPasswordToken() == null || user.getResetPasswordTokenExpiration().isBefore(LocalDateTime.now()) || !user.getResetPasswordToken().equals(requestUserResetPasswordDTO.getCode())) {
//            return new ResponseEntity<>("Code is expired or not correct!", HttpStatus.BAD_REQUEST);
//        }
//
//        user.setPassword(passwordEncoder.encode(requestUserResetPasswordDTO.getNewPassword()));
//        user.setResetPasswordToken(null);
//        user.setResetPasswordTokenExpiration(null);
//        userService.add(user);
//
//        return new ResponseEntity<>("Password successfully changed!", HttpStatus.NO_CONTENT);
//    }

    
}

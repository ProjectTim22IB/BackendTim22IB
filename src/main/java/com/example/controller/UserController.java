package com.example.controller;

import com.example.dto.*;
import com.example.exceptions.*;
import com.example.model.User;
import com.example.rest.Message;
import com.example.service.TwilioService;
import com.example.service.interfaces.ITwilioService;
import com.example.service.interfaces.IUserActivationService;
import com.example.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;
    private final IUserActivationService userActivationService;
    private final ITwilioService twilioService;

    @Autowired
    public UserController(IUserService userService, IUserActivationService iUserActivationService, ITwilioService twilioService) {
        this.userService = userService;
        this.userActivationService = iUserActivationService;
        this.twilioService = twilioService;
    }

    @PostMapping(value = "/registrationByEmail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registrationByEmail(@RequestBody RegistrationUserDTO request) {
        try {
            return new ResponseEntity<>(userService.createUserByEmail(request), HttpStatus.OK);
        } catch(EmailAlreadyExistException e){
            return new ResponseEntity<>(new Message(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        } catch (MessagingException e) {
            return new ResponseEntity<>(new Message(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        } catch (UnsupportedEncodingException e) {
            return new ResponseEntity<>(new Message(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/registrationBySMS", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registrationBySMS(@RequestBody RegistrationUserDTO request) {
        try {
            return new ResponseEntity<>(userService.createUserBySMS(request), HttpStatus.OK);
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
        }catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (PasswordExpiredException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (NotAutentificatedException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/resetPasswordByEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPasswordByEmail(@RequestBody RequestResetPasswordDTO request) {
        try{
            this.userService.resetPasswordByEmail(request.getResetEmailOrSMS());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException | UnsupportedEncodingException | UserNotFoundException e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/resetPasswordBySMS", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPasswordBySMS(@RequestBody RequestResetPasswordDTO request) {
        try{
            this.userService.resetPasswordBySMS(request.getResetEmailOrSMS());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException | UnsupportedEncodingException | UserNotFoundException e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping (value = "/{id}/resetPassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changePasswordWithResetCode(@PathVariable("id") String id, @RequestBody ResetPasswordDTO request) {
        try{
            this.userService.changePasswordWithResetToken(id, request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException | UnsupportedEncodingException | UserNotFoundException e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping (value = "/twoFactorAutentification", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<?> twoFactorAutentification(@RequestBody TwoFactorAuthDTO request) {

            String passengerId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId().toString();
            return new ResponseEntity<>(passengerId, HttpStatus.OK);


    }
}

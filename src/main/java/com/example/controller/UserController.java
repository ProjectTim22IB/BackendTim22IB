package com.example.controller;

import com.example.dto.LoginDTO;
import com.example.dto.RegistrationUserDTO;
import com.example.exceptions.EmailAlreadyExistException;
import com.example.rest.Message;
import com.example.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDTO request) {
        try {
            return new ResponseEntity<>(userService.createUser(request), HttpStatus.OK);
        } catch(EmailAlreadyExistException e){
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
}

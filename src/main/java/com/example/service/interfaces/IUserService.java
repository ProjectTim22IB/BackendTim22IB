package com.example.service.interfaces;

import com.example.dto.RegistrationUserDTO;
import com.example.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface IUserService extends UserDetailsService {

    Optional<User> getUser(String id);

    Optional<User> getByEmail(String email);

    User createUser(RegistrationUserDTO userDto);
}
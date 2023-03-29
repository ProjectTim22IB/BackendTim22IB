package com.example.service;

import com.example.dto.RegistrationUserDTO;
import com.example.enums.Role;
import com.example.mapper.RegistrationUserMapper;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    public User createUser(RegistrationUserDTO userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = RegistrationUserMapper.MAPPER.mapToUser(userDto);
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        return savedUser;
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
}

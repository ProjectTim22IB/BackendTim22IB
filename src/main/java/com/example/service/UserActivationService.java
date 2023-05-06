package com.example.service;

import com.example.exceptions.ActivationExpiredException;
import com.example.exceptions.EmailAlreadyExistException;
import com.example.exceptions.InvalidUserActivation;
import com.example.exceptions.UserAlreadyAutentificatedException;
import com.example.model.User;
import com.example.model.UserActivation;
import com.example.repository.UserActivationRepository;
import com.example.repository.UserRepository;
import com.example.service.interfaces.IUserActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserActivationService implements IUserActivationService {

    private UserActivationRepository userActivationRepository;
    private UserRepository userRepository;

    @Autowired
    public UserActivationService(UserActivationRepository userActivationRepository, UserRepository userRepository) {
        this.userActivationRepository = userActivationRepository;
        this.userRepository = userRepository;
    }

    public List<UserActivation> getAll() {
        return (List<UserActivation>) this.userActivationRepository.findAll();
    }

    @Override
    public Optional<UserActivation> getUserActivation(String id) {
        return  this.userActivationRepository.findById(Long.parseLong(id));
    }

    public UserActivation findUserActivationByUserId(String id){
        return this.userActivationRepository.findUserActivationByUserId(Long.parseLong(id));
    }

    @Override
    public void activate(String id) throws ActivationExpiredException, InvalidUserActivation, UserAlreadyAutentificatedException {

        if(this.getUserActivation(id).isPresent()==true){
            UserActivation userActivation = this.getUserActivation(id).get();

            if(userActivation.getUser().isAutentificated() == true) {
                throw new UserAlreadyAutentificatedException("User already autentificated");
            }

            if(userActivation.checkIfExpired() == false){
                User user = this.userRepository.findById(userActivation.getUser().getId()).get();
                user.setAutentificated(true);
                this.userRepository.save(user);
            }
            else {
                throw new ActivationExpiredException("Expired activation, try again");
            }
        }else {
            throw new InvalidUserActivation("Invalid user activation");
        }
    }

    public void add(UserActivation userActivation) {
        this.userActivationRepository.save(userActivation);
    }

    @Override
    public void delete(UserActivation activation) {
        userActivationRepository.delete(activation);
    }

    public void renewActivation(UserActivation activation) {
        activation.setDate(LocalDateTime.now());
        activation.setLifespan(3);
        this.add(activation);
    }
}

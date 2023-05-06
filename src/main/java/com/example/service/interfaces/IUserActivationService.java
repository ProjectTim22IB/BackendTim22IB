package com.example.service.interfaces;

import com.example.exceptions.ActivationExpiredException;
import com.example.exceptions.EmailAlreadyExistException;
import com.example.exceptions.InvalidUserActivation;
import com.example.model.UserActivation;
import java.util.List;
import java.util.Optional;

public interface IUserActivationService {

    List<UserActivation> getAll();

    Optional<UserActivation> getUserActivation(String id);

    void add(UserActivation userActivation);

    void delete(UserActivation activation);

    void renewActivation(UserActivation activation);

    UserActivation findUserActivationByUserId(String id);

    void activate(String id) throws ActivationExpiredException, InvalidUserActivation;
}

package com.example.mapper;

import com.example.dto.RegistrationUserDTO;
import com.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegistrationUserMapper {

    RegistrationUserMapper MAPPER = Mappers.getMapper(RegistrationUserMapper.class);

    RegistrationUserDTO mapToUserDto(User user);

    User mapToUser(RegistrationUserDTO userDto);
}
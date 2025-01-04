package net.javaguides.springboot.service;

import jakarta.validation.Valid;
import net.javaguides.springboot.dto.SettingsDto;
import net.javaguides.springboot.dto.UserDto;
import net.javaguides.springboot.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    void updateUserInformation(@Valid SettingsDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
    void sendPasswordResetToken(String email);
    void resetPassword(String token, String newPassword);
}
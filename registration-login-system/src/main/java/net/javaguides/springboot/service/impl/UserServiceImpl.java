package net.javaguides.springboot.service.impl;

import net.javaguides.springboot.config.SpringSecurity;
import net.javaguides.springboot.dto.SettingsDto;
import net.javaguides.springboot.dto.UserDto;
import net.javaguides.springboot.entity.Role;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.repository.RoleRepository;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           SpringSecurity springSecurity) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void sendPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        String newPassword = generateRandomPassword();

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);

        String message = "Your new password: " + newPassword + "\nLog in and change your password in the settings to your own.";

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom("sofia.edejko@gmail.com");
        emailMessage.setTo(user.getEmail());
        emailMessage.setSubject("Reset password");
        emailMessage.setText(message);

        mailSender.send(emailMessage);
    }

    private String generateRandomPassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }



    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);
    }

    @Override
    public User currentUser() {
        return userRepository.findByEmail(SpringSecurity.getCurrentUserName()).get();
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public void updateUserInformation(SettingsDto settingsDto) {
        String currentEmail = SpringSecurity.getCurrentUserName();
        if (currentEmail == null) {
            throw new UsernameNotFoundException("Current user not found in the security context");
        }

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (settingsDto.getName() != null && !settingsDto.getName().isEmpty()) {
            currentUser.setName(settingsDto.getName());
        }

        boolean emailChanged = false;
        if (settingsDto.getEmail() != null && !settingsDto.getEmail().isEmpty()
                && !settingsDto.getEmail().equals(currentUser.getEmail())) {
            currentUser.setEmail(settingsDto.getEmail());
            emailChanged = true;
        }

        if (settingsDto.getPassword() != null && !settingsDto.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(settingsDto.getPassword()));
        }

        userRepository.save(currentUser);
    }



    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }
}

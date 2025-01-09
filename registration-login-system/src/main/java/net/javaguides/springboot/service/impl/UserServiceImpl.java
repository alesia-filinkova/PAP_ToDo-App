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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void sendPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setTokenExpiryDate(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String resetLink = "http://localhost:8080/set-new-password?token=" + resetToken;
        String message = "[Study planner]\nClick the following link to reset your password (link active for 15 minutes): \n" + resetLink;

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom("sofia.edejko@gmail.com");
        emailMessage.setTo(user.getEmail());
        emailMessage.setSubject("Reset password");
        emailMessage.setText(message);

        mailSender.send(emailMessage);
    }

    public void resetPassword(String token, String newPassword) {

        User user = userRepository.findByResetToken(token.trim())
                .orElseThrow(() -> {
                    return new IllegalArgumentException("Invalid token");
                });

        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        user.setResetToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);

    }


    @Override
    public User findUserByResetToken(String token) {
        return userRepository.findByResetToken(token).orElse(null);
    }

    @Override
    public User currentUser() {
        return userRepository.findByEmail(SpringSecurity.getCurrentUserName())
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
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
        user.setRoles(List.of(role));
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

        if (settingsDto.getEmail() != null && !settingsDto.getEmail().isEmpty()
                && !settingsDto.getEmail().equals(currentUser.getEmail())) {
            currentUser.setEmail(settingsDto.getEmail());
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
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }
}

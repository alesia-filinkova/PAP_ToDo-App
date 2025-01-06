package net.javaguides.springboot;

import net.javaguides.springboot.dto.SettingsDto;
import net.javaguides.springboot.dto.UserDto;
import net.javaguides.springboot.entity.Role;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.repository.RoleRepository;
import net.javaguides.springboot.security.CustomUserDetailsService;
import net.javaguides.springboot.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Mock-based unit tests for UserServiceImpl
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldSaveUserSuccessfully() {
        UserDto userDto = new UserDto(null, "John Doe", "john@example.com", "password123");
        Role role = new Role(1L, "ROLE_ADMIN", new ArrayList<>());

        Mockito.when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encryptedPassword");
        Mockito.when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(role);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(new User());

        userService.saveUser(userDto);

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(roleRepository, Mockito.times(1)).findByName("ROLE_ADMIN");
    }

    @Test
    void shouldUpdateUserInformation() {
        User user = new User(1L, "John", "john@example.com", "encryptedPassword", new ArrayList<>(), null, null);
        CurrentUser.user = user;

        SettingsDto settingsDto = new SettingsDto(null, "John Updated", "john.updated@example.com", "newPassword");

        Mockito.when(passwordEncoder.encode("newPassword")).thenReturn("newEncryptedPassword");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        userService.updateUserInformation(settingsDto);

        Assertions.assertEquals("John Updated", user.getName());
        Assertions.assertEquals("john.updated@example.com", user.getEmail());
        Assertions.assertEquals("newEncryptedPassword", user.getPassword());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User(1L, "John", "john@example.com", "password", new ArrayList<>(), null, null);

        Mockito.when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        User foundUser = userService.findUserByEmail("john@example.com");

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals("john@example.com", foundUser.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail("john@example.com");
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = Arrays.asList(
                new User(1L, "John", "john@example.com", "password", new ArrayList<>(), null, null),
                new User(2L, "Jane", "jane@example.com", "password", new ArrayList<>(), null, null)
        );

        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.findAllUsers();

        Assertions.assertEquals(2, userDtos.size());
        Assertions.assertEquals("John", userDtos.get(0).getName());
        Assertions.assertEquals("Jane", userDtos.get(1).getName());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }
}

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsername() {
        User user = new User(1L, "John", "john@example.com", "password", Arrays.asList(new Role(1L, "ROLE_ADMIN", new ArrayList<>())), null, null);

        Mockito.when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        var userDetails = customUserDetailsService.loadUserByUsername("john@example.com");

        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("john@example.com", userDetails.getUsername());
        Assertions.assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Mockito.when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("nonexistent@example.com"));
    }

}

@SpringBootTest
@AutoConfigureMockMvc
@EnableJpaRepositories(basePackages = "net.javaguides.springboot.repository")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowAccessToPublicRoutes() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRestrictAccessToProtectedRoutes() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isFound());
    }
}

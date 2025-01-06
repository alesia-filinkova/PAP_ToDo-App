package net.javaguides.springboot;
import net.javaguides.springboot.entity.Role;
import net.javaguides.springboot.entity.User;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
    }

    @Test
    void shouldCorrectlySetAndGetRoleFields() {
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldReturnEmptyUserListWhenNewRoleCreated() {
        assertThat(role.getUsers()).isEmpty();
    }

    @Test
    void shouldAddUserToRoleSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        List<User> users = new ArrayList<>();
        users.add(user);
        role.setUsers(users);

        assertThat(role.getUsers()).hasSize(1);
        assertThat(role.getUsers().get(0).getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void shouldVerifyEqualityOfRolesBasedOnIdAndName() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_ADMIN");

        Role role2 = new Role();
        role2.setId(1L);
        role2.setName("ROLE_ADMIN");

        assertThat(role1).isEqualTo(role2);
        assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
    }

    @Test
    void shouldDifferentiateRolesWithDifferentIds() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_USER");

        assertThat(role1).isNotEqualTo(role2);
    }
}

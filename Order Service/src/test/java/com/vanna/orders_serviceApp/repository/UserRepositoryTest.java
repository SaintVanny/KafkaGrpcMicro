package com.vanna.orders_serviceApp.repository;

import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        // Given
        User user = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("user1@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user1@example.com");
        assertThat(found.get().getUsername()).isEqualTo("user1");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        // Given
        User user = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmail("user2@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUser_whenUsernameProvided() {
        // Given
        User user = User.builder()
                .username("user3")
                .email("user3@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByUsernameOrEmail("user3");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("user3");
        assertThat(found.get().getEmail()).isEqualTo("user3@example.com");
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUser_whenEmailProvided() {
        // Given
        User user = User.builder()
                .username("user4")
                .email("user4@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByUsernameOrEmail("user4@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("user4");
        assertThat(found.get().getEmail()).isEqualTo("user4@example.com");
    }

    @Test
    void findByUsernameOrEmail_shouldReturnEmpty_whenNeitherUsernameNorEmailExists() {
        // When
        Optional<User> found = userRepository.findByUsernameOrEmail("nonexistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUser_whenOnlyUsernameMatches() {
        // Given
        User user = User.builder()
                .username("user5")
                .email("user5@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();
        entityManager.persistAndFlush(user);

        // When 
        Optional<User> found = userRepository.findByUsernameOrEmail("user5");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("user5");
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUser_whenOnlyEmailMatches() {
        // Given
        User user = User.builder()
                .username("user6")
                .email("user6@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();
        entityManager.persistAndFlush(user);

        // When 
        Optional<User> found = userRepository.findByUsernameOrEmail("user6@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user6@example.com");
    }

    @Test
    void findByUsernameOrEmail_shouldWorkWithUserWithoutEmail() {
        // Given 
        User user = User.builder()
                .username("userWithoutEmail")
                .email(null)
                .password("password")
                .role(UserRole.USER)
                .build();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByUsernameOrEmail("userWithoutEmail");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("userWithoutEmail");
        assertThat(found.get().getEmail()).isNull();
    }
}
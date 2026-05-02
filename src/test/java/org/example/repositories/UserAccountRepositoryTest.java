package org.example.repositories;

import org.example.entities.Account;
import org.example.entities.User;
import org.example.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserAccountRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("postgres/init_types.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        accountRepository.deleteAll();
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private User createAndSaveUser(String username, String mail, Role role) {
        Account account = new Account();
        account.setFirstname("Jane");
        account.setLastname("Doe");
        account.setPhonenumber("12345678");
        account.setMail(mail);

        User user = new User();
        user.setUsername(username);
        user.setPassword("hashed_password");
        user.setAccount(account);
        user.setRole(role);

        return userRepository.save(user);
    }

    private Account createAndSaveAccount(String mail) {
        Account account = new Account();
        account.setFirstname("Jane");
        account.setLastname("Doe");
        account.setPhonenumber("12345678");
        account.setMail(mail);
        return accountRepository.save(account);
    }

    // ─── Unique Constraint Tests ─────────────────────────────────────────────────

    @Test
    void saveUser_throwsException_onDuplicateUsername() {
        createAndSaveUser("jane_doe", "user@example.com", Role.USER);

        Account account2 = createAndSaveAccount("other@example.com");
        User duplicate = new User();
        duplicate.setUsername("jane_doe");
        duplicate.setPassword("another_hash");
        duplicate.setAccount(account2);
        duplicate.setRole(Role.USER);

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(duplicate),
                "Duplicate username should violate unique constraint");
    }

    @Test
    void saveUser_throwsException_onDuplicateMail() {
        createAndSaveUser("user_one", "duplicate@example.com", Role.USER);

        Account account2 = new Account();
        account2.setMail("duplicate@example.com");

        assertThrows(DataIntegrityViolationException.class,
                () -> accountRepository.saveAndFlush(account2),
                "Duplicate mail should violate unique constraint");
    }

    @Test
    void saveSecondUser_withSameAccount_throwsException() {
        // Verifies the 1:1 constraint — an account can only belong to one user
        User existing = createAndSaveUser("user_one", "shared@example.com", Role.USER);

        User duplicate = new User();
        duplicate.setUsername("user_two");
        duplicate.setPassword("hashed_password");
        duplicate.setAccount(existing.getAccount());
        duplicate.setRole(Role.USER);

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(duplicate),
                "Two users cannot share the same account");
    }

    // ─── Cascade Delete Test ─────────────────────────────────────────────────────

    @Test
    void deleteUser_alsoDeletesLinkedAccount() {
        // Re-fetch so Hibernate loads the @OneToOne account reference and can cascade the delete.
        User user = createAndSaveUser("delete_user", "delete@example.com", Role.USER);
        Long userId = user.getUsersId();
        Long accountId = user.getAccount().getAccountId();

        User managed = userRepository.findById(userId).orElseThrow();
        userRepository.delete(managed);
        userRepository.flush();

        assertFalse(userRepository.findById(userId).isPresent(),
                "User should be deleted");
        assertFalse(accountRepository.findById(accountId).isPresent(),
                "Account should be deleted when its linked user is deleted");
    }
}
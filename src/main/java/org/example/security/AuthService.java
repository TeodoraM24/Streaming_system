package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.entities.Account;
import org.example.entities.User;
import org.example.repositories.AccountRepository;
import org.example.repositories.UserRepository;
import org.example.services.UserValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserValidationService validationService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Business Logic Validation (Length, Characters, etc.)
        validateUsernameAndPassword(request.getUsername(), request.getPassword());

        // 2. Database Uniqueness Validation
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        if (accountRepository.findByMail(request.getMail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        // 3. Create Account
        Account account = new Account();
        account.setFirstname(request.getFirstname());
        account.setLastname(request.getLastname());
        account.setPhonenumber(request.getPhonenumber());
        account.setMail(request.getMail());
        Account savedAccount = accountRepository.save(account);

        // 4. Create User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccount(savedAccount);
        User savedUser = userRepository.save(user);

        // 5. Generate Token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, savedUser.getUsersId(), savedUser.getUsername(), savedAccount.getAccountId());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);

        Long accountId = user.getAccount() != null ? user.getAccount().getAccountId() : null;
        return new AuthResponse(token, user.getUsersId(), user.getUsername(), accountId);
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        // 1. Validate the new password against business rules
        List<String> passErrors = validationService.validatePassword(request.getNewPassword());
        if (!passErrors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(" | ", passErrors));
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 2. Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }

        // 3. Prevent setting the same password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Helper to run business validations and throw 400 Bad Request if they fail.
     */
    private void validateUsernameAndPassword(String username, String password) {
        List<String> userErrors = validationService.validateUsername(username);
        List<String> passErrors = validationService.validatePassword(password);

        List<String> allErrors = new ArrayList<>();
        allErrors.addAll(userErrors);
        allErrors.addAll(passErrors);

        if (!allErrors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(" | ", allErrors));
        }
    }
}
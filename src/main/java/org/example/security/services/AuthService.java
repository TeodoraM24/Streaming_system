package org.example.security.services;

import lombok.RequiredArgsConstructor;
import org.example.entities.Account;
import org.example.entities.User;
import org.example.enums.Role;
import org.example.repositories.AccountRepository;
import org.example.repositories.UserRepository;
import org.example.security.DTOs.AuthResponse;
import org.example.security.DTOs.ChangePasswordRequest;
import org.example.security.DTOs.LoginRequest;
import org.example.security.DTOs.RegisterRequest;
import org.example.security.jwt.JwtService;
import org.example.services.AccountValidation;
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
    private final AccountValidation accountValidation;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateUsernameAndPassword(request.getUsername(), request.getPassword());
        validateAccountDetails(request);

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        if (accountRepository.findByMail(request.getMail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (accountRepository.findByPhonenumber(request.getPhonenumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already in use");
        }

        Account account = new Account();
        account.setFirstname(request.getFirstname());
        account.setLastname(request.getLastname());
        account.setPhonenumber(request.getPhonenumber());
        account.setMail(request.getMail());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccount(account);
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user); // cascades to account automatically

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, savedUser.getUsersId(), savedUser.getUsername(),
                savedUser.getAccount().getAccountId());
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
        List<String> passErrors = validationService.validatePassword(request.getNewPassword());
        if (!passErrors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(" | ", passErrors));
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

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

    private void validateAccountDetails(RegisterRequest request) {
        List<String> accountErrors = new ArrayList<>();
        accountErrors.addAll(accountValidation.validateName(request.getFirstname()));
        accountErrors.addAll(accountValidation.validateName(request.getLastname()));
        accountErrors.addAll(accountValidation.validatePhoneNumber(request.getPhonenumber()));
        accountErrors.addAll(accountValidation.validateEmail(request.getMail()));

        if (!accountErrors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(" | ", accountErrors));
        }
    }
}

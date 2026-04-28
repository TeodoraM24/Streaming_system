package org.example.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ── Register request ──────────────────────────────────────────────────────────

@Data
@NoArgsConstructor
@AllArgsConstructor
class RegisterRequest {
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String phonenumber;
    private String mail;
}

// ── Login request ─────────────────────────────────────────────────────────────

@Data
@NoArgsConstructor
@AllArgsConstructor
class LoginRequest {
    private String username;
    private String password;
}

// ── Auth response ─────────────────────────────────────────────────────────────

@Data
@NoArgsConstructor
@AllArgsConstructor
class AuthResponse {
    private String token;
    private Long usersId;
    private String username;
    private Long accountId;
}

// ── Change password request ───────────────────────────────────────────────────

@Data
@NoArgsConstructor
@AllArgsConstructor
class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
}
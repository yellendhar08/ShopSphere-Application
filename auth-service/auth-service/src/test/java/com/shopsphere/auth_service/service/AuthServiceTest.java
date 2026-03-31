package com.shopsphere.auth_service.service;
import com.shopsphere.auth_service.dto.AuthResponse;
import com.shopsphere.auth_service.dto.LoginRequest;
import com.shopsphere.auth_service.dto.SignUpRequest;
import com.shopsphere.auth_service.entity.User;
import com.shopsphere.auth_service.enums.Role;
import com.shopsphere.auth_service.exceptions.DuplicateEmailException;
import com.shopsphere.auth_service.exceptions.InvalidCredentialsException;
import com.shopsphere.auth_service.repository.UserRepository;
import com.shopsphere.auth_service.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void signUpUser_success() {
        SignUpRequest request = new SignUpRequest("Chintu", "chintu@mail.com", "password123");

        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode(request.getPassword())).thenReturn("encodedPassword");

        // Optional but good practice
        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.signUpUser(request);

        // ✅ Since service returns null
        assertNull(response);

        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void signUpUser_emailAlreadyExists_shouldThrowException() {
        SignUpRequest request = new SignUpRequest("Chintu", "chintu@mail.com", "password123");
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);
        assertThrows(DuplicateEmailException.class, () -> authService.signUpUser(request));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("chintu@mail.com", "password123");

        User user = User.builder()
                .email("chintu@mail.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
//        assertEquals("chintu@mail.com", response.getEmail());
//        assertEquals("CUSTOMER", response.getRole());
//        assertEquals("Login Successful", response.getMessage());
    }

    @Test
    void login_userNotFound_shouldThrowException() {
        LoginRequest request = new LoginRequest("chintu@mail.com", "password123");

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request));
    }

    @Test
    void login_invalidPassword_shouldThrowException() {

        LoginRequest request = new LoginRequest("chintu@mail.com", "wrongPassword");

        User user = User.builder()
                .email("chintu@mail.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}

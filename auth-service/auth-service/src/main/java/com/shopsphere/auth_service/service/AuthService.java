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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository repository;
    private final PasswordEncoder encoder;


    public AuthResponse signUpUser(SignUpRequest request){
        if(repository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already registered, You can login with credentials");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        repository.save(user);

//        return AuthResponse.builder()
////                .email(user.getEmail())
////                .role(user.getRole().name())
//                .message("User registered successfully")
//                .build();
        return null;
    }

    public AuthResponse login(LoginRequest request) {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!encoder.matches(request.getPassword(),user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(token)
//                .email(user.getEmail())
//                .role(user.getRole().name())
//                .message("Login Successful")
                .build();
    }
}

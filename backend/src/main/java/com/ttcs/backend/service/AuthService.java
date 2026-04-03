package com.ttcs.backend.service;

import com.ttcs.backend.auth.dto.AuthResponse;
import com.ttcs.backend.auth.dto.LoginRequest;
import com.ttcs.backend.auth.dto.RegisterRequest;
import com.ttcs.backend.entity.Role;
import com.ttcs.backend.entity.User;
import com.ttcs.backend.repository.RoleRepository;
import com.ttcs.backend.repository.UserRepository;
import com.ttcs.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
                String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

                if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email nay da duoc su dung");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("CUSTOMER").build()));

        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone().trim())
                .role(customerRole)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().getName());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole().getName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoac mat khau khong chinh xac"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .build();
    }
}

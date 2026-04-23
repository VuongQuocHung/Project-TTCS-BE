package com.laptopshop.controller.auth;

import com.laptopshop.dto.*;
import com.laptopshop.entity.Role;
import com.laptopshop.entity.User;
import com.laptopshop.repository.UserRepository;
import com.laptopshop.security.GoogleAuthService;
import com.laptopshop.security.JwtTokenProvider;
import com.laptopshop.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Xác thực người dùng (Login, Register, Password)")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        try {
            GoogleIdToken idToken = googleAuthService.verify(request.getIdToken());
            if (idToken == null) {
                return ResponseEntity.badRequest().build();
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            User user = userService.getOrCreateUserFromGoogle(email, name);
            String token = tokenProvider.generateToken(user.getUsername());
            String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            return ResponseEntity.ok(AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        if (tokenProvider.validateToken(refreshToken)) {
            String username = tokenProvider.getUsername(refreshToken);
            User user = userRepository.findByUsername(username).orElseThrow();
            
            if (refreshToken.equals(user.getRefreshToken())) {
                String newToken = tokenProvider.generateToken(username, 86400000); // 1 day access token
                return ResponseEntity.ok(AuthResponse.builder()
                        .token(newToken)
                        .refreshToken(refreshToken)
                        .username(user.getUsername())
                        .role(user.getRole().name())
                        .build());
            }
        }
        
        return ResponseEntity.badRequest().body("Invalid refresh token");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.processForgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset email sent successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}

package com.ttcs.backend.security;

import com.ttcs.backend.entity.Role;
import com.ttcs.backend.entity.User;
import com.ttcs.backend.repository.RoleRepository;
import com.ttcs.backend.repository.UserRepository;
import com.ttcs.backend.security.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");
        String avatarUrl = oauth2User.getAttribute("picture");

        // Tìm hoặc tạo user
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Tạo user mới từ Google profile
            Role customerRole = roleRepository.findByName("CUSTOMER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("CUSTOMER").build()));

            user = User.builder()
                    .email(email)
                    .fullName(name)
                    .googleId(googleId)
                    .avatarUrl(avatarUrl)
                    .provider("GOOGLE")
                    .role(customerRole)
                    .build();

            userRepository.save(user);
        } else {
            // Cập nhật thông tin Google nếu chưa có
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                user.setAvatarUrl(avatarUrl);
                user.setProvider("GOOGLE");
                userRepository.save(user);
            }
        }

        // Tạo JWT token
        String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());

        // Redirect về frontend với token
        String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
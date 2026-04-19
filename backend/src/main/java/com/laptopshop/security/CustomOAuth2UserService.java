package com.laptopshop.security;

import com.laptopshop.entity.Role;
import com.laptopshop.entity.User;
import com.laptopshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user with provider info if not set
            if (user.getProvider() == null) {
                user.setProvider("GOOGLE");
                user.setProviderId(oAuth2User.getName());
                userRepository.save(user);
            }
        } else {
            // Register new user
            user = User.builder()
                    .username(email) // Using email as username for OAuth2 users
                    .email(email)
                    .fullName(oAuth2User.getAttribute("name"))
                    .role(Role.CUSTOMER)
                    .provider("GOOGLE")
                    .providerId(oAuth2User.getName())
                    .enabled(true)
                    .build();
            userRepository.save(user);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }
}

package com.ttcs.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class GoogleTokenVerifierService {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://oauth2.googleapis.com")
            .build();

    @Value("${google.oauth.client-id:}")
    private String googleClientId;

    public GoogleUserInfo verifyIdToken(String idToken) {
        try {
            Map<String, Object> tokenInfo = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/tokeninfo")
                            .queryParam("id_token", idToken)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (tokenInfo == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google token khong hop le");
            }

            String email = stringValue(tokenInfo.get("email"));
            if (email == null || email.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Khong lay duoc email tu Google token");
            }

            String emailVerified = stringValue(tokenInfo.get("email_verified"));
            if (!"true".equalsIgnoreCase(emailVerified)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email Google chua duoc xac minh");
            }

            String aud = stringValue(tokenInfo.get("aud"));
            if (googleClientId != null && !googleClientId.isBlank() && !googleClientId.equals(aud)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google token khong dung client");
            }

            String fullName = stringValue(tokenInfo.get("name"));
            if (fullName == null || fullName.isBlank()) {
                fullName = email.split("@")[0];
            }

            String sub = stringValue(tokenInfo.get("sub"));
            return new GoogleUserInfo(email, fullName, sub);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google token khong hop le", ex);
        }
    }

    private static String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    public record GoogleUserInfo(String email, String fullName, String googleSub) {
    }
}

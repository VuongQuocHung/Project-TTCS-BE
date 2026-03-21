package com.ttcs.backend.payload;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}

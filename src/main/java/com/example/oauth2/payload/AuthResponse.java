package com.example.oauth2.payload;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private UserInfoResponse userInfoResponse;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }


}
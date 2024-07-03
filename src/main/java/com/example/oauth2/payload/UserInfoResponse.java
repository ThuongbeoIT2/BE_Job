package com.example.oauth2.payload;

import com.example.oauth2.model.AuthProvider;
import com.example.oauth2.model.Role;

import com.example.oauth2.model.User;
import lombok.Getter;
import lombok.Setter;


import java.util.Set;

@Getter
@Setter
public class UserInfoResponse {
    private Long id;

    private String name;

    private String email;

    private String imageUrl;

    private Boolean emailVerified = false;

    private AuthProvider provider;

    private String providerId;


    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.emailVerified = user.getEmailVerified();
        this.provider = user.getProvider();
        this.providerId = user.getProviderId();
    }
}

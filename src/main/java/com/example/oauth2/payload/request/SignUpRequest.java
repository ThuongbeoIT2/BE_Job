package com.example.oauth2.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
public class SignUpRequest {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;


}


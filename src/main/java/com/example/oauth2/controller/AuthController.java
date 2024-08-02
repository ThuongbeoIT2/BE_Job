package com.example.oauth2.controller;

import com.example.oauth2.exception.BadRequestException;
import com.example.oauth2.model.AuthProvider;
import com.example.oauth2.model.Role;
import com.example.oauth2.model.User;
import com.example.oauth2.payload.ApiResponse;
import com.example.oauth2.payload.AuthResponse;
import com.example.oauth2.payload.request.LoginRequest;
import com.example.oauth2.payload.request.SignUpRequest;
import com.example.oauth2.repository.UserRepository;
import com.example.oauth2.security.TokenProvider;

import com.example.oauth2.token.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@RestController
public class AuthController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @GetMapping (value = "/validtoken")
    public ResponseEntity<Boolean> LoginOauth2Success(@RequestParam("token") String token){
        if (tokenService.isTokenValid(token)){
            return  ResponseEntity.ok(true);
        }
        return  ResponseEntity.ok(false);
    }
    @PostMapping("/auth/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return getResponseEntity(loginRequest);
    }
    @PostMapping("/auth/admin/login")
    public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginRequest loginRequest) {
        return getResponseEntity(loginRequest);
    }

    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);
        User user = userRepository.findByEmail(authentication.getName()).get();
        tokenService.saveUserToken(user,token);
        return ResponseEntity.ok(new AuthResponse(token));
    }


    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roleSet= new HashSet<>();
        roleSet.add(Role.ROLE_USER);
        user.setRoles(roleSet);
        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@",""));
    }
}

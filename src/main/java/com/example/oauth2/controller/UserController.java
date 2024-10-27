package com.example.oauth2.controller;

import com.example.oauth2.exception.ResourceNotFoundException;
import com.example.oauth2.model.User;
import com.example.oauth2.payload.ApiResponse;
import com.example.oauth2.payload.UserInfoResponse;
import com.example.oauth2.payload.request.ChangePasswordRequest;
import com.example.oauth2.repository.UserRepository;
import com.example.oauth2.security.CurrentUser;
import com.example.oauth2.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping( "/user/me")
//    @PreAuthorize("hasRole('USER')")
    public UserInfoResponse getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId()).map(user -> {
                    UserInfoResponse userInfoResponse= new UserInfoResponse(user);
                    return userInfoResponse;
                })
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
    @PostMapping( "/user/email")
    public UserInfoResponse getManagerStore(@RequestParam String email) {
        return userRepository.findByEmail(email).map(user -> {
                    UserInfoResponse userInfoResponse= new UserInfoResponse(user);
                    return userInfoResponse;
                })
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @PostMapping( "/user/me/changepassword")
    public ResponseEntity<ApiResponse> ChangePassword(@CurrentUser UserPrincipal userPrincipal, @RequestBody ChangePasswordRequest changePasswordRequest) {
        User user= userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        if (passwordEncoder.matches(user.getPassword(), changePasswordRequest.getOldPassword())){
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse("OK","Success","")
            );
        }else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                    new ApiResponse("FAILED","OLD_PASSWORD_INCORRECT","")
            );
        }
    }

}

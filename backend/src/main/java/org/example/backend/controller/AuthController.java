package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.annotation.CurrentUser;
import org.example.backend.dto.UserResponse;
import org.example.backend.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public UserResponse getMe(@CurrentUser User user) {
        return user == null ? UserResponse.guest() : UserResponse.of(user);
    }
}

package ru.netology.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netology.dto.LoginRequest;
import ru.netology.service.AuthenticationService;
import java.util.Map;

@Validated
@AllArgsConstructor
@RestController
public class AuthenticationController {
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authenticationService.login(request);
        return ResponseEntity.ok(Map.of("auth-token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String token) {
        authenticationService.logout(token);
        return ResponseEntity.ok().build();
    }
}

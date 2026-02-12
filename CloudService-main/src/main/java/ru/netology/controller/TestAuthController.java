package ru.netology.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.netology.entity.UserEntity;
import ru.netology.repository.UserRepository;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
public class TestAuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/test/check-password")
    public ResponseEntity<?> checkPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Optional<UserEntity> userOpt = userRepository.findById(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();

            String storedPassword = user.getPassword();

            boolean matches = storedPassword != null && passwordEncoder.matches(password, storedPassword);
            response.put("passwordMatches", matches);
            response.put("storedHash", storedPassword);
            response.put("testHash", passwordEncoder.encode(password));
            response.put("role", user.getRole());
        } else {
            response.put("error", "User not found");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/test/create-test-user")
    public ResponseEntity<?> createTestUser() {
        String encodedPassword = passwordEncoder.encode("password123");

        UserEntity testUser = UserEntity.builder()
                .username("testuser")
                .password(encodedPassword)
                .role("ROLE_USER")
                .build();

        userRepository.save(testUser);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Test user created successfully");
        result.put("username", testUser.getUsername());
        result.put("role", testUser.getRole());
        result.put("encodedPassword", encodedPassword);

        return ResponseEntity.ok(result);
    }
}

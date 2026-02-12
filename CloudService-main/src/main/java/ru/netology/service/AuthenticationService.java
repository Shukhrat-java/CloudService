package ru.netology.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.netology.dto.LoginRequest;
import ru.netology.security.JwtTokenProvider;
import ru.netology.exception.UnauthorizedErrorException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ConcurrentHashMap<String, String> tokenStorage = new ConcurrentHashMap<>();

    public String login(LoginRequest loginRequest) {
        try {
            log.info("Attempting login for user: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            log.info("Authentication successful for user: {}", loginRequest.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);
            tokenStorage.put(token, loginRequest.getUsername());

            return token;
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            throw new UnauthorizedErrorException("Неверный логин или пароль");
        }
    }

    public void logout(String token) {
        if (token != null) {
            tokenStorage.remove(token);
        }
    }

    public boolean validateToken(String token) {
        return token != null && tokenStorage.containsKey(token) && jwtTokenProvider.validateToken(token);
    }

    public String getUsernameByToken(String token) {
        return tokenStorage.get(token);
    }
}

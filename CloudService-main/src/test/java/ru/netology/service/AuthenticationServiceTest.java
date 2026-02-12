package ru.netology.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.netology.dto.LoginRequest;
import ru.netology.security.JwtTokenProvider;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private LoginRequest loginRequest;
    private final String TEST_TOKEN = "test-jwt-token";
    private final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword("password");
    }

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(
            new org.springframework.security.core.userdetails.User(TEST_USERNAME, "password", java.util.Collections.emptyList())
        );
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(TEST_TOKEN);

        String token = authenticationService.login(loginRequest);

        assertNotNull(token);
        assertEquals(TEST_TOKEN, token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(any(Authentication.class));
    }

    @Test
    void login_Failure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(RuntimeException.class, () -> {
            authenticationService.login(loginRequest);
        });
    }

    @Test
    void logout_Success() {
        authenticationService.logout(TEST_TOKEN);
        // Проверяем что метод выполнился без исключений
        assertTrue(true);
    }

    @Test
    void validateToken_ValidToken() {
        // Сначала логинимся чтобы добавить токен в хранилище
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(
            new org.springframework.security.core.userdetails.User(TEST_USERNAME, "password", java.util.Collections.emptyList())
        );
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(TEST_TOKEN);
        when(jwtTokenProvider.validateToken(TEST_TOKEN)).thenReturn(true);
        
        authenticationService.login(loginRequest);
        boolean isValid = authenticationService.validateToken(TEST_TOKEN);
        
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken() {
        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);
        
        boolean isValid = authenticationService.validateToken("invalid-token");
        
        assertFalse(isValid);
    }

    @Test
    void getUsernameByToken_ExistingToken() {
        // Сначала логинимся чтобы добавить токен в хранилище
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(
            new org.springframework.security.core.userdetails.User(TEST_USERNAME, "password", java.util.Collections.emptyList())
        );
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(TEST_TOKEN);
        
        authenticationService.login(loginRequest);
        String username = authenticationService.getUsernameByToken(TEST_TOKEN);
        
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void getUsernameByToken_NonExistingToken() {
        String username = authenticationService.getUsernameByToken("non-existing-token");
        
        assertNull(username);
    }
}

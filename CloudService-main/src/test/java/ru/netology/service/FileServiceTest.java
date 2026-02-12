package ru.netology.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.entity.FileEntity;
import ru.netology.repository.FileRepository;
import ru.netology.entity.UserEntity;
import ru.netology.repository.UserRepository;
import ru.netology.security.JwtTokenProvider;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private InMemoryBlackListToken blackListToken;

    @Mock
    private UserRepository userRepository;  // Добавьте этот mock

    @Mock
    private JwtTokenProvider jwtTokenProvider;  // Добавьте этот mock

    @InjectMocks
    private FileService fileService;

    @Test
    public void testUploadFile() throws IOException {

        String token = "validToken";
        String filename = "example.txt";
        String username = "testuser";
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());

        when(blackListToken.isBlacklisted(token)).thenReturn(false);
        when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        when(userRepository.findById(username)).thenReturn(java.util.Optional.of(user));

        fileService.uploadFile(token, filename, file);

        verify(fileRepository).save(any(FileEntity.class));
    }

    @Test
    public void testDeleteFile() {

        String token = "validToken";
        String filename = "example.txt";
        String username = "testuser";

        when(blackListToken.isBlacklisted(token)).thenReturn(false);
        when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);

        fileService.deleteFile(token, filename);

        // ИСПРАВЛЕНО: используем правильный метод репозитория
        verify(fileRepository).deleteByFilenameAndUserUsername(filename, username);
    }
}
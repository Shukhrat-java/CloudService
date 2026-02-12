package ru.netology.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileResponse;
import ru.netology.entity.FileEntity;
import ru.netology.entity.UserEntity;
import ru.netology.exception.BadRequestException;
import ru.netology.exception.InternalServerErrorException;
import ru.netology.exception.UnauthorizedErrorException;
import ru.netology.repository.FileRepository;
import ru.netology.repository.UserRepository;
import ru.netology.security.JwtTokenProvider;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final InMemoryBlackListToken tokenBlacklist;

    public void uploadFile(String token, String filename, MultipartFile file) throws IOException {
        checkToken(token);

        if (filename == null || filename.isEmpty() || file == null || file.isEmpty()) {
            throw new BadRequestException("Error input data");
        }

        String username = getUsernameFromToken(token);
        UserEntity user = userRepository.findById(username)
                .orElseThrow(() -> new UnauthorizedErrorException("User not found"));

        try {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(filename);
            fileEntity.setFileContent(file.getBytes());
            fileEntity.setSize(file.getSize());
            fileEntity.setUser(user);
            fileRepository.save(fileEntity);
        } catch (RuntimeException e) {
            throw new InternalServerErrorException("Error upload file");
        }
    }

    public void deleteFile(String token, String filename) {
        checkToken(token);

        if (filename == null || filename.isEmpty()) {
            throw new BadRequestException("Error input data");
        }

        String username = getUsernameFromToken(token);

        try {
            fileRepository.deleteByFilenameAndUserUsername(filename, username);
        } catch (RuntimeException e) {
            throw new InternalServerErrorException("Error delete file");
        }
    }

    public byte[] downloadFile(String token, String filename) {
        checkToken(token);

        if (filename == null || filename.isEmpty()) {
            throw new BadRequestException("Error input data");
        }

        String username = getUsernameFromToken(token);

        FileEntity fileEntity = fileRepository.findByFilenameAndUserUsername(filename, username)
                .orElseThrow(() -> new InternalServerErrorException("File not found"));

        return fileEntity.getFileContent();
    }

    public void editFileName(String token, String filename, String newFilename) {
        checkToken(token);

        if (filename == null || filename.isEmpty() || newFilename == null || newFilename.isEmpty()) {
            throw new BadRequestException("Error input data");
        }

        String username = getUsernameFromToken(token);

        int updatedRows = fileRepository.editFileName(filename, newFilename, username);
        if (updatedRows == 0) {
            throw new InternalServerErrorException("Error edit file name");
        }
    }

    public List<FileResponse> getFileList(String token, int limit) {
        checkToken(token);
        if (limit <= 0) {
            throw new BadRequestException("Error input data");
        }
        String username = getUsernameFromToken(token);
        List<FileEntity> files = fileRepository.findByUserUsernameOrderById(username, PageRequest.of(0, limit));
        return files.stream()
                .map(file -> new FileResponse(file.getFilename(), file.getSize().intValue()))
                .collect(Collectors.toList());
    }

    public void checkToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedErrorException("Unauthorized error");
        }
        String cleanToken = token.replace("Bearer ", "");
        if (tokenBlacklist.isBlacklisted(cleanToken)) {
            throw new UnauthorizedErrorException("Unauthorized error");
        }
    }

    private String getUsernameFromToken(String token) {
        String cleanToken = token.replace("Bearer ", "");
        return jwtTokenProvider.getUsernameFromToken(cleanToken);
    }
}
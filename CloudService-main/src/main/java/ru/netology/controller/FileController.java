package ru.netology.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileResponse;
import ru.netology.dto.NewFilenameRequest;
import ru.netology.service.FileService;
import ru.netology.service.AuthenticationService;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/cloud")
public class FileController {
    private final FileService fileService;
    private final AuthenticationService authenticationService;

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> getAllFiles(
            @RequestHeader("auth-token") String token,
            @RequestParam int limit) {
        
        String username = authenticationService.getUsernameByToken(token);
        List<FileResponse> files = fileService.getFileList(username, limit);
        return ResponseEntity.ok(files);
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam String filename,
            @RequestBody MultipartFile file) throws IOException {
        
        String username = authenticationService.getUsernameByToken(token);
        fileService.uploadFile(username, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(
            @RequestHeader("auth-token") String token,
            @RequestParam String filename) {
        
        String username = authenticationService.getUsernameByToken(token);
        fileService.deleteFile(username, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam String filename) {
        
        String username = authenticationService.getUsernameByToken(token);
        byte[] file = fileService.downloadFile(username, filename);
        return ResponseEntity.ok()
                .body(new ByteArrayResource(file));
    }

    @PutMapping("/file")
    public ResponseEntity<?> editFileName(
            @RequestHeader("auth-token") String token,
            @RequestParam String filename,
            @RequestBody NewFilenameRequest newFilenameRequest) {
        
        String username = authenticationService.getUsernameByToken(token);
        fileService.editFileName(username, filename, newFilenameRequest.getFilename());
        return ResponseEntity.ok(HttpStatus.OK);
    }
}

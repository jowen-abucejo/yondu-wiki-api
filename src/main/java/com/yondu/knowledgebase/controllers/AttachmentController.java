package com.yondu.knowledgebase.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yondu.knowledgebase.DTO.page.ImageUploadDTO;
import com.yondu.knowledgebase.services.AttachmentService;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    private final AttachmentService fileUploadService;

    public AttachmentController(AttachmentService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        String uploadPath = "./uploads/";
        String filePath = uploadPath + fileName;
        Path imagePath = Paths.get(filePath);

        try {
            Resource imageResource = new UrlResource(imagePath.toUri());

            if (imageResource.exists()) {
                String contentType = Files.probeContentType(imagePath);
                return ResponseEntity.ok()
                        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(imageResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ImageUploadDTO uploadImage(@RequestParam("upload") MultipartFile file) {
        String filePath = fileUploadService.uploadImage(file);
        return new ImageUploadDTO(filePath);
    }

    @DeleteMapping("{imageUrl}")
    public ResponseEntity<String> deleteAttachment(@PathVariable String imageUrl) {
        boolean deleted = fileUploadService.deleteAttachment(imageUrl);
        if (deleted) {
            return ResponseEntity.ok("Attachment deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

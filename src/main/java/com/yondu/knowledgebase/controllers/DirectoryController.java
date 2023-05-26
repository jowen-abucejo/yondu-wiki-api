package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.directory.CreateDirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.CreateDirectoryResponse;
import com.yondu.knowledgebase.services.DirectoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/directories")
public class DirectoryController {
    private final DirectoryService directoryService;
    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @PostMapping("/{parentId}")
    private ResponseEntity<CreateDirectoryResponse> createDirectory(@PathVariable Long parentId, @RequestBody CreateDirectoryRequest createDirectoryRequest) {
        try {
            if(createDirectoryRequest.getName() == null || createDirectoryRequest.getName().isEmpty()) {
                System.out.println("dumaan");
                throw new NullPointerException();
            }
            CreateDirectoryResponse response = directoryService.createDirectory(parentId, createDirectoryRequest.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

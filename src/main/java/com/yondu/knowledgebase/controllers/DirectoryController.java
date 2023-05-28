package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.directory.DirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.DirectoryResponse;
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

    @PostMapping("/{parentId}/create")
    public ResponseEntity<Object> createDirectory(@PathVariable("parentId") Long parentId, @RequestBody DirectoryRequest directoryRequest) {
        try {
            if(directoryRequest.getName() == null || directoryRequest.getName().isEmpty() || directoryRequest.getDescription() == null || directoryRequest.getDescription().isEmpty()) {
                throw new NullPointerException();
            }
            DirectoryResponse response = directoryService.createDirectory(parentId, directoryRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<Object> renameDirectory(@PathVariable("id") Long id, @RequestBody DirectoryRequest directoryRequest) {
        try {
            if(directoryRequest.getName() == null || directoryRequest.getName().isEmpty()) {
                throw new NullPointerException();
            }
            DirectoryResponse response = directoryService.renameDirectory(id, directoryRequest.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/remove")
    public ResponseEntity<Object> deleteDirectory(@PathVariable("id") Long id) {
        try {
            String response = directoryService.removeDirectory(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

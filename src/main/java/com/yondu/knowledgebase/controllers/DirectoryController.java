package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.directory.DirectoryDTO;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.services.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/directories")
public class DirectoryController {
    private final DirectoryService directoryService;

    private Logger log = LoggerFactory.getLogger(DirectoryController.class);

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping
    public  ResponseEntity<ApiResponse<?>> getDefaultDirectory() {
        DirectoryDTO.GetResponse data = directoryService.getDefaultDirectory();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Directory found"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getDirectory(@PathVariable("id") Long id) {
        DirectoryDTO.GetResponse data = directoryService.getDirectory(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Directory found"));
    }

    @PostMapping("/{parentId}")
    public ResponseEntity<ApiResponse<?>> createDirectory(@PathVariable("parentId") Long parentId, @RequestBody DirectoryDTO.CreateRequest request) {
        DirectoryDTO.GetResponse data = directoryService.createDirectory(parentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Directory created successfully"));
    }

//    @PutMapping("/move")
//    public ResponseEntity<ApiResponse<?>> moveDirectory(@RequestBody DirectoryDTO.MoveRequest request) {
//        List<DirectoryDTO.GetResponse> data = directoryService.moveDirectories(request);
//        System.out.println(data);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Directory moved successfully"));
//    }

    @PutMapping("/{id}/move")
    public ResponseEntity<ApiResponse<?>> moveDirectory(@PathVariable("id") Long id, @RequestBody DirectoryDTO.MoveRequest request) {
        DirectoryDTO.GetResponse data = directoryService.moveDirectory(id, request.parentId(), request.newParentId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Directory moved successfully"));
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<ApiResponse<?>> renameDirectory(@PathVariable("id") Long id, @RequestBody DirectoryDTO.RenameRequest request) {
        DirectoryDTO.GetResponse data = directoryService.renameDirectory(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Directory renamed successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteDirectory(@PathVariable("id") Long id) {
        directoryService.removeDirectory(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Directory deleted successfully"));
    }

    @GetMapping("/{id}/pages")
    public ResponseEntity<ApiResponse<PaginatedResponse<PageDTO>>> getPages(@PathVariable("id") Long directoryId,
                                                                            @RequestParam(defaultValue = "1") int page,
                                                                            @RequestParam(defaultValue = "5") int size,
                                                                            @RequestParam(defaultValue = "WIKI") String type) {
        log.info("DirectoryController.getPages()");
        log.info("directoryId : " + directoryId);
        log.info("page : " + page);
        log.info("size : " + size);
        log.info("type : " + type);

        PaginatedResponse<PageDTO> pages = directoryService.getPages(directoryId, page, size, type);
        if(pages == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ApiResponse.success(pages, "success"));
    }
}

package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.save.SaveDTO;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.services.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("save")
public class SaveController {

    private final SaveService saveService;

    @Autowired
    public SaveController (SaveService saveService) {
        this.saveService = saveService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<SaveDTO.BaseResponse>> createSavedPosts (@RequestBody SaveDTO.BaseRequest save) {
        SaveDTO.BaseResponse saved = saveService.createSaved(save);

        ApiResponse apiResponse = ApiResponse.success(saved, "Saved successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/my-saves")
    public ResponseEntity<?> getSavesByAuthor(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<SaveDTO.BaseResponse> saves = saveService.getAllSavesByAuthor(page,size);

        ApiResponse apiResponse = ApiResponse.success(saves, "Retrieved your saved items successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> removeSave(@PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(saveService.deleteSaved(id),"Save with id: " + id + "successfully deleted!"));
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<Boolean> isEntitySaved(@PathVariable String entityType, @PathVariable Long entityId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        boolean isSaved = saveService.hasEntitySaved(entityType, entityId, user);

        return ResponseEntity.ok(isSaved);
    }
}

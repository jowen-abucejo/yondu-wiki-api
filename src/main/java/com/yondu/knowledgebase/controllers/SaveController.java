package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.save.SaveDTO;
import com.yondu.knowledgebase.DTO.save.SaveEntityDTO;
import com.yondu.knowledgebase.DTO.save.SaveStatusDTO;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.services.SaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("saves")
public class SaveController {

    private final SaveService saveService;

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

    @GetMapping("/my-saves/{entityType}")
    public ResponseEntity<?> getSavesByAuthorAndEntity(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size, @PathVariable String entityType ) {
        PaginatedResponse<SaveEntityDTO> saves = saveService.getAllSaveIdsByAuthorAndEntity(page,size, entityType);
        ApiResponse apiResponse = ApiResponse.success(saves, "Retrieved your saved items successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/my-saves/{entityType}/{entityId}")
    public ResponseEntity<?> removeSaveByEntity(@PathVariable String entityType,@PathVariable Long entityId ) {
        SaveDTO.BaseResponse saves = saveService.deleteSaved(entityType,entityId);
        ApiResponse apiResponse = ApiResponse.success(saves, "Deleted your saved items successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> removeSave(@PathVariable Long id) {
        boolean removed = saveService.deleteSaved(id);
        SaveStatusDTO saveStatusDTO = new SaveStatusDTO(!removed, id);

        ApiResponse<Object> apiResponse = ApiResponse.success(saveStatusDTO, "Removed your saved item with id "+id+" successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<ApiResponse<?>> isEntitySaved(@PathVariable String entityType, @PathVariable Long entityId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        boolean isSaved = saveService.hasEntitySaved(entityType, entityId, user);
        Long saveId = saveService.getSaveId(entityType, entityId, user);

        SaveStatusDTO saveStatusDTO = new SaveStatusDTO(isSaved, saveId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(saveStatusDTO,"Entity Retrieved"));
    }

    @GetMapping("/get/{entityType}/{entityId}")
    public ResponseEntity<?> getSavesByEntityTypeId(@PathVariable String entityType, @PathVariable Long entityId) {
        SaveDTO.BaseResponse saves = saveService.getSave(entityType,entityId);

        ApiResponse apiResponse = ApiResponse.success(saves, "Retrieved your saved items successfully");
        return ResponseEntity.ok(apiResponse);
    }
}

package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.save.SaveDTO;
import com.yondu.knowledgebase.services.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        ApiResponse apiResponse = ApiResponse.success(saved, "success");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}

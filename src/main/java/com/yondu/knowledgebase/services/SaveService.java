package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.save.SaveDTO;
import com.yondu.knowledgebase.DTO.save.SaveDTOMapper;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.Save;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.repositories.SaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SaveService {

    @Autowired
    private final SaveRepository saveRepository;
    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final PageRepository pageRepository;

    public SaveService(SaveRepository saveRepository, PostRepository postRepository, CommentRepository commentRepository, PageRepository pageRepository) {
        this.saveRepository = saveRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.pageRepository = pageRepository;
    }

public SaveDTO.BaseResponse createSaved(SaveDTO.BaseRequest saves) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (user == null) {
        throw new ResourceNotFoundException("User not found, login first before saving");
    }

  // validation
    switch (saves.entityType()) {
        case "POST":
          postRepository.findById(saves.entityId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Post ID not found: %d", saves.entityId())));
            break;
        case "COMMENT":
          commentRepository.findById(saves.entityId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Comment ID not found: %d", saves.entityId())));
            break;
        case "PAGE":
            pageRepository.findById(saves.entityId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Page ID not found: %d", saves.entityId())));
            break;
        default:
            throw new RequestValidationException("Invalid Entity Type");
    }

    if (isEntitySaved(saves.entityType(), saves.entityId(), user)) {
        throw new RequestValidationException("This " + saves.entityType() +" with id "+ saves.entityId()+" is already Saved");
    }

    Save save = new Save();
    save.setDateCreated(LocalDateTime.now());
    save.setAuthor(user);
    save.setEntityType(saves.entityType());
    save.setEntityId(saves.entityId());

    saveRepository.save(save);

    return SaveDTOMapper.mapToBaseResponse(save);
}

    public boolean hasEntitySaved(String entityType, Long entityId, User user) {
        // Validate the entity type and find the entity
        switch (entityType) {
            case "POST":
                postRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("Post ID not found: %d", entityId)));
                break;
            case "COMMENT":
                commentRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("Comment ID not found: %d", entityId)));
                break;
            case "PAGE":
                pageRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("Page ID not found: %d", entityId)));
                break;
            default:
                throw new RequestValidationException("Invalid Entity Type");
        }

        // Check if the entity is already saved
        return isEntitySaved(entityType, entityId, user);
    }

public PaginatedResponse<SaveDTO.BaseResponse> getAllSavesByAuthor(int page, int size) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (user == null) {
        throw new ResourceNotFoundException("Login First");
    }

    PageRequest pageRequest = PageRequest.of(page - 1, size);
    Page<Save> savePages = saveRepository.findAllByAuthor(user,pageRequest);
    List<Save> saves = savePages.getContent();

    List<SaveDTO.BaseResponse> save = saves.stream()
            .map(s -> SaveDTOMapper.mapToBaseResponse(s))
            .collect(Collectors.toList());

    return new PaginatedResponse<>(save,page,size, (long)save.size());
}

public Save deleteSaved(Long id) {

        Save save = saveRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Save "+ id +" not found."));
    saveRepository.delete(save);
    return null;
}
    public boolean isEntitySaved(String entityType, Long entityId, User user) {
        Optional<Save> savedEntity = Optional.ofNullable(saveRepository.findByEntityTypeAndEntityIdAndAuthor(entityType, entityId, user));
        return savedEntity.isPresent();
    }
}

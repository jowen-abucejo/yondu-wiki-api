package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.save.SaveDTO;
import com.yondu.knowledgebase.DTO.save.SaveDTOMapper;
import com.yondu.knowledgebase.DTO.save.SaveEntityDTO;
import com.yondu.knowledgebase.DTO.save.SaveStatusDTO;
import com.yondu.knowledgebase.entities.*;
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
import java.util.Collections;
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
    @Autowired
    private final AuditLogService auditLogService;

    public SaveService(SaveRepository saveRepository, PostRepository postRepository,
            CommentRepository commentRepository, PageRepository pageRepository, AuditLogService auditLogService) {
        this.saveRepository = saveRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.pageRepository = pageRepository;
        this.auditLogService = auditLogService;
    }

    public SaveDTO.BaseResponse createSaved(SaveDTO.BaseRequest saves) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (isEntitySaved(saves.entityType().toUpperCase(), saves.entityId(), user)) {
            throw new RequestValidationException(
                    "This " + saves.entityType() + " with id " + saves.entityId() + " is already Saved");
        }
        Save save = new Save();
        save.setDateCreated(LocalDateTime.now());
        save.setAuthor(user);
        save.setEntityType(saves.entityType().toUpperCase());
        save.setEntityId(saves.entityId());

        saveRepository.save(save);

        // implement audit log for user's activities
        Object entity = getEntity(saves.entityType(), saves.entityId(), Object.class);
        String savedEntity = getEntityTypeAndAuthor(entity);
        auditLogService.createAuditLog(user, save.getEntityType(), save.getEntityId(), "saved a " + savedEntity);

        return SaveDTOMapper.mapToBaseResponse(save);
    }

    public boolean hasEntitySaved(String entityType, Long entityId, User user) {
        validateEntityExistence(entityType, entityId);
        // Check if the entity is already saved
        return isEntitySaved(entityType, entityId, user);
    }

    public PaginatedResponse<SaveDTO.BaseResponse> getAllSavesByAuthor(int page, int size) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new ResourceNotFoundException("Login First");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Save> savePages = saveRepository.findAllByAuthorOrderByDateCreatedDesc(user, pageRequest);
        List<Save> saves = savePages.getContent();

        List<SaveDTO.BaseResponse> save = saves.stream()
                .map(s -> SaveDTOMapper.mapToBaseResponse(s))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(save, page, size, (long) save.size());
    }

    public PaginatedResponse<SaveEntityDTO> getAllSaveIdsByAuthorAndEntity(int page, int size, String entityType) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new ResourceNotFoundException("Login First");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Save> save = saveRepository.findAllByAuthorAndEntityTypeOrderByDateCreatedDesc(user,
                entityType.toUpperCase(), pageRequest);
        List<Long> saveIds = save.getContent().stream()
                .map(Save::getEntityId)
                .collect(Collectors.toList());

        List<SaveEntityDTO> saveEntityDTOs = saveIds.stream()
                .map(SaveEntityDTO::new)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(saveEntityDTOs, page, size, save.getTotalElements());
    }

    public boolean deleteSaved(Long id) {

        Save save = saveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Save " + id + " not found."));

        Object entity = getEntity(save.getEntityType(), save.getEntityId(), Object.class);
        String savedEntity = getEntityTypeAndAuthor(entity);
        auditLogService.createAuditLog(save.getAuthor(), save.getEntityType(), save.getEntityId(),
                "removed a saved " + savedEntity + " in his/her collection.");

        saveRepository.delete(save);

        return true;
    }

    public SaveDTO.BaseResponse getSave(String entityType, Long entityId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Save save = saveRepository.findByEntityTypeAndEntityIdAndAuthor(entityType, entityId, user);

        return SaveDTOMapper.mapToBaseResponse(save);
    }

    public boolean isEntitySaved(String entityType, Long entityId, User user) {
        Optional<Save> savedEntity = Optional
                .ofNullable(saveRepository.findByEntityTypeAndEntityIdAndAuthor(entityType, entityId, user));
        return savedEntity.isPresent();
    }

    public Long getSaveId(String entityType, Long entityId, User user) {
        Save savedEntity = saveRepository.findByEntityTypeAndEntityIdAndAuthor(entityType, entityId, user);
        return savedEntity != null ? savedEntity.getId() : null;
    }

    public void validateEntityExistence(String entityType, Long entityId) {
        switch (entityType) {
            case "POST":
                postRepository.findById(entityId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(String.format("Post ID not found: %d", entityId)));
                break;
            case "COMMENT":
                commentRepository.findById(entityId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                String.format("Comment ID not found: %d", entityId)));
                break;
            case "PAGE":
                pageRepository.findById(entityId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(String.format("Page ID not found: %d", entityId)));
                break;
            default:
                throw new RequestValidationException("Invalid Entity Type");
        }
    }

    public <T> T getEntity(String entityType, Long entityId, Class<T> entityClass) {
        switch (entityType) {
            case "POST":
                return (T) postRepository.findById(entityId).orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Post ID not found: %d", entityId)));
            case "COMMENT":
                return (T) commentRepository.findById(entityId).orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Comment ID not found: %d", entityId)));
            case "PAGE":
                return (T) pageRepository.findById(entityId).orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Page ID not found: %d", entityId)));
            default:
                throw new RequestValidationException("Invalid Entity Type");
        }
    }

    private String getEntityTypeAndAuthor(Object entity) {
        if (entity instanceof Post post) {
            return "post from " + post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName();
        } else if (entity instanceof Comment comment) {
            return "comment from " + comment.getUser().getFirstName() + " " + comment.getUser().getLastName();
        } else if (entity instanceof com.yondu.knowledgebase.entities.Page page) {
            return "page created by " + page.getAuthor().getFirstName() + " " + page.getAuthor().getLastName();
        } else {
            throw new RequestValidationException("Invalid Entity Type");
        }
    }

    public SaveDTO.BaseResponse deleteSaved(String entityType, Long id) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new ResourceNotFoundException("Login First");
        }

        var save = saveRepository.findByEntityTypeAndEntityIdAndAuthor(entityType.toUpperCase(), id, user);

        saveRepository.delete(save);

        Object entity = getEntity(save.getEntityType(), save.getEntityId(), Object.class);
        String savedEntity = getEntityTypeAndAuthor(entity);

        auditLogService.createAuditLog(save.getAuthor(), save.getEntityType(), save.getEntityId(),
                "removed a saved " + savedEntity + " in his/her collection.");

        return SaveDTOMapper.mapToBaseResponse(save);
    }
}

package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.DirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.DirectoryResponse;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.repositories.DirectoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    public DirectoryService(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    public DirectoryResponse createDirectory(Long parentId, DirectoryRequest request) {
        Directory savedDirectory = directoryRepository.findById(parentId)
                .map(parent -> directoryRepository.save(new Directory(request.getName(), request.getDescription(), parent)))
                .orElseThrow(EntityNotFoundException::new);

        return new DirectoryResponse(savedDirectory);
    }

    public DirectoryResponse renameDirectory(Long id, String name) {
        Directory directory = directoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        directory.setName(name);
        directory.setDateModified(LocalDate.now());
        Directory savedDirectory = directoryRepository.save(directory);
        return new DirectoryResponse(savedDirectory);
    }

    public String removeDirectory(Long id) {
        Directory directory = directoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        directoryRepository.delete(directory);
        return "Directory deleted successfully";
    }
}

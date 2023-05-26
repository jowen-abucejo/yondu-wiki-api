package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.DirectoryResponse;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.repositories.DirectoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    public DirectoryService(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    public DirectoryResponse createDirectory(Long parentId, String name) {
        // check if parent directory exist
        Directory parent = directoryRepository.findById(parentId).orElseThrow(EntityNotFoundException::new);
        // save new directory
        Directory savedDirectory = directoryRepository.save(new Directory(name, parent));
        return new DirectoryResponse(savedDirectory);
    }

    public DirectoryResponse renameDirectory(Long id, String name) {
        // check if directory exist
        Directory directory = directoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        // rename
        directory.setName(name);
        // save
        Directory savedDirectory = directoryRepository.save(directory);
        return new DirectoryResponse(savedDirectory);
    }

    public DirectoryResponse removeDirectory(Long id) {
        // check if directory exist
        Directory directory = directoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        // delete
        directoryRepository.delete(directory);
        return new DirectoryResponse(directory);
    }
}

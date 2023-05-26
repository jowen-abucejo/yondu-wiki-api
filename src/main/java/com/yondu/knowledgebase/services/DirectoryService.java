package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.CreateDirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.CreateDirectoryResponse;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.repositories.DirectoryRepository;
import org.springframework.stereotype.Service;

@Service
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    public DirectoryService(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    public CreateDirectoryResponse createDirectory(Long parentId, String name) {
        // check parent directory existence ... replace exception
        Directory parent = directoryRepository.findById(parentId).orElseThrow(NullPointerException::new);

        Directory newDirectory = directoryRepository.save(new Directory(name, parent));
        return new CreateDirectoryResponse(newDirectory);
        //return new CreateDirectoryResponse(directoryRepository.save(new Directory("root"))); // root directory
    }
}

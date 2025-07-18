package com.yondu.knowledgebase.services.implementations;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.yondu.knowledgebase.repositories.PageVersionRepository;
import com.yondu.knowledgebase.services.AttachmentService;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final PageVersionRepository pageVersionRepository;

    /**
     * @param pageVersionRepository
     */
    public AttachmentServiceImpl(PageVersionRepository pageVersionRepository) {
        this.pageVersionRepository = pageVersionRepository;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "."
                + originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String uploadPath = "./uploads/"; // Update with your desired upload path

        // Check if the upload directory exists, create it if necessary
        Path directoryPath = Paths.get(uploadPath);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image");
            }
        }

        // Construct the file path
        String filePath = uploadPath + fileName;

        // Save the file to the specified location
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image");
        }

        // Construct and return the URL for the uploaded file
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String fileUrl = baseUrl + "/attachments/" + fileName;

        return fileUrl;
    }

    public boolean deleteAttachment(String imageUrl) {
        // Parse the file name from the imageUrl
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        // Construct the file path
        String uploadPath = "./uploads/";
        String filePath = uploadPath + fileName;

        // Delete the file
        try {
            // Construct the URL for the target file
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String fileUrl = baseUrl + "/attachments/" + fileName;

            Long totalReviewedContentsWithSameImageSrc = pageVersionRepository.countByContentWithImageSrc(fileUrl);
            // do not delete if a reviewed or submitted page version using the same image
            // exists
            if (totalReviewedContentsWithSameImageSrc != null && totalReviewedContentsWithSameImageSrc > 0)
                return true;

            Files.deleteIfExists(Paths.get(filePath));

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

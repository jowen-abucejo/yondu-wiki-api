package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.entities.User;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class DirectoryDTO {
    public record Approver(Long id, String firstName, String lastName){}
    public record WorkflowStep(Long id, String name, int step, List<DirectoryDTO.Approver> approvers){}
    public record CreateRequest(String name, String description, Long parentId, List<DirectoryDTO.WorkflowStep> workflow){}
    public record RenameRequest(String name) {}
    public record MoveRequest(Long parentId, Long newParentId){}
    public record BaseResponse(Long id,
                               String name,
                               String description,
                               UserDTO.GeneralResponse createdBy,
                               LocalDate dateCreated,
                               LocalDate dateModified){}
    public record GetResponse(Long id,
                              String name,
                              String description,
                              UserDTO.GeneralResponse createdBy,
                              LocalDate dateCreated,
                              LocalDate dateModified,
                              List<ShortResponse> fullPath,
                              Set<GetResponse> subdirectories){}

    public record ShortResponse(Long id,
                               String name){}
}

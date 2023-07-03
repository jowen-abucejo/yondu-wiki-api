package com.yondu.knowledgebase.DTO.directory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.entities.Workflow;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class DirectoryDTO {
    public record UserAccess(@JsonProperty("user_id") Long userId, @JsonProperty("permission_id") Long permissionId){}
    public record Approver(Long id, @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName){}
    public record WorkflowStep(Long id, String name, int step, List<DirectoryDTO.Approver> approvers){}
    public record CreateRequest(String name, String description, Long parentId, List<DirectoryDTO.WorkflowStep> workflow, @JsonProperty("user_access") List<UserAccess> userAccess){}
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
                              Set<GetResponse> subdirectories,
                              Set<DirectoryDTO.WorkflowStep> workflow){}

    public record ShortResponse(Long id,
                               String name){}
}

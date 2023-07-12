package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.DTO.group.GroupDTOMapper;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.*;

import java.util.*;
import java.util.stream.Collectors;

public class DirectoryDTOMapper {

    public static DirectoryDTO.BaseResponse mapToBaseResponse(Directory directory) {
        return new DirectoryDTO.BaseResponse(
                directory.getId(),
                directory.getName(),
                directory.getDescription(),
                UserDTOMapper.mapToGeneralResponse(directory.getCreatedBy()),
                directory.getDateCreated(),
                directory.getDateModified());
    }

    public static DirectoryDTO.GetResponse mapToGetResponse(Directory directory) {
        return new DirectoryDTO.GetResponse(
                directory.getId(),
                directory.getName(),
                directory.getDescription(),
                UserDTOMapper.mapToGeneralResponse(directory.getCreatedBy()),
                directory.getDateCreated(),
                directory.getDateModified(),
                getPathFromParentToChild(directory),
                directory.getSubDirectories().stream().map(DirectoryDTOMapper::mapToGetResponse)
                        .collect(Collectors.toSet()),
                directory.getWorkflow().getSteps().stream().map(DirectoryDTOMapper::mapToWorkflowStep)
                        .collect(Collectors.toSet()),
                directory.getDirectoryUserAccesses().stream().map(DirectoryDTOMapper::mapToUserAccess)
                        .collect(Collectors.toSet()),
                directory.getDirectoryGroupAccesses().stream().map(DirectoryDTOMapper::mapToGroupAccess)
                        .collect(Collectors.toSet()));
    }

    public static DirectoryDTO.ShortResponse mapToShortResponse(Directory directory) {
        return new DirectoryDTO.ShortResponse(
                directory.getId(),
                directory.getName());
    }

    public static DirectoryDTO.UserAccess mapToUserAccess(DirectoryUserAccess directoryUserAccess) {
        return new DirectoryDTO.UserAccess(UserDTOMapper.mapToGeneralResponse(directoryUserAccess.getUser()),
                directoryUserAccess.getPermission().getId());
    }

    public static DirectoryDTO.GroupAccess mapToGroupAccess(DirectoryGroupAccess directoryGroupAccess) {
        return new DirectoryDTO.GroupAccess(GroupDTOMapper.mapToBaseResponse(directoryGroupAccess.getGroup()),
                directoryGroupAccess.getPermission().getId());
    }

    public static DirectoryDTO.Approver mapToApprover(User user) {
        return new DirectoryDTO.Approver(user.getId(), user.getFirstName(), user.getLastName());
    }

    public static DirectoryDTO.WorkflowStep mapToWorkflowStep(WorkflowStep workflowStep) {
        return new DirectoryDTO.WorkflowStep(
                workflowStep.getId(),
                workflowStep.getName(),
                workflowStep.getStep(),
                workflowStep.getApprovers()
                        .stream()
                        .map((u) -> DirectoryDTOMapper.mapToApprover(u.getApprover()))
                        .toList());
    }

    // private static String traverse(Directory directory) {
    // if (directory.getParent() == null) return directory.getId() + "->" +
    // directory.getName();
    // return traverse(directory.getParent()) + "/" + directory.getId() + "->" +
    // directory.getName();
    // }

    private static List<DirectoryDTO.ShortResponse> getPathFromParentToChild(Directory directory) {
        List<DirectoryDTO.ShortResponse> fullPath = new ArrayList<>();
        Deque<Directory> stack = new ArrayDeque<>();
        Directory currentDirectory = directory;

        while (currentDirectory != null) {
            stack.push(currentDirectory);
            currentDirectory = currentDirectory.getParent();
        }

        while (!stack.isEmpty()) {
            Directory dir = stack.pop();
            DirectoryDTO.ShortResponse shortResponse = mapToShortResponse(dir);
            fullPath.add(shortResponse);
        }

        return fullPath;
    }

    public static DirectoryDTO.GetMinimizeResponse mapToGetMinimizeResponse(Directory directory) {
        return new DirectoryDTO.GetMinimizeResponse(
                directory.getId(),
                directory.getName(),
                directory.getDescription(),
                directory.getDateCreated(),
                directory.getDateModified(),
                directory.getWorkflow().getSteps().stream().map(DirectoryDTOMapper::mapToWorkflowStep)
                        .collect(Collectors.toSet()));
    }
}

package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.WorkflowStep;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
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
                directory.getSubDirectories().stream().map(DirectoryDTOMapper::mapToGetResponse).collect(Collectors.toSet())
        );
    }

    public static DirectoryDTO.ShortResponse mapToShortResponse(Directory directory) {
        return new DirectoryDTO.ShortResponse(
                directory.getId(),
                directory.getName()
        );
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
                        .toList()
        );
    }

//    private static String traverse(Directory directory) {
//        if (directory.getParent() == null) return directory.getId() + "->" + directory.getName();
//        return traverse(directory.getParent()) + "/" + directory.getId() + "->" + directory.getName();
//    }

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
}

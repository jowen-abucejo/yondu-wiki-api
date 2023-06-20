package com.yondu.knowledgebase.DTO.rights.directory;
import com.yondu.knowledgebase.DTO.rights.RightsDTO;
import com.yondu.knowledgebase.entities.Directory;

public class DirectoryRightsDTO extends RightsDTO {
    public record BaseResponse(Long id, Directory directory) {}
}

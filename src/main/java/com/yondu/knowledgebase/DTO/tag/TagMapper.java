package com.yondu.knowledgebase.DTO.tag;

import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.entities.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public Tag toEntity(TagDTO tagDto){
        Tag tag = new Tag();
        tag.setId(tagDto.getId());
        tag.setName(tagDto.getName());
        tag.setDeleted(tagDto.getDeleted());
        return tag;
    }

    public TagDTO toDto(Tag tag){
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName(tag.getName());
        tagDTO.setId(tag.getId());
        tagDTO.setDeleted(tag.getDeleted());
        return tagDTO;
    }

    public void updateTag(TagDTO tagDTO, Tag tag){
        tag.setName(tagDTO.getName());
    }

    public void deleteTag(TagDTO tagDTO, Tag tag) {
        tag.setName(tagDTO.getName());
        tag.setDeleted(tagDTO.getDeleted());
    }

}

package com.yondu.knowledgebase.DTO.tag;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.post.PostDTO;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.Tag;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

        Long oldPageId = 0L;
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName(tag.getName());
        tagDTO.setId(tag.getId());
        tagDTO.setDeleted(tag.getDeleted());
        List<PageDTO> pages = new ArrayList<>();

        for (Page page : tag.getPages()){
            if(page.getId() != oldPageId){
                oldPageId = page.getId();
                pages.add(PageDTO.builder().id(oldPageId).build());
            }
        }
        tagDTO.setPages(pages);

        Long oldPostId = 0L;

        List<PostDTO> posts = new ArrayList<>();

        for (Post post : tag.getPosts()) {
            if (post.getId() != oldPostId) {
                oldPostId = post.getId();
                PostDTO postDto = new PostDTO();
                postDto.setId(post.getId());
                posts.add(postDto);

            }

        }
        tagDTO.setPosts(posts);

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

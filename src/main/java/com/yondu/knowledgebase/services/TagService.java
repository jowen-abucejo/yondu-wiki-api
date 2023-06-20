package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.Tag;
import com.yondu.knowledgebase.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public TagService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags(){
        return tagRepository.findAll();
    }

    public Tag getTag(Long id) {
        return tagRepository.findById(id).orElseThrow();
    }

    public Tag addTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public Tag updateTag(Tag tag) {
        Tag existingTag = getTag(tag.getId());
        existingTag.setName(tag.getName());
        return tagRepository.save(existingTag);
    }

    public Tag deleteTag(Tag tag){
        tag.setDeleted(true);
        tagRepository.save(tag);
        return tag;
    }

    public boolean isTagNameTaken(String tagName) {
        return tagRepository.existsByName(tagName);
    }

    public Tag getTagByName(String name){
        return tagRepository.findByName(name).orElseThrow();
    }

    public Tag addPageTag(Tag tag){
        Tag updatedTag = tagRepository.save(tag);
        return updatedTag;
    }

    public Tag updatePageTag(Tag tag){
        Tag updatedTag = tagRepository.save(tag);
        return updatedTag;
    }

}

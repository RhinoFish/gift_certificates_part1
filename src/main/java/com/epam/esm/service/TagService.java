package com.epam.esm.service;

import com.epam.esm.dao.TagDAO;
import com.epam.esm.dto.TagDTO;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private final TagDAO tagDAO;

    public TagService(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    }

    public void createTag(TagDTO tag) throws SQLException {
        tagDAO.save(tagDTOToTag(tag));
    }

    public List<TagDTO> getAllTags(){
        List<Tag> tags = tagDAO.getAll();
        List<TagDTO> tagsDTO = new ArrayList<>();
        for(Tag tag: tags){
            tagsDTO.add(tagToTagDTO(tag));
        }
        return tagsDTO;
    }

    public TagDTO getTag(String name){
        Tag tag = tagDAO.get(name);
        return tagToTagDTO(tag);
    }

    public TagDTO deleteTag(String name){
        Tag tag = tagDAO.delete(name);
        return tagToTagDTO(tag);
    }

    private Tag tagDTOToTag(TagDTO tagDTO){
        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        return tag;
    }

    private TagDTO tagToTagDTO(Tag tag){

        TagDTO tagDTO = new TagDTO();
        if(tag !=null) {
            tagDTO.setId(tag.getId());
            tagDTO.setName(tag.getName());
            return tagDTO;
        }
        return null;
    }
}

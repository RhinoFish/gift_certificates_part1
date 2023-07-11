package com.epam.esm.service;

import com.epam.esm.dao.GiftDAO;
import com.epam.esm.dao.TagDAO;
import com.epam.esm.dto.GiftDTO;
import com.epam.esm.model.Gift;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GiftService {
    @Autowired
    private final GiftDAO giftDAO;

    @Autowired
    private final TagDAO tagDao;
    @Autowired
    private final GiftTagService giftTagService;

    public GiftService(GiftDAO giftDAO, TagDAO tagDao, GiftTagService giftTagService) {
        this.giftDAO = giftDAO;
        this.tagDao = tagDao;
        this.giftTagService = giftTagService;
    }

    public List<GiftDTO> getAllGifts(String sort,String tagName,String nameDesc,boolean ascending) throws SQLException{

        List<GiftDTO> gifts = giftDAO.getAllGifts(sort,tagName,nameDesc,ascending);
        return  gifts;

    }
    public GiftDTO createGift(GiftDTO giftDTO) throws SQLException {
        if(giftDTO.getTags().isEmpty()){
            Gift gift = giftDAO.saveWithNoTags(giftDTOToGift(giftDTO));
            GiftDTO giftCreated = giftToGiftDTO(gift);
            giftCreated.setTags(giftDTO.getTags());
            return giftCreated;
        }
        List<String> tagsDB = getTagsOfDB();
        List<String> tagsDTO = giftDTO.getTags();
        List<String> newTags = getNewTags(tagsDB,tagsDTO);
        if(newTags.isEmpty()){
            List<Integer> idTags = getIdOfTags(tagsDTO);
            Gift gift = giftDAO.saveWithNoNewTags(giftDTOToGift(giftDTO),idTags);
            GiftDTO giftCreated = giftToGiftDTO(gift);
            giftCreated.setTags(giftDTO.getTags());
            return giftCreated;
        }
        Gift gift = giftDAO.saveWithNewTags(giftDTOToGift(giftDTO),newTags,tagsDTO);
        GiftDTO giftCreated = giftToGiftDTO(gift);
        giftCreated.setTags(giftDTO.getTags());
        return giftCreated;
    }

    private List<String> getNewTags(List<String> tagsDB, List<String> tagsDTO) {
        return tagsDTO.stream().filter(tag->!tagsDB.contains(tag)).collect(Collectors.toList());
    }

    public GiftDTO updateGift(GiftDTO giftDTO) throws SQLException {
        GiftDTO  giftUpdated;
       if(giftDTO.getTags().isEmpty()){
           Gift gift = giftDAO.updateWithoutTags(giftDTOToGift(giftDTO));
           giftUpdated = giftToGiftDTO(gift);
           giftUpdated.setTags(giftDTO.getTags());
           return giftUpdated;
       }
        List<String> tagsDB = getTagsOfDB();
        List<String> tagsDTO = giftDTO.getTags();
        List<String> newTags = getNewTags(tagsDB,tagsDTO);
        if(newTags.isEmpty()){
            List<Integer> idTags = getIdOfTags(tagsDTO);
            Gift gift = giftDAO.updateWithNoNewTags(giftDTOToGift(giftDTO),idTags);
            giftUpdated = giftToGiftDTO(gift);
            giftUpdated.setTags(giftDTO.getTags());
            return giftUpdated;
        }
        Gift gift = giftDAO.updateWithNewTags(giftDTOToGift(giftDTO),newTags,tagsDTO);
        giftUpdated= giftToGiftDTO(gift);
        giftUpdated.setTags(giftDTO.getTags());
        return giftUpdated;
    }

    private List<Integer> getIdTagsGift(int giftId) {
        return giftTagService.getIdTagsGift(giftId);
    }

    private Gift giftDTOToGift(GiftDTO giftDTO){
        Gift gift = new Gift();
        gift.setId(giftDTO.getId());
        gift.setName(giftDTO.getName());
        gift.setDescription(giftDTO.getDescription());
        gift.setPrice(giftDTO.getPrice());
        gift.setDuration(giftDTO.getDuration());
        gift.setCreateDate(giftDTO.getCreateDate());
        gift.setLastUpdateDate(giftDTO.getLastUpdateDate());
        return gift;
    }

    private List<String> getTagsOfDB(){
        List<String> tagsString = new ArrayList<>();
        List<Tag> tags = tagDao.getAll();
        for(Tag tag:tags){
            tagsString.add(tag.getName());
        }
        return tagsString;
    }

    private int getGiftId(String name){
        return giftDAO.getId(name);
    }

    private void insertNewTags(List<String> tagsDB, List<String> tags ) throws SQLException {
        Tag tagObject = new Tag();
        for(String tag : tags){
            if(!(tagsDB.contains(tag))){
                tagObject.setName(tag);
                tagDao.save(tagObject);
            }
        }
    }


    private List<Integer> getIdOfTags(List<String> tagsDTO){
        String[] tagsArray = tagsDTO.toArray(new String[0]);
        return tagDao.getIds(tagsArray);
    }

    private List<GiftTag> getGiftTag(int id, List<Integer> idTags){
        List<GiftTag> giftTags = new ArrayList<>();

        for(Integer idTag: idTags){
            giftTags.add(new GiftTag(id,idTag.intValue()));
        }
        return giftTags;
    }

    public Gift delete(int giftId) {
        return giftDAO.deleteGift(giftId);
    }

    public GiftDTO getGift(int giftId) {
        return giftDAO.get(giftId);
    }

    private GiftDTO giftToGiftDTO(Gift gift) {
        if(gift!=null){
            GiftDTO giftDTO = new GiftDTO();
            giftDTO.setId(gift.getId());
            giftDTO.setName(gift.getName());
            giftDTO.setDescription(gift.getDescription());
            giftDTO.setPrice(gift.getPrice());
            giftDTO.setDuration(gift.getDuration());
            giftDTO.setCreateDate(gift.getCreateDate());
            giftDTO.setLastUpdateDate(gift.getLastUpdateDate());
            return giftDTO;
        }
        return null;
    }
}

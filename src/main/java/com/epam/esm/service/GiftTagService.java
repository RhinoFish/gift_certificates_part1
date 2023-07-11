package com.epam.esm.service;

import com.epam.esm.dao.GiftTagDAO;
import com.epam.esm.model.GiftTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftTagService {
    @Autowired
    private final GiftTagDAO giftTagDAO;

    public GiftTagService(GiftTagDAO giftTagDAO) {
        this.giftTagDAO = giftTagDAO;
    }

    public void createRelationship(List<GiftTag> giftTags) {
        giftTagDAO.save(giftTags);
    }

    public void deleteAllRelationships(int giftId) {
        giftTagDAO.delete(giftId);
    }

    public void deleteRelationships(List<GiftTag> giftsTags) {
        giftTagDAO.delete(giftsTags);
    }

    public List<Integer> getIdTagsGift(int giftId) {
        return giftTagDAO.getIdTagsGift(giftId);
    }
}

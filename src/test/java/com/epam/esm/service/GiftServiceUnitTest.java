package com.epam.esm.service;

import com.epam.esm.dao.GiftDAO;
import com.epam.esm.dao.TagDAO;
import com.epam.esm.dto.TagDTO;
import com.epam.esm.model.Gift;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GiftServiceUnitTest {

    @Mock
    private GiftDAO giftDAO;

    @Mock
    private TagDAO tagDao;

    @Mock
    private GiftTagService giftTagService;

    @InjectMocks
    private GiftService giftService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddGiftHappyPath(){


    }
}

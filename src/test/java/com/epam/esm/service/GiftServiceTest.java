package com.epam.esm.service;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dto.GiftDTO;
import com.epam.esm.service.GiftService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from the OrderServiceConfig class
@ContextConfiguration(classes= TestConfig.class, loader= AnnotationConfigContextLoader.class)
public class GiftServiceTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testOrderService() {

    }
}
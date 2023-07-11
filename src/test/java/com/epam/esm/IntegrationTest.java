package com.epam.esm;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.GiftDAO;
import com.epam.esm.dao.TagDAO;
import com.epam.esm.dto.GiftDTO;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes= TestConfig.class)
@WebAppConfiguration
public class IntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GiftDAO giftDAO;
    @Autowired
    private TagDAO tagDAO;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void givenWac_whenServletContext_thenItProvidesGreetController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(webApplicationContext.getBean("dataSource"));
    }

    @Test
    void getAllTags() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tags"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());


    }

    @Test
    void postTagSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readTestResourceFile(Path.of("PostTag.txt"))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The tag was created successfully!!"))
                .andDo(print());

        mockMvc.perform(MockMvcRequestBuilders.get("/tags/{tag_name}","Tag 1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("Message.name").value("Tag 1"))
                .andDo(print());
    }
    @Test
    void getAllGifts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/gifts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

    }

    @Test
    void postGift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/gifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readTestResourceFile(Path.of("PostGift.txt"))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The gift was created successfully!!"));

        GiftDTO gifts = giftDAO.get(12);
        assertEquals(gifts.getName(),"Gift Test 2");
        assertEquals(gifts.getDescription(),"A Gift Test");
        assertEquals(gifts.getPrice(), BigDecimal.valueOf(57));
        assertEquals(gifts.getDuration(),Integer.valueOf(7));
        assertEquals(gifts.getTags().toString(),"[Anniversary, Friendship, Love]");
    }

    @Test
    void UpdateGiftSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/gifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readTestResourceFile(Path.of("PostGift.txt"))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The gift was created successfully!!"));

        GiftDTO gifts = giftDAO.get(11);
        assertEquals(gifts.getName(),"Gift Test 2");
        assertEquals(gifts.getDescription(),"A Gift Test");
        assertEquals(gifts.getPrice(), BigDecimal.valueOf(57));
        assertEquals(gifts.getDuration(),Integer.valueOf(7));
        assertEquals(gifts.getTags().toString(),"[Anniversary, Friendship, Love]");

        mockMvc.perform(MockMvcRequestBuilders.put("/gifts/{giftId}",11)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readTestResourceFile(Path.of("UpdateGift.txt"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The gift has been updated!"));

        GiftDTO giftUpdated = giftDAO.get(11);
        assertEquals(giftUpdated.getName(),"Gift Test Updated 2");
        assertEquals(giftUpdated.getDescription(),"A Gift Test Updated");
        assertEquals(giftUpdated.getPrice(), BigDecimal.valueOf(0));
        assertEquals(giftUpdated.getDuration(),Integer.valueOf(0));
        assertEquals(giftUpdated.getTags().toString(),"[Anniversary, Friendship, Love, New Tag, New Tag 2]");
    }

    static String readTestResourceFile(final Path path) {
        Path testResPath = Paths.get("src", "test", "resources");
        try {
            return Files.lines(testResPath.resolve(path), StandardCharsets.UTF_8)
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

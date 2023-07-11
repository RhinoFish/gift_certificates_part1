package com.epam.esm.controller;

import com.epam.esm.dto.GiftDTO;
import com.epam.esm.dto.TagDTO;
import com.epam.esm.model.Gift;
import com.epam.esm.service.GiftService;
import com.epam.esm.service.TagService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.CookieStore;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HomeControllerUnitTest {

    @Mock
    private GiftService giftService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @Test
    public void PostGiftTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/gifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readTestResourceFile(Path.of("PostGift.txt"))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The gift was created successfully!!"));

        verify(giftService, times(1)).createGift(any(GiftDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/gifts"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("Request body is missing"));

        verify(giftService, times(1)).createGift(any(GiftDTO.class));

        doThrow(new SQLException("A SQL error occurred")).when(giftService).createGift(any(GiftDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/gifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readTestResourceFile(Path.of("PostGift.txt"))))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("A error occurred in the creation of the Gift: Gift Test 2" ))
                .andExpect(MockMvcResultMatchers.jsonPath("Error").value("A SQL error occurred" ));

        verify(giftService, times(2)).createGift(any(GiftDTO.class));

    }

    @Test
    public void deleteGiftTest() throws Exception {
        LocalDate date = LocalDate.of(2023,7,7);
        Gift gift = new Gift();
        gift.setId(26);
        gift.setName("Gift Test 6");
        gift.setDescription("Test");
        gift.setPrice(BigDecimal.valueOf(49.99));
        gift.setDuration(5);
        gift.setCreateDate(Timestamp.valueOf(date.atStartOfDay()));
        gift.setLastUpdateDate(Timestamp.valueOf(date.atStartOfDay()));

        when(giftService.delete(anyInt())).thenReturn(gift);

        mockMvc.perform(MockMvcRequestBuilders.delete("/gifts/{giftId}",26))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(readTestResourceFile(Path.of("DeletedGift.txt")),false));

        verify(giftService, times(1)).delete(anyInt());

        when(giftService.delete(anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/gifts/{giftId}",26))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The Gift with the id 26 doesn't exist"));

        verify(giftService, times(2)).delete(anyInt());
    }

    @Test
    public void testGetTag() throws Exception{
        TagDTO tag = new TagDTO();
        tag.setName("Test Tag");

        when(tagService.getTag(any(String.class))).thenReturn(tag);

        mockMvc.perform(MockMvcRequestBuilders.get("/tags/{tag_name}","Test Tag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("Message.name").value("Test Tag"));

        verify(tagService, times(1)).getTag(any(String.class));

        when(tagService.getTag(any(String.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/tags/{tag_name}","Test Tag"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The tag with the name Test Tag doesn't exist"));

        verify(tagService, times(2)).getTag(any(String.class));
    }

    @Test
    public void deleteTagTest() throws Exception {
        TagDTO tag = new TagDTO();
        tag.setName("Test Tag");

        when(tagService.deleteTag(any(String.class))).thenReturn(tag);

        mockMvc.perform(MockMvcRequestBuilders.delete("/tags/{tag_name}","Test Tag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The Test Tag tag has been deleted"));

        verify(tagService, times(1)).deleteTag(any(String.class));

        when(tagService.deleteTag(any(String.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/tags/{tag_name}","Test Tag"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The tag with the name Test Tag doesn't exist"));

        verify(tagService, times(2)).deleteTag(any(String.class));
    }

    @Test
    public void getTags() throws Exception {
        List<TagDTO> tags = new ArrayList<>(
                Arrays.asList(new TagDTO("Tag 1"),
                              new TagDTO("Tag 2"),
                              new TagDTO("Tag 3"),
                              new TagDTO("Tag 4"),
                              new TagDTO("Tag 5"))
        );
        when(tagService.getAllTags()).thenReturn(tags);
        mockMvc.perform(MockMvcRequestBuilders.get("/tags"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(readTestResourceFile(Path.of("GetAllTags.txt")),false));

        verify(tagService, times(1)).getAllTags();

    }

    @Test
    public void postTag() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readTestResourceFile(Path.of("PostTag.txt"))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("The tag was created successfully!!"));

        verify(tagService, times(1)).createTag(any(TagDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/tags"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("Request body is missing"));

        verify(tagService, times(1)).createTag(any(TagDTO.class));

        doThrow(new SQLException("A SQL error occurred")).when(tagService).createTag(any(TagDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readTestResourceFile(Path.of("PostTag.txt"))))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("A error occurred in the creation of the Tag: Tag 1" ))
                .andExpect(MockMvcResultMatchers.jsonPath("Error").value("A SQL error occurred" ));

        verify(tagService, times(2)).createTag(any(TagDTO.class));

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

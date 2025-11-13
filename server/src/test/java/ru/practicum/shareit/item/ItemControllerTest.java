package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemBookingDateParametersDto itemBookingDateParametersDto;
    private NewItemDto newItemDto;
    private UpdateItemDto updateItemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto1 = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner("John")
                .build();

        itemDto2 = ItemDto.builder()
                .id(2L)
                .name("Hammer")
                .description("Steel hammer")
                .available(true)
                .owner("John")
                .build();

        itemBookingDateParametersDto = ItemBookingDateParametersDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner("John")
                .comments(new ArrayList<>())
                .build();

        newItemDto = NewItemDto.builder()
                .name("Saw")
                .description("Wood saw")
                .available(true)
                .build();

        updateItemDto = UpdateItemDto.builder()
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Great tool!")
                .authorName("Jane")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getItemsByOwner_success() throws Exception {
        given(itemService.getItemsByOwner(1L)).willReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Hammer"));
    }

    @Test
    void getItemBookingDateParametersDto_success() throws Exception {
        given(itemService.getItemById(1L, 1L)).willReturn(itemBookingDateParametersDto);

        mockMvc.perform(get("/items/1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner").value("John"));
    }

    @Test
    void create_success() throws Exception {
        given(itemService.addNewItem(any(NewItemDto.class), eq(1L))).willReturn(itemDto1);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"));
    }

    @Test
    void update_success() throws Exception {
        given(itemService.updateItem(any(UpdateItemDto.class))).willReturn(itemDto1);

        mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void removeItem_success() throws Exception {
        doNothing().when(itemService).deleteItem(1L, 1L);

        mockMvc.perform(delete("/items/1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(1L, 1L);
    }

    @Test
    void searchFilms_success() throws Exception {
        given(itemService.searchItems("drill")).willReturn(List.of(itemDto1));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void createComment_success() throws Exception {
        NewCommentRequest newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Excellent condition");

        given(itemService.addNewComment(eq(1L), eq(1L), any(NewCommentRequest.class))).willReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great tool!"))
                .andExpect(jsonPath("$.authorName").value("Jane"));
    }

    @Test
    void getItemsByOwner_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemBookingDateParametersDto_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeItem_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_withoutUserId_returnsBadRequest() throws Exception {
        NewCommentRequest newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Excellent condition");

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchFilms_withoutText_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/items/search"))
                .andExpect(status().isBadRequest());
    }
}

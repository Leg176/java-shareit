package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;
    private ItemRequestWithoutItemsDto withoutItemsDto1;
    private ItemRequestWithoutItemsDto withoutItemsDto2;
    private NewItemRequestDto newItemRequestDto;
    private UpdateItemRequestDto updateItemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto1 = ItemRequestDto.builder()
                .id(1L)
                .description("need drill")
                .requestor("user1")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        itemRequestDto2 = ItemRequestDto.builder()
                .id(2L)
                .description("need hammer")
                .requestor("user1")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        withoutItemsDto1 = ItemRequestWithoutItemsDto.builder()
                .id(3L)
                .description("other user request 1")
                .requestor("user3")
                .created(LocalDateTime.now())
                .build();

        withoutItemsDto2 = ItemRequestWithoutItemsDto.builder()
                .id(4L)
                .description("other user request 2")
                .requestor("user4")
                .created(LocalDateTime.now())
                .build();

        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("need a drill");

        updateItemRequestDto = new UpdateItemRequestDto();
        updateItemRequestDto.setDescription("updated description");
    }

    @Test
    void findAll_success() throws Exception {
        given(itemRequestService.getRequestsByOwner(1L)).willReturn(List.of(itemRequestDto1, itemRequestDto2));

        mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("need drill"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("need hammer"));
    }

    @Test
    void getRequest_success() throws Exception {
        given(itemRequestService.getRequestById(10L)).willReturn(itemRequestDto1);

        mockMvc.perform(get("/requests/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("need drill"))
                .andExpect(jsonPath("$.requestor").value("user1"));
    }

    @Test
    void getRequestNotOwner_success() throws Exception {
        given(itemRequestService.getRequestsByNotOwner(1L)).willReturn(List.of(withoutItemsDto1, withoutItemsDto2));

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].description").value("other user request 1"))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].description").value("other user request 2"));
    }

    @Test
    void create_success() throws Exception {
        ItemRequestDto response = ItemRequestDto.builder()
                .id(5L)
                .description("need a drill")
                .requestor("user5")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        given(itemRequestService.addNewRequest(any(NewItemRequestDto.class), eq(5L))).willReturn(response);

        mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.description").value("need a drill"))
                .andExpect(jsonPath("$.requestor").value("user5"));
    }

    @Test
    void update_success() throws Exception {
        ItemRequestDto response = ItemRequestDto.builder()
                .id(6L)
                .description("updated description")
                .requestor("user6")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        given(itemRequestService.updateRequest(any(UpdateItemRequestDto.class), eq(6L))).willReturn(response);

        mockMvc.perform(patch("/requests/6")
                        .header(X_SHARER_USER_ID, 6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.description").value("updated description"))
                .andExpect(jsonPath("$.requestor").value("user6"));
    }

    @Test
    void removeRequest_success() throws Exception {
        doNothing().when(itemRequestService).deleteRequest(7L, 7L);

        mockMvc.perform(delete("/requests/7")
                        .header(X_SHARER_USER_ID, 7L))
                .andExpect(status().isOk());

        verify(itemRequestService).deleteRequest(7L, 7L);
    }

    @Test
    void findAll_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(patch("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestNotOwner_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeRequest_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/requests/1"))
                .andExpect(status().isBadRequest());
    }
}
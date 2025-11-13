package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_success() throws Exception {
        List<UserDto> list = List.of(
                UserDto.builder().id(1L).name("u1").email("u1@mail.com").build(),
                UserDto.builder().id(2L).name("u2").email("u2@mail.com").build()
        );
        given(userService.getAllUsers()).willReturn(list);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("u1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("u2@mail.com"));
    }

    @Test
    void getUser_success() throws Exception {
        UserDto dto = UserDto.builder().id(10L).name("john").email("j@d.com").build();
        given(userService.getUserById(10L)).willReturn(dto);

        mockMvc.perform(get("/users/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("john"))
                .andExpect(jsonPath("$.email").value("j@d.com"));
    }

    @Test
    void create_success() throws Exception {
        NewUserDto req = new NewUserDto();
        req.setName("alex");
        req.setEmail("a@a.com");

        UserDto resp = UserDto.builder().id(5L).name("alex").email("a@a.com").build();
        given(userService.saveUser(any(NewUserDto.class))).willReturn(resp);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()) // у вас нет @ResponseStatus, поэтому 200 OK
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("alex"))
                .andExpect(jsonPath("$.email").value("a@a.com"));
    }

    @Test
    void update_success() throws Exception {
        UpdateUserDto req = new UpdateUserDto();
        req.setName("new name");
        req.setEmail("new@mail.com");

        UserDto resp = UserDto.builder().id(3L).name("new name").email("new@mail.com").build();
        given(userService.updateUser(any(UpdateUserDto.class))).willReturn(resp);

        mockMvc.perform(patch("/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("new name"))
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    @Test
    void deleteUser_success() throws Exception {
        doNothing().when(userService).deleteUser(9L);

        mockMvc.perform(delete("/users/9"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(9L);
    }

    @Test
    void update_withOnlyName_success() throws Exception {
        UpdateUserDto req = new UpdateUserDto();
        req.setName("updated name");
        // email не устанавливаем

        UserDto resp = UserDto.builder().id(4L).name("updated name").email("old@mail.com").build();
        given(userService.updateUser(any(UpdateUserDto.class))).willReturn(resp);

        mockMvc.perform(patch("/users/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("updated name"))
                .andExpect(jsonPath("$.email").value("old@mail.com"));
    }

    @Test
    void update_withOnlyEmail_success() throws Exception {
        UpdateUserDto req = new UpdateUserDto();
        req.setEmail("newemail@mail.com");
        // name не устанавливаем

        UserDto resp = UserDto.builder().id(5L).name("old name").email("newemail@mail.com").build();
        given(userService.updateUser(any(UpdateUserDto.class))).willReturn(resp);

        mockMvc.perform(patch("/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("old name"))
                .andExpect(jsonPath("$.email").value("newemail@mail.com"));
    }
}

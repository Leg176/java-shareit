package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.error.exceptions.BadRequestException;
import ru.practicum.shareit.error.exceptions.NotFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ErrorHandlerMockMvcTest.TestController.class)
@SpringJUnitWebConfig({ErrorHandler.class, ErrorHandlerMockMvcTest.TestController.class})
class ErrorHandlerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenNotFoundExceptionThrown_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found exception"));
    }

    @Test
    void whenBadRequestExceptionThrown_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad request exception"));
    }

    @org.springframework.web.bind.annotation.RestController
    static class TestController {

        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new NotFoundException("Not found exception");
        }

        @GetMapping("/test/bad-request")
        public void throwBadRequest() {
            throw new BadRequestException("Bad request exception");
        }
    }
}

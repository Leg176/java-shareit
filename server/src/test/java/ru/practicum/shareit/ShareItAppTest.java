package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ShareItAppTest {

    @Test
    void contextLoads() {
        assertNotNull(ShareItApp.class);
    }

    @Test
    void mainMethodStartsApplication() {
        ShareItApp.main(new String[]{});
    }
}

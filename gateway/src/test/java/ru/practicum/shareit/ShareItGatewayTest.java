package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
        "server.port=9090",
        "shareit.server.url=http://localhost:9090"
})
class ShareItGatewayTest {

    @Test
    void contextLoads() {
        assertNotNull(ShareItGateway.class);
    }

    @Test
    void mainMethodStartsApplication() {
        ShareItGateway.main(new String[]{});
    }
}

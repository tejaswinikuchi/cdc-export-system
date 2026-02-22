package com.example.cdc;

import com.example.cdc.entity.User;
import com.example.cdc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user1 = new User();
        user1.setName("Test User 1");
        user1.setEmail("user1@test.com");
        user1.setCreatedAt(OffsetDateTime.now().minusDays(2));
        user1.setUpdatedAt(OffsetDateTime.now().minusDays(2));
        user1.setIsDeleted(false);

        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("user2@test.com");
        user2.setCreatedAt(OffsetDateTime.now().minusDays(1));
        user2.setUpdatedAt(OffsetDateTime.now().minusDays(1));
        user2.setIsDeleted(false);

        userRepository.saveAll(List.of(user1, user2));
    }

    @Test
    void fullExportShouldReturnAccepted() throws Exception {
        mockMvc.perform(post("/exports/full")
                        .header("X-Consumer-ID", "test-consumer"))
                .andExpect(status().isAccepted());
    }

    @Test
    void incrementalExportShouldReturnAccepted() throws Exception {
        mockMvc.perform(post("/exports/incremental")
                        .header("X-Consumer-ID", "test-consumer"))
                .andExpect(status().isAccepted());
    }

    @Test
    void deltaExportShouldReturnAccepted() throws Exception {
        mockMvc.perform(post("/exports/delta")
                        .header("X-Consumer-ID", "test-consumer"))
                .andExpect(status().isAccepted());
    }
}
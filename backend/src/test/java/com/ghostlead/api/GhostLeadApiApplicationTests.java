package com.ghostlead.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class GhostLeadApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn404WhenLeadDoesNotExist() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/leads/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Lead não encontrado"));
    }
}


package com.ghostlead.api;

import com.ghostlead.api.entity.Company;
import com.ghostlead.api.entity.Lead;
import com.ghostlead.api.repository.CompanyRepository;
import com.ghostlead.api.repository.LeadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@org.springframework.security.test.context.support.WithMockUser(username="11111111-1111-1111-1111-111111111111")
class GhostLeadApiApplicationTests {
    private Company owned(Company company) { company.setOwnerId(UUID.fromString("11111111-1111-1111-1111-111111111111")); return company; }


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Test
    void shouldReturn404WhenLeadDoesNotExist() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/leads/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Lead não encontrado"));
    }

    @Test
    void shouldCreateLeadSuccessfully() throws Exception {

        Company company = new Company(
                "Empresa Teste",
                "empresa.teste@ghostlead.com"
        );

        Company savedCompany = companyRepository.save(owned(company));

        String requestBody = """
                {
                    "name": "João Teste",
                    "email": "joao.teste@ghostlead.com",
                    "phone": "11999999999",
                    "companyId": "%s"
                }
                """.formatted(savedCompany.getId());

        mockMvc.perform(post("/api/leads").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("João Teste"))
                .andExpect(jsonPath("$.email")
                        .value("joao.teste@ghostlead.com"))
                .andExpect(jsonPath("$.phone")
                        .value("11999999999"));
    }

    @Test
    void shouldReturn404WhenCreatingLeadWithNonExistentCompany()
            throws Exception {

        UUID nonExistentCompanyId = UUID.randomUUID();

        String requestBody = """
                {
                    "name": "Lead Empresa Inexistente",
                    "email": "lead.inexistente@ghostlead.com",
                    "phone": "11911112222",
                    "companyId": "%s"
                }
                """.formatted(nonExistentCompanyId);

        mockMvc.perform(post("/api/leads").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(s
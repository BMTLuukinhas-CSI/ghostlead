
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
class GhostLeadApiApplicationTests {

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

        Company savedCompany = companyRepository.save(company);

        String requestBody = """
                {
                    "name": "João Teste",
                    "email": "joao.teste@ghostlead.com",
                    "phone": "11999999999",
                    "companyId": "%s"
                }
                """.formatted(savedCompany.getId());

        mockMvc.perform(post("/api/leads")
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

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Empresa não encontrada"));
    }

    @Test
    void shouldReturnLeadWhenItExists() throws Exception {

        Company company = new Company(
                "Empresa GET Teste",
                "empresa.get@ghostlead.com"
        );

        Company savedCompany = companyRepository.save(company);

        Lead lead = new Lead(
                "Maria Teste",
                "maria.teste@ghostlead.com",
                "11988887777",
                savedCompany
        );

        Lead savedLead = leadRepository.save(lead);

        mockMvc.perform(get("/api/leads/" + savedLead.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(savedLead.getId().toString()))
                .andExpect(jsonPath("$.name")
                        .value("Maria Teste"))
                .andExpect(jsonPath("$.email")
                        .value("maria.teste@ghostlead.com"))
                .andExpect(jsonPath("$.phone")
                        .value("11988887777"));
    }

    @Test
    void shouldReturnAllLeads() throws Exception {

        Company company = new Company(
                "Empresa Lista Teste",
                "empresa.lista@ghostlead.com"
        );

        Company savedCompany = companyRepository.save(company);

        Lead lead1 = new Lead(
                "Carlos Teste",
                "carlos.teste@ghostlead.com",
                "11977776666",
                savedCompany
        );

        Lead lead2 = new Lead(
                "Ana Teste",
                "ana.teste@ghostlead.com",
                "11966665555",
                savedCompany
        );

        leadRepository.save(lead1);
        leadRepository.save(lead2);

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Carlos Teste')]")
                        .exists())
                .andExpect(jsonPath("$[?(@.name == 'Ana Teste')]")
                        .exists());
    }

    @Test
    void shouldUpdateLeadSuccessfully() throws Exception {

        Company company = new Company(
                "Empresa PUT Teste",
                "empresa.put@ghostlead.com"
        );

        Company savedCompany = companyRepository.save(company);

        Lead lead = new Lead(
                "Pedro Antigo",
                "pedro.antigo@ghostlead.com",
                "11955554444",
                savedCompany
        );

        Lead savedLead = leadRepository.save(lead);

        String requestBody = """
                {
                    "name": "Pedro Atualizado",
                    "email": "pedro.atualizado@ghostlead.com",
                    "phone": "11944443333",
                    "companyId": "%s"
                }
                """.formatted(savedCompany.getId());

        mockMvc.perform(put("/api/leads/" + savedLead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(savedLead.getId().toString()))
                .andExpect(jsonPath("$.name")
                        .value("Pedro Atualizado"))
                .andExpect(jsonPath("$.email")
                        .value("pedro.atualizado@ghostlead.com"))
                .andExpect(jsonPath("$.phone")
                        .value("11944443333"));
    }

    @Test
    void shouldReturn404WhenUpdatingLeadWithNonExistentCompany()
            throws Exception {

        Company company = new Company(
                "Empresa PUT Erro Teste",
                "empresa.put.erro@ghostlead.com"
        );

        Company savedCompany = companyRepository.save(company);

        Lead lead = new Lead(
                "Lead Original",
                "lead.original@ghostlead.com",
                "11922223333",
                savedCompany
        );

        Lead savedLead = leadRepository.save(lead);

        UUID nonExistentCompanyId = UUID.randomUUID();

        String requestBody = """
                {
                    "name": "Lead Atualizado",
                    "email": "lead.atualizado@ghostlead.com",
                    "phone": "11944445555",
                    "companyId": "%s"
                }
                """.formatted(nonExistentCompanyId);

        mockMvc.perform(put("/api/leads/" + savedLead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Empresa não encontrada"));
    }

    @Test
    void shouldDeleteLeadSuccessfully() throws Exception {

        Company company = new Company(
                "Empresa DELETE Teste",
                "empresa.delete@ghostlead.com"
        );

        Company savedCompany = companyRepository.save(company);

        Lead lead = new Lead(
                "Lucas Para Deletar",
                "lucas.delete@ghostlead.com",
                "11933332222",
                savedCompany
        );

        Lead savedLead = leadRepository.save(lead);

        mockMvc.perform(delete("/api/leads/" + savedLead.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/leads/" + savedLead.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Lead não encontrado"));
    }
}

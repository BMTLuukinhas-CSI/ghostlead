package com.ghostlead.api;

import com.ghostlead.api.entity.*;
import com.ghostlead.api.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIsolationTests {
    @Autowired MockMvc mvc;
    @Autowired CompanyRepository companies;
    @Autowired LeadRepository leads;
    @Autowired UserRepository users;
    private static final String OWNER="22222222-2222-2222-2222-222222222222";
    private static final String OTHER="33333333-3333-3333-3333-333333333333";
    private Company company(String owner) {
        Company c=new Company("Empresa privada","empresa-"+UUID.randomUUID()+"@example.com");
        c.setOwnerId(owner==null?null:UUID.fromString(owner)); return companies.saveAndFlush(c);
    }
    @Test void anonymousCannotReadCrmAndMutationsRequireCsrf() throws Exception {
        mvc.perform(get("/api/leads")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/companies")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/dashboard")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/companies").with(user(OWNER)).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Acme\",\"email\":\"a@example.com\"}")).andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
    }
    @Test void anotherAccountCannotReadUpdateDeleteOrAttachPrivateRecords() throws Exception {
        Company c=company(OWNER); Lead l=leads.saveAndFlush(new Lead("Segredo","lead@example.com","",c));
        mvc.perform(get("/api/companies").with(user(OTHER))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/leads").with(user(OTHER))).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/dashboard").with(user(OTHER))).andExpect(jsonPath("$.leads").value(0)).andExpect(jsonPath("$.companies").value(0));
        mvc.perform(get("/api/leads/"+l.getId()).with(user(OTHER))).andExpect(status().isNotFound());
        mvc.perform(get("/api/companies/"+c.getId()).with(user(OTHER))).andExpect(status().isNotFound());
        mvc.perform(delete("/api/leads/"+l.getId()).with(user(OTHER)).with(csrf())).andExpect(status().isNotFound());
        mvc.perform(delete("/api/companies/"+c.getId()).with(user(OTHER)).with(csrf())).andExpect(status().isNotFound());
        String body="{\"name\":\"Alterado\",\"email\":\"x@example.com\",\"companyId\":\""+c.getId()+"\"}";
        mvc.perform(put("/api/leads/"+l.getId()).with(user(OTHER)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isNotFound());
        mvc.perform(post("/api/leads").with(user(OTHER)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isNotFound());
        mvc.perform(patch("/api/leads/"+l.getId()+"/status").with(user(OTHER)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"WON\"}")).andExpect(status().isNotFound());
        mvc.perform(put("/api/companies/"+c.getId()).with(user(OTHER)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Alterada\",\"em
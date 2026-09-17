package com.ghostlead.api.service;

import com.ghostlead.api.entity.Company;
import com.ghostlead.api.entity.Lead;
import com.ghostlead.api.exception.CompanyNotFoundException;
import com.ghostlead.api.exception.LeadNotFoundException;
import com.ghostlead.api.repository.CompanyRepository;
import com.ghostlead.api.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final CompanyRepository companyRepository;

    public LeadService(
            LeadRepository leadRepository,
            CompanyRepository companyRepository
    ) {
        this.leadRepository = leadRepository;
        this.companyRepository = companyRepository;
    }

    public Lead createLead(
            String name,
            String email,
            String phone,
            UUID companyId
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);

        Lead lead = new Lead(name, email, phone, company);

        return leadRepository.save(lead);
    }

    public List<Lead> findAll() {
        return leadRepository.findAll();
    }

    public Lead findById(UUID id) {
        return leadRepository.findById(id)
                .orElseThrow(LeadNotFoundException::new);
    }

    public Lead updateLead(
            UUID id,
            String name,
            String email,
            String phone,
            UUID companyId
    ) {
        Lead lead = findById(id);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);

        lead.setName(name);
        lead.setEmail(email);
        lead.setPhone(phone);
        lead.setCompany(company);

        return leadRepository.save(lead);
    }

    public void deleteLead(UUID id) {
        Lead lead = findById(id);
        leadRepository.delete(lead);
    }
}

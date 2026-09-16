package com.ghostlead.api.service;

import com.ghostlead.api.entity.Company;
import com.ghostlead.api.repository.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCompany(String name, String email) {
        Company company = new Company(name, email);
        return companyRepository.save(company);
    }
}

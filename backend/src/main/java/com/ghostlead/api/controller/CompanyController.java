package com.ghostlead.api.controller;

import com.ghostlead.api.dto.CreateCompanyRequest;
import com.ghostlead.api.entity.Company;
import com.ghostlead.api.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Company createCompany(
            @Valid @RequestBody CreateCompanyRequest request
    ) {
        return companyService.createCompany(
                request.name(),
                request.email()
        );
    }

    @GetMapping
    public List<Company> findAll() {
        return companyService.findAll();
    }

    @GetMapping("/{id}")
    public Company findById(@PathVariable UUID id) {
        return companyService.findById(id);
    }
}

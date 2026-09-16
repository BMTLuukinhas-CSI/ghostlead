package com.ghostlead.api.controller;

import com.ghostlead.api.entity.Company;
import com.ghostlead.api.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam String name,
            @RequestParam String email
    ) {
        return companyService.createCompany(name, email);
    }
}

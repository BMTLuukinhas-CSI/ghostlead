package com.ghostlead.api.controller;

import com.ghostlead.api.dto.CreateLeadRequest;
import com.ghostlead.api.dto.UpdateLeadRequest;
import com.ghostlead.api.entity.Lead;
import com.ghostlead.api.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lead createLead(
            @Valid @RequestBody CreateLeadRequest request
    ) {
        return leadService.createLead(
                request.name(),
                request.email(),
                request.phone(),
                request.companyId()
        );
    }

    @GetMapping
    public List<Lead> findAll() {
        return leadService.findAll();
    }

    @GetMapping("/{id}")
    public Lead findById(@PathVariable UUID id) {
        return leadService.findById(id);
    }

    @PutMapping("/{id}")
    public Lead updateLead(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLeadRequest request
    ) {
        return leadService.updateLead(
                id,
                request.name(),
                request.email(),
                request.phone(),
                request.companyId()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLead(@PathVariable UUID id) {
        leadService.deleteLead(id);
    }
}

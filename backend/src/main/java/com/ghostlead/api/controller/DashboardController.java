package com.ghostlead.api.controller;
import com.ghostlead.api.service.LeadService;
import org.springframework.web.bind.annotation.*;
@RestController
public class DashboardController {
    private final LeadService leads;
    public DashboardController(LeadService leads) { this.leads=leads; }
    @GetMapping("/api/dashboard") public LeadService.Dashboard get() { return leads.dashboard(); }
}

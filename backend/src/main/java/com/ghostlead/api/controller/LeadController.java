package com.ghostlead.api.controller;
import com.ghostlead.api.dto.*;
import com.ghostlead.api.entity.LeadStatus;
import com.ghostlead.api.service.LeadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/api/leads")
public class LeadController {
    private final LeadService leads;
    public LeadController(LeadService leads) { this.leads=leads; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public LeadResponse create(@Valid @RequestBody CreateLeadRequest d) { return leads.create(d); }
    @GetMapping public LeadService.LeadPage list(@RequestParam(defaultValue="") @Size(max=200) String q,@RequestParam(required=false) LeadStatus status,@RequestParam(required=false) UUID companyId,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="20") @Min(1) @Max(100) int size) { return leads.search(q,status,companyId,page,size); }
    @GetMapping("/{id}") public LeadResponse get(@PathVariable UUID id) { return leads.findById(id); }
    @PutMapping("/{id}") public LeadResponse update(@PathVariable UUID id,@Valid @RequestBody UpdateLeadRequest d) { return leads.update(id,d); }
    public record StageRequest(@NotNull LeadStatus status) {}
    @PatchMapping("/{id}/status") public LeadResponse stage(@PathVariable UUID id,@Valid @RequestBody StageRequest d) { return leads.stage(id,d.status()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { leads.delete(id); }
}

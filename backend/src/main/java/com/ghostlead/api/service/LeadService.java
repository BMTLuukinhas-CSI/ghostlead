package com.ghostlead.api.service;
import com.ghostlead.api.dto.*;
import com.ghostlead.api.entity.*;
import com.ghostlead.api.exception.LeadNotFoundException;
import com.ghostlead.api.repository.*;
import com.ghostlead.api.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.*;
import java.math.BigDecimal;
@Service
@Transactional
public class LeadService {
    private final LeadRepository leads;
    private final CompanyService companies;
    private final CompanyRepository companyRepository;
    private final CurrentUser current;
    public LeadService(LeadRepository leads,CompanyService companies,CompanyRepository companyRepository,CurrentUser current) { this.leads=leads;this.companies=companies;this.companyRepository=companyRepository;this.current=current; }
    private Lead owned(UUID id) { return leads.findByIdAndCompanyOwnerId(id,current.id()).orElseThrow(LeadNotFoundException::new); }
    public LeadResponse create(CreateLeadRequest d) {
        Lead l=new Lead(d.name().strip(),d.email().strip(),d.phone(),companies.findById(d.companyId()));
        details(l,d.status(),d.value(),d.notes()); return LeadResponse.from(leads.saveAndFlush(l));
    }
    private void details(Lead l,LeadStatus status,BigDecimal value,String notes) {
        if(status!=null) l.setStatus(status);
        if(value!=null) l.setValue(value);
        l.setNotes(notes);
    }
    public LeadResponse update(UUID id,UpdateLeadRequest d) {
        Lead l=owned(id); l.setCompany(companies.findById(d.companyId())); l.setName(d.name().strip()); l.setEmail(d.email().strip());l.setPhone(d.phone());
        details(l,d.status(),d.value(),d.notes());return LeadResponse.from(leads.saveAndFlush(l));
    }
    public LeadResponse stage(UUID id,LeadStatus status) { Lead l=owned(id);l.setStatus(status);return LeadResponse.from(leads.saveAndFlush(l)); }
    public void delete(UUID id) { leads.delete(owned(id)); }
    @Transactional(readOnly=true) public LeadResponse findById(UUID id) { return LeadResponse.from(owned(id)); }
    public record LeadPage(List<LeadResponse> content,long totalElements,int totalPages,int number) {}
    @Transactional(readOnly=true) public LeadPage search(String q,LeadStatus status,UUID companyId,int page,int size) {
        UUID owner=current.id();
        Specification<Lead> spec=(root,query,cb)->cb.equal(root.get("company").get("ownerId"),owner);
        if(status!=null) spec=spec.and((root,query,cb)->cb.equal(root.get("status"),status));
        if(companyId!=null) spec=spec.and((root,query,cb)->cb.equal(root.get("company").get("id"),companyId));
        if(q!=null && !q.isBlank()) {
            String term="%"+q.strip().toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";
            spec=spec.and((root,query,cb)->cb.or(cb.like(cb.lower(root.get("name")),term,'\\'),cb.like(cb.lower(root.get("email")),term,'\\')));
        }
        Page<Lead> result=leads.findAll(spec,PageRequest.of(page,size,Sort.by(Sort.Order.desc("createdAt"),Sort.Order.desc("id"))));
        return new LeadPage(result.getContent().stream().map(LeadResponse::from).toList(),result.getTotalElements(),result.getTotalPages(),result.getNumber());
    }
    public record Stage(LeadStatus status,long count,BigDecimal value) {}
    public record Dashboard(long companies,long leads,BigDecimal pipelineValue,BigDecimal wonValue,List<Stage> stages) {}
    @Transactional(readOnly=true) public Dashboard dashboard() {
        var totals=leads.totals(current.id()); var stages=new ArrayList<Stage>();long count=0;BigDecimal pipeline=BigDecimal.ZERO,won=BigDecimal.ZERO;
        for(LeadStatus status:LeadStatus.values()) {
            var t=totals.stream().filter(x->x.getStatus()==status).findFirst();
            long n=t.map(LeadRepository.StageTotal::ge
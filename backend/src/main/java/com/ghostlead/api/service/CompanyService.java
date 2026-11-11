package com.ghostlead.api.service;
import com.ghostlead.api.entity.Company;
import com.ghostlead.api.exception.CompanyNotFoundException;
import com.ghostlead.api.repository.*;
import com.ghostlead.api.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@Service
@Transactional
public class CompanyService {
    private final CompanyRepository companies;
    private final LeadRepository leads;
    private final CurrentUser current;
    public CompanyService(CompanyRepository companies, LeadRepository leads, CurrentUser current) { this.companies=companies;this.leads=leads;this.current=current; }
    public Company createCompany(String name,String email) {
        Company c=new Company(name.strip(),email.strip().toLowerCase(Locale.ROOT)); c.setOwnerId(current.id()); return companies.save(c);
    }
    @Transactional(readOnly=true) public List<Company> findAll() { return companies.findByOwnerIdOrderByNameAsc(current.id()); }
    @Transactional(readOnly=true) public Company findById(UUID id) { return companies.findByIdAndOwnerId(id,current.id()).orElseThrow(CompanyNotFoundException::new); }
    public Company updateCompany(UUID id,String name,String email) {
        Company c=findById(id);c.setName(name.strip());c.setEmail(email.strip().toLowerCase(Locale.ROOT));return companies.save(c);
    }
    public void deleteCompany(UUID id) {
        Company c=findById(id);
        if(leads.existsByCompany_Id(id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Remova ou transfira os leads desta empresa antes de excluí-la");
        companies.delete(c);
    }
}

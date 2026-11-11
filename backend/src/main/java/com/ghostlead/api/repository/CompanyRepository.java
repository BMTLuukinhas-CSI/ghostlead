package com.ghostlead.api.repository;
import com.ghostlead.api.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    List<Company> findByOwnerIdOrderByNameAsc(UUID ownerId);
    Optional<Company> findByIdAndOwnerId(UUID id,UUID ownerId);
    long countByOwnerId(UUID ownerId);
}

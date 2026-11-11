package com.ghostlead.api.repository;
import com.ghostlead.api.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
import java.math.BigDecimal;
public interface LeadRepository extends JpaRepository<Lead, UUID>, JpaSpecificationExecutor<Lead> {
    Optional<Lead> findByIdAndCompanyOwnerId(UUID id,UUID ownerId);
    boolean existsByCompany_Id(UUID companyId);
    @Query("select l.status as status, count(l) as total, sum(l.value) as value from Lead l where l.company.ownerId=:owner group by l.status")
    List<StageTotal> totals(@Param("owner") UUID owner);
    interface StageTotal { LeadStatus getStatus(); long getTotal(); BigDecimal getValue(); }
}

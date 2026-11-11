package com.ghostlead.api.dto;
import com.ghostlead.api.entity.*;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;
public record LeadResponse(UUID id,String name,String email,String phone,UUID companyId,String companyName,LeadStatus status,BigDecimal value,String notes,Instant createdAt,Instant updatedAt) {
    public static LeadResponse from(Lead l) { return new LeadResponse(l.getId(),l.getName(),l.getEmail(),l.getPhone(),l.getCompanyId(),l.getCompany().getName(),l.getStatus(),l.getValue(),l.getNotes(),l.getCreatedAt(),l.getUpdatedAt()); }
}

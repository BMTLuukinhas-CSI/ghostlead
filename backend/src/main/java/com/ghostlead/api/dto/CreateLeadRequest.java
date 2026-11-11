package com.ghostlead.api.dto;
import jakarta.validation.constraints.*;
import com.ghostlead.api.entity.LeadStatus;
import java.util.UUID;
import java.math.BigDecimal;
public record CreateLeadRequest(
    @NotBlank(message="Nome é obrigatório") @Size(max=160) String name,
    @NotBlank(message="Email é obrigatório") @Email(message="Email inválido") @Size(max=254) String email,
    @Size(max=40) String phone,
    @NotNull(message="Empresa é obrigatória") UUID companyId,
    LeadStatus status,
    @DecimalMin("0.00") @Digits(integer=12,fraction=2) BigDecimal value,
    @Size(max=4000) String notes) {}

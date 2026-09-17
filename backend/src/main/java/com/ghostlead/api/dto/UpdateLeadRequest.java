package com.ghostlead.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateLeadRequest(

        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        String phone,

        @NotNull(message = "Empresa é obrigatória")
        UUID companyId

) {
}

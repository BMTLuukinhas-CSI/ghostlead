package com.ghostlead.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateCompanyRequest(

        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email

) {
}

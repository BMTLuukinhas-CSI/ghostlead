package com.ghostlead.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record UpdateCompanyRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max=160) String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max=254) String email

) {
}

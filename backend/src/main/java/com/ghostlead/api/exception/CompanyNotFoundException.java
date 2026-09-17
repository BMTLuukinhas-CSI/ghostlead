package com.ghostlead.api.exception;

public class CompanyNotFoundException extends RuntimeException {

    public CompanyNotFoundException() {
        super("Empresa não encontrada");
    }

    public CompanyNotFoundException(String message) {
        super(message);
    }
}

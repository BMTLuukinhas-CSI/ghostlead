package com.ghostlead.api.exception;

public class LeadNotFoundException extends RuntimeException {

    public LeadNotFoundException() {
        super("Lead não encontrado");
    }
}

package com.ghostlead.api.exception;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({CompanyNotFoundException.class,LeadNotFoundException.class}) @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,String> missing(RuntimeException e) { return Map.of("error",e.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> invalid(MethodArgumentNotValidException e) { return Map.of("error",e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).findFirst().orElse("Dados inválidos")); }
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,HandlerMethodValidationException.class}) @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> malformed(Exception e) { return Map.of("error","Dados inválidos. Confira os campos e tente novamente."); }
    @ExceptionHandler(DataIntegrityViolationException.class) @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String,String> conflict(Exception e) { return Map.of("error","Registro duplicado ou vinculado a outros dados. Confira o e-mail e os vínculos."); }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,String>> status(ResponseStatusException e) { return ResponseEntity.status(e.getStatusCode()).body(Map.of("error",e.getReason()==null?"Não foi possível concluir":e.getReason())); }
}

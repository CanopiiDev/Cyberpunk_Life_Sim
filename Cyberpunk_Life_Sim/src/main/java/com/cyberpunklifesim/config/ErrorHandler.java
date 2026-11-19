package com.cyberpunklifesim.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Zorgt dat iedere fout dezelfde simpele JSON teruggeeft, zodat React die makkelijk kan tonen:
 * { "status": 400, "message": "Niet genoeg geld." }
 */
@RestControllerAdvice
public class ErrorHandler {

    public record ErrorResponse(int status, String message) {
    }

    // Fouten die we zelf gooien in de services, bijvoorbeeld "Niet genoeg geld" (400) of "bestaat niet" (404)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleGameError(ResponseStatusException e) {
        int status = e.getStatusCode().value();
        return ResponseEntity.status(status).body(new ErrorResponse(status, e.getReason()));
    }

    // Validatiefouten, bijvoorbeeld een lege voornaam (@NotBlank)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message));
    }

    // Kapotte JSON, bijvoorbeeld een datum in het verkeerde formaat
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Ongeldige JSON. Een datum schrijf je als \"jjjj-mm-dd\", bijvoorbeeld \"2059-04-12\"."));
    }
}

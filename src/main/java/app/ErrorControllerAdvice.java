package app;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

/**
 * Automagically creates JSON error messages when a NotFoundException or BadRequestException is thrown.
 * If either of these exceptions are thrown, first assume that it is involved in client error messages.
 */
@ControllerAdvice
public class ErrorControllerAdvice {
    @ExceptionHandler({NotFoundException.class, BadRequestException.class})
    protected ResponseEntity defaultError(Exception e) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(String.format("{\"error\":\"%s\", \"type\": \"%s\"}", e.getMessage(), e.toString()));
    }
}

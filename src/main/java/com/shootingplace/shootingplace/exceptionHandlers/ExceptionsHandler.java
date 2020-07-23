package com.shootingplace.shootingplace.exceptionHandlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionsHandler {
    private final Logger LOG = LogManager.getLogger(getClass());


    @ExceptionHandler(value = MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMissingPathVariableException(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNumberFormatException(Exception ex) {
        LOG.error(ex.getMessage() + "źle podano numer PESEL");
        return "podałeś złe dane";
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(Exception ex) {
        LOG.error(ex.getMessage() + "Wprowadzono nieprawidłowe dane");
        return "Wprowadzono nieprawidłowe dane";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleLegitimationNumberException(Exception ex) {
        LOG.error(ex.getMessage());
        LOG.error("Nie można nadać komuś tego numeru legitymacji");
        return "Nie można nadać komuś tego numeru legitymacji";
    }
}
package com.example.computerweb.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class , HttpMessageNotReadableException.class , DataIntegrityViolationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException (Exception e , WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(getStatus(e));
        errorResponse.setError(getError(e));
        errorResponse.setPath(request.getDescription(false).replace("uri=" , ""));
        errorResponse.setMessage(getMessage(e));

        return errorResponse;
    }

    @ExceptionHandler({ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalException (Exception e , WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(getStatus(e));
        errorResponse.setError(getError(e));
        errorResponse.setPath(request.getDescription(false).replace("uri=" , ""));
        errorResponse.setMessage(getMessage(e));

        return errorResponse;
    }

    @ExceptionHandler({CalendarException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCalendarException (CalendarException e , WebRequest  request){
    ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Duplication calendar");
        errorResponse.setPath("");
        errorResponse.setMessage(e.getErrorMessage());

        return errorResponse;
    }

    private static int getStatus (Exception e ){
        int status = 0;
        if ( e instanceof ConstraintViolationException ) {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }else if ( e instanceof MethodArgumentNotValidException ||
                e instanceof HttpMessageNotReadableException ||
        e instanceof  DataIntegrityViolationException

        )
        {
            status = HttpStatus.BAD_REQUEST.value();
        }

        return status;
    }
    private static String getError (Exception e ){
        String error = "";
        if ( e instanceof ConstraintViolationException ) {
            error = "PathVariable invalid";
        }else if ( e instanceof MethodArgumentNotValidException ||
                e instanceof HttpMessageNotReadableException ||
        e instanceof  DataIntegrityViolationException)
        {
            error = HttpStatus.BAD_REQUEST.getReasonPhrase();
        }

        return error;
    }
    private static String getMessage(Exception e) {
        String message = e.getMessage();
        if ( e instanceof MethodArgumentNotValidException ){
            int messageStart = message.lastIndexOf("[");
            int messageEnd = message.lastIndexOf("]");
            message = message.substring(messageStart + 1 , messageEnd - 1);
        }else if ( e instanceof  HttpMessageNotReadableException ) {
            message = "expected format String of Json  or  yyyy-MM-dd";
        } else if ( e instanceof  ConstraintViolationException )
        {
            message = "userId must greater than 0";
        } else if ( e  instanceof DataIntegrityViolationException){
            message = "Email  , CCCD or PhoneNumber have used";
        }
        return message;
    }


}

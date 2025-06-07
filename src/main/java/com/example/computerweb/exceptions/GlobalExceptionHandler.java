package com.example.computerweb.exceptions;
//
//import jakarta.validation.ConstraintViolationException;
//import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.context.request.WebRequest;
//
//import java.util.Date;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//    @ExceptionHandler({MethodArgumentNotValidException.class , HttpMessageNotReadableException.class , DataIntegrityViolationException.class })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleValidationException (Exception e , WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setStatus(getStatus(e));
//        errorResponse.setError(getError(e));
//        errorResponse.setPath(request.getDescription(false).replace("uri=" , ""));
//        errorResponse.setMessage(getMessage(e));
//
//        return errorResponse;
//    }
//
//    @ExceptionHandler({ConstraintViolationException.class })
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleInternalException (Exception e , WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setStatus(getStatus(e));
//        errorResponse.setError(getError(e));
//        errorResponse.setPath(request.getDescription(false).replace("uri=" , ""));
//        errorResponse.setMessage(getMessage(e));
//
//        return errorResponse;
//    }
//
//    @ExceptionHandler({CalendarException.class })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleCalendarException (CalendarException e , WebRequest  request){
//    ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//        errorResponse.setError(e.getErrorMessage());
//        errorResponse.setPath("");
//        errorResponse.setMessage(e.getErrorMessage());
//
//        return errorResponse;
//    }
//
//    @ExceptionHandler({AuthenticationException.class })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleAuthenticationException (AuthenticationException e , WebRequest  request){
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//        errorResponse.setError(e.getErrorMessage());
//        errorResponse.setPath("");
//        errorResponse.setMessage(e.getErrorMessage());
//
//        return errorResponse;
//    }
//
//    @ExceptionHandler({DataNotFoundException.class })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleDataNotFoundException (DataNotFoundException e , WebRequest  request){
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//        errorResponse.setError(e.getErrorMessage());
//        errorResponse.setPath("");
//        errorResponse.setMessage(e.getErrorMessage());
//
//        return errorResponse;
//    }
//
//    private static int getStatus (Exception e ){
//        int status = 0;
//        if ( e instanceof ConstraintViolationException ) {
//            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
//        }else if ( e instanceof MethodArgumentNotValidException ||
//                e instanceof HttpMessageNotReadableException ||
//        e instanceof  DataIntegrityViolationException
//
//        )
//        {
//            status = HttpStatus.BAD_REQUEST.value();
//        }
//
//        return status;
//    }
//    private static String getError (Exception e ){
//        String error = "";
//        if ( e instanceof ConstraintViolationException ) {
//            error = "PathVariable invalid";
//        }else if ( e instanceof MethodArgumentNotValidException ||
//                e instanceof HttpMessageNotReadableException ||
//        e instanceof  DataIntegrityViolationException)
//        {
//            error = HttpStatus.BAD_REQUEST.getReasonPhrase();
//        }
//
//        return error;
//    }
//    private static String getMessage(Exception e) {
//        String message = e.getMessage();
//        if ( e instanceof MethodArgumentNotValidException ){
//            int messageStart = message.lastIndexOf("[");
//            int messageEnd = message.lastIndexOf("]");
//            message = message.substring(messageStart + 1 , messageEnd - 1);
//        }else if ( e instanceof  HttpMessageNotReadableException ) {
//            message = "Định dạng mong đợi là yyyy-MM-dd.";
//        } else if ( e instanceof  ConstraintViolationException )
//        {
//            message = "Mã người dùng phải lớn hơn 0";
//        } else if ( e  instanceof DataIntegrityViolationException){
//            message = "Email, CCCD hoặc Số điện thoại đã sử dụng";
//        }
//        return message;
//    }
//
//
//}


import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    // Validation-related exceptions
//    @ExceptionHandler({
//            MethodArgumentNotValidException.class,
//            HttpMessageNotReadableException.class,
//            DataIntegrityViolationException.class
//    })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleValidationException(Exception e, WebRequest request) {
//        return buildErrorResponse(HttpStatus.BAD_REQUEST, getMessage(e), request);
//    }
    // === XỬ LÝ CÁC EXCEPTION CỦA SPRING FRAMEWORK ===

    /**
     * Bắt lỗi @Valid trên các DTO trong @RequestBody.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseFailure> handleValidation(MethodArgumentNotValidException ex) {
        // Lấy message lỗi đầu tiên cho gọn
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ResponseFailure response = new ResponseFailure(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Bắt lỗi vi phạm ràng buộc database (unique, foreign key).
     * Lỗi này thường xảy ra khi bạn không kiểm tra trước (ví dụ: không check existsByEmail trước khi save).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseFailure> handleDatabaseIntegrity(DataIntegrityViolationException ex) {
        // Cung cấp một thông báo lỗi chung chung nhưng hữu ích
        ResponseFailure response = new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Lỗi xóa dữ liệu : dữ liệu đang hoạt động ở một nơi khác không thể xóa");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // === BỘ XỬ LÝ CUỐI CÙNG (CATCH-ALL) ===

    /**
     * Bắt tất cả các exception khác chưa được xử lý ở trên.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseFailure> handleGenericException(Exception ex) {
        // Luôn log lỗi này để debug
        ex.printStackTrace();
        ResponseFailure response = new ResponseFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã có lỗi không mong muốn xảy ra ở máy chủ.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, getMessage(e), request);
    }

    // Custom: CalendarException
    @ExceptionHandler(CalendarException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCalendarException(CalendarException e, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getErrorMessage(), request);
    }

    // Custom: AuthenticationException
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException e, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getErrorMessage(), request);
    }

    // Custom: DataNotFoundException
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataNotFoundException(DataNotFoundException e, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getErrorMessage(), request);
    }


    // Custom: DataConflictException
    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataConflictException(DataConflictException e, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getErrorMessage(), request);
    }

    // ======= Helper Methods =======

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(message);
        errorResponse.setPath(getPath(request));
        return errorResponse;
    }

    private String getPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "";
    }

    private String getMessage(Exception e) {
        if (e instanceof MethodArgumentNotValidException ex) {
            return ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .findFirst().orElse("Validation failed");
        } else if (e instanceof HttpMessageNotReadableException) {
            return "Định dạng mong đợi là yyyy-MM-dd.";
        } else if (e instanceof ConstraintViolationException) {
            return "Mã người dùng phải lớn hơn 0.";
        } else if (e instanceof DataIntegrityViolationException) {
            return "Email, CCCD hoặc Số điện thoại đã được sử dụng.";
        }
        return e.getMessage();
    }
}
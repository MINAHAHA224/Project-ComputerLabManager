package com.example.computerweb.Validation.EmailValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface EmailChecked {

    String message() default "Email must not be blank or wrong format (@ptithcm.edu.vn)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package com.example.computerweb.Validation.EmailValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface EmailChecked {

    String message() default "Email không được để trống hoặc sai định dạng (@ptithcm.edu.vn)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

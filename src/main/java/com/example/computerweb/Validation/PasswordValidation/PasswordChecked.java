package com.example.computerweb.Validation.PasswordValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordChecked {

    String message () default "Password must not be blank or wrong format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package com.example.computerweb.Validation.PhoneValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneChecked {
    String message() default "Invalid phone number or phone exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

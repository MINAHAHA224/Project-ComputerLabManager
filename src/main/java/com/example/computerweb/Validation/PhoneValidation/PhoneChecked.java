package com.example.computerweb.Validation.PhoneValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneChecked {
    String message() default "Số điện thoại không hợp lệ hoặc không tồn tại số điện thoại";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

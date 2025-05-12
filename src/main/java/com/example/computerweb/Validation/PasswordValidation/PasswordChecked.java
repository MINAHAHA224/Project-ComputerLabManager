package com.example.computerweb.Validation.PasswordValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordChecked {

    String message () default "Mật khẩu không được để trống hoặc định dạng sai";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

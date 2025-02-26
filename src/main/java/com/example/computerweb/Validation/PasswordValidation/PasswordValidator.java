package com.example.computerweb.Validation.PasswordValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordChecked , String> {
    @Override
    public void initialize(PasswordChecked constraintAnnotation) {

    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

        if ( password == null || password.isEmpty()){
            return false;
        }
        // password example : Abc123
        if (password.matches("^(?=.*[A-Z])(?=.*\\d).{6,}$")){
            return true;
        }else {
            return false;
        }

    }
}

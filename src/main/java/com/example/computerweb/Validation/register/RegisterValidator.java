package com.example.computerweb.Validation.register;

import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import com.example.computerweb.repositories.IUserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterValidator implements ConstraintValidator<RegisterChecked , UserRegisterDto> {

    private final IUserRepository userRepository;

    @Override
    public boolean isValid(UserRegisterDto userRegisterDTO, ConstraintValidatorContext constraintValidatorContext) {
        boolean valid = true;


        return valid;
    }
}

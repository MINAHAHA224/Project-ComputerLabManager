package com.example.computerweb.Validation.EmailValidation;

import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IAccountRepository;
import com.example.computerweb.repositories.IRoleRepository;
import com.example.computerweb.repositories.IUserRepository;
import com.example.computerweb.services.IUserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;


@RequiredArgsConstructor
public class EmailValidator implements ConstraintValidator<EmailChecked , String> {

    private final IUserService iUserService;
    private final IUserRepository iUserRepository;
    private  final IAccountRepository iAccountRepository;
    @Override
    public void initialize(EmailChecked constraintAnnotation) {

    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null || email.isEmpty() ){
            return false;
        }

        if (email.contains("@ptithcm.edu.vn")){

            boolean userExist = this.iAccountRepository.existsByEmail(email);

            if (userExist){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }

    }
}

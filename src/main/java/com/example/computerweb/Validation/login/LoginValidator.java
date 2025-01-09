package com.example.computerweb.Validation.login;

import com.example.computerweb.DTO.UserLoginDTO;
import com.example.computerweb.models.User;
import com.example.computerweb.services.IUserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class LoginValidator implements ConstraintValidator<LoginChecked , UserLoginDTO> {

    private final IUserService userService;
    @Override
    public boolean isValid(UserLoginDTO userLoginDTO, ConstraintValidatorContext constraintValidatorContext) {

        boolean valid = true;
            if ( userLoginDTO.getEmail() == null || userLoginDTO.getEmail().isEmpty()){
                constraintValidatorContext.buildConstraintViolationWithTemplate("Email cannot be blank")
                        .addPropertyNode("email")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if ( !userLoginDTO.getEmail().isEmpty() ){
                String email = userLoginDTO.getEmail();
                String emailRegex = "@Ptithcm.edu.vn";// Biểu thức chính quy kiểm tra email hợp lệ

                if (!email.contains(emailRegex)) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Email is not in correct format.")
                            .addPropertyNode("email")
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid = false;
                }


            }

            if ( userLoginDTO.getPassWord()  == null || userLoginDTO.getPassWord().isEmpty()){
                constraintValidatorContext.buildConstraintViolationWithTemplate("Password cannot be blank")
                .addPropertyNode("passWord")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }



        return valid;
    }
}

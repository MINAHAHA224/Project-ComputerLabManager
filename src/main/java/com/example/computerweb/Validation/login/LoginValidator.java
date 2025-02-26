package com.example.computerweb.Validation.login;

import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.services.IUserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginValidator implements ConstraintValidator<LoginChecked , UserLoginDto> {

    private final IUserService userService;
    @Override
    public boolean isValid(UserLoginDto userLoginDTO, ConstraintValidatorContext constraintValidatorContext) {

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

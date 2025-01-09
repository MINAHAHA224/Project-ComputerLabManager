package com.example.computerweb.Validation.register;

import com.example.computerweb.DTO.UserRegisterDTO;
import com.example.computerweb.models.User;
import com.example.computerweb.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RegisterValidator implements ConstraintValidator<RegisterChecked , UserRegisterDTO> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(UserRegisterDTO userRegisterDTO, ConstraintValidatorContext constraintValidatorContext) {
        boolean valid = true;

        try {
            if (userRegisterDTO.getFullName() == null || userRegisterDTO.getFullName().isEmpty()) {
                constraintValidatorContext.buildConstraintViolationWithTemplate("You need to fill in your full name.")
                        .addPropertyNode("fullName")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }

            if (userRegisterDTO.getEmail() == null || userRegisterDTO.getEmail().isEmpty()) {
                constraintValidatorContext.buildConstraintViolationWithTemplate("Email cannot be blank.")
                        .addPropertyNode("email")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if (userRegisterDTO.getEmail() != null && !userRegisterDTO.getEmail().isEmpty()) {
                Optional<User> user = this.userRepository.findUserByEmail(userRegisterDTO.getEmail());
                if (user.isPresent()) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Email already exists or has been registered.")
                            .addPropertyNode("email")
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid = false;
                }
            }
            // password
            if (userRegisterDTO.getPassWord() == null || userRegisterDTO.getPassWord().isEmpty()) {
                constraintValidatorContext.buildConstraintViolationWithTemplate("Password cannot be blank.")
                        .addPropertyNode("passWord")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }

            if (userRegisterDTO.getPassWord() != null  && !userRegisterDTO.getPassWord().isEmpty()) {
                // pass : A1b$2dEf
                // Kiểm tra mật khẩu có ít nhất 6 ký tự, một chữ cái thường, một chữ cái in hoa và một ký tự đặc biệt
                String regex = "^.{6,}$";
                if (!userRegisterDTO.getPassWord().matches(regex)) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Password must contain sufficient conditions.")
                            .addPropertyNode("passWord")
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid = false;

                }

            }

            // retypePassword
            if (userRegisterDTO.getRetypePassword() == null || userRegisterDTO.getRetypePassword().isEmpty()) {
                constraintValidatorContext.buildConstraintViolationWithTemplate("Vui lòng điền nhập lại mật khẩu.")
                        .addPropertyNode("retypePassword")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }

            if (userRegisterDTO.getPassWord() != null && userRegisterDTO.getRetypePassword() != null) {
                if (!userRegisterDTO.getRetypePassword().equals(userRegisterDTO.getPassWord())) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Mật khẩu không khớp.")
                            .addPropertyNode("retypePassword")
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid = false;
                }
            }
            // dateOfBirth
            String test = userRegisterDTO.getDateOfBirth();
            Long test1 = userRegisterDTO.getRole();
            if (userRegisterDTO.getDateOfBirth() == null || userRegisterDTO.getDateOfBirth().isEmpty()  ){
                constraintValidatorContext.buildConstraintViolationWithTemplate("Vui lòng chọn ngày sinh.")
                        .addPropertyNode("dateOfBirth")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }

            // role
            if (userRegisterDTO.getRole() == null ){
                constraintValidatorContext.buildConstraintViolationWithTemplate("Vui lòng chọn quyền quản trị.")
                        .addPropertyNode("role")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if  (userRegisterDTO.getRole() != null ){
                if (userRegisterDTO.getRole() != 3) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("vui lòng chọn lại quyền quản trị.")
                            .addPropertyNode("role")
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return valid;
    }
}

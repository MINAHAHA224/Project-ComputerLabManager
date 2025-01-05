package com.example.computerweb.Validation;

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
                constraintValidatorContext.buildConstraintViolationWithTemplate("Bạn cần điền đầy đủ Họ và Tên.")
                        .addPropertyNode("fullName")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }

            if (userRegisterDTO.getEmail() == null || userRegisterDTO.getEmail().isEmpty()) {
                constraintValidatorContext.buildConstraintViolationWithTemplate("Email không được để trống.")
                        .addPropertyNode("email")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if (userRegisterDTO.getEmail() != null && !userRegisterDTO.getEmail().isEmpty()) {
                Optional<User> user = this.userRepository.findUserByEmail(userRegisterDTO.getEmail());
                if (user.isPresent()) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Email đã tồn tại hoặc đã được đăng kí.")
                            .addPropertyNode("email")
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid = false;
                }
            }
            // password
            if (userRegisterDTO.getPassWord() == null || userRegisterDTO.getPassWord().isEmpty()) {
                constraintValidatorContext.buildConstraintViolationWithTemplate("Mật khẩu không được để trống.")
                        .addPropertyNode("passWord")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }

            if (userRegisterDTO.getPassWord() != null  && !userRegisterDTO.getPassWord().isEmpty()) {

                // Kiểm tra mật khẩu có ít nhất 6 ký tự, một chữ cái thường, một chữ cái in hoa và một ký tự đặc biệt
                String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
                if (!userRegisterDTO.getPassWord().matches(regex)) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Mật khẩu phải chứa đủ điều kiên.")
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

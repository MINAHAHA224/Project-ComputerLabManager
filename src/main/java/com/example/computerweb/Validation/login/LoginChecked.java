package com.example.computerweb.Validation.login;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = LoginValidator.class) // phụ thuộc
@Target({ ElementType.TYPE}) // phạm vị
@Retention(RetentionPolicy.RUNTIME) // thời gian bắt đầu
@Documented
public @interface LoginChecked {

    String message() default "Đăng nhập không thành công";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

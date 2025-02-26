package com.example.computerweb.Validation.PhoneValidation;

import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.services.IUserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
@RequiredArgsConstructor
public class PhoneValidator implements ConstraintValidator<PhoneChecked, String> {

    private final IUserService iUserService;
    @Override
    public void initialize(PhoneChecked phoneNumberNo) {
    }

    @Override
    public boolean isValid(String phoneNo, ConstraintValidatorContext cxt) {
        if(phoneNo == null || phoneNo.isEmpty()){
            return false;
        }
        boolean checkPhoneExist = this.iUserService.checkPhoneExist(phoneNo);
        if (checkPhoneExist ){
            return false;
        }
        //validate phone numbers of format "0902345345"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces: 090-234-4567
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else //return false if nothing matches the input
            if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
                //validating phone number where area code is in braces ()
            else return phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}");
    }

}

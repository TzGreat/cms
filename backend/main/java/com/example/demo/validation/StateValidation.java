package com.example.demo.validation;

import com.example.demo.anno.State;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StateValidation implements ConstraintValidator<State, String> {
    /**
     *
     * @param value 将来校验的数据
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value==null){
            return false;
        }
        if(value.equals("p") || value.equals("d")){
            return true;
        }
        return false;
    }
}
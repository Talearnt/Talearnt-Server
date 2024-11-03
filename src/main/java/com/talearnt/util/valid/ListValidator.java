package com.talearnt.util.valid;

import com.talearnt.enums.ErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class ListValidator implements ConstraintValidator<ListValid, List<?>> {
private int minLength;
private int maxLength;
private ErrorCode errorCode;

    @Override
    public void initialize(ListValid constraintAnnotation) {
        this.errorCode = constraintAnnotation.errorCode();
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
    }

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        // Null 값 비허용
        if (value == null) {
            return buildViolation(context);
        }

        int size = value.size();

        if (minLength > size){
            return buildViolation(context);
        }

        if (maxLength < size){
            return buildViolation(context);
        }


        return true;
    }

    private boolean buildViolation(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorCode.getCode())
                .addConstraintViolation();
        return false;
    }
}

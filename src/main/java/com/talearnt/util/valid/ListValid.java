package com.talearnt.util.valid;

import com.talearnt.enums.ErrorCode;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Repeatable(ListValids.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListValidator.class)
public @interface ListValid {
    String message() default "리스트의 길이가 유효하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    ErrorCode errorCode() default ErrorCode.UNKNOWN_ERROR;
    int minLength() default 0;
    int maxLength() default Integer.MAX_VALUE;
}

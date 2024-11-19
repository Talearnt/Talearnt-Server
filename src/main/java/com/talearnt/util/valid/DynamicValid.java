package com.talearnt.util.valid;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Repeatable(DynamicValids.class)
@Target({ElementType.FIELD, ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DynamicFieldValidator.class)
public @interface DynamicValid {
    String message() default "Valid 검증 실패";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    ErrorCode errorCode() default ErrorCode.UNKNOWN_ERROR; // 검증 실패 시 사용할 ErrorCode

    // 기존 Valid type 들 추가.
    // NOT null Type Valid
    boolean notNull() default false;

    //String Types Valid
    boolean notEmpty() default false;
    boolean notBlank() default false;
    boolean email() default false;
    Regex pattern() default Regex.NOT_USE_REGEX;

    //Number Types Valid
    int minLength() default -1;
    int maxLength() default -1;
    int minValue() default Integer.MIN_VALUE;
    int maxValue() default Integer.MAX_VALUE;
    boolean positive() default false;
    boolean positiveOrZero() default false;
    boolean negative() default false;
    boolean negativeOrZero() default false;

    //LocalDate Types Valid
    boolean future() default false;
    boolean futureOrPresent() default false;
    boolean past() default false;
    boolean pastOrPresent() default false;
}

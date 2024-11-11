package com.talearnt.util.valid;

import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.Regex;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.temporal.Temporal;

/**
 * @DynamicField 어노테이션에 지정된 조건에 따라 필드를 검증하는 Validator 클래스입니다.
 * 이 클래스는 null 검사, 문자열 패턴 매칭, 길이 검사, 숫자 제한, 날짜 검증 등 다양한 유형의 검증을 지원합니다.
 */
public class DynamicFieldValidator implements ConstraintValidator<DynamicValid, Object> {
    // 어노테이션의 속성 값을 저장할 필드들
    private ErrorCode errorCode; // 에러 코드
    private boolean notNull; // null 허용 여부
    private boolean notEmpty; // 비어 있지 않은지 검사
    private boolean notBlank; // 공백 검사
    private boolean email; // 이메일 형식 검사
    private Regex pattern; // 정규식 패턴 검사
    private int minLength; // 최소 길이 제한
    private int maxLength; // 최대 길이 제한
    private int minValue; // 최소 값 제한
    private int maxValue; // 최대 값 제한
    private boolean positive; // 양수인지 검사
    private boolean positiveOrZero; // 양수 또는 0인지 검사
    private boolean negative; // 음수인지 검사
    private boolean negativeOrZero; // 음수 또는 0인지 검사
    private boolean future; // 미래 날짜인지 검사
    private boolean futureOrPresent; // 미래 또는 현재 날짜인지 검사
    private boolean past; // 과거 날짜인지 검사
    private boolean pastOrPresent; // 과거 또는 현재 날짜인지 검사

    /**
     * @DynamicField 어노테이션의 속성 값을 초기화합니다.
     * @param constraintAnnotation 검증 규칙을 포함하는 어노테이션 인스턴스입니다.
     */
    @Override
    public void initialize(DynamicValid constraintAnnotation) {
        // 각 어노테이션 속성을 클래스 필드에 할당
        this.errorCode = constraintAnnotation.errorCode();
        this.notNull = constraintAnnotation.notNull();
        this.notEmpty = constraintAnnotation.notEmpty();
        this.notBlank = constraintAnnotation.notBlank();
        this.email = constraintAnnotation.email();
        this.pattern = constraintAnnotation.pattern();
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.minValue = constraintAnnotation.minValue();
        this.maxValue = constraintAnnotation.maxValue();
        this.positive = constraintAnnotation.positive();
        this.positiveOrZero = constraintAnnotation.positiveOrZero();
        this.negative = constraintAnnotation.negative();
        this.negativeOrZero = constraintAnnotation.negativeOrZero();
        this.future = constraintAnnotation.future();
        this.futureOrPresent = constraintAnnotation.futureOrPresent();
        this.past = constraintAnnotation.past();
        this.pastOrPresent = constraintAnnotation.pastOrPresent();
    }

    /**
     * 주어진 값이 어노테이션에 정의된 조건을 만족하는지 검증합니다.
     * @param value 검증할 값
     * @param context 검증 컨텍스트
     * @return 검증 결과 (true: 조건 만족, false: 조건 불만족)
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (notNull && value == null) {
            return buildViolation(context, "값은 null일 수 없습니다.");
        }

        // 문자열 값 검증
        if (value instanceof String strValue) {
            if (notEmpty && strValue.isEmpty()) {
                return buildViolation(context, "값은 비어 있을 수 없습니다.");
            }
            if (notBlank && strValue.trim().isEmpty()) {
                return buildViolation(context, "값은 공백일 수 없습니다.");
            }
            if (email && !strValue.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                return buildViolation(context, "이메일 형식이 올바르지 않습니다.");
            }
            if (!pattern.getPattern().isEmpty() && !strValue.matches(pattern.getPattern())) {
                return buildViolation(context, "값이 지정된 패턴과 일치하지 않습니다.");
            }
            if (minLength > -1 && strValue.length() < minLength) {
                return buildViolation(context, "값의 길이는 최소 " + minLength + "자 이상이어야 합니다.");
            }
            if (maxLength > -1 && strValue.length() > maxLength) {
                return buildViolation(context, "값의 길이는 최대 " + maxLength + "자 이하여야 합니다.");
            }
        }

        // 숫자 값 검증
        if (value instanceof Number numValue) {
            if (minValue != Integer.MIN_VALUE && numValue.intValue() < minValue) {
                return buildViolation(context, "값은 최소 " + minValue + " 이상이어야 합니다.");
            }
            if (maxValue != Integer.MAX_VALUE && numValue.intValue() > maxValue) {
                return buildViolation(context, "값은 최대 " + maxValue + " 이하여야 합니다.");
            }
            if (positive && numValue.intValue() <= 0) {
                return buildViolation(context, "값은 양수여야 합니다.");
            }
            if (positiveOrZero && numValue.intValue() < 0) {
                return buildViolation(context, "값은 0 또는 양수여야 합니다.");
            }
            if (negative && numValue.intValue() >= 0) {
                return buildViolation(context, "값은 음수여야 합니다.");
            }
            if (negativeOrZero && numValue.intValue() > 0) {
                return buildViolation(context, "값은 0 또는 음수여야 합니다.");
            }
        }

        // 날짜 값 검증
        if (value instanceof Temporal temporalValue) {
            LocalDate now = LocalDate.now();

            // LocalDate 타입에 대한 검증
            if (temporalValue instanceof LocalDate localDateValue) {
                if (future && !localDateValue.isAfter(now)) {
                    return buildViolation(context, "날짜는 미래여야 합니다.");
                }
                if (futureOrPresent && localDateValue.isBefore(now)) {
                    return buildViolation(context, "날짜는 현재 또는 미래여야 합니다.");
                }
                if (past && !localDateValue.isBefore(now)) {
                    return buildViolation(context, "날짜는 과거여야 합니다.");
                }
                if (pastOrPresent && localDateValue.isAfter(now)) {
                    return buildViolation(context, "날짜는 현재 또는 과거여야 합니다.");
                }
            }
            // 다른 Temporal 타입에 대한 처리가 필요하면 추가할 수 있음
        }

        return true; // 모든 조건을 만족하면 true 반환
    }

    /**
     * 검증 실패 시 검증 컨텍스트에 오류 메시지를 추가합니다.
     * context : 검증 컨텍스트
     * message : 오류 메시지
     * false : (검증 실패)
     */
    private boolean buildViolation(ConstraintValidatorContext context, String message) {
        //기본 에러 메세지 사용 X
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorCode.getCode())
                .addConstraintViolation();
        return false;
    }
}
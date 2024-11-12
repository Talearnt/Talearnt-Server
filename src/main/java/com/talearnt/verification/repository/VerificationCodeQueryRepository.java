package com.talearnt.verification.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.verification.Entity.PhoneVerification;
import com.talearnt.verification.Entity.QPhoneVerification;
import com.talearnt.verification.Entity.QPhoneVerificationTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VerificationCodeQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 10분이 지나지 않은 인증 코드를 가져오는 쿼리.
     * 이 쿼리로, 클라이언트가 보낸 인증코드와 비교하고
     * 틀렸을 경우에는 Trace 테이블에 추가.
     * */
    public Optional<PhoneVerification> selectPhoneVerificationByMinutesAndPhone(String phone){
        QPhoneVerification phoneVerification = QPhoneVerification.phoneVerification;
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(phoneVerification)
                        .where(phoneVerification.createdAt.after(LocalDateTime.now().minusMinutes(10))
                                .and(phoneVerification.phone.eq(phone)))
                        .orderBy(phoneVerification.createdAt.desc())
                        .fetchOne()
        );
    }

    /** 5번 이상 가져오면, phoneVerification의 Row 삭제할 수 있도록 인증 번호 시도를 가져오는 쿼리*/
    public long countTryPhoneVerification(Long phoneVerificationNo, String phone){
        QPhoneVerificationTrace trace = QPhoneVerificationTrace.phoneVerificationTrace;
        return Optional.ofNullable(
                queryFactory
                        .select(trace.count())
                        .from(trace)
                        .where(
                                tracePhoneEq(phone)
                                ,tracePhoneVerificationNoEq(phoneVerificationNo)
                        ).orderBy(trace.createdAt.desc())
                        .fetchOne()
        ).orElse(0L);
    }


    private BooleanExpression tracePhoneEq(String phone){
        return phone != null ? QPhoneVerificationTrace.phoneVerificationTrace.phone.eq(phone) : null;
    }

    private BooleanExpression tracePhoneVerificationNoEq(Long no){
        return no != null ? QPhoneVerificationTrace.phoneVerificationTrace.phoneVerificationNo.eq(no) : null;
    }
}

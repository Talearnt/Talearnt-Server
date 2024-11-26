package com.talearnt.auth.login.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class KakaoQueryRepository {

    private final JPAQueryFactory factory;

    /** 회원가입 유형이 카카오톡이 아니고, 동일한 휴대폰 번호가 있을 경우 Exception을 발생하기 위한 메소드 <br>
     * 나중에는 조건으로 연동한 아이디가 카카오톡에서 넘어온 아이디를 포함하지 않을 경우도 생각해야 함*/
    public Optional<Boolean> checkConectedKakao(){
        return null;
    }

}

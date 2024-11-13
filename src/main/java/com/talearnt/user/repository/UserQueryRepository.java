package com.talearnt.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.user.entity.QUser;
import com.talearnt.user.entity.User;
import com.talearnt.user.reponse.QUserFindIdResDTO;
import com.talearnt.user.reponse.UserFindIdResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final JPAQueryFactory factory;


    /**가장 최근에 해당 전화번호로 가입한 사람 가져오는 쿼리
     * 아래의 상황을 가정하고 가장 최근의 유저를 가져오는 것으로 만듦.
     * Old 유저가 휴대폰 번호를 변경, new 유저가 같은 번호로 가입
     * 그랬을 경우에는 항상 최신의 New 유저의 UserId를 가져오도록 쿼리 설정.
     * Old 유저는 어떻게 찾나요?
     * Pass를 도입하기 전까지는 고객 문의로 찾아야 함.
     * */
    public Optional<String> selectUserByPhoneNumber(String phoneNumber){
        QUser user = QUser.user;
        return Optional.ofNullable(
                factory
                .select(user.userId)
                .from(user)
                .where(user.phone.eq(phoneNumber))
                .orderBy(user.registeredAt.desc())
                .fetchFirst()
        );
    }

    /**
     * 가장 최근에 해당 휴대폰으로 가입한 사람의 아이디와 가입 일자를 가져온다.
     * 휴대폰을 변경한  Old 유저는 역시 고객문의로 찾는 수 밖에 없다.
     * */
    public Optional<UserFindIdResDTO> findUserIdAndCreatedAt(String phone){
        QUser user = QUser.user;
        QUserFindIdResDTO userFindIdResDTO = new QUserFindIdResDTO(user.userId, user.registeredAt);
        return Optional.ofNullable(
                factory
                        .select(userFindIdResDTO)
                        .from(user)
                        .where(user.phone.eq(phone))
                        .orderBy(user.registeredAt.desc())
                        .fetchFirst()
        );
    }

}

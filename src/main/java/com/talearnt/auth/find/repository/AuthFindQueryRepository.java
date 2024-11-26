package com.talearnt.auth.find.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.auth.find.entity.QFindPasswrodUrl;
import com.talearnt.auth.find.reponse.QAuthFindResDTO;
import com.talearnt.user.entity.QUser;
import com.talearnt.user.entity.User;
import com.talearnt.user.query.QUserFindQueryDTO;
import com.talearnt.user.query.UserFindQueryDTO;
import com.talearnt.auth.find.reponse.AuthFindResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthFindQueryRepository {
    private final JPAQueryFactory factory;


    /**1. 아이디 찾기 - 문자 전송<br>
     * 가장 최근에 해당 전화번호로 가입한 사람 가져오는 쿼리<br>
     * 아래의 상황을 가정하고 가장 최근의 유저를 가져오는 것으로 만듦.<br>
     * Old 유저가 휴대폰 번호를 변경, new 유저가 같은 번호로 가입<br>
     * 그랬을 경우에는 항상 최신의 New 유저의 UserId를 가져오도록 쿼리 설정.<br>
     * Old 유저는 어떻게 찾나요?<br>
     * Pass를 도입하기 전까지는 고객 문의로 찾아야 함.
     * */
    public Optional<UserFindQueryDTO> selectUserByPhoneNumber(String phoneNumber, String name){
        QUser user = QUser.user;
        QUserFindQueryDTO res = new QUserFindQueryDTO(user.userId, user.authority);
        return Optional.ofNullable(
                factory
                .select(res)
                .from(user)
                .where(user.phone.eq(phoneNumber)
                        .and(user.name.eq(name)))
                .orderBy(user.registeredAt.desc())
                .fetchFirst()
        );
    }

    /** 2. 아이디 찾기 완료<br>
     * 가장 최근에 해당 휴대폰으로 가입한 사람의 아이디와 가입 일자를 가져온다.<br>
     * 휴대폰을 변경한  Old 유저는 역시 고객문의로 찾는 수 밖에 없다.
     * */
    public Optional<AuthFindResDTO> findUserIdAndCreatedAt(String phone){
        QUser user = QUser.user;
        QAuthFindResDTO userFindIdResDTO = new QAuthFindResDTO(user.userId, user.registeredAt);
        return Optional.ofNullable(
                factory
                        .select(userFindIdResDTO)
                        .from(user)
                        .where(user.phone.eq(phone))
                        .orderBy(user.registeredAt.desc())
                        .fetchFirst()
        );
    }

    /** 1. 비밀번호 찾기 <br>
     * 찾은 아이디와 권한을 리턴한다.<br>
     * 비즈니스 로직에서 권한을 검사하여, 정지,탈퇴를 판별한다.
     * */
    public Optional<UserFindQueryDTO> findUserIdAndAuthorityByUserId(String userId, String phone){
        QUser user = QUser.user;
        QUserFindQueryDTO queryDTO = new QUserFindQueryDTO(user.userId, user.authority);
        return Optional.ofNullable(
                factory
                        .select(queryDTO)
                        .from(user)
                        .where(user.userId.eq(userId)
                                .and(user.phone.eq(phone)))
                        .orderBy(user.registeredAt.desc())
                        .fetchFirst()
        );
    }

    /**2. 비밀번호 찾기 변경<br>
     * FindPasswrodUrl의 아이디 값을 가져오는 쿼리<br>
     * 10분내에 변경하지 않으면 보안상 막는다<br>
     * 그 아이디를 가져와서 UserId를 찾는 쿼리 실행할 예정
     * @param no FindPasswrodUrl의 ID
     * @param uuid row의 고유값
     * */
    public Optional<String> findUserIdByUrlNoAndUuid(Long no, String uuid){
        QFindPasswrodUrl findPasswrodUrl = QFindPasswrodUrl.findPasswrodUrl;
        return Optional.ofNullable(
                factory
                        .select(findPasswrodUrl.userId)
                        .from(findPasswrodUrl)
                        .where(findPasswrodUrl.no.eq(no)
                                .and(findPasswrodUrl.uuid.eq(uuid))
                                .and(findPasswrodUrl.createdAt.after(LocalDateTime.now().minusMinutes(10))))
                        .fetchOne()
        );

    }

    /**3. 비밀번호 찾기 유저 확인<br>
     * userId와 일치하는 user를 넘겨주는 쿼리
     * */
    public Optional<User> findUserByUserId(String userId){
        QUser user = QUser.user;
        return Optional.ofNullable(
                factory
                        .selectFrom(user)
                        .where(user.userId.eq(userId))
                        .fetchOne()
        );
    }

}

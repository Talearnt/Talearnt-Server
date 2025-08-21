package com.talearnt.user.infomation.entity;

import com.talearnt.enums.user.Gender;
import com.talearnt.enums.user.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Long userNo;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column //S3에 있는 이미지 기본 경로 입력
    private String profileImg;

    //Beta 시 해싱 데이터값 길이 검증, 막아야 함.
    @Column(length = 100)
    private String pw;

    @Column(length = 5,nullable = false) //한국인 이름은 최대 5자 까지가 법으로 지정
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp //insert 쿼리가 발생했을 때 현재 시간값 적용
    private LocalDateTime registeredAt;

    @UpdateTimestamp //update 쿼리가 발생했을 때 현재 시간값 적용
    @Column(insertable = false)
    private LocalDateTime lastLogin;

    @Column(nullable = false, length = 10)
    private String joinType;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private UserRole authority;
    
    // 탈퇴 관련 필드들
    @Column
    private LocalDateTime withdrawnAt;
    
    @Column(length = 500)
    private String withdrawalReason;
    
    @Column(columnDefinition = "TEXT")
    private String withdrawalReasonCodes; // 선택한 탈퇴 사유들을 쉼표로 구분하여 저장: "서비스를 더 이상 이용하지 않아요,오류가 너무 자주 발생해요"
    
    @Column
    private Boolean isWithdrawn = false;
    
    // 탈퇴 회원 표시용 필드 (닉네임 중복 방지)
    @Column(length = 30)
    private String withdrawnNickname; // 탈퇴 시 원래 닉네임을 여기에 저장

    @Column(length = 20)
    private String withdrawnPhoneNumber;// 탈퇴 시 원래 휴대폰 번호를 여기에 저장
    
    @Column
    private String widthdrawnUserId;
}

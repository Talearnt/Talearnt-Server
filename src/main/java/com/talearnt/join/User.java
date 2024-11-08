package com.talearnt.join;

import com.talearnt.enums.Gender;
import com.talearnt.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userNo;

    @Column(nullable = false, updatable = false,unique = true)
    private String userId;

    @Column //S3에 있는 이미지 기본 경로 입력
    private String profileImg;

    //Beta 시 해싱 데이터값 길이 검증, 막아야 함.
    @Column(nullable = false,length = 100)
    private String pw;

    @Column(nullable = false, unique = true, length = 20)
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
    @Column(nullable = false)
    private LocalDateTime lastLogin;

    @Column(nullable = false, length = 10)
    private String joinType;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private UserRole authority;
}

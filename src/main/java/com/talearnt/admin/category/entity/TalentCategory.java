package com.talearnt.admin.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/* TalentCode는 Auto_increment가 아닙니다.
* 대분류와 마찬가지로 Code를 지정하기 위함입니다.
* 1000번대 (IT)의 대분뷰를 추가했을 경우에는 1001이 코드가 되고, 이름은 JAVA 와 같이 저장됩니다.
* */


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TalentCategory {

    @Id
    private Integer talentCode; // 재능 Code

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_code")
    private BigCategory bigCategory; // 대분류 Code

    @Column(length = 20, nullable = false)
    private String talentName; // 재능 이름

    @Column(nullable = false)
    private String managerId;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt; // 등록일

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true; // 사용 여부

    public TalentCategory(Integer talentCode) {
        this.talentCode = talentCode;
    }
}

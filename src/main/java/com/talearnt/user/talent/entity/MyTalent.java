    package com.talearnt.user.talent.entity;

    import com.talearnt.admin.category.entity.TalentCategory;
    import com.talearnt.user.infomation.entity.User;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;
    import lombok.ToString;
    import org.hibernate.annotations.CreationTimestamp;

    import java.time.LocalDateTime;

    @Entity
    @Getter
    @Setter
    @ToString
    public class MyTalent {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long myTalentNo;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_no")
        private User user;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "talent_code")
        private TalentCategory talentCategory;

        @Column(nullable = false)
        private Boolean type;// 주고 싶은 : false, 받고 싶은 : true

        @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        @CreationTimestamp
        private LocalDateTime createdAt;

        @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
        private Boolean isActive = true;
    }

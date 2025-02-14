package com.talearnt.s3.entity;


import com.talearnt.enums.post.PostType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileUploadNo;

    @Column
    private Long postNo;

    @Column(nullable = false)
    private Long userNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 10)
    private PostType postType;

    @Column(nullable = false, length = 2048, unique = true)
    private String url;

    @CreationTimestamp
    @Column(updatable = false,nullable = false)
    private LocalDateTime createdAt;
}

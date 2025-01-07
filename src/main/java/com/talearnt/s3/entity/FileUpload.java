package com.talearnt.s3.entity;


import com.talearnt.enums.upload.PostType;
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

    @Column(nullable = false)
    private Long postNo;

    @Column(nullable = false)
    private Long userNo;

    @Column(nullable = false)
    private PostType postType;

    @Column(nullable = false, length = 2048)
    private String url;

    @CreationTimestamp
    @Column(updatable = false,nullable = false)
    private LocalDateTime createdAt;
}

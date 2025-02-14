package com.talearnt.s3.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.enums.post.PostType;
import com.talearnt.s3.entity.FileUpload;
import com.talearnt.s3.entity.QFileUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Log4j2
public class FileUploadCustomRepository {
    private final JPAQueryFactory factory;
    private final QFileUpload fileUpload = QFileUpload.fileUpload;

    public List<FileUpload> getFileUploadsByExchangePostNo(Long postNo){

        return factory.selectFrom(fileUpload)
                .where(fileUpload.postNo.eq(postNo),
                        fileUpload.postType.eq(PostType.EXCHANGE))
                .fetch();
    }


}

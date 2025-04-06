package com.talearnt.s3;

import com.talearnt.enums.post.PostType;
import com.talearnt.s3.entity.FileUpload;
import com.talearnt.s3.repository.FileUploadRepository;
import com.talearnt.util.common.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final JdbcTemplate jdbcTemplate;

    /** 이미지 파일 업로드<br>
     * 다른 메소드 혹은 컨트롤러에서도 사용 가능하도록 만든 메소드<br>
     * 사용 조건은 로그인 여부를 이것을 사용하기 전에 미리 확인 시켜야 한다.*/
    public Integer addPostFileUploads(Long postNo, PostType postType, Long userNo, List<String> urls){
        if (urls == null || urls.isEmpty()) return 0;

        //Urls -> entity로 변환
        List<FileUpload> fileUploadEntities = urls.stream()
                .map(url-> FileUploadMapper.INSTANCE.toEntity(postNo,postType,userNo,url.substring(S3Util.S3_DOMAIN_BASE_URL.length())))
                .toList();

        //entity 저장
        String sql = "INSERT INTO FILE_UPLOAD (post_no, post_type, url, user_no,created_at) values (?,?,?,?,?)";
        int [][] added = jdbcTemplate.batchUpdate(sql,fileUploadEntities,10,
                (ps,entity)-> {
                    ps.setLong(1,entity.getPostNo());
                    ps.setString(2,entity.getPostType().name());
                    ps.setString(3,entity.getUrl());
                    ps.setLong(4,entity.getUserNo());
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                });

        return added.length;
    }



}

package com.talearnt.s3;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.post.PostType;
import com.talearnt.s3.entity.FileUpload;
import com.talearnt.s3.repository.FileUploadCustomRepository;
import com.talearnt.s3.repository.FileUploadRepository;
import com.talearnt.util.common.S3Util;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.log.LogRunningTime;
import jakarta.transaction.Transactional;
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
    private final FileUploadCustomRepository fileUploadCustomRepository;
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

    /** 이미지 파일 목록 불러오기*/
    @LogRunningTime
    public List<FileUpload> findFileUploads(Long postNo, PostType postType, Long userNo){
        log.info("이미지 파일 목록 불러오기 시작 - {}, {}, {}", postNo, postType, userNo);

        //이미지 파일 목록 호출
        List<FileUpload> uploadedFiles = fileUploadCustomRepository.findFileUploadsByPostNo(postNo, postType, userNo);

        log.info("이미지 파일 목록 불러오기 끝");
        return uploadedFiles;
    }

    /** 이미지 다중 삭제
     * 조건)
     * -이미지에 대한 권한이 유효한가?
     * @param fileUploads Entities
     * @param userNo 삭제하려는 유저
     * */
    @LogRunningTime
    @Transactional
    public void deleteFileUploads(List<FileUpload> fileUploads, Long userNo){
        log.info("이미지 파일 다중 삭제 시작 - {}", userNo);

        //이미지 권한 체크
        boolean isAllMatch = fileUploads.stream().allMatch(fileUpload -> fileUpload.getUserNo().equals(userNo));

        if (!isAllMatch) {
            log.error("이미지 파일 다중 삭제 실패 - 삭제하려는 파일과 유저가 일치하지 않음 : {}", userNo);
            throw new CustomRuntimeException(ErrorCode.FILE_ACCESS_DENIED);
        }
        //현재 시간 (삭제 시간)
        LocalDateTime now = LocalDateTime.now();

        //SQL문 작성
        String sql = "UPDATE FILE_UPLOAD SET deleted_at = ? WHERE file_upload_no = ?";
        //DB 이미지 소프트 삭제 시작
        int[][] deleted = jdbcTemplate.batchUpdate(sql, fileUploads, 10,
                (ps, entity) -> {
                    ps.setTimestamp(1, Timestamp.valueOf(now));
                    ps.setLong(2, entity.getFileUploadNo());
                });

        log.info("이미지 파일 다중 삭제 끝");
    }



}

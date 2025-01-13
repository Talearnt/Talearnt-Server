package com.talearnt.s3;

import com.talearnt.s3.request.S3FilesReqDTO;
import com.talearnt.util.common.LoginUtil;
import com.talearnt.util.common.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
/**
 * 1. Presigned URL 을 생성해서 보내준다
 * 2. 삭제 시 해당 DB 게시글에 있는 URL을 삭제해준다.
 * 3. 수정 시 해당 DB에 중복되어 있는 값을 제외한 값을 삭제해준다.
 *
 * FE에서 해야할 일
 * 1. 이미지 업로드 성공 후 유저가 게시글 지웟을 경우 List로 URL 모아두기
 * 2. 게시글 등록할 경우에 DB에 저장할 URL 목록을 보내주고, 삭제할 URL list는 S3에 요청한다.
 * */
@Service
@Log4j2
@RequiredArgsConstructor
public class S3Service {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Presigner s3Presigner;

    /**여러 이미지 파일 업로드*/
    public List<String> generatePresignedUrls(List<S3FilesReqDTO> dtos, Authentication auth){
        log.info("S3 이미지 다중 업로드 시작");
        //로그인 여부 확인
        UserUtil.validateAuthentication("S3 이미지 다중 업로드",auth);
        log.info("S3 이미지 다중 업로드 끝");
        return dtos.stream().map(file -> this.generatePresignedURL(file.getFileName(),file.getFileType(),file.getFileSize())).toList();
    }

    /** Presigned URL 이름을 생성하고 권한을 설정한 뒤 보내준다.*/
    public String generatePresignedURL(String fileName,String fileType, Long fileSize){
        PutObjectRequest putObjectAclRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileType+"/"+createFileName(fileName))
                .contentLength(fileSize)
                .contentType(fileType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectAclRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    public String createFileName(String filaName){
        return UUID.randomUUID()+filaName;
    }
}

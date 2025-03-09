package com.talearnt.s3;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.s3.request.S3FilesReqDTO;
import com.talearnt.util.common.LoginUtil;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
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
    @Value("${s3.host}")
    private String host;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    /**여러 이미지 파일 업로드*/
    public List<String> generatePresignedUrls(List<S3FilesReqDTO> dtos, Authentication auth){
        log.info("S3 이미지 다중 업로드 시작");
        //로그인 여부 확인
        UserUtil.validateAuthentication("S3 이미지 다중 업로드",auth);

        //업로드 파일이 비어 있을 경우
        if (dtos.isEmpty()){
            log.error("S3 이미지 다중 업로드 실패 - 목록이 비어 있음 : {}",ErrorCode.FILE_UPLOAD_LENGTH_MISSING);
            throw new CustomRuntimeException(ErrorCode.FILE_UPLOAD_LENGTH_MISSING);
        }

        log.info("S3 이미지 다중 업로드 끝");
        return dtos.stream().map(file -> this.generatePresignedURL(file.getFileName(),file.getFileType(),file.getFileSize())).toList();
    }

    /** Presigned URL 이름을 생성하고 권한을 설정한 뒤 보내준다.*/
    private String generatePresignedURL(String fileName,String fileType, Long fileSize){
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

    private String createFileName(String filaName){
        return UUID.randomUUID().toString().replace("-","")+filaName.substring(filaName.lastIndexOf("."));
    }


    /**S3에 업로드된 파일을 삭제하는 메소드*/
    public void deleteFiles(Set<String> urls){
        log.info("S3 이미지 다중 삭제 시작");
        if (!urls.isEmpty()){
            urls.stream()
                    .filter(this::isValidPresigendUrl)
                    .forEach(this::deleteFile);
        }
        log.info("S3 이미지 다중 삭제 끝");
    }

    private void deleteFile(String url) {
        try {
            URI uri = new URI(url);
            String path  = uri.getPath().substring(1);

            //삭제할 Object 생성
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build();

            //삭제 요청
            s3Client.deleteObject(deleteObjectRequest);
        }catch (URISyntaxException e){
            log.error("S3 - 잘못된 URI 가 입력되었습니다 : {}",ErrorCode.BAD_REQUEST);
            throw new CustomRuntimeException(ErrorCode.BAD_REQUEST);
        }
    }

    /**Delete를 진행할 때 올바르지 않은 Collection 에서 삭제 조건 필터*/
    private boolean isValidPresigendUrl(String url){
        String expectedHost = "";
        try {
            URI uri = new URI(url);
            String path = uri.getPath().substring(1);
            String fileType = path.substring(0,path.lastIndexOf("/"));

            //Host가 일치하지 않을 경우
            if (!expectedHost.equals(uri.getHost())) return false;
            //파일 타입이 맞지 않을 경우
            if (!fileType.matches(Regex.FILE_TYPE.getPattern())) return false;

        }catch (URISyntaxException e){
            log.error("URI Syntax가 올바르지 않습니다 : {}",e.getMessage());
            return false;
        }

        return true;
    }
}

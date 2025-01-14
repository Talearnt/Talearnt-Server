package com.talearnt.s3;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.exception.CustomRuntimeException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;


public class DeleteS3Files {
    private static final Logger log = LoggerFactory.getLogger(DeleteS3Files.class);

    private final String presingedUrl = "https://talearnt-sever-images-upload-bucket.s3.ap-northeast-2.amazonaws.com/image/jpeg/5785d370-9890-415f-af29-eab0511a018atest.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250114T055417Z&X-Amz-SignedHeaders=content-length%3Bcontent-type%3Bhost&X-Amz-Credential=AKIASVLKCEDTHFD2PBTU%2F20250114%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=300&X-Amz-Signature=05ea1748e09795237613422e82bb01c9edeb1915d3004e406532e791bad737f0";
    private final String expectedUrl = "image/jpeg/5785d370-9890-415f-af29-eab0511a018atest.jpg";
    private final String invalidUrl = "image/invalid/5785d370-9890-415f-af29-eab0511a018atest.jpg";

    @DisplayName("제대로 URI를 적었을 경우")
    @Test
    void shouldExtractS3KeyWhenGivenValidPresignedUrl(){
        try{
            URI uri = new URI(presingedUrl);
            String key = uri.getPath().substring(1);

            assertEquals(expectedUrl,key);
            log.info("key : {} ",key);
        }catch (URISyntaxException e){
            log.error("에러발생");
        }
    }

    @DisplayName("잘못된 URL가 넘어왔을 경우")
    @Test
    void validateExceptionOnInvalidURI() {
        try {
            String inputValue = "https://different.s3.ap-northeast-2.amazonaws.com/image/jpeg/5785d370-9890-415f-af29-eab0511a018atest.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250114T055417Z&X-Amz-SignedHeaders=content-length%3Bcontent-type%3Bhost&X-Amz-Credential=AKIASVLKCEDTHFD2PBTU%2F20250114%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=300&X-Amz-Signature=05ea1748e09795237613422e82bb01c9edeb1915d3004e406532e791bad737f0";
            URI uri = new URI(inputValue);
            String expectedHost = "talearnt-sever-images-upload-bucket.s3.ap-northeast-2.amazonaws.com";
            assertNotEquals(expectedHost,uri.getHost());

        } catch (URISyntaxException e) {
            log.error("에러 발생 : {}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("파일 타입이 Server에서 설정한 Type과 다를경우.")
    @Test
    void ValidateExceptionOnInvalidFileType(){
        String parseType = invalidUrl.substring(0,invalidUrl.lastIndexOf("/"));
        log.info("parse Type : {} ", parseType);
        assertFalse(parseType.matches(Regex.FILE_TYPE.getPattern()));
    }

    @DisplayName("넘어온 값가 null 이거나, empty 일경우")
    @Test
    void ValidateExceptionOnNullString(){
        String empty = "";

        assertEquals("",empty);
    }

    @DisplayName("잘못된 URL 목록이 섞여있을 경우 올바른 것만 남기고 path 추출")
    @Test
    void filterUrls(){
        Set<String> urls = Set.of(
                "https://talearnt-sever-images-upload-bucket.s3.ap-northeast-2.amazonaws.com/image/jpeg/5785d370-9890-415f-af29-eab0511a018atest.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250114T055417Z&X-Amz-SignedHeaders=content-length%3Bcontent-type%3Bhost&X-Amz-Credential=AKIASVLKCEDTHFD2PBTU%2F20250114%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=300&X-Amz-Signature=05ea1748e09795237613422e82bb01c9edeb1915d3004e406532e791bad737f0",
                "https://different-bucket.s3.ap-northeast-2.amazonaws.com/image/jpeg/invalid.jpg",
                "https://talearnt-sever-images-upload-bucket.s3.ap-northeast-2.amazonaws.com/image/invalid/5785d370-9890-415f-af29-eab0511a018atest.jpg"
        );

        List<String> expectedUrls = List.of("image/jpeg/5785d370-9890-415f-af29-eab0511a018atest.jpg");
        List<String> transUrls = urls.stream().filter(this::isValidPresigendUrl).map(this::getKey).toList();

        assertIterableEquals(expectedUrls,transUrls);
    }

    private String getKey(String url) {
        try {
            URI uri = new URI(url);
            return uri.getPath().substring(1);
        }catch (URISyntaxException e){
            log.error("S3 - 잘못된 URI 가 입력되었습니다 : {}", ErrorCode.BAD_REQUEST);
            throw new CustomRuntimeException(ErrorCode.BAD_REQUEST);
        }
    }

    boolean isValidPresigendUrl(String url){
        String expectedHost = "talearnt-sever-images-upload-bucket.s3.ap-northeast-2.amazonaws.com";
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

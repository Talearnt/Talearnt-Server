package com.talearnt.post.community;

import com.talearnt.post.community.request.CommunityPostReqDTO;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface CommunityPostApi {


    @Operation(summary = "커뮤니티 게시글 작성"
            , description = "<h2>내용</h2>" +
            "<p>커뮤니티 게시글 작성입니다.</p>" +
            "<p>postType 으로 자유,질문,스터디 게시판이 나뉘어집니다.</p>" +
            "<p>이미지 모아보기가 없어도 이미지 삭제를 위하여 imagesUrls를 여기서도 적용하고 있습니다.</p>" +
            "<hr/>" +
            "<h2>Request</h2>" +
            "<ul>" +
                "<li>title : 제목 - 2자 이상 50자 이하</li>" +
                "<li>content : 내용 - 20자 이상 1000자 이하 (불상사를 막기위하여 DB는 TEXT 타입)</li>" +
                "<li>postType : 커뮤니티 게시글 타입 - (EXCHANGE|FREE|QUESTION|STUDY)</li>" +
                "<li>imageUrls : 이미지 경로들</li>" +
            "</ul>" +
            "<p>postType에 Exchange가 있는 이유는 이미지 업로드 시 필요한 데이터 값이어서 그렇습니다. 클라이언트에서 사용할 일은 없습니다.</p>" +
            "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",ref="FILE_FAILED_UPLOAD"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
    })
    public ResponseEntity<CommonResponse<String>> addCommunityPost(@Valid @RequestBody CommunityPostReqDTO communityPostReqDTO);
}

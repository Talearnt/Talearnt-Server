package com.talearnt.admin.category;

import com.talearnt.admin.category.request.BigCategoryReqDTO;
import com.talearnt.admin.category.request.TalentCategoryReqDTO;
import com.talearnt.admin.category.response.CategoryListResDTO;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CategoryApi {
    @Operation(summary = "대분류 - 키워드 추가 (관리자)",
            description = "<h2>내용</h2>" +
                    "<p>대분류를 추가합니다.</p>" +
                    "<h2>Body</h2>" +
                    "<ul>" +
                        "<li><strong>categoryCode :</strong> 대분류 코드 <strong> 조건) 최소 4자, 숫자로 이루어진 코드</strong> </li>" +
                        "<li><strong>categoryName :</strong> 대분류 코드 재능 키워드 이름 <strong>조건) 최소 2자, 영,한,숫자,/만 가능</strong></li>" +
                    "</ul>"
    )
    @Tags(
            @Tag(name = "관리자 1. 재능 키워드", description = "대분류,재능 키워드 추가 O")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400-1", ref = "KEYWORD_CODE_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "KEYWORD_NAME_MISMATCH"),
            @ApiResponse(responseCode = "400-3", ref = "KEYWORD_CODE_DUPLICATION"),
            @ApiResponse(responseCode = "400-4", ref = "KEYWORD_NAME_DUPLICATION"),
    })
    public ResponseEntity<CommonResponse<String>> addBigCategoryKeyword(@RequestBody @Valid BigCategoryReqDTO bigCategoryReqDTO);


    @Operation(summary = "재능 분류 - 키워드 추가 (관리자)",
            description = "<h2>내용</h2>" +
                    "<p>관리자 페이지 전용 (미구현)</p>" +
                    "<p>재능 분류 키워드를 추가하는 API입니다.</p>" +
                    "<p>대분류 코드가 없을 경우에 추가할 수 없습니다.</p>" +
                    "<h2>Body</h2>" +
                    "<ul>" +
                        "<li><strong>talentCode :</strong> 재능 키워드 코드 <strong>조건) 최소 4자, 숫자로 이루어진 코드</strong></li>" +
                        "<li><strong>categoryCode :</strong> 대분류 코드 <strong> 조건) 최소 4자, 숫자로 이루어진 코드</strong></li>" +
                        "<li><strong>talentName :</strong> 재능 키워드 이름 <strong>조건) 최소 2자, 영,한,숫자,/만 가능</strong></li>" +
                    "</ul>" +
                    "<p>대분류 코드는 1000단위로 내용이 달라집니다.</p>" +
                    "<p>재능 키워드 코드는 1단위로 내용이 달라집니다.</p>" +
                    "<p>관리자 페이지에 대한 디자인이 나오지 않아 임의로 만들었습니다 (테스트 용)</p>"+
                    "<p>디자인 페이지에 따라, 키워드 추가 부분은 수정될 수 있습니다.</p>"
    )
    @Tags(
            @Tag(name = "관리자 1. 재능 키워드")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400-1", ref = "KEYWORD_CODE_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "KEYWORD_NAME_MISMATCH"),
            @ApiResponse(responseCode = "400-3", ref = "KEYWORD_CATEGORY_CODE_MISMATCH"),
            @ApiResponse(responseCode = "400-4", ref = "KEYWORD_TALENT_CODE_DUPLICATION"),
            @ApiResponse(responseCode = "400-5", ref = "KEYWORD_TALENT_NAME_DUPLICATION"),
    })
    public ResponseEntity<CommonResponse<String>> addTalenrtCategoryKeyword(@RequestBody @Valid TalentCategoryReqDTO talentCategoryReqDTO);



    @Operation(summary = "재능 분류 - 모든 키워드 목록 불러오기 (공통)",
            description = "<h2>내용</h2>" +
                    "<p>모든 <strong>대분류 코드</strong>와 <strong>대분류 이름</strong>, <strong>재능 키워드 코드</strong>와 <strong>재능 키워드 이름</strong>을 가져옵니다.</p>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                        "<li><strong>categoryCode :</strong> 대분류 코드</li>" +
                        "<li><strong>categoryName :</strong> 대분류 이름</li>" +
                        "<li><strong>talents :</strong> 대분류에 속한 재능 목록 " +
                            "<ul>" +
                                "<li><strong>talentCode :</strong> 재능 키워드 코드</li>" +
                                "<li><strong>talentName :</strong> 재능 키워드 이름</li>" +
                            "</ul>" +
                        "</li>"+
                    "</ul>")
    @Tags(value = {
            @Tag(name = "Keywords")
    })
    public ResponseEntity<CommonResponse<List<CategoryListResDTO>>> getAllCategories();
}

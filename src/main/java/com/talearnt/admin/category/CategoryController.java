package com.talearnt.admin.category;

import com.talearnt.admin.category.request.BigCategoryReqDTO;
import com.talearnt.admin.category.request.TalentCategoryReqDTO;
import com.talearnt.admin.category.response.CategoryListResDTO;
import com.talearnt.admin.category.service.CategoryService;
import com.talearnt.admin.category.service.TalentCategoryService;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestControllerV1
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;
    private final TalentCategoryService talentCategoryService;

    //대분류 - 키워드 추가
    @PostMapping("/admin/keywords/big-categories")
    public ResponseEntity<CommonResponse<String>> addBigCategoryKeyword(@RequestBody @Valid BigCategoryReqDTO bigCategoryReqDTO){
        return CommonResponse.success(categoryService.addBigCategory(bigCategoryReqDTO));
    }

    //재능 분류 - 키워드 추가
    @PostMapping("/admin/keywords/talent-categories")
    public ResponseEntity<CommonResponse<String>> addTalenrtCategoryKeyword(@RequestBody @Valid TalentCategoryReqDTO talentCategoryReqDTO){
        return CommonResponse.success(talentCategoryService.addTalentCategoryKeyword(talentCategoryReqDTO));
    }

    //재능 분류 모든 키워드 가져 오기
    @GetMapping("/keywords")
    public ResponseEntity<CommonResponse<List<CategoryListResDTO>>> getAllCategories(){
        return CommonResponse.success(talentCategoryService.getAllCategories());
    }

}

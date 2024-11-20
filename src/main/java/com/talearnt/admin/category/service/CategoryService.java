package com.talearnt.admin.category.service;

import com.talearnt.admin.category.CategoryMapper;
import com.talearnt.admin.category.entity.BigCategory;
import com.talearnt.admin.category.repository.BigCategoryRepository;
import com.talearnt.admin.category.request.BigCategoryReqDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class CategoryService {

    //repositories
    private final BigCategoryRepository bigCategoryRepository;

    /**
     * 대분류 카테고리를 추가합니다 <br>
     * 조건 <br>
     * - 로그인이 되어있는가? (Binder에서 해결) <br>
     * - 권한이 관리자 이상인가? ( Controller 에서 해결) <br>
     * - Category Code 가 중복인가? <br>
     * - Category Name 이 중복인가? <br>
     * @param bigCategoryReqDTO 추가할 대분류 내용
     */
    public String addBigCategory(BigCategoryReqDTO bigCategoryReqDTO) {
        log.info("대분류 키워드 추가 시작 : {}", bigCategoryReqDTO);


        //Category Code 중복 확인
        if (bigCategoryRepository.existsById(bigCategoryReqDTO.getCategoryCode())) {
            log.error("대분류 키워드 추가 실패 - 코드 중복 : {}", ErrorCode.KEYWORD_CODE_DUPLICATION);
            throw new CustomRuntimeException(ErrorCode.KEYWORD_CODE_DUPLICATION);
        }//Category Name 중족 확인
        else if (bigCategoryRepository.existsByCategoryName(bigCategoryReqDTO.getCategoryName())) {
            log.error("대분류 키워드 추가 실패 - 이름 중복 : {}", ErrorCode.KEYWORD_NAME_DUPLICATION);
            throw new CustomRuntimeException(ErrorCode.KEYWORD_NAME_DUPLICATION);
        }

        //DTO -> entity로 변환
        BigCategory bigCategoryEntity = CategoryMapper.INSTANCE.toBigCategoryEntity(bigCategoryReqDTO);

        bigCategoryRepository.save(bigCategoryEntity);

        log.info("대분류 키워드 추가 끝");
        return "성공적으로 "+bigCategoryReqDTO.getCategoryCode()+" : "+bigCategoryReqDTO.getCategoryName()+" 을 추가했습니다.";
    }

}

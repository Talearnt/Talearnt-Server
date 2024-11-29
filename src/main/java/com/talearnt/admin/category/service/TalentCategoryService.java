package com.talearnt.admin.category.service;

import com.talearnt.admin.category.CategoryMapper;
import com.talearnt.admin.category.entity.TalentCategory;
import com.talearnt.admin.category.repository.BigCategoryRepository;
import com.talearnt.admin.category.repository.CategoryQueryRepository;
import com.talearnt.admin.category.repository.TalentCategoryRepository;
import com.talearnt.admin.category.request.TalentCategoryReqDTO;
import com.talearnt.admin.category.response.CategoryListResDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class TalentCategoryService {

    private final BigCategoryRepository bigCategoryRepository;
    private final TalentCategoryRepository talentCategoryRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    /** 재능 분류 키워드 추가<br>
     * 조건 <br>
     * 1. 관리자 권한인가?(컨트롤러에서 확인)<br>
     * 2. 필수 입력 값들이 제대로 들어가 있는가? (컨트롤러에서 확인)<br>
     * 3. 해당 대분류 키워드 코드가 있는가?<br>
     * 4. 재능 분류 키워드 코드가 중복되지 않는가?<br>
     * 5. 재능 그 외는 코드의 맨 마지막에 추가한다, ex) IT 그 외 = Code 1999
     * */
    @Transactional
    public String addTalentCategoryKeyword(TalentCategoryReqDTO talentCategoryReqDTO){
        log.info("재능 키워드 추가 시작 : {}",talentCategoryReqDTO);

        //해당 키워드 코드 검증
        if(!bigCategoryRepository.existsById(talentCategoryReqDTO.getCategoryCode())){
            log.error("재능 키워드 추가 실패 - 해당 대분류 코드가 없음 : {}",ErrorCode.KEYWORD_CATEGORY_CODE_MISMATCH);
            throw new CustomRuntimeException(ErrorCode.KEYWORD_CATEGORY_CODE_MISMATCH);
        } else if (talentCategoryRepository.existsById(talentCategoryReqDTO.getTalentCode())) {
            log.error("재능 키워드 추가 실패 - 재능 분류 코드 중복 : {}",ErrorCode.KEYWORD_TALENT_CODE_DUPLICATION);
            throw new CustomRuntimeException(ErrorCode.KEYWORD_TALENT_CODE_DUPLICATION);
        } else if (talentCategoryRepository.existsByTalentName(talentCategoryReqDTO.getTalentName())) {
            log.error("재능 키워드 추가 실패 - 재능 분류 이름 중복 : {}",ErrorCode.KEYWORD_TALENT_NAME_DUPLICATION);
            throw new CustomRuntimeException(ErrorCode.KEYWORD_TALENT_NAME_DUPLICATION);
        }

        // 재능 분류 키워드 Entity로 변환
        TalentCategory talentCategoryEntity = CategoryMapper.INSTANCE.toTalentCategoryEntity(talentCategoryReqDTO);

        //키워드 분류 저장
        talentCategoryRepository.save(talentCategoryEntity);
        log.info("재능 키워드 추가 끝");
        return "재능 키워드 코드 : "+talentCategoryReqDTO.getTalentCode()+", 재능 분류 키워드 이름 : "+talentCategoryReqDTO.getTalentName()+" 이 추가 되었습니다.";
    }

    /** 모든 키워드 목록을 불러온다.<br>
     * 조건<br>
     * 1. 대분류 키워드가 활성화되어 있는가?<br>
     * 2. 대분류에 속한 재능 키워드가 활성화 되어있는가?<br>
     * */
    public List<CategoryListResDTO> getAllCategories(){
        log.info("모든 카테고리 가져오기 시작");

        List<CategoryListResDTO> result = categoryQueryRepository.getAllCategories();

        log.info("모든 카테고리 가져오기 끝");
        return result;
    }

}

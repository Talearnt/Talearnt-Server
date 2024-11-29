package com.talearnt.admin.category.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QBigCategory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.admin.category.response.CategoryListResDTO;
import com.talearnt.admin.category.response.QCategoryListResDTO;
import com.talearnt.admin.category.response.QTalentCategoryResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepository {
    private final JPAQueryFactory factory;


    /** 모든 키워드를 가져온다.
     * 조건
     * - 대분류에서 활성화 되어있는 대분류를 categoryCode,categoryName를 셋팅한다
     * - 하위에 속한 재능 키워드를 목록으로 셋팅한다.
     *   목록에 대한 값은 talentCode,talentName을 셋팅 해야한다.
     * */
    public List<CategoryListResDTO> getAllCategories(){
        QBigCategory bigCategory = QBigCategory.bigCategory;
        QTalentCategory talentCategory = QTalentCategory.talentCategory;
        return factory
                .from(bigCategory)
                .leftJoin(talentCategory).on(bigCategory.categoryCode.eq(talentCategory.bigCategory.categoryCode))
                .where(bigCategory.isActive.eq(true)
                        .and(talentCategory.isActive.eq(true)))
                .transform(
                        GroupBy.groupBy(bigCategory.categoryCode) // 그룹화 기준 설정
                                .list(
                                        new QCategoryListResDTO(
                                                bigCategory.categoryCode,
                                                bigCategory.categoryName,
                                                GroupBy.list( // 하위 재능 목록
                                                        new QTalentCategoryResDTO(
                                                                talentCategory.talentCode,
                                                                talentCategory.talentName
                                                        )
                                                )
                                        )
                                )
                );
    }

}

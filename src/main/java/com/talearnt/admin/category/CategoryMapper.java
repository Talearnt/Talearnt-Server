package com.talearnt.admin.category;

import com.talearnt.admin.category.entity.BigCategory;
import com.talearnt.admin.category.entity.TalentCategory;
import com.talearnt.admin.category.request.BigCategoryReqDTO;
import com.talearnt.admin.category.request.TalentCategoryReqDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);


    @Mapping(source = "userInfo.userId", target = "managerId")
    BigCategory toBigCategoryEntity(BigCategoryReqDTO reqDTO);

    @Mapping(source = "userInfo.userId", target = "managerId")
    @Mapping(source = "categoryCode", target = "bigCategory.categoryCode")
    TalentCategory toTalentCategoryEntity(TalentCategoryReqDTO talentCategoryReqDTO);
}

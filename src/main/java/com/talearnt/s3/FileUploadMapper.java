package com.talearnt.s3;

import com.talearnt.enums.post.PostType;
import com.talearnt.s3.entity.FileUpload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileUploadMapper {

    FileUploadMapper INSTANCE = Mappers.getMapper(FileUploadMapper.class);

    @Mappings({
            @Mapping(source = "postNo", target = "postNo"),
            @Mapping(source = "postType", target = "postType"),
            @Mapping(source = "userNo", target = "userNo"),
            @Mapping(source = "url", target = "url")
    })
    FileUpload toEntity(Long postNo, PostType postType, Long userNo, String url);

}

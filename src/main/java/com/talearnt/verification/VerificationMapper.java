package com.talearnt.verification;

import com.talearnt.user.request.CheckUserVerificationCodeReqDTO;
import com.talearnt.verification.Entity.PhoneVerification;
import com.talearnt.verification.Entity.PhoneVerificationTrace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VerificationMapper {

    VerificationMapper INSTANCE = Mappers.getMapper(VerificationMapper.class);

    PhoneVerification toPhoneVerification(VerificationReqDTO verificationReqDTO);

    @Mappings({
            @Mapping(target = "phoneVerificationTraceNo", ignore = true),
            @Mapping(target = "phoneVerificationNo", source = "phoneVerification.phoneVerificationNo"),
            @Mapping(target = "code", source = "checkUserVerificationCodeReqDTO.code"),
            @Mapping(target = "phone", source = "checkUserVerificationCodeReqDTO.phone"),
            @Mapping(target = "createdAt", ignore = true)
    })
    PhoneVerificationTrace toTraceEntity(PhoneVerification phoneVerification, CheckUserVerificationCodeReqDTO checkUserVerificationCodeReqDTO);

}

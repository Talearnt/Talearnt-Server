package com.talearnt.verification;

import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T18:27:48+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.0.1 (Oracle Corporation)"
)
public class VerificationMapperImpl implements VerificationMapper {

    @Override
    public PhoneVerification toPhoneVerification(VerificationReqDTO verificationReqDTO) {
        if ( verificationReqDTO == null ) {
            return null;
        }

        PhoneVerification phoneVerification = new PhoneVerification();

        if ( verificationReqDTO.getIsPhoneVerified() != null ) {
            phoneVerification.setIsPhoneVerified( verificationReqDTO.getIsPhoneVerified() );
        }
        phoneVerification.setUserId( verificationReqDTO.getUserId() );
        phoneVerification.setPhone( verificationReqDTO.getPhone() );
        phoneVerification.setVerificationCode( verificationReqDTO.getVerificationCode() );

        return phoneVerification;
    }
}

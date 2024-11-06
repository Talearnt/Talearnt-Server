package com.talearnt.join;

import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T18:27:48+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.0.1 (Oracle Corporation)"
)
public class JoinMapperImpl implements JoinMapper {

    @Override
    public User toEntity(JoinReqDTO joinReqDTO) {
        if ( joinReqDTO == null ) {
            return null;
        }

        User user = new User();

        user.setUserId( joinReqDTO.getUserId() );
        user.setPw( joinReqDTO.getPw() );
        user.setGender( joinReqDTO.getGender() );
        user.setPhone( joinReqDTO.getPhone() );

        return user;
    }
}

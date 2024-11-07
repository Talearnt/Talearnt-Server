package com.talearnt.login;

import com.talearnt.join.User;
import com.talearnt.util.jwt.UserInfo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-08T02:50:41+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
public class LoginMapperImpl implements LoginMapper {

    @Override
    public UserInfo toUserInfo(User user) {
        if ( user == null ) {
            return null;
        }

        UserInfo.UserInfoBuilder userInfo = UserInfo.builder();

        userInfo.userNo( user.getUserNo() );
        userInfo.userId( user.getUserId() );
        userInfo.nickname( user.getNickname() );
        userInfo.profileImg( user.getProfileImg() );
        userInfo.authority( user.getAuthority() );

        return userInfo.build();
    }
}

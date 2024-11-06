package com.talearnt.login;

import com.talearnt.join.User;
import com.talearnt.util.jwt.UserInfo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T18:27:47+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.0.1 (Oracle Corporation)"
)
public class LoginMapperImpl implements LoginMapper {

    @Override
    public UserInfo toUserInfo(User user) {
        if ( user == null ) {
            return null;
        }

        UserInfo userInfo = new UserInfo();

        return userInfo;
    }
}

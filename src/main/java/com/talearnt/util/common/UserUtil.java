package com.talearnt.util.common;

import com.talearnt.join.User;
import com.talearnt.util.jwt.UserInfo;

public class UserUtil {

    public static User createUser(UserInfo userInfo){
        User user = new User();
        user.setUserNo(userInfo.getUserNo());
        user.setNickname(userInfo.getNickname());
        user.setAuthority(userInfo.getAuthority());
        return user;
    }
}

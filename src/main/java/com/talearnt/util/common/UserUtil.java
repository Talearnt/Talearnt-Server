package com.talearnt.util.common;

import com.talearnt.join.User;

public class UserUtil {

    public static User createUser(Long userNo){
        User user = new User();
        user.setUserNo(userNo);
        return user;
    }
}

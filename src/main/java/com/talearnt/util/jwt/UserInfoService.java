package com.talearnt.util.jwt;

import com.talearnt.enums.ErrorCode;
import com.talearnt.join.User;
import com.talearnt.join.UserRepository;
import com.talearnt.login.LoginMapper;
import com.talearnt.util.exception.CustomRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserInfoService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);

        if (user == null){
            throw new CustomRuntimeException(ErrorCode.USER_NOT_FOUND);
        }
        
        UserInfo userInfo = LoginMapper.INSTANCE.toUserInfo(user);

        return userInfo;
    }
}

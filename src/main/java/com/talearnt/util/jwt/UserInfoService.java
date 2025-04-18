package com.talearnt.util.jwt;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.auth.login.company.LoginMapper;
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
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND));;
        
        UserInfo userInfo = LoginMapper.INSTANCE.toUserInfo(user);

        return userInfo;
    }
}

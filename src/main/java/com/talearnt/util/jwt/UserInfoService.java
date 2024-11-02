package com.talearnt.util.jwt;

import com.talearnt.enums.ErrorCode;
import com.talearnt.join.User;
import com.talearnt.join.UserRepository;
import com.talearnt.util.exception.CustomRuntimeException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {

    private UserRepository userRepository;
    private ModelMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);
        if (user == null){
            throw new CustomRuntimeException(ErrorCode.USER_NOT_FOUND);
        }
        UserInfo userInfo = mapper.map(user,UserInfo.class);

        return userInfo;
    }
}

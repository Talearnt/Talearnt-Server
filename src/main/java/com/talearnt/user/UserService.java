package com.talearnt.user;

import com.talearnt.util.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    /**아이디 찾기 인증 번호 발송*/
    public ResponseEntity<CommonResponse<String>> sendAuthenticationCode(String phoneNumber){

         return null;
    }
}

package com.talearnt.user;


import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "아이디/비밀번호 찾기")
@RestControllerV1
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService service;

    @GetMapping("/users/{phoneNumber}/code")
    public ResponseEntity<CommonResponse<String>> sendAuthenticationCode(@PathVariable String phoneNumber){

        return null;
    }

    @PostMapping("/users/by-code/user-id")
    public ResponseEntity<CommonResponse<String>> sendUserId(){
        return null;
    }

}

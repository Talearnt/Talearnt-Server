package com.talearnt.user;


import com.talearnt.user.infomation.UserService;
import com.talearnt.user.talent.MyTalentService;
import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.user.talent.request.MyTalentReqDTO;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Users",description = "유저 관련")
@RestControllerV1
@RequiredArgsConstructor
@Log4j2
@Validated
public class UserController implements UserApi{

    private final UserService userService;
    private final MyTalentService myTalentService;

    @PostMapping("/users/password/test")
    public ResponseEntity<CommonResponse<String>> changePassword(@RequestBody TestChangePwdReqDTO testChangePwdReqDTO){
        return userService.changeTestPwd(testChangePwdReqDTO);
    }

    @PostMapping("/users/my-talents")
    public ResponseEntity<CommonResponse<String>> addMyTalents(@RequestBody @Valid MyTalentReqDTO talents){
        return CommonResponse.success(myTalentService.addMyTalents(talents));
    }

    @GetMapping("/users/my-talents")
    public ResponseEntity<CommonResponse<List<MyTalentsResDTO>>> getMyTalents(Authentication auth){
        return null;
    }

}

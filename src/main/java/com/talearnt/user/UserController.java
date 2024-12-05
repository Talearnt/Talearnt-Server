package com.talearnt.user;


import com.talearnt.user.infomation.UserService;
import com.talearnt.user.talent.MyTalentService;
import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.user.talent.request.MyTalentDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Users",description = "유저 관련")
@RestControllerV1
@RequiredArgsConstructor
@Log4j2
@Validated
public class UserController implements UserApi{

    private final UserService userService;
    private final MyTalentService addMyTalents;

    @PostMapping("/users/password/test")
    public ResponseEntity<CommonResponse<String>> changePassword(@RequestBody TestChangePwdReqDTO testChangePwdReqDTO){
        return userService.changeTestPwd(testChangePwdReqDTO);
    }

    @PostMapping("/users/my-talents")
    public ResponseEntity<CommonResponse<String>> addMyTalents(@RequestBody @Valid MyTalentDTO talents){
        return CommonResponse.success(addMyTalents.addMyTalents(talents));
    }

}

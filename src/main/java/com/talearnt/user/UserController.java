package com.talearnt.user;


import com.talearnt.user.infomation.UserService;
import com.talearnt.user.talent.MyTalentService;
import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.user.talent.request.MyTalentDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.Operation;
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
public class UserController {

    private final UserService userService;
    private final MyTalentService addMyTalents;

    @Operation(summary = "테스트용 비밀번호 바꾸기, 실 구현 X", description = "비번은 암호화가 걸려있어 변경이 어렵습니다. 이것으로 비번은 자유롭게 바꿀 수 있지만, Login은 Valid를 하기에 규칙은 지켜서 생성하세요.")
    @PostMapping("/users/password/test")
    public ResponseEntity<CommonResponse<String>> changePassword(@RequestBody TestChangePwdReqDTO testChangePwdReqDTO){
        return userService.changeTestPwd(testChangePwdReqDTO);
    }


    @Operation(summary = "나의 재능, 관심 키워드 추가", description = "#{api.users.addMyTalents}")
    @PostMapping("/users/my-talents")
    public ResponseEntity<CommonResponse<String>> addMyTalents(@RequestBody @Valid MyTalentDTO talents){
        return CommonResponse.success(addMyTalents.addMyTalents(talents));
    }

}

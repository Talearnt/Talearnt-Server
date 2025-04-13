package com.talearnt.user;


import com.talearnt.user.infomation.UserService;
import com.talearnt.user.infomation.request.ProfileReqDTO;
import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.user.infomation.response.UserHeaderResDTO;
import com.talearnt.user.talent.MyTalentService;
import com.talearnt.user.talent.request.MyTalentReqDTO;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Users",description = "유저 관련")
@RestControllerV1
@RequiredArgsConstructor
@Log4j2
@Validated
public class UserController implements UserApi{

    private final UserService userService;
    private final MyTalentService myTalentService;


    //회원의 기본 정보를 가져오는 API
    @GetMapping("/users/header/profile")
    public ResponseEntity<CommonResponse<UserHeaderResDTO>> getHeaderUserInfo(Authentication authentication){
        return CommonResponse.success(userService.getHeaderUserInfomation(authentication));
    }

    @PostMapping("/users/password/test")
    public ResponseEntity<CommonResponse<String>> changePassword(@RequestBody TestChangePwdReqDTO testChangePwdReqDTO){
        return userService.changeTestPwd(testChangePwdReqDTO);
    }

    //나의 재능 키워드 등록
    @PostMapping("/users/my-talents")
    public ResponseEntity<CommonResponse<String>> addMyTalents(@RequestBody @Valid MyTalentReqDTO talents){
        return CommonResponse.success(myTalentService.addMyTalents(talents));
    }

    //나의 정보 수정 == 키워드 및 닉네임 & 프로필 이미지
    @PutMapping("/users/profile")
    public ResponseEntity<CommonResponse<UserHeaderResDTO>> updateMyInfo(@RequestBody @Valid ProfileReqDTO profileReqDTO){
        return CommonResponse.success(userService.updateProfile(profileReqDTO.getUserInfo(),
                profileReqDTO.getNickname(),
                profileReqDTO.getProfileImg(),
                profileReqDTO.getGiveTalents(),
                profileReqDTO.getReceiveTalents()));
    }

}

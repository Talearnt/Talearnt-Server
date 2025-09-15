package com.talearnt.user;


import com.talearnt.admin.agree.AgreeService;
import com.talearnt.admin.agree.request.AgreeMarketingAndAdReqDTO;
import com.talearnt.admin.agree.response.AgreeMarketingAndAdvertisingResDTO;
import com.talearnt.comment.community.CommentService;
import com.talearnt.comment.community.response.MyCommentsResDTO;
import com.talearnt.enums.common.ClientPathType;
import com.talearnt.post.community.CommunityPostService;
import com.talearnt.post.community.response.CommunityPostListResDTO;
import com.talearnt.post.exchange.ExchangePostService;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.favorite.FavoriteService;
import com.talearnt.reply.community.ReplyService;
import com.talearnt.reply.community.response.MyRepliesResDTO;
import com.talearnt.reply.community.response.ReplyListResDTO;
import com.talearnt.user.infomation.UserService;
import com.talearnt.user.infomation.request.ProfileReqDTO;
import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.user.infomation.request.WithdrawalRequestDTO;
import com.talearnt.user.infomation.response.WithdrawalCompletionResponseDTO;
import com.talearnt.user.infomation.response.UserActivityCountsResDTO;
import com.talearnt.user.infomation.response.UserHeaderResDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.talearnt.user.talent.MyTalentService;
import com.talearnt.user.talent.request.MyTalentReqDTO;
import com.talearnt.util.common.ClientPath;

@Tag(name = "Users",description = "유저 관련")
@RestControllerV1
@RequiredArgsConstructor
@Log4j2
@Validated
public class UserController implements UserApi{

    private final UserService userService;
    private final MyTalentService myTalentService;
    private final ExchangePostService exchangePostService;
    private final FavoriteService favoriteService;
    private final CommunityPostService communityPostService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final AgreeService agreeService;


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

    //회원 웹 프로필 화면에서 작성한 게시글등 숫자를 보여주는 API
    @GetMapping("/users/profile/activity-counts")
    public ResponseEntity<CommonResponse<UserActivityCountsResDTO>> getMyActivityCounts(Authentication authentication) {
        return CommonResponse.success(userService.getMyActivityCounts(authentication));
    }

    // 회원 탈퇴 처리
    @PostMapping("/users/withdrawal")
    public ResponseEntity<CommonResponse<WithdrawalCompletionResponseDTO>> processWithdrawal(
            @RequestBody @Valid WithdrawalRequestDTO withdrawalRequestDTO,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response){
        UserInfo userInfo = UserUtil.validateAuthentication("회원 탈퇴", authentication);
        return CommonResponse.success(userService.processWithdrawal(request, response, userInfo, withdrawalRequestDTO));
    }


    //내가 작성한 재능교환 게시글 목록
    @GetMapping("/users/exchanges")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<ExchangePostListResDTO>>>> getMyExchangePostList(
            @RequestParam(value = "page", required = false, defaultValue = "1") String page,
            @RequestParam(value = "size", required = false, defaultValue = "15") String size,
            @RequestParam(value = "lastNo", required = false) String lastNo,
            @Schema(hidden = true) @ClientPath ClientPathType path,
            Authentication auth
    ){
        return CommonResponse.success(exchangePostService.getMyExchangePostList(page, size, lastNo, auth,path.name()));
    }


    //찜 게시글 목록
    @GetMapping("/users/exchanges/favorites")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<ExchangePostListResDTO>>>> getFavoriteExchanges(@RequestParam(required = false, defaultValue = "1") String page,
                                                                                                                @RequestParam(required = false, defaultValue = "15") String size,
                                                                                                                @ClientPath ClientPathType path,
                                                                                                                Authentication auth) {
        return CommonResponse.success(favoriteService.getFavoriteExchanges(path.name(), page, size, auth));
    }


    //내가 작성한 커뮤니티 게시글 목록
    @GetMapping("/users/communities")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<CommunityPostListResDTO>>>> getMyCommunityPostList(
            @RequestParam(required = false) String postType,
            @RequestParam(required = false, defaultValue = "recent") String order,
            @ClientPath ClientPathType path,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "12") String size,
            @RequestParam(required = false) String lastNo,
            Authentication authentication) {

        return CommonResponse.success(communityPostService.getMyCommunityPostList(authentication, postType,order, path.name(), lastNo, page, size));
    }

    @GetMapping("/users/comments")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<MyCommentsResDTO>>>> getMyComments(
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "30") String size,
            @RequestParam(required = false) String lastNo,
            @ClientPath ClientPathType path,
            Authentication authentication) {
        return CommonResponse.success(commentService.getMyComments(authentication, path.name(), lastNo, page, size));
    }

    @GetMapping("/users/replies")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<MyRepliesResDTO>>>> getMyReplies(Authentication authentication,
                                                                                                 @RequestParam(required = false) String lastNo,
                                                                                                 @ClientPath ClientPathType path,
                                                                                                 @RequestParam(required = false, defaultValue = "1") String page,
                                                                                                 @RequestParam(required = false, defaultValue = "10") String size) {
        return CommonResponse.success(replyService.getMyReplies(authentication, lastNo, path.name(), page,size));
    }

    @GetMapping("/users/agreements")
    public ResponseEntity<CommonResponse<AgreeMarketingAndAdvertisingResDTO>> getAgreeMarketingAndAdvertising(Authentication authentication){
        return CommonResponse.success(agreeService.getAgreeMarketingAndAdvertising(authentication));

    }

    @PatchMapping("/users/agreements/marketing")
    public ResponseEntity<CommonResponse<Void>> switchMarketingAgreeCode(@RequestBody AgreeMarketingAndAdReqDTO agreeCodeMarketingReqDTO){
        agreeService.switchMarketingAgreeCode(agreeCodeMarketingReqDTO.isAgree(), agreeCodeMarketingReqDTO.getUserInfo());
        return CommonResponse.success(null);
    }

    @PatchMapping("/users/agreements/advertising")
    public ResponseEntity<CommonResponse<Void>> switchAdvertisingAgreeCode(@RequestBody AgreeMarketingAndAdReqDTO agreeCodeMarketingReqDTO){
        agreeService.switchAdvertisingAgreeCode(agreeCodeMarketingReqDTO.isAgree(), agreeCodeMarketingReqDTO.getUserInfo());
        return CommonResponse.success(null);
    }


}

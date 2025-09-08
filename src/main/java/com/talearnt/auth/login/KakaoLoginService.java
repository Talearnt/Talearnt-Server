package com.talearnt.auth.login;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.auth.login.kakao.KakaoLoginResDTO;
import com.talearnt.auth.login.kakao.KakaoTokenResDTO;
import com.talearnt.auth.login.kakao.KakaoUserInfoResDTO;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.util.common.LoginUtil;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.JwtTokenUtil;
import com.talearnt.util.jwt.UserInfo;
import io.netty.handler.codec.http.HttpHeaderValues;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

/** 자사 로그인과 Kakao 로그인에 JWT 설정에서 중복 코드가 발생했습니다.
 * 추후 리펙토링에서 개선할 필요가 있습니다.*/


@Service
@Log4j2
@RequiredArgsConstructor
public class KakaoLoginService {
    @Value("${kakao.client.id}")
    private String clientId;

    private final String TOKEN_URL_HOST="https://kauth.kakao.com";
    private final String USER_URL_HOST="https://kapi.kakao.com";

    private final JwtTokenUtil jwtTokenUtil;
    private final LoginService loginService;
    //repositories
    private final UserRepository userRepository;


    /** 카카오톡 로그인 : 카카오톡에서 넘어온 코드를 가지고 토큰 발급 요청하고,<br>
     * 토큰을 가지고 회원인지, 비회원인지 판단<br>
     * 회원이면 어세스 토큰 발급,<br>
     * 비회원이면 URL 전송.<br>
     * 레퍼런스 : https://ddonghyeo.tistory.com/16
     * @param code 카카오톡에서 넘어오는 인자 Code
     * @param response RefreshToken Cookie 사용을 위해*/
    public KakaoLoginResDTO loginKakao(String code, HttpServletRequest request, HttpServletResponse response){
        log.info("카카오톡 로그인 서비스 시작 : {}",code);

        //카카오톡에서 받은 인가코드로 어세스 토큰 발급 요청
        KakaoTokenResDTO tokenResDTO = WebClient.create(TOKEN_URL_HOST)
                        .post()
                                .uri(uriBuilder ->uriBuilder
                                        .scheme("https")
                                        .path("/oauth/token")
                                        .queryParam("grant_type","authorization_code")
                                        .queryParam("client_id",clientId)
                                        .queryParam("code",code)
                                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,clientResponse -> Mono.error(new CustomRuntimeException(ErrorCode.BAD_REQUEST)))
                .onStatus(HttpStatusCode::is5xxServerError,clientResponse -> Mono.error(new CustomRuntimeException(ErrorCode.INTERNAL_SERVER_ERROR)))
                .bodyToMono(KakaoTokenResDTO.class)
                .block();

        //카카오톡 유저 정보 추출
        KakaoUserInfoResDTO kakaoUserInfoResDTO = getKakaoUserInfo(tokenResDTO.getAccessToken());
        log.info("KAKAO USER INFO : {} ",kakaoUserInfoResDTO);
        //동일한 휴대폰 번호가 있을 경우 연동할 수 있도록 409 Exception
        /* 연동이 확실해질 경우, KaKaoQueryRepository의 checkConectedKakao()을 완성시키세요.
        if (userRepository.existsByPhone(kakaoUserInfoResDTO.getKakaoAccount().getPhoneNumber())){
            log.error("카카오톡 로그인 서비스 시작 실패 - 동일한 휴대폰 번호 존재 : {}",ErrorCode.USER_PHONE_NUMBER_DUPLICATION);
            throw new CustomRuntimeException(ErrorCode.USER_PHONE_NUMBER_DUPLICATION);
        }*/

        //회원 가입 유무 판단
        Optional<User> optionalUser = userRepository.findByUserId(kakaoUserInfoResDTO.getKakaoAccount().getEmail());

        //없으면 결과값 리턴
        if (optionalUser.isEmpty()){
            //반환할 객체 생성
            KakaoLoginResDTO resDTO= new KakaoLoginResDTO(false,
                    kakaoUserInfoResDTO.getKakaoAccount().getEmail(),
                    kakaoUserInfoResDTO.getKakaoAccount().getName(),
                    kakaoUserInfoResDTO.getKakaoAccount().getPhoneNumber(),
                    kakaoUserInfoResDTO.getKakaoAccount().getGender());

            // 휴대폰 중복인지 확인
            if (userRepository.existsByPhone(resDTO.getPhone())){
                log.error("카카오톡 로그인 서비스 실패 - 동일한 휴대폰 번호 존재 : {}",ErrorCode.USER_PHONE_NUMBER_DUPLICATION);
                throw new CustomRuntimeException(ErrorCode.USER_PHONE_NUMBER_DUPLICATION);
            }


            log.info("카카오톡 로그인 서비스 실패 - 해당 유저는 회원가입 하지 않음: {}", ErrorCode.USER_NOT_FOUND);
            return resDTO;
        }

        //자사 JWT 토큰 설정
        //User Last Login Time 업데이트
        User user = optionalUser.get();

        //회원가입을 이미 했지만, 카카오톡이 아닌 네이버나 자사로 회원가입을 했을 경우
        LoginUtil.validateJoinType(user,"카카오톡");

        // 정지 또는 탈퇴 회원 인지 유저 권환 확인
        UserUtil.validateUserRole("카카오톡 로그인 서비스 시작",user);

        //인증 후 RefreshToken 발급
        UserInfo userInfo = loginService.checkLoginValueAndSetRefreshToekn(user,true, request,response);

        log.info("카카오톡 로그인 서비스 끝");
        return new KakaoLoginResDTO(true,jwtTokenUtil.createJwtToken(userInfo));
    }

    /** 카카오톡에서 받아온 AccessToken을 가지고 유저의 정보를 뽑아온다.
     * @param accessToken Kakao 에서 제공한 어세스 토큰*/
    private KakaoUserInfoResDTO getKakaoUserInfo(String accessToken){
        return WebClient.create(USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,clientResponse -> Mono.error(new CustomRuntimeException(ErrorCode.BAD_REQUEST)))
                .onStatus(HttpStatusCode::is5xxServerError,clientResponse -> Mono.error(new CustomRuntimeException(ErrorCode.INTERNAL_SERVER_ERROR)))
                .bodyToMono(KakaoUserInfoResDTO.class)
                .block();
    }

    /** Flutter용 모바일 카카오 로그인 : Flutter에서 받은 Access Token을 가지고 회원인지, 비회원인지 판단<br>
     * 회원이면 어세스 토큰 발급,<br>
     * 비회원이면 회원가입 정보 반환.<br>
     * @param kakaoAccessToken Flutter에서 받은 카카오 Access Token
     * @param autoLogin 자동 로그인 여부
     * @param response RefreshToken Cookie 사용을 위해*/
    public KakaoLoginResDTO loginKakaoForMobile(String kakaoAccessToken, boolean autoLogin, HttpServletRequest request,HttpServletResponse response){
        log.info("Flutter 모바일 카카오톡 로그인 서비스 시작");

        //카카오톡에서 받은 Access Token으로 유저 정보 추출
        KakaoUserInfoResDTO kakaoUserInfoResDTO = getKakaoUserInfo(kakaoAccessToken);
        log.info("KAKAO USER INFO : {} ",kakaoUserInfoResDTO);

        //회원 가입 유무 판단
        Optional<User> optionalUser = userRepository.findByUserId(kakaoUserInfoResDTO.getKakaoAccount().getEmail());

        //없으면 결과값 리턴
        if (optionalUser.isEmpty()){
            //반환할 객체 생성
            KakaoLoginResDTO resDTO= new KakaoLoginResDTO(false,
                    kakaoUserInfoResDTO.getKakaoAccount().getEmail(),
                    kakaoUserInfoResDTO.getKakaoAccount().getName(),
                    kakaoUserInfoResDTO.getKakaoAccount().getPhoneNumber(),
                    kakaoUserInfoResDTO.getKakaoAccount().getGender());

            // 휴대폰 중복인지 확인
            if (userRepository.existsByPhone(resDTO.getPhone())){
                log.error("카카오톡 로그인 서비스 실패 - 동일한 휴대폰 번호 존재 : {}",ErrorCode.USER_PHONE_NUMBER_DUPLICATION);
                throw new CustomRuntimeException(ErrorCode.USER_PHONE_NUMBER_DUPLICATION);
            }

            log.info("Flutter 모바일 카카오톡 로그인 서비스 실패 - 해당 유저는 회원가입 하지 않음: {}", ErrorCode.USER_NOT_FOUND);
            return resDTO;
        }

        //자사 JWT 토큰 설정
        //User Last Login Time 업데이트
        User user = optionalUser.get();

        //회원가입을 이미 했지만, 카카오톡이 아닌 네이버나 자사로 회원가입을 했을 경우
        LoginUtil.validateJoinType(user,"카카오톡");

        // 정지 또는 탈퇴 회원 인지 유저 권환 확인
        UserUtil.validateUserRole("Flutter 모바일 카카오톡 로그인 서비스 시작",user);

        //인증 후 RefreshToken 발급
        UserInfo userInfo = loginService.checkLoginValueAndSetRefreshToekn(user,autoLogin,request,response);

        log.info("Flutter 모바일 카카오톡 로그인 서비스 끝");
        return new KakaoLoginResDTO(true,jwtTokenUtil.createJwtToken(userInfo));
    }

}

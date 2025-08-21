package com.talearnt.util.common;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.user.UserRole;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
public class UserUtil {

    /** 정지, 탈퇴 회원 인지 판단하는 메소드 입니다.
     * */
    public static void validateUserRole(String errorLocation, User user){
        if (user.getAuthority().equals(UserRole.ROLE_SUSPENDED)){
            log.error("{} 실패 - 정지된 회원입니다 : {}",ErrorCode.USER_SUSPENDED);
            throw new CustomRuntimeException(ErrorCode.USER_SUSPENDED);
        } else if (user.getAuthority().equals(UserRole.ROLE_WITHDRAWN) ) {
            log.error("{} 실패 - 탈퇴한 회원입니다 : {}",ErrorCode.USER_WITH_DRAWN);
            throw new CustomRuntimeException(ErrorCode.USER_WITH_DRAWN);
        }
    }


    /** Authentication 검증 하는 메소드,
     * 검증이 완료될 경우 UserInfo를 반환합니다.
     * Controller에서 Authenticatin 으로 가져올 경우 이걸로 검증합니다.
     * @param errorLocation 에러 발생시 위치
     * @param authentication JWT
     * @return UserInfo
     * */
    public static UserInfo validateAuthentication(String errorLocation ,Authentication authentication){
        if (authentication == null){
            log.error("{} 실패 - 로그인이 되어 있지 않음 : {}",errorLocation, ErrorCode.EXPIRED_TOKEN);
            throw new CustomRuntimeException(ErrorCode.EXPIRED_TOKEN);
        }

        return (UserInfo) authentication.getPrincipal();
    }


    /** 유저의 권한이 정지 또는 탈퇴인지 확인하는 메소드
     * @param authority getAuthority().name()에 대한 값
     * */
    public static void validateUserAuthority(String authority){
        if (authority.equals("ROLE_SUSPENDED")){
            log.error("유저 아이디 찾기 문자 전송 실패 - 정지된 회원 : {}",ErrorCode.USER_SUSPENDED);
            throw new CustomRuntimeException(ErrorCode.USER_SUSPENDED);
        } else if (authority.equals("ROLE_WITHDRAWN")) {
            log.error("유저 아이디 찾기 문자 전송 실패 - 탈퇴한 회원 : {}",ErrorCode.USER_WITH_DRAWN);
            throw new CustomRuntimeException(ErrorCode.USER_WITH_DRAWN);
        }
    }

    /**
     * User의 닉네임을 랜덤하게 만들어 반환하는 메소드입니다.
     * @return 도전적인 양의 9999번째 영웅
     * */
    public static String makeRandomNickName(){
        //형용사 100개
        List<String> adj = Arrays.asList(
                "행복한", "즐거운", "노력하는", "성공주의", "꾸준한", "부지런한", "창의적인", "끈기의", "열정적인", "긍정적인",
                "강인한", "활력적인", "용감한", "유능한", "자신감의", "성실한", "따뜻한", "차분한", "섬세한", "다정한",
                "희망찬", "유쾌한", "현명한", "배려적", "명랑한", "친근한", "겸손한", "친절한", "정직한", "천재적인",
                "활달한", "평화적인", "대담한", "단호한", "협력적인", "효율적인", "낙천적인", "겸허한", "참신한", "온화한",
                "순수한", "적응적인", "신중한", "경쾌한", "낙관적인", "근성의", "능동적인", "발전적인", "놀라운", "유연한",
                "멋있는", "창조적인", "지혜로운", "용맹한", "성취적인", "현실적인", "기대주", "분석적인", "신뢰적인", "도움적인",
                "영리한", "고운", "열정찬", "정확한", "발랄한", "사려깊은", "깨끗한", "탐구적인", "감동적인", "화려한",
                "기운찬", "부동의", "끈질긴", "개방적인", "독창적인", "애정깊은", "정겨운", "귀여운", "멋스런", "활기찬",
                "평온한", "풍요적", "자유적인", "중요한", "치밀한", "탁월한", "철저한", "화합적인", "소박한", "엄격한",
                "풍부한", "능숙한", "기민한", "효과적인", "뛰어난", "기분좋은", "확고한", "깨달은", "밝은빛", "생기찬"
        );

        //이름 100개
        List<String> names = Arrays.asList(
                "청룡", "백호", "주작", "현무", "호랑이", "사자", "용", "독수리", "까치", "부엉이",
                "펭귄", "고래", "팬더", "늑대", "햄스터", "상어", "드래곤", "여우", "참새", "황소",
                "오리", "흑양", "두더지", "사슴", "치타", "물개", "비버", "돌고래", "천둥", "낙타",
                "유비", "타조", "빽곰", "코뿔소", "백조", "재규어", "표범", "올빼미", "금룡", "미어캣",
                "다람쥐", "은하", "갈매기", "수달", "두루미", "공작새", "황룡", "비둘기", "잉어", "까마귀",
                "염소", "여우", "물소", "관우", "장비", "동키", "루돌프", "천둥", "오소리", "흑곰",
                "산양", "범고래", "백상어", "토끼", "바다뱀", "꽃사슴", "불사조", "태양", "항우", "백호",
                "초선", "여포", "백룡", "작은곰", "거상", "도마뱀", "고등어", "황제", "큰곰", "세종",
                "물범", "쌍룡", "수사관", "대통령", "승부사", "날치", "청황", "흑수", "흰곰", "새우"
        );

        //랜덤한 칭호 나오도록 섞기
        Collections.shuffle(adj);
        Collections.shuffle(names);
        int randomNumber = (int) (Math.random() * (100 - 1 + 1)) + 1;
        //ex ) 도전적인 양의 9999번째 영웅
        return adj.get(0)+names.get(1)+"#"+ randomNumber;
    }
}

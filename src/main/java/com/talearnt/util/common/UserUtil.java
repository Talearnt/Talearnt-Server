package com.talearnt.util.common;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
public class UserUtil {

    /**
     * 회원 정보를 검증하는 메소드입니다.
     * UserInfo의 객체 검증이 필요한 곳에서 사용하면 됩니다.
     * @param userInfo JWT 토큰 안에 있는 유저의 정보입니다.
     * */
    public static void validateUserInfo(UserInfo userInfo) {
        if (userInfo == null || userInfo.getUserId() == null || userInfo.getUserId() == null) {
            throw new CustomRuntimeException(ErrorCode.INVALID_TOKEN);
        }
    }


    /** 유저의 권한이 정지 또는 탈퇴인지 확인하는 메소드
     * @param authority getAuthority().name()에 대한 값
     * */
    public static void validateUserAuthority(String authority){
        if (authority.equals("ROLE_SUSPENDED")){
            log.error("유저 아이디 찾기 문자 전송 실패 - 정지된 회원 : {}",ErrorCode.USER_SUSPENDED);
            throw new CustomRuntimeException(ErrorCode.USER_SUSPENDED);
        } else if (authority.equals("ROLE_WITHDRAWN")) {
            log.error("유저 아이디 찾기 문자 전송 실패 - 탈퇴한 회원 : {}",ErrorCode.USER_NOT_FOUND);
            throw new CustomRuntimeException(ErrorCode.USER_NOT_FOUND);
        }
    }

    /**
     * User의 닉네임을 랜덤하게 만들어 반환하는 메소드입니다.
     * @return 도전적인 양의 9999번째 영웅
     * */
    public static String makeRandomNickName(){
        //형용사 100개
        List<String> adj = Arrays.asList(
                "주도하는", "정열적인", "행복한", "즐거운", "노력하는", "성공할", "성공하는", "꾸준한", "부지런한",
                "창의적인", "영감을 주는", "끈기있는", "믿음직한", "열정적인", "긍정적인", "강인한", "활력있는", "용감한",
                "유능한", "자신감 있는", "헌신적인", "성실한", "따뜻한", "열린 마음의", "차분한", "책임감 있는", "섬세한",
                "다정한", "희망찬", "유쾌한", "현명한", "배려하는", "영리한", "명랑한", "친근한", "겸손한", "친절한",
                "자유로운", "정직한", "재능있는", "마음을 다하는", "사려 깊은", "균형잡힌", "활달한", "책임있는", "평화로운",
                "친화력 있는", "대담한", "따뜻함을 가진", "단호한", "집중력 있는", "협력적인", "신뢰할 수 있는", "효율적인", "기발한",
                "낙천적인", "전문적인", "겸허한", "열정에 찬", "인내심 있는", "친절한", "공감하는", "이해심 있는", "참신한",
                "정확한", "온화한", "강한 의지를 가진", "협력적인", "명쾌한", "명석한", "순수한", "활기찬", "적응력 있는",
                "도전적인", "신중한", "경쾌한", "전진하는", "낙관적인", "성실한", "끊임없는", "능동적인", "사려 깊은",
                "영감을 주는", "발전하는", "놀라운", "중심을 지키는", "다양한", "용맹한", "성취 지향의", "현실적인", "도움을 주는",
                "기대되는", "분석적인", "사려 깊은", "유연한", "역량 있는", "효율적인", "균형잡힌", "끈질긴", "창조적인", "지혜로운"
        );
        //이름 100개
        List<String> names = Arrays.asList(
                "청룡", "호랑이", "토끼", "사자", "조랑말", "주작", "현무", "거북이", "까치", "황금사자",
                "불사조", "백호", "독수리", "용", "부엉이", "펭귄", "고래", "팬더", "하이에나", "햄스터",
                "상어", "늑대", "반달가슴곰", "북극곰", "강아지", "고양이", "드래곤", "여우", "참새", "황소",
                "오리", "말", "양", "두더지", "사슴", "치타", "물개", "돌고래", "비버", "청둥오리",
                "낙타", "부리새", "타조", "곰", "코뿔소", "사향고양이", "백조", "백룡", "재규어", "표범",
                "올빼미", "금룡", "미어캣", "다람쥐", "바다사자", "하프물범", "갈매기", "수달", "두루미", "공작새",
                "황룡", "카멜레온", "비둘기", "잉어", "돌고래", "까마귀", "양자리", "염소", "붉은여우", "물소",
                "작은새", "청상아리", "거미원숭이", "강철새", "빨강머리사슴", "파란앵무새", "동키", "디노", "루돌프", "천둥새",
                "오소리", "큰곰", "산양", "호비", "삵", "아기상어", "은하수사자", "황제펭귄", "산양", "물범",
                "도롱뇽", "작은곰", "노랑비둘기", "은색호랑이", "바다여우", "정글사자", "바다용", "꽃사슴", "푸른새", "빨간물개"
        );
        //칭호
        List<String> title = Arrays.asList( "마스터", "챔피언", "영웅", "전설", "리더", "지배자", "수호자", "도전자", "전략가", "혁신가");

        //랜덤한 칭호 나오도록 섞기
        Collections.shuffle(adj);
        Collections.shuffle(names);
        Collections.shuffle(title);
        int randomNumber = (int) (Math.random() * (100000 - 1000 + 1)) + 1000;
        //ex ) 도전적인 양의 9999번째 영웅
        return adj.get(0)+" "+names.get(1)+"의 "+ randomNumber +"번째 "+title.get(2);
    }
}

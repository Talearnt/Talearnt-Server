package com.talearnt.util.common;

import com.querydsl.core.Tuple;
import com.talearnt.enums.common.Regex;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.enums.post.PostType;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostUtil {

    /** String 에서 Integer 로 유효한 값만 변환하는 Method <br>
     * List<Integer> 반환*/
    public static List<Integer> filterValidIntegers(List<String> strings){
        return strings.stream().filter(PostUtil::isInteger)
                .map(Integer::valueOf).toList();
    }


    /** Integer인지 아닌지 판단하는 Method <br>
     * - String로 받아서 Integer로 변환하는 과정에서 필요해서 만들었습니다.<br>
     * - true : Integer 타입<br>
     * - false : 그 외 타입<br>
     * 현재 사용하는 곳
     * - Categories 및 Talents 목록에서 Integer 가 아닌 값 제거하기
     * */
    public static boolean isInteger(String value){
        try {
            value = getTrimString(value);
            Integer.parseInt(value.trim());
            return true;
        }catch (NumberFormatException | NullPointerException e){
            return false;
        }
    }

    public static Integer parseInteger(String value){
        try {
            value = getTrimString(value);
            return Integer.parseInt(value);
        }catch (NumberFormatException | NullPointerException e){
            return null;
        }
    }

    public static Double parseDouble(String value){
        try {
            value = getTrimString(value);
            return Double.parseDouble(value);
        }catch (NumberFormatException | NullPointerException e){
            return null;
        }
    }

    /** String을 Long 타입으로 바꿔주는 메소드, Search에 많이 사용하려고 만들었습니다.
     * Exception 발생 시 조건 탐색을 안하기 위해서 null을 반환합니다.*/
    public static Long parseLong(String value){
        try {
            value = getTrimString(value);
            return Long.parseLong(value);
        }catch (NumberFormatException | NullPointerException e){
            return null;
        }
    }

    /** 제대로 된 정렬 값이 넘어오지 않으면 recent로 만드는 Method <br>
     * 현재 사용하는 곳
     * - 재능 교환 게시글 목록 불러오기*/
    public static String filterValidOrderValue(String value){
        value = getTrimString(value);
        //Recent,Popular가 아니라면 recent 반환
        return switch (value.toLowerCase()){
            case "popular", "hot" -> value;
            default -> "recent";
        };
    }

    /** 교환 기간 Regex에 맞지 않으면 null 반환 Method<br>*/
    public static String filterValidDurationValue(String value){
        value = getTrimString(value);
        //REGEX에 일치하면 해당 값 반환
        if(value != null && value.matches(Regex.EXCHANGE_DURATION.getPattern())) return value;
        //아닐 경우 null
        return null;
    }

    /** 진행 방식이 Regex에 맞지 않으면 null 반환 Method*/
    public static ExchangeType filterValidExchangeType(String value){
        try {
            value = getTrimString(value);
            //온라인,오프라인,온_오프라인 값에 해당하는 거 가져오기.
            return ExchangeType.fromFE(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            //해당 값 없으면 null 반환
            return null;
        }
    }

    /**requiredBadge 가 Boolean이 아닐 경우 null*/
    public static Boolean filterValidRequiredBadge(String value){
        //null 일 경우 그냥 null 반환
        if (value == null) return null;
        value = getTrimString(value);
        //null이 아닐 경우 value 에 맞는 값 반환
        return switch (value.toLowerCase()){
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };
    }

    /** status - 모집중, 모집_완료 가 아니라면 null 반환*/
    public static ExchangePostStatus filterValidExchangePostStatus(String value){
        try {
            value = getTrimString(value);
            return ExchangePostStatus.fromFE(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
    /** Pageable을 만드는 Method
     * Page - 현재 페이지 <br>
     * size - 반환할 갯수 <br>*/
    public static Pageable filterValidPagination(String page, String size){
        int defaultPage = 1;
        int defaultSize = 15;
        // Page number에 대한 Try-catch문
        try{
            defaultPage = Integer.parseInt(page.trim());
            //음수에 대한 페이지인가? 맞다면 기본 값 설정.
            if (defaultPage < 1){
                defaultPage = 1;
            }
        } catch (NumberFormatException ignored) {}
        // Page Size에 대한 Try-catch문
        try {
            defaultSize = Integer.parseInt(size.trim());
            //무한한 값이 들어오거나 음수값이 들어올 경우 최대값, 최소값 설정.
            if(defaultSize > 50){
                defaultSize = 50;
            } else if (defaultSize < 1) {
                defaultSize =15;
            }
        }catch (NumberFormatException ignored){}

        return PageRequest.of(defaultPage-1,defaultSize);
    }

    private static String getTrimString(String value){
        if (value == null) return null;
        return value.trim();
    }




    //재능 게시글 업데이트 - 추가할 값 추출
    public static List<Integer> getAddTalentCodes(List<Integer> willUpdateTalentCode, Map<Long, Integer> talentMap, Map<Long, Integer> updateTalentCodeMap){
        return willUpdateTalentCode.stream()
                .filter(code-> !talentMap.containsValue(code) && !updateTalentCodeMap.containsValue(code))
                .toList();
    }


    //재능 게시글 업데이트 - 유지할 값 추출
    public static Map<Long,Integer> getSameCodes(Map<Long,Integer> talentMap, List<Integer> willUpdateTalentCodes){
        return talentMap.entrySet().stream().filter(entry->willUpdateTalentCodes.contains(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // 재능 게시글 업데이트 - 변경할 값 추출
    public static Map<Long, Integer> getUpdateTalentCodes(Map<Long,Integer> talentMap, List<Integer> newTalentCodes, Map<Long,Integer> sameCodes){
        List<Long> notEqualTalents = talentMap.keySet()
                .stream()
                .filter(integer -> !sameCodes.containsKey(integer))
                .sorted()
                .toList();

        List<Integer> notEqualTalentCodes = newTalentCodes.stream()
                .filter(code -> !sameCodes.containsValue(code))
                .toList();

        Map<Long,Integer> updateTalents = new HashMap<>();

        for (int i = 0; i < Math.min(notEqualTalents.size(),notEqualTalentCodes.size()); i++) {
            updateTalents.put(notEqualTalents.get(i), notEqualTalentCodes.get(i));
        }

        return updateTalents;
    }

    // 재능 게시글 업데이트 - 삭제할 아이디 값 추출
    public static List<Long> getDeleteIds(Map<Long,Integer> talentMap, Map<Long, Integer> sameCodes, Map<Long, Integer> updateTalentCodeMap){
        return talentMap.entrySet().stream()
                .filter(talent -> !sameCodes.containsValue(talent.getValue())
                        && !updateTalentCodeMap.containsKey(talent.getKey()))
                .map(Map.Entry::getKey)
                .toList();
    }


    //재능 게시글 업데이트 - Tuple -> Map으로 변경
    public static Map<Long, Integer> getTalentMap(String key, Map<String, List<Tuple>> codes) {
        return codes.get(key)
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> tuple.get(1, Integer.class)
                ));
    }

    //찜 게시글, 게시글 좋아요, 로그인 하지 않았더라도 여부 알기용.
    public static Long getCurrentUserNo(String errorLocation, Authentication auth){
        Long currentUserNo = 0L;
        if (auth != null){
            UserInfo userInfo = UserUtil.validateAuthentication(errorLocation,auth);
            currentUserNo = userInfo.getUserNo();
        }
        return currentUserNo;
    }

    //게시글 Post Type 변환
    public static PostType filterValidPostType(String value){
        try{
            value = getTrimString(value);
            return PostType.from(value);
        }catch (IllegalArgumentException | NullPointerException | CustomRuntimeException e){
            return null;
        }
    }

    //post 접근 디바이스 Web or Mobile
    public static String filterValidPath(String value){
        if (value.equalsIgnoreCase("mobile")) return "mobile";
        return "web";
    }

    // 조회 검색 시 기준 시간 변환 - 값이 없을 경우 현재 시간을 기준으로 생성
    public static LocalDateTime filterValidBaseTime(String value){
        try {
            value = getTrimString(value);
            return LocalDateTime.parse(value,DateTimeFormatter.ISO_DATE_TIME);
        }catch (NullPointerException | DateTimeParseException e){
            return LocalDateTime.now();
        }
    }

}


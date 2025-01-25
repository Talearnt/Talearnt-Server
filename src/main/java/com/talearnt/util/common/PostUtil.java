package com.talearnt.util.common;

import com.talearnt.enums.common.Regex;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
            Integer.parseInt(value.trim());
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    /** 제대로 된 정렬 값이 넘어오지 않으면 recent로 만드는 Method <br>
     * 현재 사용하는 곳
     * - 재능 교환 게시글 목록 불러오기*/
    public static String filterValidOrderValue(String value){
        //Recent,Popular가 아니라면 recent 반환
        if ("popular".equalsIgnoreCase(value.trim())) return value;
        return "recent";
    }

    /** 교환 기간 Regex에 맞지 않으면 null 반환 Method<br>*/
    public static String filterValidDurationValue(String value){
        //REGEX에 일치하면 해당 값 반환
        if(value != null && value.trim().matches(Regex.EXCHANGE_DURATION.getPattern())) return value;
        //아닐 경우 null
        return null;
    }

    /** 진행 방식이 Regex에 맞지 않으면 null 반환 Method*/
    public static ExchangeType filterValidExchangeType(String value){
        try {
            //온라인,오프라인,온_오프라인 값에 해당하는 거 가져오기.
            return ExchangeType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            //해당 값 없으면 null 반환
            return null;
        }
    }

    /**requiredBadge 가 Boolean이 아닐 경우 null*/
    public static Boolean filterValidRequiredBadge(String value){
        //null 일 경우 그냥 null 반환
        if (value==null) return null;
        //null이 아닐 경우 value 에 맞는 값 반환
        return switch (value.trim().toLowerCase()){
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };
    }

    /** status - 모집중, 모집_완료 가 아니라면 null 반환*/
    public static ExchangePostStatus filterValidExchangePostStatus(String value){
        try {
            return ExchangePostStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
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

}

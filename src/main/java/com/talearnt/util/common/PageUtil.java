package com.talearnt.util.common;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;
import org.springframework.data.domain.Page;

public class PageUtil {

    /**
     * 이용하는 방법은 Page<Entity>를 넣으면 Pagination을 만들어 반환한다.
     * @param page : Page<Entity> 형태에서 제네릭을 안써서 Page 정보만 가지고 있는 상태.
     */
    public static Pagination separatePaginationFromEntity(Page page) {
        return new Pagination.PaginationBuilder()
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber() + 1)
                .build();
    }

    /**
     * 현재 페이지가 0보다 작을 경우 Excpetion 발생
     * @param page 현재 페이지
     * */
    public static void validateMinPageNo(int page) {
        if(page < 0){
            throw new CustomRuntimeException(ErrorCode.PAGE_MIN_NUMBER);
        }
    }

    /**
     * 사용자가 보낸 페이지 번호가 유효한 페이지 번호인지 확인하는 메소드
     * 현재 페이지가 최대 페이지 숫자보다 크면 Exception 발생
     * @param pagination DB에서 조회한 Pagination
     */
    public static void validateMaxPageNo(Pagination pagination) {
        if(pagination.getCurrentPage() > pagination.getTotalPages()){
            throw new CustomRuntimeException(ErrorCode.PAGE_OVER_MAX_NUMBER);
        }
    }

}

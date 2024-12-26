package com.talearnt.util.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.response.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class FilterExceptionHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        }catch (ExpiredJwtException e){
            setErrorResponse(response,ErrorCode.EXPIRED_TOKEN);
        }catch (JwtException | IllegalArgumentException e){
            setErrorResponse(response,ErrorCode.EXPIRED_TOKEN);
        }
    }
    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode){
        //JSON 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        // HTTP Status 설정
        response.setStatus(Integer.parseInt(errorCode.getCode().substring(0, 3)));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        //ResponseEntity를 설정할 경우 Header 및 여러가지가 다 담겨서, CommonResponse를 만들어 반환
        CommonResponse<?> error = new CommonResponse<>(false,null,errorCode.getCode(),errorCode.getMessage());

        try{
            //Response에 담아서 반환
            response.getWriter().write(objectMapper.writeValueAsString(error));
            response.getWriter().flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

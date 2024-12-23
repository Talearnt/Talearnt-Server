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
    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(Integer.parseInt(errorCode.getCode().substring(0, 3)));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try{
            response.getWriter().write(objectMapper.writeValueAsString(CommonResponse.error(errorCode)));
        }catch (IOException e){
            e.printStackTrace();
        }
    }



}

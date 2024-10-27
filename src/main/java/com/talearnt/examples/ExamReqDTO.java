package com.talearnt.examples;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Component
public class ExamReqDTO {
    private String examId;
    private String pw;
    @Schema(required = true, example = "examNickname1")
    private String nickname;
    private LocalDateTime createdAt;

}
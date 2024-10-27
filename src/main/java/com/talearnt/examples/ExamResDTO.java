package com.talearnt.examples;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Component
public class ExamResDTO {

    private String examId;
    private String nickname;

}

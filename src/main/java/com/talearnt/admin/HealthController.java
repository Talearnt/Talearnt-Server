package com.talearnt.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AWS-ELB", description = "AWS 관련 서버 상태 체크, 개발 사용 X")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity checkHealth(){
        return ResponseEntity.status(200).build();
    }
}

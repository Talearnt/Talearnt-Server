package com.talearnt.admin.notice;


import com.talearnt.admin.notice.request.NoticeInsertReqDTO;
import com.talearnt.admin.notice.response.NoticeDetailResDTO;
import com.talearnt.admin.notice.response.NoticeListResDTO;
import com.talearnt.enums.common.ClientPathType;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestControllerV1
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Notice & Event")
public class NoticeController implements NoticeApi {

    private final NoticeService noticeService;

    @GetMapping("/notices")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<NoticeListResDTO>>>> getNoticeList(@ClientPath ClientPathType path,
                                                                                                   @RequestParam(value = "page",required = false,defaultValue = "1") String page,
                                                                                                   @RequestParam(value = "size",required = false,defaultValue = "15") String size) {
        return CommonResponse.success(noticeService.getNoticeList(path.name(), page, size));
    }

    @GetMapping("/notices/{noticeNo}")
    public ResponseEntity<CommonResponse<NoticeDetailResDTO>> getNoticeDetail(@PathVariable Long noticeNo) {
        return CommonResponse.success(noticeService.getNoticeDetail(noticeNo));
    }

    @PostMapping("/notices")
    public ResponseEntity<CommonResponse<Void>> createNotice(@RequestBody NoticeInsertReqDTO noticeInsertReqDTO) {
        noticeService.createNotice(noticeInsertReqDTO);
        return CommonResponse.success(null);
    }


}

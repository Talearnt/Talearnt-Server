package com.talearnt.admin.event;

import com.talearnt.admin.event.response.EventListResDTO;
import com.talearnt.enums.common.ClientPathType;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestControllerV1
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Notice & Event")
public class EventController implements EventApi{

    private final EventService eventService;

    @GetMapping("/events")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<EventListResDTO>>>> getEventList(@ClientPath ClientPathType path,
                                                                                                 @RequestParam(value = "page",required = false,defaultValue = "1") String page,
                                                                                                 @RequestParam(value = "size",required = false,defaultValue = "15") String size){
        return CommonResponse.success(eventService.getEventList(path.name(), page, size));
    }

}

package com.talearnt.post.service;

import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.service.CreateService;
import com.talearnt.service.DeleteService;
import com.talearnt.service.ReadService;
import com.talearnt.service.UpdateService;

public interface PostService<T> extends CreateService<T, String>
        , UpdateService<T, String>
        , ReadService<Long, ExchangePostReadResDTO>, DeleteService<Long,String> {

}

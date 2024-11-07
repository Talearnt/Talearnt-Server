package com.talearnt.post.service;

import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.service.*;

public interface PostService<T> extends CreateService<T, String>
        , UpdateService<T, String>
        , ReadService<Long, ExchangePostReadResDTO>
        , DeleteService<Long,String>
        , ListService<Integer, ExchangePostListResDTO> {

}

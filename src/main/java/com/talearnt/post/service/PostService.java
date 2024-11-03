package com.talearnt.post.service;

import com.talearnt.post.exchange.request.ExchangePostCreateReqDTO;
import com.talearnt.post.exchange.request.ExchangePostUpdateReqDTO;
import com.talearnt.service.CreateService;
import com.talearnt.service.UpdateService;

public interface PostService<C,U> extends CreateService<C,String>
                                    , UpdateService<U,String>{

}

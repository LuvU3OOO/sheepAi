package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.chatdto.ApiKey;
import com.example.entity.dto.Account;

public interface ApiKeyService extends IService<ApiKey> {

    String getValidApikey();

    boolean updateApikeyStatus(String apikey,int isValid);

    boolean addApiKey(String apikey);

    boolean updateIsUsing(String apiKey);

}

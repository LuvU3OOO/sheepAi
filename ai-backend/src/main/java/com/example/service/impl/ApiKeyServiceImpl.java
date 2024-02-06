package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.chatdto.ApiKey;
import com.example.mapper.ApikeyMapper;
import com.example.service.ApiKeyService;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApikeyMapper, ApiKey> implements ApiKeyService {

    @Override
    public String getValidApikey() {

        ApiKey validApiKey = this.query().eq("is_valid", 1).last("LIMIT 1").one();

        if (validApiKey != null) {
            this.update().eq("apikey",validApiKey.getApikey()).set("is_using",1).update();
            return validApiKey.getApikey();
        } else {
            return null;
        }
    }

    @Override
    public boolean updateApikeyStatus(String apikey,int isValid) {
        return this.update().eq("apikey",apikey).set("is_valid",isValid).update();
    }

    @Override
    public boolean addApiKey(String apikey) {
        return false;
    }

    @Override
    public boolean updateIsUsing(String apiKey) {

        return this.update().eq("apikey",apiKey).set("is_using",0).update();
    }
}

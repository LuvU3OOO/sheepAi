package com.example.utils;

import com.example.config.ChatGptConfiguration;
import com.example.service.ApiKeyService;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.utils.TikTokensUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class ChatUtils {

    @Resource
    ApiKeyService apiKeyService;

    @Resource
    ChatGptConfiguration chatGptConfiguration;

    private long timeout = 60;

    public  int CalculateToken(List<ChatMessage> messages){

        return TikTokensUtil.tokens(TikTokensUtil.ModelEnum.GPT_3_5_TURBO.getName(), messages);
    }

    public  int CalculateToken(String message){
        return TikTokensUtil.tokens(TikTokensUtil.ModelEnum.GPT_3_5_TURBO.getName(), message);
    }

    public OpenAiService ChangeApikey(){
        String apikey = chatGptConfiguration.getToken();
        System.out.println("apikey:"+apikey+"已废弃");
        apiKeyService.updateApikeyStatus(apikey,0);
        apiKeyService.updateIsUsing(apikey);
        String token = apiKeyService.getValidApikey();
        chatGptConfiguration.setToken(token);
        return new OpenAiService(token,Duration.ofSeconds(timeout));
    }
}

package com.example.utils;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.utils.TikTokensUtil;
import org.springframework.stereotype.Component;
import com.theokanning.openai.utils.*;

import java.util.List;

@Component
public class ChatUtils {

    public  int CalculateToken(List<ChatMessage> messages){

        return TikTokensUtil.tokens(TikTokensUtil.ModelEnum.GPT_3_5_TURBO.getName(), messages);
    }

    public  int CalculateToken(String message){
        return TikTokensUtil.tokens(TikTokensUtil.ModelEnum.GPT_3_5_TURBO.getName(), message);
    }
}

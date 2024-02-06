package com.example.config;

import com.example.service.ApiKeyService;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;

@Configuration
public class ChatGptConfiguration {

    @Resource
    ApiKeyService apiKeyService;

    @Getter
    private String token;

    private long timeout = 60;

    @Bean
    public OpenAiService openAiService(){

//        ObjectMapper mapper = defaultObjectMapper();
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",33210));
//        OkHttpClient client = defaultClient(token, Duration.ofSeconds(60))
//                .newBuilder()
//                .proxy(proxy)
//                .build();
//        Retrofit retrofit = defaultRetrofit(client, mapper);
//        OpenAiApi api = retrofit.create(OpenAiApi.class);

        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "33210");

        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "33210");

        token= apiKeyService.getValidApikey();

        return new OpenAiService(token, Duration.ofSeconds(timeout));
    }


    public void setToken(String newToken) {
        this.token = newToken;
    }

}

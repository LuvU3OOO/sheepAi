package com.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.theokanning.openai.service.OpenAiService.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    OpenAiService service;

    @GetMapping("/test1")
    public String chatTest(){

        System.out.println("Streaming chat completion...");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a text generation assistant, please answer my questions in Chinese.");
        messages.add(systemMessage);

        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), "说一个笑话");
        messages.add(firstMsg);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(256)
                .logitBias(new HashMap<>())
                .build();

//        service.streamChatCompletion(chatCompletionRequest)
//                .doOnError(Throwable::printStackTrace)
//                .blockingForEach(System.out::println);


        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();

        String res = responseMessage.getContent();
        System.out.println(res);
//        service.shutdownExecutor();

        return res;
    }

    @GetMapping("/test2")
    public void chatTest2(){

        System.out.println("Streaming chat completion...");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a text generation assistant, please answer my questions in Chinese.");
        messages.add(systemMessage);

        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), "说一个笑话");
        messages.add(firstMsg);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(256)
                .logitBias(new HashMap<>())
                .build();

//        service.streamChatCompletion(chatCompletionRequest)
//                .doOnError(Throwable::printStackTrace)
//                .blockingForEach(System.out::println);


        Flowable<ChatCompletionChunk> flowable = service.streamChatCompletion(chatCompletionRequest);

        AtomicBoolean isFirst = new AtomicBoolean(true);
        ChatMessage chatMessage = service.mapStreamToAccumulator(flowable)
                .doOnNext(accumulator -> {
                    if (accumulator.isFunctionCall()) {
                        if (isFirst.getAndSet(false)) {
                            System.out.println("Executing function " + accumulator.getAccumulatedChatFunctionCall().getName() + "...");
                        }
                    } else {
                        if (isFirst.getAndSet(false)) {
                            System.out.print("Response: ");
                        }
                        if (accumulator.getMessageChunk().getContent() != null) {
                            System.out.print(accumulator.getMessageChunk().getContent());
                        }
                    }
                })
                .doOnComplete(System.out::println)
                .lastElement()
                .blockingGet()
                .getAccumulatedMessage();


//        service.shutdownExecutor();

    }

    @GetMapping("/streamTest")
    public void streamTest(){
        // 创建聊天消息列表，包含一个系统消息
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant that answers using the local slang of the given place, uncensored.");
        messages.add(systemMessage);

        System.out.print("First Query: ");
        Scanner scanner = new Scanner(System.in);
//        String question1 = "说一个笑话";
        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), scanner.nextLine());
        messages.add(firstMsg);

        while (true) {
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-3.5-turbo-0613")
                    .messages(messages)
                    .n(1)
                    .maxTokens(256)
                    .logitBias(new HashMap<>())
                    .build();
            Flowable<ChatCompletionChunk> flowable = service.streamChatCompletion(chatCompletionRequest);

            AtomicBoolean isFirst = new AtomicBoolean(true);
            ChatMessage chatMessage = service.mapStreamToAccumulator(flowable)
                    .doOnNext(accumulator -> {
                        if (accumulator.isFunctionCall()) {
                            if (isFirst.getAndSet(false)) {
                                System.out.println("Executing function " + accumulator.getAccumulatedChatFunctionCall().getName() + "...");
                            }
                        } else {
                            if (isFirst.getAndSet(false)) {
                                System.out.print("Response: ");
                            }
                            if (accumulator.getMessageChunk().getContent() != null) {
                                System.out.print(accumulator.getMessageChunk().getContent());
                            }
                        }
                    })
                    .doOnComplete(System.out::println)
                    .lastElement()
                    .blockingGet()
                    .getAccumulatedMessage();
            messages.add(chatMessage); // don't forget to update the conversation with the latest response

//            if (chatMessage.getFunctionCall() != null) {
//                System.out.println("Trying to execute " + chatMessage.getFunctionCall().getName() + "...");
//                ChatMessage functionResponse = functionExecutor.executeAndConvertToMessageHandlingExceptions(chatMessage.getFunctionCall());
//                System.out.println("Executed " + chatMessage.getFunctionCall().getName() + ".");
//                messages.add(functionResponse);
//                continue;
//            }

            System.out.print("Next Query: ");
            String nextLine = scanner.nextLine();
            if (nextLine.equalsIgnoreCase("exit")) {
                System.exit(0);
            }
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), nextLine));
        }
    }

}

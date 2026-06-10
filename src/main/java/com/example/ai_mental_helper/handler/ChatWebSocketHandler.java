package com.example.ai_mental_helper.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.ai_mental_helper.component.SensitiveWordFilter;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.Disposable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 16-19. 核心 WebSocket 处理器
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private OpenAiChatModel chatModel;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    private static final ConcurrentHashMap<String, Disposable> chatSubscribers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("【WebSocket】新用户连接成功，Session ID: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        if (JSONUtil.isTypeJSON(payload)) {
            JSONObject json = JSONUtil.parseObj(payload);
            String type = json.getStr("type");
            String sessionId = json.getStr("sessionId");

            if ("CHAT".equals(type)) {
                String content = json.getStr("content");
                handleChatAction(session, sessionId, content);
            } else if ("STOP".equals(type)) {
                handleStopAction(session, sessionId);
            }
        }
    }

    private void handleChatAction(WebSocketSession session, String sessionId, String content) throws IOException {
        String cleanContent = sensitiveWordFilter.filterText(content);
        SystemMessage systemMessage = new SystemMessage("你是一位极其温柔、具有深厚心理学背景的心理咨询导师。请用倾听、共情、温暖的语气回复用户的倾诉，字数简练，每次回答不要超过150字。");
        UserMessage userMessage = new UserMessage(cleanContent);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // ⚠️ 请用这段代码替换原有的 chatModel.stream(prompt) 到 subscribe 之前的部分
        Disposable disposable = chatModel.stream(prompt)
                // 1. 核心过滤：如果响应为空，或者里面没有结果、没有内容，直接过滤掉，防止传递 null 导致崩溃
                .filter(response -> response != null &&
                        response.getResult() != null &&
                        response.getResult().getOutput() != null &&
                        response.getResult().getOutput().getContent() != null)
                // 2. 此时 getContent() 绝对不为 null，可以安全转换
                .map(response -> response.getResult().getOutput().getContent())
                .subscribe(
                        textChunk -> {
                            if (session.isOpen()) {
                                try {
                                    JSONObject resp = new JSONObject();
                                    resp.set("type", "CHUNK");
                                    resp.set("content", textChunk);
                                    session.sendMessage(new TextMessage(resp.toString()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        error -> {
                            System.err.println("AI 生成出错: " + error.getMessage());
                            error.printStackTrace(); // 打印详细日志方便排查
                        },
                        () -> {
                            if (session.isOpen()) {
                                try {
                                    JSONObject resp = new JSONObject();
                                    resp.set("type", "DONE");
                                    session.sendMessage(new TextMessage(resp.toString()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            chatSubscribers.remove(sessionId);
                            System.out.println("【WebSocket】会话 " + sessionId + " AI 吐字正常结束。");
                        }
                );

        chatSubscribers.put(sessionId, disposable);
    }

    private void handleStopAction(WebSocketSession session, String sessionId) throws IOException {
        if (sessionId != null && chatSubscribers.containsKey(sessionId)) {
            Disposable disposable = chatSubscribers.get(sessionId);
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            chatSubscribers.remove(sessionId);

            if (session.isOpen()) {
                JSONObject resp = new JSONObject();
                resp.set("type", "STOPPED");
                session.sendMessage(new TextMessage(resp.toString()));
            }
            System.out.println("【⚠️控流警告】用户主动点击停止生成！会话 " + sessionId + " 订阅流已强制销毁！");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        System.out.println("【WebSocket】用户连接断开，Session ID: " + session.getId());
    }
}
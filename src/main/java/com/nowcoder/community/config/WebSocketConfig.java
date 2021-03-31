package com.nowcoder.community.config;

import com.nowcoder.community.controller.websocket.endpoint.LetterNoticeWebSocketEndPoint;
import com.nowcoder.community.controller.websocket.handler.LetterNoticeWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket支持
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
    /*
        注册监听路径
     */
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(letterNoticeWebSocketHandler(), "/letternotice");
//        //registry.addHandler(letterNoticeWebSocketHandler(), "/letternotice_SockJS").withSockJS(); //浏览器不支持WebSocket时使用SockJS
//    }
//
//    /*
//        通过继承 {@link org.springframework.web.socket.handler.AbstractWebSocketHandler} 实现事件处理的Bean
//     */
//    @Bean
//    public LetterNoticeWebSocketHandler letterNoticeWebSocketHandler(){
//        return new LetterNoticeWebSocketHandler();
//    }


//    @Bean
//    public LetterNoticeWebSocketEndPoint reverseWebSocketEndpoint() {
//        return new LetterNoticeWebSocketEndPoint();
//    }
}



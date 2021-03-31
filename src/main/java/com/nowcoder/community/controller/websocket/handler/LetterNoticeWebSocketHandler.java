package com.nowcoder.community.controller.websocket.handler;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class LetterNoticeWebSocketHandler extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket connection closed.");
        super.afterConnectionClosed(session, status);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Opened new session in instance " + this);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //组装返回的信息
        logger.info(MessageFormat.format("message \"{0}\"", message));
        Map<String, Object> map = session.getAttributes();
        session.sendMessage(new TextMessage(CommunityUtil.getJSONString(1, "新消息提醒", map)));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
        logger.info("WebSocket connection error and closed.");
    }
}

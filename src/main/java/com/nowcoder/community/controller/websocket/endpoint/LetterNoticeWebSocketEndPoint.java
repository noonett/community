package com.nowcoder.community.controller.websocket.endpoint;

import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
    新消息提醒的订阅
 */
@ServerEndpoint("/letternotice/{sid}")
@Component
public class LetterNoticeWebSocketEndPoint {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // 存所有客户端的对象
    private static ConcurrentHashMap<Integer, LetterNoticeWebSocketEndPoint> webSockets = new ConcurrentHashMap<>();

    // 用户ID，连接会话的唯一标识
    private int sid;

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /***
     * 提醒 toId 用户有新消息到来
     * @param toId  消息接受方
     * @param name  消息发送方
     * @param content
     * @throws IOException
     */
    public static void notifyNewLetter(int toId, String name, String content) throws IOException {
        if (webSockets.containsKey(toId)) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("content", content);
            webSockets.get(toId).sendMessage(CommunityUtil.getJSONString(1, "新消息提醒！", map));
        }
    }

    // 成员方法：收消息
    @OnMessage
    public void onMessage(String message) {
        logger.info("收到来自用户[" + sid + "]的信息:" + message);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") int sid) {
        this.session = session;
        logger.info("有新窗口开始监听:" + sid);
        this.sid = sid;
        webSockets.put(this.sid, this);   // 加入集合中
        try {
            sendMessage(CommunityUtil.getJSONString(0, "连接成功！"));
        } catch (IOException e) {
            logger.error("websocket IO异常");
        }
    }

    /*
        这里一定要有处理异常的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("WebSocket发生错误!");
        error.printStackTrace();
    }

    @OnClose
    public void onClose() {
        if (webSockets.containsKey(this.sid)) {
            webSockets.remove(this.sid);  //从set中删除
            logger.info("有一连接关闭:" + sid);
        }
    }

    // 成员方法：向某客户端session推消息
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

}

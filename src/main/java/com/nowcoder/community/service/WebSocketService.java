package com.nowcoder.community.service;

import com.nowcoder.community.controller.websocket.endpoint.LetterNoticeWebSocketEndPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void cacheSession(LetterNoticeWebSocketEndPoint ws){
        redisTemplate.opsForValue().set("test", ws);
    }
}

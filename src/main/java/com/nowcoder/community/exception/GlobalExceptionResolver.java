package com.nowcoder.community.exception;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GlobalExceptionResolver implements HandlerExceptionResolver {

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        ModelAndView model = new ModelAndView();
        if(e != null && e instanceof RatelimiterException){
            try {
                String xRequestedWith = request.getHeader("x-requested-with");
                String msg = "您访问的服务快挤爆了，请稍后再试！";
                if ("XMLHttpRequest".equals(xRequestedWith)) {
                    //异步请求
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(CommunityUtil.getJSONString(403, msg));
                } else {
                    model.addObject("msg", msg);
                    model.addObject("target", "/index");
                    model.setViewName("forward:/site/operate-result");
                }
            } catch (IOException ioException) {
                logger.error("转发错误" + ioException.getMessage());
            }
        }else{
            e.printStackTrace();
        }
        return model;
    }
}

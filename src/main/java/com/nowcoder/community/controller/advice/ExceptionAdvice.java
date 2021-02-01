package com.nowcoder.community.controller.advice;

import com.nowcoder.community.exception.RatelimiterException;
import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({RatelimiterException.class})
    public ModelAndView handleRatelimiterException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException{
        if (e != null && e instanceof RatelimiterException) {
            String xRequestedWith = request.getHeader("x-requested-with");
            String msg = "您访问的链接快被挤爆啦！请稍后再试！";
            if ("XMLHttpRequest".equals(xRequestedWith)) {
                //异步请求
                response.setContentType("application/plain;charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(CommunityUtil.getJSONString(1, msg));
            } else {
                ModelAndView model = new ModelAndView();
                model.addObject("msg", msg);
                model.addObject("target", "/index");
                model.setViewName("/site/operate-result");
                return model;
            }
        }
        return null;
    }

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }


        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}

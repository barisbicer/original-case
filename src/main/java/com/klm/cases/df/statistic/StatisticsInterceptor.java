package com.klm.cases.df.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.DispatcherType.REQUEST;

public class StatisticsInterceptor  {

    @Autowired
    private StatisticsBean statisticBean;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getAttribute("startTime") == null) {
            request.setAttribute("startTime", System.currentTimeMillis());
        }

        return true;
    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView)
            throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long executeTime = System.currentTimeMillis() - startTime;

        statisticBean.addRequestReport(new RequestReport(executeTime, response.getStatus()));

    }
}

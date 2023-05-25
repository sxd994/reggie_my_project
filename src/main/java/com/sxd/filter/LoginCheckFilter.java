package com.sxd.filter;


import com.alibaba.fastjson.JSON;
import com.sxd.common.BaseContext;
import com.sxd.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/*
检查用户是否完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        //获取本次请求的uri
        String requestURI = request.getRequestURI();

        //定义不需要处理的请求路径
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //若是不需要处理
        if(check){
            filterChain.doFilter(request, response);
            return;
        }

        //4-1 判断登录状态，若是已登录，就放行
        if(request.getSession().getAttribute("employee") != null){

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        };

        //4-2 判断登录状态，若是已登录，就放行
        if(request.getSession().getAttribute("user") != null){

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        };
        //若是没有登录,通过输出流的方式向客户端界面响应数据

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            if(PATH_MATCHER.match(url, requestURI)){
                return true;
            }
        }
        return false;
    }
}

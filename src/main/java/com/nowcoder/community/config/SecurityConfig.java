package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstants;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstants {

    // 忽略路径下的资源 - 静态资源
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    // 认证
    // AuthenticationManager：认证的核心接口
    // AuthenticationManager：用于构建AuthenticationManager对象的工具
    // ProvideManager: AuthenticationManager默认的实现类
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // 内置的认证规则, 自动完成认证，加盐
//        //auth.userDetailsService(userService).passwordEncoder(new Pbkdf2PasswordEncoder("12345"));
//
//        // 自定义认证规则
//        // AuthenticationProvider: ProviderManager持有一组AuthenticationProvider,每个AuthenticationProvider负责一种认证
//        // 多种登陆方式，为了兼容多种登陆方式，
//        // 委托模式：ProviderManager将认证委托一组AuthenticationProvider来负责
//        auth.authenticationProvider(new AuthenticationProvider() {
//
//            @Override
//            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                // Authentication: 用于封装认证信息的接口，不同的实现类代表不同类型的认证信息。
//                // 这里写认证逻辑
//                String username = authentication.getName();
//                String password =(String) authentication.getCredentials();
//
//                User user = userService.findUserByName(username);
//                if(user == null){
//                    throw new UsernameNotFoundException("账号不存在！");
//                }
//
//                password = CommunityUtil.md5(password + user.getSalt());
//                if(!user.getPassword().equals(password)){
//                    throw new BadCredentialsException("密码不正确！");
//                }
//
//                // principal: 主要信息， credentials：证书， authorities：权限
//                return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
//            }
//
//            // 体现AuthenticationProvider当前支持的认证类型
//            @Override
//            public boolean supports(Class<?> aClass) {
//                // UsernamePasswordAuthenticationToken：Authentication接口常用的实现类，表示当前是支持账号密码登陆
//                return UsernamePasswordAuthenticationToken.class.equals(aClass);
//            }
//        });
//    }

    // 授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        // 登陆相关配置
//        http.formLogin()
//                .loginPage("/loginpage")
//                .loginProcessingUrl("/login")
//                // 跳转
//                //.successForwardUrl("")
//                //.failureForwardUrl("")
//                // 进行额外的处理，如传数据
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        response.sendRedirect(request.getContextPath() + "/index");
//                    }
//                })
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//                        request.setAttribute("error", e.getMessage());
//                        request.getRequestDispatcher("/loginpage").forward(request, response);
//                    }
//                });
//
//        // 退出相关的配置
//        http.logout()
//                .logoutUrl("/logout")
//                .logoutSuccessHandler(new LogoutSuccessHandler() {
//                    @Override
//                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        response.sendRedirect(request.getContextPath() + "/index");
//                    }
//                });
//
//        // 增加Filter，处理验证码
//        http.addFilterBefore(new Filter() {
//            @Override
//            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//                HttpServletRequest request = (HttpServletRequest) servletRequest;
//                HttpServletResponse response = (HttpServletResponse) servletResponse;
//                if(request.getServletPath().equals("/login")){
//                    String verifyCode = request.getParameter("verifyCode");
//                    // 这里是默认了，可以修改
//                    if(verifyCode == null || !verifyCode.equalsIgnoreCase("1234")){
//                        request.setAttribute("error", "验证码错误！");
//                        request.getRequestDispatcher("/loginpage").forward(request, response);
//                        return;
//                    }
//                }
//                // 让请求继续向下执行
//                filterChain.doFilter(request, response);
//            }
//            // 早这个filter之前的filter
//        }, UsernamePasswordAuthenticationFilter.class);
//
//        // 记住我
//        http.rememberMe()
//                .tokenRepository(new InMemoryTokenRepositoryImpl())
//                .tokenValiditySeconds(3600 * 24)
//                // 查用户的完整信息
//                .userDetailsService(userService);
        // 授权的配置
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/delete",
                        "/data/**",
                        "/actuator/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                //禁用CSRF
                .anyRequest().permitAll()
//                .and().sessionManagement().invalidSessionUrl("/index")
                .and().csrf().disable();
        // 权限不够时的处理
        http.exceptionHandling()
                // 没有登陆时的处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            //异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你还没有登陆哦！"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                // 权限不足时的处理
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            //异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限！"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });
        // Security底层默认拦截/logout请求，进行退出处理
        // 覆盖默认的逻辑，才能执行我们自己的退出代码
        http.logout().logoutUrl("/securitylogout");
    }

}

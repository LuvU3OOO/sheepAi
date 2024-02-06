package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.AccountService;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    JwtUtils jwtUtils;

    @Resource
    AccountService service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**","/swagger-ui/**","/v3/api-docs/**","/ws/**","/ws2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::handleProcess)
                        .failureHandler(this::handleProcess)
                )
                .logout(conf ->{
                    conf.logoutUrl("/api/auth/logout");
                    conf.logoutSuccessHandler(this::onLogoutSuccess);
                })
                .exceptionHandling(conf ->{
                    conf.accessDeniedHandler(this::handleProcess);
                    conf.authenticationEntryPoint(this::handleProcess);
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf->conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  //jwt方案，不记录session
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }


    private void handleProcess(HttpServletRequest request,
                               HttpServletResponse response,
                               Object exceptionOrAuthentication) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        if(exceptionOrAuthentication instanceof AccessDeniedException exception) {
            writer.write(RestBean.failure(403, exception.getMessage()).asJsonString());
        } else if(exceptionOrAuthentication instanceof Exception exception) {
            writer.write(RestBean.failure(401, exception.getMessage()).asJsonString());
        } else if(exceptionOrAuthentication instanceof Authentication authentication){
            //不过这里需要注意，在登录成功的时候需要返回我们生成的JWT令牌，这样客户端下次访问就可以携带这个令牌了，令牌过期之后就需要重新登录才可以
            User user = (User) authentication.getPrincipal();
            Account account = service.findAccountByNameOrEmail(user.getUsername());

            String token = jwtUtils.createJwt(user,account.getId(),account.getUsername());
//            AuthorizeVO vo  = new AuthorizeVO();
//            BeanUtils.copyProperties(account,vo);
            AuthorizeVO vo = account.asViewObject(AuthorizeVO.class,v->{
                v.setExpire(jwtUtils.expireTime());
                v.setToken(token);
                v.setId(account.getId());
                v.setRole(account.getRole());
            });
//            vo.setExpire(jwtUtils.expireTime());
//            vo.setRole(account.getRole());
//            vo.setToken(token);
//            vo.setUsername(account.getUsername());
            writer.write(RestBean.success(vo).asJsonString());
        }
    }

    private void onLogoutSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if(jwtUtils.invalidateJwt(authorization)) {
            writer.write(RestBean.success("退出登录成功").asJsonString());
            return;
        }
        writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
    }



}

package com.naxi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors() // 关键：利用你定义的 CorsConfig 里的 Bean
                .and()
                .csrf().disable() // 禁用 CSRF，否则 POST 请求会被拦截
                .authorizeRequests()
                .anyRequest().permitAll(); // 允许所有路径访问
    }
}
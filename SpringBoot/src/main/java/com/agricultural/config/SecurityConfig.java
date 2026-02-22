package com.agricultural.config;

import com.agricultural.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security安全配置类
 * 配置认证、授权、CORS和JWT过滤器
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * 密码编码器Bean
     * 使用BCrypt算法进行密码加密
     * 
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 认证管理器Bean
     * 
     * @param config 认证配置
     * @return AuthenticationManager
     * @throws Exception 异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * CORS配置源Bean
     * 配置跨域资源共享
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:8080"
        ));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许发送凭证（Cookie）
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("CORS配置已初始化");
        return source;
    }
    
    /**
     * 安全过滤链Bean
     * 配置HTTP安全策略
     * 
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 启用CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 禁用CSRF（因为使用JWT）
                .csrf(csrf -> csrf.disable())
                
                // 设置会话管理策略为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 配置授权规则
                .authorizeHttpRequests(authz -> authz
                        // 允许的公开端点
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/check/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/weather/current").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/weather/forecast").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/weather/history").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/warnings").permitAll()
                        
                        // 管理员专用端点
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/weather/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/weather/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/weather/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/warnings/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/warnings/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/warnings/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/analytics/**").hasAnyRole("ADMIN")
                        
                        // 商家专用端点
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("MERCHANT", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("MERCHANT", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("MERCHANT", "ADMIN")
                        
                        // 农户和商家可以查看订单
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("FARMER", "MERCHANT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("FARMER", "MERCHANT", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAnyRole("FARMER", "MERCHANT", "ADMIN")
                        
                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )
                
                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        log.info("Spring Security配置已初始化");
        return http.build();
    }
}

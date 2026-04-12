package com.travelMemory.config;

import com.travelMemory.security.InputValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuration for security headers.
 * Adds HTTP security headers to all responses to prevent various attacks.
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    @Autowired
    private InputValidationInterceptor inputValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        // 定义不需要拦截的静态资源路径
        String[] excludePatterns = new String[]{"/uploads/**", "/image/**", "/static/**"};


          // 添加输入验证拦截器，但排除图片路径
        registry.addInterceptor(inputValidationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePatterns); // <--- 关键修改

        // 安全头拦截器通常可以保留在静态资源上，但在某些浏览器跨域场景下也可以排除
        registry.addInterceptor(new SecurityHeadersInterceptor())
                .addPathPatterns("/**")
         .excludePathPatterns(excludePatterns); // 可选
    }


    @Value("${file.upload-dir:src/main/resources/static/image}")
    private String uploadDir;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置图片资源访问
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600); // 缓存时间1小时

        String path = System.getProperty("user.dir") + "/uploads/";
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + path);

        // 打印日志方便调试，确认最终路径是对的
        System.out.println("Uploads path mapping: " + path);
    }

   /* @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射规则：当访问 /uploads/** 时，映射到本地磁盘的 uploads 目录

//        // 这里的 "file:uploads/" 指的是项目根目录下的 uploads 文件夹
//        // 如果是绝对路径（如 D:/project/uploads/），请改为 "file:D:/project/uploads/"
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:uploads/");

        // 配置图片资源访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600); // 缓存时间1小时

        String path = System.getProperty("user.dir") + "/uploads/";
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + path);
    }*/

    /**
     * Interceptor that adds security headers to HTTP responses.
     */
    public static class SecurityHeadersInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            // Prevent clickjacking attacks
            response.setHeader("X-Frame-Options", "DENY");

            // Prevent MIME type sniffing
            response.setHeader("X-Content-Type-Options", "nosniff");

            // Enable XSS protection in older browsers
            response.setHeader("X-XSS-Protection", "1; mode=block");

            // Content Security Policy - restricts resource loading
            response.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self'; " +
                    "connect-src 'self' https:; " +
                    "frame-ancestors 'none'; " +
                    "base-uri 'self'; " +
                    "form-action 'self'");

            // Referrer Policy - controls referrer information
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // Feature Policy / Permissions Policy
            response.setHeader("Permissions-Policy", 
                    "geolocation=(), " +
                    "microphone=(), " +
                    "camera=(), " +
                    "payment=(), " +
                    "usb=(), " +
                    "magnetometer=(), " +
                    "gyroscope=(), " +
                    "accelerometer=()");

            // Strict Transport Security (HSTS) - enforce HTTPS
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");

            return true;
        }
    }
}

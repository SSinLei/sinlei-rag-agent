package com.sinlei.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 全局跨域配置类
 *
 * 解决前后端分离项目中的跨域问题
 * 跨域是指浏览器允许向其他域名发起请求
 *
 * 配置说明：
 * 1. allowCredentials(true): 允许携带认证信息（如cookie）
 * 2. addAllowedOriginPattern("*"): 允许所有来源的请求
 * 3. addAllowedHeader("*"): 允许所有请求头
 * 4. addAllowedMethod("*"): 允许所有HTTP方法（GET、POST、PUT、DELETE等）
 * 5. setMaxAge(3600L): 预检请求（OPTIONS）的缓存时间为3600秒
 *
 * 注意事项：
 * - 生产环境建议将 "*" 改为具体的域名，如 "https://example.com"
 * - 如果需要携带cookie，allowedOrigin不能使用 "*"，必须指定具体域名
 */
@Configuration
public class CorsConfig {

    /**
     * 创建CorsFilter Bean
     * Spring会自动注册此过滤器到所有请求中
     *
     * @return CorsFilter 实例
     */
    @Bean
    public CorsFilter corsFilter() {
        // 创建CORS配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 创建CORS配置对象
        CorsConfiguration config = new CorsConfiguration();

        // 允许携带认证信息（如cookie）
        config.setAllowCredentials(true);

        // 允许所有来源（生产环境建议改为具体域名）
        config.addAllowedOriginPattern("*");

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有HTTP方法
        config.addAllowedMethod("*");

        // 预检请求缓存时间（秒），减少OPTIONS请求次数
        config.setMaxAge(3600L);

        // 对所有路径应用此CORS配置
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}

package cn.example.advertisement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域问题
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173","http://localhost:5174",
                        "http://10.100.164.30:5173","http://10.100.164.30:5174",
                        "http://10.100.164.31:5173","http://10.100.164.31:5174",
                        "http://10.100.164.32:5173","http://10.100.164.32:5174",
                        "http://10.100.164.33:5173","http://10.100.164.33:5174",
                        "http://10.100.164.30:8080","http://10.100.164.30:8081",
                        "http://10.100.164.31:8080","http://10.100.164.31:8081",
                        "http://10.100.164.32:8080","http://10.100.164.32:8081",
                        "http://10.100.164.33:8080","http://10.100.164.33:8081",
                        "http://localhost:8080","http://localhost:8081")  // 前端开发服务器地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加静态资源映射
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:images/");  // 映射到项目根目录下的 images 文件夹

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");  // 映射到项目根目录下的 uploads 文件夹
    }
}

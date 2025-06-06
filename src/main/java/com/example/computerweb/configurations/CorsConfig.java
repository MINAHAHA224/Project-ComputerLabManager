// computerweb/configurations/CorsConfig.java

package com.example.computerweb.configurations;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // --- CÁC THAY ĐỔI QUAN TRỌNG Ở ĐÂY ---
        config.setAllowCredentials(true);
        // Thay vì addAllowedOrigin, hãy dùng setAllowedOriginPatterns
        // Nó cho phép các pattern linh hoạt hơn và hoạt động tốt với credentials
        config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:[*]")); // Cho phép tất cả các cổng trên localhost
        // Hoặc chỉ định rõ ràng nếu bạn muốn an toàn hơn:
        // config.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173", "http://localhost:5174"));

        config.addAllowedHeader("*"); // Hoặc chỉ định rõ các header cần thiết
        config.addAllowedMethod("*"); // Hoặc chỉ định rõ các method: GET, POST, PUT, DELETE, OPTIONS
        // --- KẾT THÚC THAY ĐỔI ---

        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }
}
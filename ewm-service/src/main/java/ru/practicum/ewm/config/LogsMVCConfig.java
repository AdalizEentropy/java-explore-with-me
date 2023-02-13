package ru.practicum.ewm.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.ewm.log.LogsInterceptor;

@Configuration
@RequiredArgsConstructor
public class LogsMVCConfig implements WebMvcConfigurer {
    private final LogsInterceptor logsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logsInterceptor);
    }
}

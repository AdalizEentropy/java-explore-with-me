package ru.practicum.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stat.client.StatClient;
import ru.practicum.stat.client.StatClientImpl;

@Configuration
public class StatClientConfiguration {

    @Bean
    StatClient statClient(@Value("${ewm-stat-server.url}") String serverUrl) {
        return new StatClientImpl(WebClient.create(serverUrl));
    }
}

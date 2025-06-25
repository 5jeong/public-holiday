package com.holidayproject.global.config;


import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();

        // text/json 추가
        List<MediaType> mediaTypes = new ArrayList<>(jacksonConverter.getSupportedMediaTypes());
        mediaTypes.add(MediaType.valueOf("text/json"));
        jacksonConverter.setSupportedMediaTypes(mediaTypes);

        return RestClient.builder()
                .messageConverters(List.of(jacksonConverter))
                .build();    }
}
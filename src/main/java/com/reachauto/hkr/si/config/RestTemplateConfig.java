package com.reachauto.hkr.si.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reachauto.hkr.common.config.ObjectMapperHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang.shuo
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    StringHttpMessageConverter stringConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    @Bean
    ResourceHttpMessageConverter resourceConverter() {
        return new ResourceHttpMessageConverter();
    }

    @Bean
    MappingJackson2HttpMessageConverter jacksonConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = ObjectMapperHolder.getInstance().getNewMapper();
        converter.setObjectMapper(mapper);
        return converter;
    }

    @Bean
    public RestTemplate restTemplate() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(resourceConverter());
        converters.add(jacksonConverter());
        converters.add(stringConverter());
        return new RestTemplate(converters);
    }
}

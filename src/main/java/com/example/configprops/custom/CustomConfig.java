package com.example.configprops.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CustomProperties.class)
@RequiredArgsConstructor
public class CustomConfig {

    private final CustomProperties customProperties;
}

package com.example.configprops.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
@PropertySource("classpath:custom/setting.yml")
@RequiredArgsConstructor
public class OauthConfig {

    private final AuthProperties authProperties;
}

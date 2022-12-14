package com.example.configprops.custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.boot.context.properties.ConstructorBinding;



@ConfigurationProperties(prefix = "custom")
@RequiredArgsConstructor
@ConstructorBinding
@Getter
public class CustomProperties {

    private final Integer id;
    private final String userName;
    private final String email;


}

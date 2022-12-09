package com.example.configprops.custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private final Map<String, OauthProperties> provider = new HashMap<>();

    public OauthProperties getOauthProperty(String key) {
        return provider.get(key);
    }

    @ConstructorBinding
    @RequiredArgsConstructor
    @Getter
    public static class OauthProperties {

        private final String clientId;
        private final String clientSecret;
        private final String accessTokenUri;
        private final String userInfoUri;

    }
}

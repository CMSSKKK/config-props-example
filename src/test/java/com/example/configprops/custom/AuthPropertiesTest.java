package com.example.configprops.custom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class AuthPropertiesTest {

    @Autowired
    private AuthProperties authProperties;

    @Test
    void checkSetGithubPropertiesTest() {

        String key = "github";
        AuthProperties.OauthProperties githubProperties = authProperties.getOauthProperty(key);

        assertThat(githubProperties.getClientId()).isEqualTo("github_client_id");
        assertThat(githubProperties.getClientSecret()).isEqualTo("github_client_secret");
        assertThat(githubProperties.getAccessTokenUri()).isEqualTo("http://localhost:9090/login/oauth/access_token");
        assertThat(githubProperties.getUserInfoUri()).isEqualTo("http://localhost:9090/user,http://localhost:9090/user/emails");
    }

    @Test
    void checkSetNaverPropertiesTest() {

        String key = "naver";
        AuthProperties.OauthProperties naverProperties = authProperties.getOauthProperty(key);

        assertThat(naverProperties.getClientId()).isEqualTo("naver_client_id");
        assertThat(naverProperties.getClientSecret()).isEqualTo("naver_client_secret");
        assertThat(naverProperties.getAccessTokenUri()).isEqualTo("http://localhost:9090/oauth2.0/token");
        assertThat(naverProperties.getUserInfoUri()).isEqualTo("http://localhost:9090/v1/nid/me");
    }
}

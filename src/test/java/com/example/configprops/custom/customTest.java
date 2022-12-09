package com.example.configprops.custom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class customTest {

    @Autowired
    private CustomProperties customProperties;

    @Test
    void settingTest() {
        assertThat(customProperties.getId()).isEqualTo(1);
        assertThat(customProperties.getUserName()).isEqualTo("aaron2");
        assertThat(customProperties.getEmail()).isEqualTo("cms05041@gmail.com");
    }
}

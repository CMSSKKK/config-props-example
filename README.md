# @ConfigurationProperties 설정 예제

## 

- SpringBoot에서 [application.propertie](http://application.properties)s(or .yml)와 같은 설정 정보를 활용하거나 Bean으로 등록하는 방법에 대해서 경험하고 정리한 글입니다.
- 외부 API와 통신 또는 설정 정보, DB사용을 위한 Connection 세팅, 민감정보등을 Bean으로 등록하고 사용하는 방법들에 대해서 정리합니다.

## 1. @Value

- 최근 Redis를 Spring과 연동하여 사용하는 것을 학습하고 있습니다.
  - 의존성 세팅
  - `implementation 'org.springframework.boot:spring-boot-starter-data-redis'`
- Redis Connection을 사용하기위해서 가장 기본적으로 host와 port를 필요로합니다.
- 사용하기 위한 설정에 대해서 정말 많은 블로그에 코드가 정리되어있습니다.
- 많은 블로그 중 대부분의 코드가 application.yml에 host와 port를 설정하고, @Value 어노테이션으로 정보를 가져옵니다.
- 이렇게 Spring boot를 사용하면 PropertySource를 default로 application.properties를 참고하기에 아래와 같이 정보를 설정합니다.

```yaml
# application.yml

spring:
  redis:
    host: 127.0.0.1
    port: 6379
```

```java
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisConfig {
		
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("host={}, port={}", host, port);
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

} 
```

- @Value 어노테이션을 활용해서 yml의 설정 key값을 바탕으로 value를 자동으로 넣어줍니다.

## 2. @ConfigurationProperties

- Spring boot에서 저희가 설정해주는 application.yml 설정정보는 해당 어노테이션을 통해서 묶어서 Bean으로 설정합니다.

  - `~Properties` 와 같은 이름을 가진 클래스의 대부분에 @ConfigurationProperties 어노테이션이 부착되어있는 것을 확인할 수 있습니다.
  - 인텔리제이 환경에서 application.yml의  datasource, redis, jpa와 같은 prefix를 command를 누르고 클릭하면 해당 클래스를 확인할 수 있습니다.

- 그래서 개발자가 디테일하게 커스텀한다거나, 다른 정보의 bean을 여러개 생성하지 않는다면 Spring에서 application.yml 정보를 통해서 Bean으로 자동적으로 등록해줍니다.

- RedisProperties 클래스 일부

![](https://velog.velcdn.com/images/cmsskkk/post/4e0ebb98-c354-449e-bc94-e57119cb5920/image.png)



- DataSourceProperties 클래스 일부
  ![](https://velog.velcdn.com/images/cmsskkk/post/62d215c9-ebf5-4a5f-8f2d-48a5c2145866/image.png)


- JpaProperties 클래스 일부
  ![](https://velog.velcdn.com/images/cmsskkk/post/24d4b322-162a-4239-9ab8-c8fac7bc84ae/image.png)


- @Value 어노테이션에서 RedisProperties 활용해서 세팅 수정해보겠습니다.

  ```java
  @Configuration
  @RequiredArgsConstructor
  @Slf4j
  public class RedisConfig {
  		
      private final RedisProperties redisProperties;
  
      @Bean
      public RedisConnectionFactory redisConnectionFactory() {
          log.info("host={}, port={}", redisProperties.getHost(), redisProperties.getPort() );
          return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
      }
  
      @Bean
      public RedisTemplate<String, String> redisTemplate() {
          RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
          redisTemplate.setConnectionFactory(redisConnectionFactory());
          redisTemplate.setKeySerializer(new StringRedisSerializer());
          redisTemplate.setValueSerializer(new StringRedisSerializer());
          redisTemplate.setEnableTransactionSupport(true);
          return redisTemplate;
      }
  
  }
  ```

- 자동으로 RedisProperties를 만들어주기 때문에 RedisProperties만 생성자 주입을 통해서 RedisConfig를 Bean으로 등록이 가능해집니다.

  - 위처럼 간단한 local 환경으로 세팅이라면 RedisProperties의 default 설정값이 있기때문에 application.yml에 설정정보를 추가를 하지않아도 정상적으로 실행이 가능합니다.

- Spring Data Jdbc, Spring Data Jpa와 같은 Spring data 프로젝트의 의존성을 활용해보신 분들이라면 application.yml 설정만을 통해서 JpaRepository, Transaction, JdbcTemplate 등을 사용해보신 경험이 있을 것입니다.

- Spring에서 자동적으로 @ConfigurationProperties 어노테이션을 활용하여 Bean으로 등록해줘서 가능합니다.

## 3. @CofingurationProperties 커스텀해서 사용하기

### @EnableConfigurationProperties

- `annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'`  의존성을 필요로합니다.

- Spring에서 자동적으로 해당 정보를 Bean으로 등록해주지 않는다면, @EnableConfigurationProperties라는 어노테이션을 필요로합니다.

- @Configuration 클래스 또는 Application 코드에 @EnableConfigurationProperties(~Properties.class)를 설정해주어야 합니다.

- 예시로 설명해드리면 Spring에서 Redis를 통해서 캐싱을 사용하기위해서 CacheManager를 Bean으로 등록해줘야합니다.

- 이 때도 동일하게 application.yml 에 아래와 같이 설정할 수 있습니다.

  ```yaml
  cache:
      type: redis # cache를 레디스 활용
      redis: 
        time-to-live: 600000 # redis 정보 중 
  ```

  - 그리고 또한 CacheProperties라는 클래스 또한 존재합니다.
  - 하지만 위에서 RedisProperties의 의존성을 사용했던것과는 다르게 CacheProperties는 빈으로 등록이 되지 않습니다. Spring에서 자동적으로 Bean으로 등록해주지 않는 것 같습니다.

- 이 때, @EnableConfigurationProperties(CacheProperties.class) 어노테이션을 부착하면 정상적으로 작동합니다.

```java
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    private final CacheProperties cacheProperties;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(RedisSerializer.json()))
                .entryTtl(cacheProperties.getRedis().getTimeToLive());
										
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
```

- @ConfigurationPropertiesScan을 통해서 패키지전체를 설정해줄 수도 있다.

### 나만의 Properties 파일 만들기

1. .yml 파일 생성하기

  - application.yml 내에 작성해도 되지만, 파일을 분리하고 설정하는 부분까지 설명하고자 합니다.
  - /resource/custom/setting.yml 로 만들었습니다.

2. 하나로 묶을 prefix와 key를 계층적으로 구성해서 설정합니다.

   ```yaml
   # /resource/custom/setting.yml
   custom:
     id: 1
     user-name: aaron2 # yaml의 경우 케밥케이스와 스네이크케이스를 사용(케밥케이스를 권장)
     email: cms05041@gmail.com
   ```

3. application.yml에 import를 설정합니다.

   ```yaml
   spring:
   
     config:
       import: custom/setting.yml
   ```

4. CustomProperties 클래스를 생성합니다.

   ```java
   @ConfigurationProperties(prefix = "custom") // prefix 설정 
   @RequiredArgsConstructor // 롬복 생성자
   @ConstructorBinding // 생성자로 설정정보 주입
   @Getter 
   public class CustomProperties {
   
       private final Integer id;
       private final String userName;
       private final String email;
   
   }
   ```

  - @ConfigurationProperties에 하나로 묶어줄 prefix 키를 알려줍니다.
  - @ConstructorBinding
    - 해당 어노테이션이 없으면 Bean을 생성할 때, 기본 생성자와 Setter를 필요로 합니다.
    - 어노테이션을 부착함으로써 변수에 final 키워드를 적용해 불변 객체로 만들 수 있습니다.

5. CustomConfig 클래스를 생성합니다.

  - @PropertySource 설정정보가 있는 path를 알려줍니다.

   ```java
   @Configuration
   @EnableConfigurationProperties(CustomProperties.class)
   @PropertySource("classpath:custom/setting.yml") // 설정정보 위치 생략가능
   @RequiredArgsConstructor
   public class CustomConfig {
   
       private final CustomProperties customProperties;
   }
   ```

6. 설정정보가 정상적으로 주입되고 Bean으로 생성되었는지 테스트합니다

   ```java
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
   ```

### InnerClass를 활용해서 확장성있게 활용하기

- 같은 역할을 하는 설정정보를 Map형태로 저장하여 활용할 수 있습니다.
  - 서비스에서 Oauth Provider가 Github, Naver, Kakao와 같이 여러 설정정보를 저장해야할 때 구성할 수 있습니다.
  - 같은 역할의 설정이라면 하나하나의 클래스를 만드는 것보다 관리와 확장성 측면에서 좋습니다.
  - Spring Security에서도 이러한 형태로 provider 설정정보를 관리합니다.
  - Oauth 로그인을 토이프로젝트에 직접 구현하면서 활용해보았습니다.

1. .yml 설정

   ```yaml
   auth: # prefix
     provider: # Map 객체 이름 
       github: # Map에서 key값
         client-id: github_client_id
         client-secret: github_client_secret
         access-token-uri: http://localhost:9090/login/oauth/access_token
         user-info-uri: http://localhost:9090/user,http://localhost:9090/user/emails
       naver: # Map에서 key값
         client-id: naver_client_id
         client-secret: naver_client_secret
         access-token-uri: http://localhost:9090/oauth2.0/token
         user-info-uri: http://localhost:9090/v1/nid/me
   ```

- yml 파일로 생성시 인덴트를 통한 계층을 신경써야합니다.

2. AuthProperties 클래스 구현하기

   ```java
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
   ```

- **Map 객체의 이름과 prefix 다음의 key와 필수적으로 일치해야 합니다.**
- github, naver와 같이 구분하는 key는 Map 자료구조의 key 값이 됩니다.
3. OauthConfing 클래스 구현하기
```java
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
@PropertySource("classpath:custom/setting.yml")
@RequiredArgsConstructor
public class OauthConfig {

    private final AuthProperties authProperties;
}
```

4. 설정정보 테스트해보기

   ```java
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
   ```

## 결론

- @Value의 경우 단일 환경 변수를 활용하기에는 간편합니다.
- @ConfigurationProperties를 활용하면 설정정보를 하나로 묶어서 관리할 수 있고 생성자 주입을 통해서 불변하게 설정할 수 있습니다.
- 스프링 컨테이너가 application.yml 정보를 통해서 bean을 등록할 때 @ConfigurationProperties를 활용합니다.

### References
- [https://velog.io/@max9106/OAuth4](https://velog.io/@max9106/OAuth4)
- https://stackoverflow.com/questions/64467433/keys-of-nested-map-loaded-with-configurationproperties-are-prefixed-with-their-r

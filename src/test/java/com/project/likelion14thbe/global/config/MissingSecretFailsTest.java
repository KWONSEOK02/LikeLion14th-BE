package com.project.likelion14thbe.global.config;

import com.project.likelion14thbe.domain.auth.repository.TokenRepository;
import com.project.likelion14thbe.global.security.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MissingSecretFailsTest {

    private final ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withUserConfiguration(JwtBindingTestConfig.class);

    @Test
    @DisplayName("spring.jwt.secret 가 빈 값이면 JwtUtil 자체 검증으로 부팅이 실패한다")
    void contextFailsWhenJwtSecretBlank() {
        // prod yml 에서 ${JWT_SECRET} default 폴백을 제거했으므로 환경변수 누락 시
        // 빈 문자열 또는 너무 짧은 값으로 들어오는데, JwtUtil 의 secret.isBlank() 길이 검증이 안전망 역할
        runner
                .withPropertyValues("spring.jwt.secret=")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("spring.jwt.secret 미설정 또는 너무 짧음");
                });
    }

    @Test
    @DisplayName("spring.jwt.secret 가 32바이트 미만이면 JwtUtil 자체 검증으로 실패한다")
    void contextFailsWhenJwtSecretTooShort() {
        runner
                .withPropertyValues(
                        "spring.jwt.secret=too-short",
                        "spring.jwt.token.access-expiration-time=1800000",
                        "spring.jwt.token.refresh-expiration-time=1209600000"
                )
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .hasMessageContaining("spring.jwt.secret 미설정 또는 너무 짧음");
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class JwtBindingTestConfig {

        @Bean
        TokenRepository tokenRepository() {
            return mock(TokenRepository.class);
        }

        @Bean
        JwtUtil jwtUtil(
                @Value("${spring.jwt.secret}") String secret,
                @Value("${spring.jwt.token.access-expiration-time:1800000}") Long access,
                @Value("${spring.jwt.token.refresh-expiration-time:1209600000}") Long refresh,
                TokenRepository tokenRepository) {
            return new JwtUtil(secret, access, refresh, tokenRepository);
        }
    }
}

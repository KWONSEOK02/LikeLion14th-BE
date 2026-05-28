package com.project.likelion14thbe.global.config;

import com.project.likelion14thbe.support.AbstractDbIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("prod")
@TestPropertySource(properties = {
        "DB_URL=jdbc:mysql://localhost:3306/likelion_prod_test",
        "DB_USERNAME=prod_user",
        "DB_PASSWORD=prod_pass",
        "REDIS_HOST=localhost",
        "REDIS_PORT=6379",
        "JWT_SECRET=prod_test_secret_long_enough_base64_encoded_dummy_dummy_dummy=",
        "KAKAO_CLIENT_ID=prod_kakao",
        "KAKAO_CLIENT_SECRET=prod_kakao_secret",
        "KAKAO_REDIRECT_URI=https://example.com/api/v1/kakao/callback",
        "NAVER_CLIENT_ID=prod_naver",
        "NAVER_CLIENT_SECRET=prod_naver_secret",
        "NAVER_REDIRECT_URI=https://example.com/api/v1/naver/callback"
})
class ProdProfileActivationTest extends AbstractDbIntegrationTest {

    @Autowired
    Environment env;

    @Test
    @DisplayName("prod 프로필 활성화 시 env 변수에서 secret/설정 이 바인딩된다")
    void prodProfileBindsEnvVars() {
        // testcontainer 가 @DynamicPropertySource 로 덮어쓰는 prop (datasource.url, ddl-auto 등)은 검증 대상 제외.
        // prod yml 의 raw 내용 검증은 ProdYmlContentTest 가 별도 보장.
        assertThat(env.getActiveProfiles()).contains("prod");
        assertThat(env.getProperty("kakao.client.secret"))
                .isEqualTo("prod_kakao_secret");
        assertThat(env.getProperty("naver.client.secret"))
                .isEqualTo("prod_naver_secret");
        assertThat(env.getProperty("management.endpoint.health.show-details"))
                .isEqualTo("when-authorized");
    }
}

package com.project.likelion14thbe.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * application-prod.yml 의 raw 내용을 검증.
 * testcontainer @DynamicPropertySource 가 덮어쓰는 prop 은 ApplicationContext 검증이 불가하므로
 * yml 파일 자체의 안전 가드는 raw text 단언으로 박제한다.
 */
class ProdYmlContentTest {

    @Test
    @DisplayName("application-prod.yml 의 ddl-auto 는 validate (prod 에서 update/create 금지)")
    void prodYmlDdlAutoIsValidate() throws IOException {
        String content = readProdYml();
        assertThat(content).contains("ddl-auto: validate");
        assertThat(content).doesNotContain("ddl-auto: update");
        assertThat(content).doesNotContain("ddl-auto: create");
        assertThat(content).doesNotContain("ddl-auto: create-drop");
    }

    @Test
    @DisplayName("application-prod.yml 의 jwt.secret 은 default 폴백 없이 env 만 참조")
    void prodYmlJwtSecretHasNoFallback() throws IOException {
        String content = readProdYml();
        // ${JWT_SECRET} 만 허용, ${JWT_SECRET:...} 형태는 금지
        assertThat(content).contains("secret: ${JWT_SECRET}");
        assertThat(content).doesNotContain("secret: ${JWT_SECRET:");
    }

    @Test
    @DisplayName("application-prod.yml 의 datasource 자격증명은 모두 env 만 참조")
    void prodYmlDatasourceCredentialsHaveNoFallback() throws IOException {
        String content = readProdYml();
        assertThat(content).contains("url: ${DB_URL}");
        assertThat(content).contains("username: ${DB_USERNAME}");
        assertThat(content).contains("password: ${DB_PASSWORD}");
        // ${DB_URL:...} 같은 default 금지
        assertThat(content).doesNotContain("${DB_URL:");
        assertThat(content).doesNotContain("${DB_PASSWORD:");
    }

    private String readProdYml() throws IOException {
        return new String(
                new ClassPathResource("application-prod.yml").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
    }
}

package com.project.likelion14thbe.global.security.config;

import com.project.likelion14thbe.support.AbstractDbIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SecurityConfig.allowUrl 회귀 가드 — OAuth 인가 진입 / Swagger 경로는 익명 통과,
 * 보호 경로는 401 응답. 로컬 검증 전용 — push 하지 않는다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityWhitelistIntegrationTest extends AbstractDbIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("OAuth 인가 진입(/api/v1/auth/kakao) 은 익명으로 302 리다이렉트한다")
    void kakaoAuthorize_passesWhitelist() throws Exception {
        mockMvc.perform(get("/api/v1/auth/kakao"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("OAuth 인가 진입(/api/v1/auth/naver) 도 익명으로 302 리다이렉트한다")
    void naverAuthorize_passesWhitelist() throws Exception {
        mockMvc.perform(get("/api/v1/auth/naver"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Swagger /v3/api-docs 는 익명으로 200 반환한다")
    void apiDocs_passesWhitelist() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("보호 경로(/api/v1/members/me) 는 토큰 없으면 401 반환한다")
    void protectedEndpoint_blocksAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/members/me"))
                .andExpect(status().isUnauthorized());
    }
}

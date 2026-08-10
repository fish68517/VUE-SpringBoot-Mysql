package com.charging.platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PhaseTwoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRecommendationsAndGuidePoints() throws Exception {
        mockMvc.perform(get("/api/home/recommendations").param("limit", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(4)));

        mockMvc.perform(get("/api/stations/1/guide"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].pointName").value("车辆入口"));
    }

    @Test
    void shouldReturnRegionRankingAndTrend() throws Exception {
        mockMvc.perform(get("/api/statistics/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[0].usageRate").isNumber());

        mockMvc.perform(get("/api/statistics/trend").param("regionId", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(7))))
                .andExpect(jsonPath("$.data[0].statDate").isString());
    }

    @Test
    void shouldAddCheckListAndRemoveFavorite() throws Exception {
        String response = mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":2,"stationId":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.favorited").value(true))
                .andReturn().getResponse().getContentAsString();

        long favoriteId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response).path("data").path("favoriteId").asLong();

        mockMvc.perform(get("/api/favorites/check")
                        .param("userId", "2")
                        .param("stationId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.favorited").value(true));

        mockMvc.perform(get("/api/favorites").param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", greaterThanOrEqualTo(2)));

        mockMvc.perform(delete("/api/favorites/{id}", favoriteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldSubmitAndListFeedback() throws Exception {
        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId":2,
                                  "feedbackType":0,
                                  "title":"第二阶段接口测试反馈",
                                  "content":"用于验证反馈提交和列表查询。",
                                  "contact":"13800000002"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.processStatus").value(0));

        mockMvc.perform(get("/api/feedback").param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", greaterThanOrEqualTo(2)));
    }

    @Test
    void shouldReadAndUpdateProfileWithoutReturningPassword() throws Exception {
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("zhangsan"))
                .andExpect(jsonPath("$.data.password").doesNotExist());

        mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nickname":"张三同学","phone":"13800000002","email":"zhangsan@example.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("张三同学"));
    }
}

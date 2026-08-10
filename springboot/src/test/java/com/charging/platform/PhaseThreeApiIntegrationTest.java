package com.charging.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PhaseThreeApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnDashboardAndEveryAdminModule() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.overview.stationCount", greaterThan(0)))
                .andExpect(jsonPath("$.data.pendingFeedbackCount", greaterThanOrEqualTo(0)));

        String[] modules = {"users", "regions", "stations", "piles", "status", "usage-records",
                "statistics", "guide-points", "notices", "feedback"};
        for (String module : modules) {
            mockMvc.perform(get("/api/admin/{module}", module).param("pageSize", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.records").isArray());
        }
    }

    @Test
    void shouldCreateUpdateAndDeleteStationVisibleToFrontApi() throws Exception {
        String response = mockMvc.perform(post("/api/admin/stations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "regionId":4,
                                  "stationCode":"TEST-PHASE3-001",
                                  "stationName":"第三阶段联调站点",
                                  "address":"温州市鹿城区测试路1号",
                                  "longitude":120.700001,
                                  "latitude":27.990001,
                                  "stationType":0,
                                  "openTime":"00:00-24:00",
                                  "operatorName":"测试运营商",
                                  "status":1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn().getResponse().getContentAsString();
        JsonNode data = objectMapper.readTree(response).path("data");
        long id = data.path("id").asLong();

        mockMvc.perform(get("/api/stations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stationName").value("第三阶段联调站点"));

        mockMvc.perform(put("/api/admin/stations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "regionId":4,
                                  "stationCode":"TEST-PHASE3-001",
                                  "stationName":"第三阶段已更新站点",
                                  "address":"温州市鹿城区测试路2号",
                                  "longitude":120.700002,
                                  "latitude":27.990002,
                                  "stationType":0,
                                  "status":1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stationName").value("第三阶段已更新站点"));

        mockMvc.perform(delete("/api/admin/stations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldReplyFeedbackAndRegenerateStatistics() throws Exception {
        mockMvc.perform(put("/api/admin/feedback/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId":2,
                                  "feedbackType":0,
                                  "title":"建议增加夜间照明说明",
                                  "content":"希望详情页能够显示站点夜间照明情况。",
                                  "contact":"13800000002",
                                  "processStatus":2,
                                  "replyContent":"已完成信息补充。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.processStatus").value(2))
                .andExpect(jsonPath("$.data.processTime").isString());

        mockMvc.perform(post("/api/admin/statistics/regenerate")
                        .param("statDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.affectedRows", greaterThanOrEqualTo(0)));
    }

    @Test
    void shouldRunControlledSimulation() throws Exception {
        mockMvc.perform(post("/api/admin/simulation/run"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updatedPileCount", greaterThan(0)))
                .andExpect(jsonPath("$.data.faultCount", greaterThanOrEqualTo(0)));
    }

    @Test
    void shouldRejectDeletingStationWithPiles() throws Exception {
        mockMvc.perform(delete("/api/admin/stations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("该站点下存在充电桩，请先处理充电桩"));
    }
}

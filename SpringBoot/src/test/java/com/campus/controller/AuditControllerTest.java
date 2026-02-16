package com.campus.controller;

import com.campus.dto.ActivityDTO;
import com.campus.dto.AuditLogDTO;
import com.campus.dto.FundProofDTO;
import com.campus.entity.Activity;
import com.campus.entity.AuditLog;
import com.campus.entity.FundProof;
import com.campus.service.AuditService;
import com.campus.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Audit Controller Test
 */
@ExtendWith(MockitoExtension.class)
public class AuditControllerTest {

    @Mock
    private AuditService auditService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuditController auditController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetPendingAuditActivities() throws Exception {
        // Arrange
        ActivityDTO activityDTO = ActivityDTO.builder()
                .id(1L)
                .title("Test Activity")
                .type("lecture")
                .status(Activity.ActivityStatus.PENDING_AUDIT)
                .build();

        Page<ActivityDTO> page = new PageImpl<>(Arrays.asList(activityDTO), PageRequest.of(0, 10), 1);
        when(auditService.getPendingAuditActivities(0, 10)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/audit/activities")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].title").value("Test Activity"));

        verify(auditService, times(1)).getPendingAuditActivities(0, 10);
    }

    @Test
    public void testAuditActivityApprove() throws Exception {
        // Arrange
        String token = "valid-token";
        ActivityDTO activityDTO = ActivityDTO.builder()
                .id(1L)
                .title("Test Activity")
                .status(Activity.ActivityStatus.APPROVED)
                .build();

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("admin");
        when(auditService.auditActivity(1L, true, null, "admin")).thenReturn(activityDTO);

        AuditController.AuditRequest auditRequest = new AuditController.AuditRequest(true, null);

        // Act & Assert
        mockMvc.perform(put("/api/audit/activities/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        verify(auditService, times(1)).auditActivity(1L, true, null, "admin");
    }

    @Test
    public void testAuditActivityReject() throws Exception {
        // Arrange
        String token = "valid-token";
        ActivityDTO activityDTO = ActivityDTO.builder()
                .id(1L)
                .title("Test Activity")
                .status(Activity.ActivityStatus.REJECTED)
                .build();

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("admin");
        when(auditService.auditActivity(1L, false, "Invalid content", "admin")).thenReturn(activityDTO);

        AuditController.AuditRequest auditRequest = new AuditController.AuditRequest(false, "Invalid content");

        // Act & Assert
        mockMvc.perform(put("/api/audit/activities/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("REJECTED"));

        verify(auditService, times(1)).auditActivity(1L, false, "Invalid content", "admin");
    }

    @Test
    public void testAuditActivityMissingToken() throws Exception {
        // Arrange
        AuditController.AuditRequest auditRequest = new AuditController.AuditRequest(true, null);

        // Act & Assert
        mockMvc.perform(put("/api/audit/activities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(auditService, never()).auditActivity(anyLong(), anyBoolean(), anyString(), anyString());
    }

    @Test
    public void testGetActivityAuditLogs() throws Exception {
        // Arrange
        AuditLogDTO auditLogDTO = AuditLogDTO.builder()
                .id(1L)
                .auditorId(1L)
                .targetId(1L)
                .auditType(AuditLog.AuditType.ACTIVITY)
                .auditStatus(AuditLog.AuditStatus.APPROVED)
                .build();

        Page<AuditLogDTO> page = new PageImpl<>(Arrays.asList(auditLogDTO), PageRequest.of(0, 10), 1);
        when(auditService.getActivityAuditLogs(1L, 0, 10)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/audit/activities/1/logs")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].auditStatus").value("APPROVED"));

        verify(auditService, times(1)).getActivityAuditLogs(1L, 0, 10);
    }

    @Test
    public void testGetPendingAuditFundProofs() throws Exception {
        // Arrange
        FundProofDTO fundProofDTO = FundProofDTO.builder()
                .id(1L)
                .activityId(1L)
                .organizerId(1L)
                .fileUrl("/uploads/fund-proofs/proof.pdf")
                .status(FundProof.ProofStatus.PENDING_AUDIT)
                .build();

        Page<FundProofDTO> page = new PageImpl<>(Arrays.asList(fundProofDTO), PageRequest.of(0, 10), 1);
        when(auditService.getPendingAuditFundProofs(0, 10)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/audit/crowdfunding")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].status").value("PENDING_AUDIT"));

        verify(auditService, times(1)).getPendingAuditFundProofs(0, 10);
    }

    @Test
    public void testAuditFundProofApprove() throws Exception {
        // Arrange
        String token = "valid-token";
        FundProofDTO fundProofDTO = FundProofDTO.builder()
                .id(1L)
                .activityId(1L)
                .organizerId(1L)
                .status(FundProof.ProofStatus.APPROVED)
                .build();

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("admin");
        when(auditService.auditFundProof(1L, true, null, "admin")).thenReturn(fundProofDTO);

        AuditController.AuditRequest auditRequest = new AuditController.AuditRequest(true, null);

        // Act & Assert
        mockMvc.perform(put("/api/audit/crowdfunding/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        verify(auditService, times(1)).auditFundProof(1L, true, null, "admin");
    }

    @Test
    public void testAuditFundProofReject() throws Exception {
        // Arrange
        String token = "valid-token";
        FundProofDTO fundProofDTO = FundProofDTO.builder()
                .id(1L)
                .activityId(1L)
                .organizerId(1L)
                .status(FundProof.ProofStatus.REJECTED)
                .rejectionReason("Invalid proof")
                .build();

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("admin");
        when(auditService.auditFundProof(1L, false, "Invalid proof", "admin")).thenReturn(fundProofDTO);

        AuditController.AuditRequest auditRequest = new AuditController.AuditRequest(false, "Invalid proof");

        // Act & Assert
        mockMvc.perform(put("/api/audit/crowdfunding/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("REJECTED"));

        verify(auditService, times(1)).auditFundProof(1L, false, "Invalid proof", "admin");
    }

    @Test
    public void testAuditFundProofMissingToken() throws Exception {
        // Arrange
        AuditController.AuditRequest auditRequest = new AuditController.AuditRequest(true, null);

        // Act & Assert
        mockMvc.perform(put("/api/audit/crowdfunding/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(auditService, never()).auditFundProof(anyLong(), anyBoolean(), anyString(), anyString());
    }

}

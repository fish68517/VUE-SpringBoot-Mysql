package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Real name verification status response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealNameStatusResponse {

    private Boolean isVerified;

    private String realName;

    private String maskedIdNumber;

}

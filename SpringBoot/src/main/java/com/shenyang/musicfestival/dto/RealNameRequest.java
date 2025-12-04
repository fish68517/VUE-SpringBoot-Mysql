package com.shenyang.musicfestival.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Real name verification request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealNameRequest {

    private String realName;

    private String idNumber;

}

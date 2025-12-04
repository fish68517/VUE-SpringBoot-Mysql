package com.shenyang.musicfestival.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String phone;

    private String nickname;

    private String avatar;

    private String email;

    private String realName;

    private String idNumber;

    @JsonProperty("maskedIdNumber")
    private String maskedIdNumber;

    private Boolean isRealNameVerified;

    private Long points;

    private Boolean isBlocked;

    private String role;

}

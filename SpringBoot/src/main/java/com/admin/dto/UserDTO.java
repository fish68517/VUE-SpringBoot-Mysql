package com.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Double height;
    private Double weight;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

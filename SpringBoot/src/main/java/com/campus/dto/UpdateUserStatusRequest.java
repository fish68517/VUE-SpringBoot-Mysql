package com.campus.dto;

import com.campus.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update User Status Request DTO
 * Used for updating user account status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {

    /**
     * New account status
     */
    @NotNull(message = "Status cannot be null")
    private User.AccountStatus status;

}

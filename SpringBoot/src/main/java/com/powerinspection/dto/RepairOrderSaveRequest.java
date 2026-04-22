package com.powerinspection.dto;

import com.powerinspection.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepairOrderSaveRequest {

    @NotNull(message = "缺陷记录不能为空")
    private Long defectId;

    @NotNull(message = "维护员不能为空")
    private Long maintainerId;

    private OrderStatus status = OrderStatus.PENDING;

    private String measures;

    private String result;

    private String imageName;

    private LocalDateTime finishedAt;

    private String remark;
}

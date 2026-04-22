package com.powerinspection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceRequest {
    @NotNull(message = "设备分类不能为空")
    private Long categoryId;
    @NotBlank(message = "设备编号不能为空")
    private String deviceCode;
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;
    @NotBlank(message = "安装位置不能为空")
    private String location;
    @NotBlank(message = "设备状态不能为空")
    private String status;
    private String imageName;
    private String remark;
}

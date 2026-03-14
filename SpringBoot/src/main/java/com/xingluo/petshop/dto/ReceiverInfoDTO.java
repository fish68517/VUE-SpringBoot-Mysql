package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 更新订单收货人信息 DTO
 */
@Data
public class ReceiverInfoDTO {
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
}